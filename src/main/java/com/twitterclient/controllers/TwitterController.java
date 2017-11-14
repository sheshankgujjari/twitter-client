package com.twitterclient.controllers;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.social.ExpiredAuthorizationException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.*;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.twitterclient.utilities.TwitterHelper;

import javax.inject.Inject;

@RestController
@RequestMapping(TwitterController.TWITTER_BASE_URI)
public class TwitterController {
    public static final String TWITTER_BASE_URI = "/tweets";

    private static final long WORLDWIDE_WOE = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(TwitterController.class);


    @Autowired
    public Twitter twitter;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Inject
    public TwitterController(TwitterTemplate twitter, ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
        this.twitter = twitter;
        LOG.info("initialized twitter : " + twitter);
    }

    @RequestMapping(value = "/getFollowersCount/{username}")
    public int getFollowersCount(@PathVariable(name = "username") String username) {
        return twitter.friendOperations().getFollowers(username).size();
    }

    @RequestMapping(value = "/gethashtags/{hashTag}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Tweet> getTweets(@PathVariable final String hashTag)
    {
        return TwitterHelper.getHashTagTweets(twitter, hashTag);
    }
    
    @RequestMapping(value = "/getmytimeline/{name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Tweet> getMyTimeLine(@PathVariable final String name)
    {
        return TwitterHelper.getMyTimeLine(twitter, name);
    }
    
    @RequestMapping(value = "/getuserprofile", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public TwitterProfile getUserProfile()
    {
        return TwitterHelper.getUserProfile(twitter);
    }
    
    @RequestMapping(value = "/getscreenname",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getScreenName()
    {
        return TwitterHelper.getScreenName(twitter);
    }
    
    @RequestMapping(value = "/getfriends", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CursoredList<TwitterProfile> getFriends()
    {
        CursoredList<TwitterProfile> friends = TwitterHelper.getFriends(twitter);

        return friends;
    }
    
    @RequestMapping(value = "/gethometimeline", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Tweet> getHomeTimeLine()
    {
        return TwitterHelper.getHomeTimeLine(twitter);
    }
    
    @RequestMapping(value = "/uploadtimeline", method = RequestMethod.POST ,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void uploadTimeLine()
    {
        TwitterHelper.updateStatus(twitter, "Test");
    }

    @RequestMapping(value="/timeline", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Tweet> showTimeline() {
        return showTimeline("Home");
    }

    @RequestMapping(value="/timeline/{timelineType}", method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Tweet> showTimeline(@PathVariable("timelineType") String timelineType) {
        if (timelineType.equals("Home")) {
            return twitter.timelineOperations().getHomeTimeline();
        } else if(timelineType.equals("User")) {
            return twitter.timelineOperations().getUserTimeline();
        } else if(timelineType.equals("Mentions")) {
            return twitter.timelineOperations().getMentions();
        } else if(timelineType.equals("Favorites")) {
            return twitter.timelineOperations().getFavorites();
        }
        return null;
    }


    @RequestMapping(value="/tweet", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Tweet postTweet(String message) {
        return twitter.timelineOperations().updateStatus(message);
    }

    @RequestMapping(value="/search", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Tweet> showTrends(@RequestParam("query") String query) {
        return twitter.searchOperations().search(query).getTweets();
    }

    @RequestMapping(value="/trends", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Trends showTrends() {
        return twitter.searchOperations().getLocalTrends(WORLDWIDE_WOE);
    }

    @RequestMapping(value="/friends", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CursoredList<TwitterProfile> friends() {
       return twitter.friendOperations().getFriends();
    }

    @RequestMapping(value="/followers", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CursoredList<TwitterProfile> followers(Model model) {
        return twitter.friendOperations().getFollowers();
    }

    @RequestMapping("/twitter/revoked")
    public void simulateExpiredToken() {
        throw new ExpiredAuthorizationException("twitter");
    }
}