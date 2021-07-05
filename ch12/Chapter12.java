package uk.ac.soton.ecs.kk8g18.ch12;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openimaj.data.DataSource;
import org.openimaj.data.dataset.Dataset;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.experiment.dataset.sampling.GroupSampler;
import org.openimaj.experiment.dataset.sampling.GroupedUniformRandomisedSampler;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.experiment.evaluation.classification.ClassificationEvaluator;
import org.openimaj.experiment.evaluation.classification.ClassificationResult;
import org.openimaj.experiment.evaluation.classification.analysers.confusionmatrix.CMAnalyser;
import org.openimaj.experiment.evaluation.classification.analysers.confusionmatrix.CMResult;
import org.openimaj.feature.DiskCachingFeatureExtractor;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureExtractor;
import org.openimaj.feature.SparseIntFV;
import org.openimaj.feature.local.data.LocalFeatureListDataSource;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.annotation.evaluation.datasets.Caltech101;
import org.openimaj.image.annotation.evaluation.datasets.Caltech101.Record;
import org.openimaj.image.feature.dense.gradient.dsift.ByteDSIFTKeypoint;
import org.openimaj.image.feature.dense.gradient.dsift.DenseSIFT;
import org.openimaj.image.feature.dense.gradient.dsift.PyramidDenseSIFT;
import org.openimaj.image.feature.local.aggregate.BagOfVisualWords;
import org.openimaj.image.feature.local.aggregate.BlockSpatialAggregator;
import org.openimaj.image.feature.local.aggregate.PyramidSpatialAggregator;
import org.openimaj.io.IOUtils;
import org.openimaj.ml.annotation.linear.LiblinearAnnotator;
import org.openimaj.ml.annotation.linear.LiblinearAnnotator.Mode;
import org.openimaj.ml.clustering.ByteCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;
import org.openimaj.ml.clustering.kmeans.ByteKMeans;
import org.openimaj.ml.kernel.HomogeneousKernelMap;
import org.openimaj.ml.kernel.HomogeneousKernelMap.KernelType;
import org.openimaj.ml.kernel.HomogeneousKernelMap.WindowType;
import org.openimaj.util.pair.IntFloatPair;

import de.bwaldvogel.liblinear.SolverType;
//import uk.ac.soton.ecs.kk8g18.test.App.PHOWExtractor;

public class Chapter12 {

	
public static void main( String[] args ) throws Exception {
		
		GroupedDataset<String, VFSListDataset<Record<FImage>>, Record<FImage>> allData = Caltech101.getData(ImageUtilities.FIMAGE_READER);
		GroupedDataset<String, ListDataset<Record<FImage>>, Record<FImage>> data = GroupSampler.sample(allData, 5, false);
		
		GroupedRandomSplitter<String, Record<FImage>> splits = 	new GroupedRandomSplitter<String, Record<FImage>>(data, 15, 0, 15);
	
		//reduced the step size to 3
		DenseSIFT dsift = new DenseSIFT(3, 7);
		
		//trying different scale size
		PyramidDenseSIFT<FImage> pdsift = new PyramidDenseSIFT<FImage>(dsift, 6f, 4);
		
		
		HardAssigner<byte[], float[], IntFloatPair> assigner =	trainQuantiser(GroupedUniformRandomisedSampler.sample(splits.getTrainingDataset(), 30), pdsift);
		
		
		//Applying a homogeneous kernel map onto the data
		//causes the program to take longer
		HomogeneousKernelMap h = new HomogeneousKernelMap(KernelType.Chi2,WindowType.Rectangular);		
		FeatureExtractor<DoubleFV, Record<FImage>> extractor = new PHOWExtractor(pdsift, assigner);
		extractor = h.createWrappedExtractor(extractor);
		
		LiblinearAnnotator<Record<FImage>, String> ann = new LiblinearAnnotator<Record<FImage>, String>(extractor, Mode.MULTICLASS, SolverType.L2R_L2LOSS_SVC, 1.0, 0.00001);
		ann.train(splits.getTrainingDataset());
		
		
		ClassificationEvaluator<CMResult<String>, String, Record<FImage>> eval = 
				new ClassificationEvaluator<CMResult<String>, String, Record<FImage>>(
					ann, splits.getTestDataset(), new CMAnalyser<Record<FImage>, String>(CMAnalyser.Strategy.SINGLE));
					
		Map<Record<FImage>, ClassificationResult<String>> guesses = eval.evaluate();
		CMResult<String> result = eval.analyse(guesses);
		System.out.println(result.getSummaryReport());
	}

	public static void m() throws IOException {
		
		GroupedDataset<String, VFSListDataset<Record<FImage>>, Record<FImage>> allData = Caltech101.getData(ImageUtilities.FIMAGE_READER);
		GroupedDataset<String, ListDataset<Record<FImage>>, Record<FImage>> data = GroupSampler.sample(allData, 5, false);
		
		GroupedRandomSplitter<String, Record<FImage>> splits = 	new GroupedRandomSplitter<String, Record<FImage>>(data, 15, 0, 15);
	
		DenseSIFT dsift = new DenseSIFT(3, 7);
		PyramidDenseSIFT<FImage> pdsift = new PyramidDenseSIFT<FImage>(dsift, 6f, 4);
		
		
		//reads the hard assigner from a file
		HardAssigner<byte[], float[], IntFloatPair> assigner = IOUtils.readFromFile(new File("assign"));
		
				
		
		
		FeatureExtractor<DoubleFV, Record<FImage>> ex = new PHOWExtractor(pdsift, assigner);
		
		
		//Uses a DiskCachingFeatureExtractor to save to or read from disk
		DiskCachingFeatureExtractor<DoubleFV, Record<FImage>> extractor = new DiskCachingFeatureExtractor<DoubleFV, Record<FImage>>(new File("t"),ex);
		
		
		
		LiblinearAnnotator<Record<FImage>, String> ann = new LiblinearAnnotator<Record<FImage>, String>(extractor, Mode.MULTICLASS, SolverType.L2R_L2LOSS_SVC, 1.0, 0.00001);
		ann.train(splits.getTrainingDataset());
		
		
		ClassificationEvaluator<CMResult<String>, String, Record<FImage>> eval = 
				new ClassificationEvaluator<CMResult<String>, String, Record<FImage>>(
					ann, splits.getTestDataset(), new CMAnalyser<Record<FImage>, String>(CMAnalyser.Strategy.SINGLE));
					
		Map<Record<FImage>, ClassificationResult<String>> guesses = eval.evaluate();
		CMResult<String> result = eval.analyse(guesses);
		
		//writes the hard assigner to a file
		IOUtils.writeToFile(assigner,new File("assign"));
		
		System.out.println(result.getSummaryReport());
	}
	
	static HardAssigner<byte[], float[], IntFloatPair> trainQuantiser(Dataset<Record<FImage>> sample, PyramidDenseSIFT<FImage> pdsift){
		List<LocalFeatureList<ByteDSIFTKeypoint>> allkeys = new ArrayList<LocalFeatureList<ByteDSIFTKeypoint>>();

		for (Record<FImage> rec : sample) {
				FImage img = rec.getImage();

				pdsift.analyseImage(img);
				allkeys.add(pdsift.getByteKeypoints(0.005f));
		}

		if (allkeys.size() > 10000)
			allkeys = allkeys.subList(0, 10000);

		ByteKMeans km = ByteKMeans.createKDTreeEnsemble(300);
		DataSource<byte[]> datasource = new LocalFeatureListDataSource<ByteDSIFTKeypoint, byte[]>(allkeys);
		ByteCentroidsResult result = km.cluster(datasource);

		return result.defaultHardAssigner();
	}
	
	static class PHOWExtractor implements FeatureExtractor<DoubleFV, Record<FImage>> {
	    PyramidDenseSIFT<FImage> pdsift;
	    HardAssigner<byte[], float[], IntFloatPair> assigner;

	    public PHOWExtractor(PyramidDenseSIFT<FImage> pdsift, HardAssigner<byte[], float[], IntFloatPair> assigner)
	    {
	        this.pdsift = pdsift;
	        this.assigner = assigner;
	    }

	    public DoubleFV extractFeature(Record<FImage> object) {
	        FImage image = object.getImage();
	        pdsift.analyseImage(image);

	        BagOfVisualWords<byte[]> bovw = new BagOfVisualWords<byte[]>(assigner);

	        //BlockSpatialAggregator<byte[], SparseIntFV> spatial = new BlockSpatialAggregator<byte[], SparseIntFV>(bovw, 2, 2);

	        PyramidSpatialAggregator<byte[], SparseIntFV> spatial = new PyramidSpatialAggregator<byte[], SparseIntFV>(bovw, 2, 4);

	        
	        return spatial.aggregate(pdsift.getByteKeypoints(0.015f), image.getBounds()).normaliseFV();
	    }
	}
}
