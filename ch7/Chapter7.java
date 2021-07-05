package uk.ac.soton.ecs.kk8g18.ch7;

import java.net.URL;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.processing.edges.SUSANEdgeDetector;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.xuggle.XuggleVideo;

public class Chapter7 {

	
	public static void main( String[] args ) throws Exception {
		
	
		
		Video<MBFImage> video;
		video = new XuggleVideo(new URL("http://static.openimaj.org/media/tutorial/keyboardcat.flv"));
		
//		VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);
//		for (MBFImage mbfImage : video) {
//		    DisplayUtilities.displayName(mbfImage.process(new CannyEdgeDetector()), "videoFrames");
//		}
//		
		VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);
		display.addVideoListener(new VideoDisplayListener<MBFImage>() {
			public void beforeUpdate(MBFImage frame) {
		        //frame.processInplace(new CannyEdgeDetector());
				
				//Uses a different processing opetarion(SUSANEdgeDetector) instead of canny edge detector
		        frame.processInplace(new SUSANEdgeDetector());
		    }

		    public void afterUpdate(VideoDisplay<MBFImage> display) {
		    }
		  });
		
	}
	
}
