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
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.twitterclient.utilities.TwitterHelper;

import javax.inject.Inject;

@RestController
@RequestMapping(TwitterController.TWITTER_BASE_URI)
public class TwitterController {
    public static final String TWITTER_BASE_URI = "/svc/v1/tweets";

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
        if(connectionRepository.findPrimaryConnection(Twitter.class) == null) {
            LOG.error("No connection to Twitter available");
            return -1;
        }
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
    
    @RequestMapping(value = "/getScreenName",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getScreenName()
    {
        return TwitterHelper.getScreenName(twitter);
    }
    
    @RequestMapping(value = "/getFriends", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getFriends(Model model)
    {
        CursoredList<TwitterProfile> friends = TwitterHelper.getFriends(twitter);
        model.addAttribute("friends", friends);
        return "hello";
    }
    
    @RequestMapping(value = "/getHomeTimeLine", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Tweet> getHomeTimeLine()
    {
        return TwitterHelper.getHomeTimeLine(twitter);
    }
    
    @RequestMapping(value = "/uploadTimeLine", method = RequestMethod.POST)
    @ResponseBody
    public void uploadTimeLine()
    {
        TwitterHelper.updateStatus(twitter, "Test");
    }

    @RequestMapping(value="/twitter/timeline", method=RequestMethod.GET)
    public String showTimeline(Model model) {
        return showTimeline("Home", model);
    }

    @RequestMapping(value="/twitter/timeline/{timelineType}", method=RequestMethod.GET)
    public String showTimeline(@PathVariable("timelineType") String timelineType, Model model) {
        if (timelineType.equals("Home")) {
            model.addAttribute("timeline", twitter.timelineOperations().getHomeTimeline());
        } else if(timelineType.equals("User")) {
            model.addAttribute("timeline", twitter.timelineOperations().getUserTimeline());
        } else if(timelineType.equals("Mentions")) {
            model.addAttribute("timeline", twitter.timelineOperations().getMentions());
        } else if(timelineType.equals("Favorites")) {
            model.addAttribute("timeline", twitter.timelineOperations().getFavorites());
        }
        model.addAttribute("timelineName", timelineType);
        return "twitter/timeline";
    }


    @RequestMapping(value="/twitter/tweet", method=RequestMethod.POST)
    public String postTweet(String message) {
        twitter.timelineOperations().updateStatus(message);
        return "redirect:/twitter";
    }

    @RequestMapping(value="/twitter/search", method=RequestMethod.GET)
    public String showTrends(@RequestParam("query") String query, Model model) {
        model.addAttribute("timeline", twitter.searchOperations().search(query).getTweets());
        return "twitter/timeline";
    }

    @RequestMapping(value="/twitter/trends", method=RequestMethod.GET)
    public String showTrends(Model model) {
        model.addAttribute("trends", twitter.searchOperations().getLocalTrends(WORLDWIDE_WOE));
        return "twitter/trends";
    }

    @RequestMapping(value="/twitter/friends", method=RequestMethod.GET)
    public String friends(Model model) {
        model.addAttribute("profiles", twitter.friendOperations().getFriends());
        return "twitter/friends";
    }

    @RequestMapping(value="/twitter/followers", method=RequestMethod.GET)
    public String followers(Model model) {
        model.addAttribute("profiles", twitter.friendOperations().getFollowers());
        return "twitter/friends";
    }

    @RequestMapping(value="/twitter", method=RequestMethod.GET)
    public String home(Principal currentUser, Model model) {
        Connection<Twitter> connection = connectionRepository.findPrimaryConnection(Twitter.class);
        if (connection == null) {
            return "redirect:/connect/twitter";
        }
        model.addAttribute("profile", connection.getApi().userOperations().getUserProfile());
        return "twitter/profile";
    }

    @RequestMapping("/twitter/revoked")
    public void simulateExpiredToken() {
        throw new ExpiredAuthorizationException("twitter");
    }
}