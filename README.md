Apache Camel Twitter Example With Spring Boot
=============================================
The main purpose of this project is to demonstrate Apache Camel and it's Twitter component.
Apache Camel is used within the Spring Boot context. 

TOC
---
- [1 Description Of The Project](#1-description-of-the-project) <br/>
- [2 Enterprise Integration Patterns](#2-enterprise-integration-patterns) <br/>
- [3 Project Setup](#3-project-setup) <br/>
- [4 Twitter Tokens](#4-twitter-tokens) <br/>
- [5 Notes, Future Improvements, TODOs](#5-notes-future-improvements-todos) <br/>


 1 Description Of The Project
-----------------------------

This simple stand-alone application checks the result of twitter-search for the configured
search-term regularly with a constant period which is also configurable, then converts the
Twitter Status object into a custom Twitter Info object and persist it into the PostgreSQL
database.

<p align="center">
	<img src="https://github.com/bzdgn/apache-camel-twitter-with-spring-boot-example/blob/master/screenshots/00_diagram.PNG" alt="diagram">
</p>

As can be seen in the diagram, the main input of the application is retrieved from Twitter.
Apache Camel's Twitter Component is used to retrieve the input. The input data format is
in the Status class of the Twitter4j component. Then, this class is transformed into the
custom TweetInfo class and lastly, the TweetInfo class object instances are persisted into
the PostgreSQL database.

The flow is simple, every N seconds, the output of the tweeter-search is retrieved. This N
seconds and the search-term is configurable via application.properties file. After each 
cycle, the tweets are persisted to the TWEET_INFO table in the database.

After the start of the program, the retrieved tweets are going to be logged as follows;

<p align="center">
	<img src="https://github.com/bzdgn/apache-camel-twitter-with-spring-boot-example/blob/master/screenshots/01_eclipse_capture.PNG" alt="eclipse_capture">
</p>

And also, database is going to be populated as can be seen as follows;

<p align="center">
	<img src="https://github.com/bzdgn/apache-camel-twitter-with-spring-boot-example/blob/master/screenshots/02_db_capture.PNG" alt="db_capture">
</p>

[Go back to TOC](#toc)


 2 Enterprise Integration Patterns
----------------------------------
Apache Camel is actually an implementation of Enterprise Integration Patterns. There are a
few patterns used in this demo project;

- [Message Channel](http://camel.apache.org/message-channel.html)
- [Message Translator](http://camel.apache.org/message-translator.html)

There are two Route classes in the router package which are both Message Channels.
Between the channels, the data type should be transformed from the twitter4j.Status
into the TweetInfo class structure. To make that, Message Translator is used by using
the Processor interface. TweetInfo processor is as simple example of Message Translator.

[Go back to TOC](#toc)


 3 Project Setup
----------------
Before starting the fat-jar, there must be an existing postgresql database. It's better
to use a PostgreSQL docker container for the simplicity;

For a windows solution, first create a volume, in this case

```
docker volume create pg-docker-volume
```

Then use this volume with the postgresql container;

```
docker run --name pg-docker -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 -v pg-docker-volume:/var/lib/postgresql/data  postgres
```

The second step is to run the pg-admin (or you can use the command-line), and create a test
database. For this project, as it is written in the application.properties file, the name
of the database is **test_db**. So you need to create a database with this name.

Third step is to create the **TWEET_INFO** table as below;

```
CREATE TABLE TWEET_INFO (
    ID SERIAL           PRIMARY KEY,
    TWEET_ID            BIGINT,
    TEXT                VARCHAR(280) NOT NULL,
    USERNAME            VARCHAR(50),
    LANGUAGE            VARCHAR(50),
    LOCATION            VARCHAR(50),
    FAVOURITE_COUNT     INT,
    CREATION_DATE       TIMESTAMP NOT NULL
);
```

Now you can install with

`mvn clean install`

and then make the fat-jar with;

`mvn package`

then run the fat jar as below;

```
java -jar twitter-monitor.jar
```

[Go back to TOC](#toc)


 4 Twitter Tokens
-----------------
In the application.properties file, you should populate the twitter related entries,
which are;

- twitter.consumerKey
- twitter.consumerSecret
- twitter.accessToken
- twitter.accessTokenSecret

In order to receive these, you need to create a twitter developer account, create
an twitter application and receive your tokens as can be seen below;

<p align="center">
	<img src="https://github.com/bzdgn/apache-camel-twitter-with-spring-boot-example/blob/master/screenshots/03_twitter_tokens.PNG" alt="twitter_tokens_screen">
</p>


[Go back to TOC](#toc)


 5 Notes, Future Improvements, TODOs
------------------------------------
One hard thing to get used to a new framework is to know and get used to how to mock the
dependencies of the new framework. In this case, in the context of Apache Camel, one thing
I have found hard is to mock the Camel components like Twitter Component or SQL Component.

Best practices of testing these components is a #1 todo to add to this project.

A second things is, a different type of Message Translator can be used. So one to-do
investigation is to see if it is possible to transform the message body directly into
a bean properly. Then the TweetInfoProcessor can be updated or changed.

A third thing is, which way is better to configure the routes. Having one simple class or
having multiple classes as a Spring Component. Which one is easier to maintain, which is
better to read and test?

A fourth thing is, the whole thing should be dockerized. I have already written a docker
file but that's not enough. The better solution is to create a docker-compose file. I have
also put a compose file here but it's failing right now. The application container cannot
communicate with the database container, this should be fixed.


[Go back to TOC](#toc)
