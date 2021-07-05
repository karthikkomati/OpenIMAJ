package uk.ac.soton.ecs.kk8g18.ch5;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.BasicMatcher;
import org.openimaj.feature.local.matcher.BasicTwoWayMatcher;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.MatchingUtilities;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.math.geometry.transforms.HomographyModel;
import org.openimaj.math.geometry.transforms.HomographyRefinement;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.geometry.transforms.estimation.RobustHomographyEstimator;
import org.openimaj.math.model.fit.RANSAC;

public class Chapter5 {

	public static void main(String[] args) throws MalformedURLException, IOException {
		
		
		MBFImage query = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/query.jpg"));
		MBFImage target = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/target.jpg"));
		 
		DoGSIFTEngine engine = new DoGSIFTEngine();	
		LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
		LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());
		
		//LocalFeatureMatcher<Keypoint> matcher = new BasicMatcher<Keypoint>(80);
		
		//Uses a two way matcher
		LocalFeatureMatcher<Keypoint> matcher = new BasicTwoWayMatcher<Keypoint>();
		
		//Uses RobustHomographyEstimator
		RobustHomographyEstimator rh = new RobustHomographyEstimator(50.0, 1500, new RANSAC.PercentageInliersStoppingCondition(0.5),HomographyRefinement.NONE);
		
		matcher.setModelFeatures(queryKeypoints);
		matcher.findMatches(targetKeypoints);
		
		MBFImage basicMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
		DisplayUtilities.display(basicMatches);
		
		
		//RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(50.0, 1500, new RANSAC.PercentageInliersStoppingCondition(0.5));
		
		matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(new FastBasicKeypointMatcher<Keypoint>(8), rh);
		
		//matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(new FastBasicKeypointMatcher<Keypoint>(8), modelFitter);
		
		matcher.setModelFeatures(queryKeypoints);
		matcher.findMatches(targetKeypoints);

		MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(),RGBColour.RED);

		DisplayUtilities.display(consistentMatches);
				

	}

}
