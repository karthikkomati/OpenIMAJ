package uk.ac.soton.ecs.kk8g18.ch13;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.experiment.dataset.util.DatasetAdaptors;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.model.EigenImages;

public class Chapter13 {

	
	public static void main( String[] args ) throws Exception {
		
		VFSGroupDataset<FImage> dataset = new VFSGroupDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
	
//		int nTraining = 5;
		
		//reducing the training set size while keeping the testing set size the same
		//this causes lower accuracy
		int nTraining = 3;
		int nTesting = 5;
		GroupedRandomSplitter<String, FImage> splits = new GroupedRandomSplitter<String, FImage>(dataset, nTraining, 0, nTesting);
		GroupedDataset<String, ListDataset<FImage>, FImage> training = splits.getTrainingDataset();
		GroupedDataset<String, ListDataset<FImage>, FImage> testing = splits.getTestDataset();
		
		List<FImage> basisImages = DatasetAdaptors.asList(training);
		int nEigenvectors = 100;
		EigenImages eigen = new EigenImages(nEigenvectors);
		eigen.train(basisImages);
		
		List<FImage> eigenFaces = new ArrayList<FImage>();
		for (int i = 0; i < 12; i++) {
		    eigenFaces.add(eigen.visualisePC(i));
		}
		DisplayUtilities.display("EigenFaces", eigenFaces);
		
		Map<String, DoubleFV[]> features = new HashMap<String, DoubleFV[]>();
		for (final String person : training.getGroups()) {
		    final DoubleFV[] fvs = new DoubleFV[nTraining];

		    for (int i = 0; i < nTraining; i++) {
		        final FImage face = training.get(person).get(i);
		        fvs[i] = eigen.extractFeature(face);
		    }
		    features.put(person, fvs);
		}
	
		double correct = 0, incorrect = 0;
		int a =0;
		for (String truePerson : testing.getGroups()) {
		    for (FImage face : testing.get(truePerson)) {
		        DoubleFV testFeature = eigen.extractFeature(face);

		        String bestPerson = null;
		        double minDistance = Double.MAX_VALUE;
		        for (final String person : features.keySet()) {
		            for (final DoubleFV fv : features.get(person)) {
		                double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);

		                if (distance < minDistance) {
		                    minDistance = distance;
		                    bestPerson = person;
		                }
		            }
		        }
		        
		        //Incorporates a threshold
		        int t = 10;
		        //Having the threshold be too high would cause it to have no affect since most or even all of the distances would be lower than it
		        //Having a very low threshold could also mean that not many guesses are made
		        //I found having a threshold of 10 will give a high accuracy while also keeping the total percend guessed high
		        
		        
		        //Only makes a guess if the distance is lower than the threshold
		        //This causes an increase in accuracy
		        if(minDistance > t) {
		        	a++;
		        }
		        else {

		        System.out.println("Actual: " + truePerson + "\tguess: " + bestPerson);

		        if (truePerson.equals(bestPerson))
		            correct++;
		        else
		            incorrect++;
		        }
		    }
		}
		
		System.out.println("unguessed: " + a + " total: "+(correct + incorrect+a));

		System.out.println("Accuracy: " + (correct / (correct + incorrect)));
		
		
		//reconstructs a random original image 
		FImage im = eigen.reconstruct(eigen.extractFeature(testing.getRandomInstance())).normalise();
		
		DisplayUtilities.display(im,"image");
	
	}
	
	
}
