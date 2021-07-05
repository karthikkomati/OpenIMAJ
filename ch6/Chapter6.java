package uk.ac.soton.ecs.kk8g18.ch6;

import java.util.Map.Entry;
import java.util.Random;

import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

public class Chapter6 {

	
public static void main( String[] args ) throws Exception {
		
	
		

		
				
		VFSGroupDataset<FImage> groupedFaces = new VFSGroupDataset<FImage>( "zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
	
		//Displays random image of each person in the dataset
		for (final Entry<String, VFSListDataset<FImage>> entry : groupedFaces.entrySet()) {
			//DisplayUtilities.display(entry.getKey(), entry.getValue());
			
			
			Random rand = new Random();
			int a = rand.nextInt(entry.getValue().size());
			DisplayUtilities.display(entry.getValue().get(a));
			
		}
		
//		FlickrAPIToken flickrToken = DefaultTokenFactory.get(FlickrAPIToken.class);
//		FlickrImageDataset<FImage> cats = 
//		FlickrImageDataset.create(ImageUtilities.FIMAGE_READER, flickrToken, "cat", 10);
//		DisplayUtilities.display("Cats", cats);
		

	}
	
	
	
}
