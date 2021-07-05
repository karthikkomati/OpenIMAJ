package uk.ac.soton.ecs.kk8g18.ch3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.connectedcomponent.GreyscaleConnectedComponentLabeler;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.processor.PixelProcessor;
import org.openimaj.image.segmentation.FelzenszwalbHuttenlocherSegmenter;
import org.openimaj.image.segmentation.SegmentationUtilities;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.shape.Ellipse;
import org.openimaj.ml.clustering.FloatCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;
import org.openimaj.ml.clustering.kmeans.FloatKMeans;

import uk.ac.soton.ecs.kk8g18.ch1.Chapter1;
import uk.ac.soton.ecs.kk8g18.ch2.Chapter2;

/**
 * OpenIMAJ Hello world!
 *
 */
public class Chapter3 {
	

    public static void pixelProcessor() throws MalformedURLException, IOException {
    	

    	MBFImage input = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));
    	input = ColourSpace.convert(input, ColourSpace.CIE_Lab);
    	FloatKMeans cluster = FloatKMeans.createExact(2);
    	float[][] imageData = input.getPixelVectorNative(new float[input.getWidth() * input.getHeight()][3]);
    	FloatCentroidsResult result = cluster.cluster(imageData);
    	
           	
    	
    	final float[][] cen = result.centroids;
    	
    	final HardAssigner<float[],?,?> a = result.defaultHardAssigner();
    	
    	
    	//uses a pixel processor to replace each pixel with its class centroid
    	
    	//using a pixel processor makes the program run faster since it avoids using nested loops
    	
    	input.processInplace(new PixelProcessor<Float[]>() {
    		
    		
    		
    	    public Float[] processPixel(Float[] pixel) {
    	    	float[] p = new float[pixel.length];
    	    	for(int i = 0;i < pixel.length; i++) {
    	    		p[i] = pixel[i];
    	    	}
    	    	int c = a.assign(p);
    	    	Float[] c1 = new Float[cen[c].length];
    	    	for(int j = 0; j<cen[c].length;j++) {
    	    		c1[j] = cen[c][j];
    	    	}
				return c1;
    	        
    	    }
    	});
    	

    	
    	
    	input = ColourSpace.convert(input, ColourSpace.RGB);

    	

    	GreyscaleConnectedComponentLabeler labeler = new GreyscaleConnectedComponentLabeler();
    	List<ConnectedComponent> components = labeler.findComponents(input.flatten());

    	int i = 0;
    	for (ConnectedComponent comp : components) {
    	    if (comp.calculateArea() < 50) 
    	        continue;
    	    input.drawText("Point:" + (i++), comp.calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);
    	}

    	DisplayUtilities.display(input);

    }
    
    //Uses FelzenszwalbHuttenlocherSegmenter to implement a different segmentation algorithm    
    public static void segmentation() throws MalformedURLException, IOException {
    	
    	MBFImage input = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));
    	FelzenszwalbHuttenlocherSegmenter<MBFImage> f = new FelzenszwalbHuttenlocherSegmenter<MBFImage>();
    	
    	
    	
    	MBFImage im = SegmentationUtilities.renderSegments(input,f.segment(input));
    	DisplayUtilities.display(im,"final");
    }
}
