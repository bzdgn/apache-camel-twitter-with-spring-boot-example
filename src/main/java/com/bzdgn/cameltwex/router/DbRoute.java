package com.bzdgn.cameltwex.router;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DbRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		from("direct:dbroute")
		.log(LoggingLevel.INFO, "DbRoute: Received at DbRoute")
		.to("sql: " +
				"INSERT INTO TWEET_INFO " +
				"(TWEET_ID, TEXT, USERNAME, LANGUAGE, LOCATION, FAVOURITE_COUNT, CREATION_DATE) " +
				"VALUES " +
				"( :#${body.tweetId}, :#${body.text}, :#${body.username}, :#${body.language}, " +
				" :#${body.location}, :#${body.favouriteCount}, :#${body.creationDate} )"
		)
		.log(LoggingLevel.INFO, "DbRoute: Persisted to DB")
		.to("log:DbRoute");
	}

}
