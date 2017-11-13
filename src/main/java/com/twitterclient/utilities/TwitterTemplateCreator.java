package com.twitterclient.utilities;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class TwitterTemplateCreator {
    @Autowired
    private Environment env;
    private static final Logger LOG = LoggerFactory.getLogger(TwitterTemplateCreator.class);

    @Value("${spring.social.twitter.app-id}")
    private String consumerKey;

    @Value("${spring.social.twitter.app-secret}")
    private String consumerSecret;

    @Value("${spring.social.twitter.access-token}")
    private String accessToken;

    @Value("${spring.social.twitter.access-token-secret}")
    private String accessTokenSecret;

    @Bean
    public TwitterTemplate twitterTemplate() {
        LOG.info("init TwitterTemplateConfiguration{" +
                "consumerKey='" + consumerKey + '\'' +
                ", consumerSecret='" + consumerSecret + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", accessTokenSecret='" + accessTokenSecret + '\'' +
                '}');
        return new TwitterTemplate(consumerKey,
                consumerSecret, accessToken, accessTokenSecret);
    }

    public Twitter getTwitterTemplate() {
        String consumerKey = env.getProperty("spring.social.twitter.app-id");
        String consumerSecret = env.getProperty("spring.social.twitter.app-secret");
        String accessToken = env.getProperty("spring.social.twitter.access-token");
        String accessTokenSecret = env.getProperty("spring.social.twitter.access-token-secret");
        Preconditions.checkNotNull(consumerKey);
        Preconditions.checkNotNull(consumerSecret);
        Preconditions.checkNotNull(accessToken);
        Preconditions.checkNotNull(accessTokenSecret);

        TwitterTemplate twitterTemplate =
                new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        return twitterTemplate;
    }
}
