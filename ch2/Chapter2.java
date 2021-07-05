package uk.ac.soton.ecs.kk8g18.ch2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.shape.Ellipse;

public class Chapter2 {
	
	public static void main( String[] args ) throws MalformedURLException, IOException {
    	
	       
	    	
	    	MBFImage image = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));
	    	
	    	System.out.println(image.colourSpace);
	    	
	    	
	    	
	    	
	    	//creates a named window to display images so only one window needs to be used throughout
	    	DisplayUtilities.displayName(image, "test");
	    	DisplayUtilities.displayName(image.getBand(0), "test");
	    	
	    	MBFImage clone = image.clone();
	    	for (int y=0; y<image.getHeight(); y++) {
	    	    for(int x=0; x<image.getWidth(); x++) {
	    	        clone.getBand(1).pixels[y][x] = 0;
	    	        clone.getBand(2).pixels[y][x] = 0;
	    	    }
	    	}
	    	
	    	
	    	DisplayUtilities.displayName(clone,"test");
	    	
	    	clone.getBand(1).fill(0f);
	    	clone.getBand(2).fill(0f);
	    	
	    	image.processInplace(new CannyEdgeDetector());
	    	
	    	DisplayUtilities.displayName(image, "test");
	    	
	    	
	    	
	    	image.drawShapeFilled(new Ellipse(700f, 450f, 20f, 10f, 0f), RGBColour.WHITE);
	    	
	    	//creates a green border around the speech bubbles
	    	image.drawShape(new Ellipse(700f, 450f, 20f, 10f, 0f), RGBColour.GREEN);
	    	
	    	image.drawShapeFilled(new Ellipse(650f, 425f, 25f, 12f, 0f), RGBColour.WHITE);
	    	image.drawShape(new Ellipse(650f, 425f, 25f, 12f, 0f), RGBColour.GREEN);
	    	
	    	image.drawShapeFilled(new Ellipse(600f, 380f, 30f, 15f, 0f), RGBColour.WHITE);
	    	image.drawShape(new Ellipse(600f, 380f, 30f, 15f, 0f), RGBColour.GREEN);
	    	
	    	image.drawShapeFilled(new Ellipse(500f, 300f, 100f, 70f, 0f), RGBColour.WHITE);
	    	image.drawShape(new Ellipse(500f, 300f, 100f, 70f, 0f), RGBColour.GREEN);
	    	image.drawText("OpenIMAJ is", 425, 300, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
	    	image.drawText("Awesome", 425, 330, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
	    	DisplayUtilities.displayName(image, "test");

	    }

}
