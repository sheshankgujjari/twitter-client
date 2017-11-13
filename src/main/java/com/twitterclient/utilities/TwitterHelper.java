package com.twitterclient.utilities;

import java.util.List;

import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;

public class TwitterHelper {

	public static Twitter getTwitterAccess()
	{
		TwitterTemplateCreator twitterTemplateCreator = new TwitterTemplateCreator();
		Twitter twitter = twitterTemplateCreator.getTwitterTemplate();
		return twitter;
	}

	 public static List<Tweet> getHashTagTweets(Twitter twitter, String hashTag)
	 {
		return twitter.searchOperations().search(hashTag, 20).getTweets();
	 }
	    
	 public static List<Tweet> getMyTimeLine(Twitter twitter, String name)
	    {
	        return twitter.timelineOperations().getUserTimeline(name);

	    }
	 
	    public static TwitterProfile getUserProfile(Twitter twitter)
	    {
	        TwitterProfile profile = twitter.userOperations().getUserProfile();
	        return profile;

	    }

	    public static String getScreenName(Twitter twitter)
	    {
	        String profileId = twitter.userOperations().getScreenName();
	        return profileId;
	    }

	    public static void updateStatus(Twitter twitter, String status)
	    {
	        twitter.timelineOperations().updateStatus("Spring Social is awesome!");
	    }

	    public static List<Tweet> getHomeTimeLine(Twitter twitter)
	    {
	        List<Tweet> tweets = twitter.timelineOperations().getHomeTimeline();
	        return tweets;
	    }

	    public static CursoredList<TwitterProfile> getFriends(Twitter twitter)
	    {
	        CursoredList<TwitterProfile> friends = twitter.friendOperations().getFriends();
	        return friends;
	    }
}
