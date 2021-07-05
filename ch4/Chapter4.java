package uk.ac.soton.ecs.kk8g18.ch4;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;

public class Chapter4 {
	


	public static void main(String[] args) throws IOException {
		
	
		URL[] imageURLs = new URL[] {
				   new URL( "http://openimaj.org/tutorial/figs/hist1.jpg" ),
				   new URL( "http://openimaj.org/tutorial/figs/hist2.jpg" ), 
				   new URL( "http://openimaj.org/tutorial/figs/hist3.jpg" ) 
				};

		List<MultidimensionalHistogram> histograms = new ArrayList<MultidimensionalHistogram>();
		
		HistogramModel model = new HistogramModel(4, 4, 4);

		for( URL u : imageURLs ) {
		    model.estimateModel(ImageUtilities.readMBF(u));
		    histograms.add( model.histogram.clone() );
		}
				
		for( int i = 0; i < histograms.size(); i++ ) {
		    for( int j = i; j < histograms.size(); j++ ) {
		      
		    	//Uses a different comparison measure(Intersection)
		    	//In this case the higher the value the more similar they are
		    	//Gives the same result as the other comparison(first two images are the most similar)
		    	double distance = histograms.get(i).compare( histograms.get(j), DoubleFVComparison.INTERSECTION);	       
		        
		        
		    	System.out.println(distance);
		    }
		}
		
		int a = 0,b = 0;
		double dis = Double.MAX_VALUE;
		for( int i = 0; i < histograms.size(); i++ ) {
		    for( int j = i; j < histograms.size(); j++ ) {
		        double distance = histograms.get(i).compare( histograms.get(j), DoubleFVComparison.EUCLIDEAN );
		      
		        //calculates the two most similar images that are not the same
		        if(distance !=0 && distance < dis) {
		        	dis = distance;
		        	a=i;
		        	b=j; 
		        }
		        
		        
		        System.out.println(distance);
		    }
		}
		//The first and second images are the most similar which matches what I expected from looking at the images
		
		//Displays the two most similar images that are not the same
		DisplayUtilities.display(ImageUtilities.readMBF(imageURLs[a]));
		DisplayUtilities.display(ImageUtilities.readMBF(imageURLs[b]));
		

	}

}
