package com.bzdgn.cameltwex.router;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.twitter.search.TwitterSearchComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bzdgn.cameltwex.processor.TweetInfoProcessor;

@Component
public class TwitterFeedRoute extends RouteBuilder {
	
    @Value("${twitter.consumerKey}")
    private String consumerKey;
    
    @Value("${twitter.consumerSecret}")
    private String consumerSecret;
    
    @Value("${twitter.accessToken}")
    private String accessToken;
    
    @Value("${twitter.accessTokenSecret}")
    private String accessTokenSecret;
    
    @Value("${app.interval}")
	protected int interval;
    
    @Value("${app.search_term}")
	protected String searchTerm;
    
    /*
     * Neither in Construction nor in PostConstruct this can be handled.
     * TwitterSearchComponent in the context must be prepared before fromF used.
     * 
     */
    protected void prepareContextTwitterSearchComponent() {
		TwitterSearchComponent twitterSearchComponent = getContext().getComponent("twitter-search", TwitterSearchComponent.class);
		
		twitterSearchComponent.setAccessToken(accessToken);
		twitterSearchComponent.setAccessTokenSecret(accessTokenSecret);
		twitterSearchComponent.setConsumerKey(consumerKey);
		twitterSearchComponent.setConsumerSecret(consumerSecret);
    }
    
	@Override
	public void configure() throws Exception {
		prepareContextTwitterSearchComponent();
		
        fromF("twitter-search://%s?delay=%s", searchTerm, interval)
        .log(LoggingLevel.INFO, "TwitterFeedRoute: Received Status.")
        .to("log:TwitterFeedRoute")
		.process(new TweetInfoProcessor())		// EIP: Message Translator
        .log(LoggingLevel.INFO, "TwitterFeedRoute: Status transformed into TweetEntity.")
        .to("direct:dbroute");					// EIP: Message Channel
	}

}