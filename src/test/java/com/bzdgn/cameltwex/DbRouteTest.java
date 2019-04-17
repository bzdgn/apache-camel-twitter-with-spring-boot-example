package com.bzdgn.cameltwex;

import org.apache.camel.LoggingLevel;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.TestPropertySource;

import com.bzdgn.cameltwex.model.TweetInfo;
import com.bzdgn.cameltwex.router.DbRoute;

/*
 * https://dzone.com/articles/unit-testing-with-apache-camel
 * 
 * https://github.com/apache/camel/blob/master/components/camel-sql/src/test/java/org/apache/camel/component/sql/SqlProducerNoopTest.java
 */
@TestPropertySource("classpath:test.properties")
public class DbRouteTest extends CamelTestSupport {

	private EmbeddedDatabase db;

	@Before
	public void setUp() throws Exception {
		db = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.DERBY)
				.addScript("sql/02_CREATE_TEST_TABLE.SQL").build();

		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();

		db.shutdown();
	}

	@Test
	public void testDbRoute() throws InterruptedException {
		String expected = "expected";

		MockEndpoint mock = getMockEndpoint("mock:endpoint");

		mock.expectedBodiesReceived(expected);
		
		TweetInfo input = new TweetInfo();
		input.setTweetId(3L);
		input.setText("myTweet");
		
		template.sendBody("direct:dbroute", input);
		assertMockEndpointsSatisfied();
	}
	
	@Override
    public RoutesBuilder createRouteBuilder() throws Exception {
        return new DbRoute() {
            @Override
            public void configure() throws Exception {
                // required for the sql component
                getContext().getComponent("sql", SqlComponent.class).setDataSource(db);

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
        		.to("mock:endpoint");
            }
        };
	}

}
