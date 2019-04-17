package com.bzdgn.cameltwex;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * https://dzone.com/articles/unit-testing-with-apache-camel
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
public class TwitterFeedRouteTest extends CamelTestSupport  {

}
