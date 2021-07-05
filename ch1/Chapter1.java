package uk.ac.soton.ecs.kk8g18.ch1;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;

public class Chapter1 {

	public static void main(String[] args) {
		 	MBFImage image = new MBFImage(500,70, ColourSpace.RGB);

	        //Fill the image with white
	        image.fill(RGBColour.WHITE);
	        		        
	        //Render some test into the image
	        //Renders other text in a different font and colour
	        image.drawText("Chapter 1 Exercise", 10, 60, HersheyFont.GOTHIC_ENGLISH, 50, RGBColour.MAGENTA);

	        //Apply a Gaussian blur
	        image.processInplace(new FGaussianConvolve(2f));
	        
	        //Display the image
	        DisplayUtilities.display(image);
		
	}
}
