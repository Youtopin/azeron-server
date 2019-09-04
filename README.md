# Azeron-Server
The scalable and reliable event messaging library, Wraps nats.io and uses Java Spring framework.

---

Azeron is simple event messaging library that wraps [nats.io](https://nats.io/) java connector and handles event messaging for you. Currently azeron is only available as Spring module.

**The advantage** of using Azeron client-server instead of nats is that Azeron handles message recovery for you. Nats is a `FIRE AND FORGET` system. But Azeron does its best to not to forget any message ever! Although nats already supports `streaming` or can turn itself to a `fire and know` system ([look here](https://nats-io.github.io/docs/developer/concepts/acks.html)) but it still forgets after a timeout or when client disconnects from nats server. Meanwhile, with nats streaming, we are loosing latency for message delivery.

Azeron Server can act as a big (possibly clustered) listener to all the channels clients are subscribing to using nats (Azeron Client). It persists all the messages so acknowledges are performed between Azeron server and client (not client to client). Clients can later query un-acknowledge messages from Azeron Server (which happens automatically in azeron environment).
Also Azeron can keep listening to events for a service while they are down and provide them their un-acknowledge messages when they are up and running.


The thoughts behind Azeron is to provide simple abstraction layer to anyone who wants to use it.
For example, the repository layer of Azeron server is abstract. You can implement it with any database you want. Or you can implement clustered nodes for high-availability. There are different examples of Azeron available [here](https://github.com/pinect-io/azeron-examples).

### Is azeron a good solution for me?

If following list matches your needs, then you might want to consider using Azeron.

- Using a lightweight and configurable messaging system
- A very fast message delivery when all instances are up
- Your messages are only text based, and mostly not too large
- Ability to handle message persistence on any database you prefer (but also bing able to choose between different persisting strategies (in next releases))
- You don't care about chance of loosing some of your messages (fixable through the configuration you choose, also it will be fixed in next releases)


While message delivery in azeron is fast (check nats [benchmarks](https://bravenewgeek.com/dissecting-message-queues/)) by using nats, It might have drawbacks such as having higher load of messages on nats, as it is used for connection persistence and message recovery too. To be more clear, Azeron uses nats connection in order to handle things such as PING, ACKNOWLEDGES and RECOVERY.

## Installation

There is no builds available at maven centrals at the moment. The simplest strategy is to add azeron to your local maven repository.

Clone from source

	git clone https://github.com/sepehr-gh/azeron-server.git

build with maven

	mvn clean install

use azeron in your maven POM dependencies

	<dependency>
		<groupId>io.pinect</groupId>
		<artifactId>azeron-server</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>


## Usage

Annotate your Spring Boot Application with `@EnableAzeronServer`

	@EnableAzeronServer
	@Configuration
	public class AzeronConfiguration {
	}

Implement your own message repository to override default one (does not persist anything anywhere)

	@Service("messageRepository")
	public class MyAzeronServerMessageRepository implements MessageRepository {
		...
	}

MessageRepository class is from package `io.pinect.azeron.server.domain.repository`

You are good to go.

## Configuration

You can use these configurations in your *application.properties* or *application.yml* file.

Interval of azeron servers channel sync

	azeron.server.channel-sync-interval-seconds=20

Inverval of azeron servers information sync

	azeron.server.info-sync-interval-seconds=20

Nats idle timeout

	azeron.server.nats.idle-time-out=60000

default queue name for azeron server instances

	azeron.server.queue-name=azeron-server

nats hosts (array) to connect to

	azeron.server.nats.hosts=nats://localhost:4222

**NOTE**: Azeron does not automatically install NATS on your system or embedd it.

## API

Azeron server also provides some HTTP API's that helps clients and developers to use service. Based on your needs you might want to force authentication on these addresses too.

#### Server information

Provides information on all discovered azeron server instances, number of registered channels and nats configurations they use.

Address: **/api/v1/info**

Example response:

	{
	  "results": [
		{
		  "serverUUID": "a751f023-f92e-4a86-8c00-51a3a5385cb0",
		  "nats": {
			"hosts": [
			  "nats://localhost:4222"
			],
			"useEpoll": false,
			"idleTimeOut": 60000,
			"pedanic": false,
			"reconnectWaitSeconds": 0
		  },
		  "channelsCount": 0
		}
	  ]
	}

#### Nats information

Provides information on current server instance nats configuration.

Address **/api/v1/nats**

Example response:

	{
	  "hosts": [
		"nats://localhost:4222"
	  ],
	  "useEpoll": false,
	  "idleTimeOut": 60000,
	  "pedanic": false,
	  "reconnectWaitSeconds": 0
	}


#### PING

Ping request to check if server is up and running.

Address: **/api/v1/ping**
Query Parameters:

`[String] serviceName`: serviceName of service that is calling ping to see if server has discovered this particular server.

Example reponse:

	{
	  "status": "OK",
	  "discovered": false,
	  "askedForDiscovery": false
	}

if `serviceName` parameter is provided, `askedForDiscovery` will be true in reponse. Then, `discover` determines if azeron server has discovered the client. This is then used in client in re-registeration process.

#### SEEN

Provides seen (acknowledgement) service over HTTP.

Address: **/api/v1/seen (PUT)**
Parameters:

`[String] messageId`: acknowledged message id.
`[List<String>] messageIds`: list of acknowledged message ids.
`[String] serviceName`: service name that has acknowledged message.
`[String] reqId`: request id to get back in response

Example reponse:

	{
	  "status": "OK",
	  "reqId": ********
	}


## EXTRA

Here are some extra things you can add to your Azeron Server configuration for better performance or more usability.

### Message Repository Decorator

There is a `MessageRepositoryDecorator` in `io.pinect.azeron.server.decorator` to help you decorate your message repositories.

#### MapCacheMessageRepositoryDecorator

This decorator, adds a caching layer around your repository. This cache can be simply a `Java Concurrent Hash Map` in single node environment.
This decorator accepts size of cache for maximum number of messages held in the map. Also the priority of removing messages from cache is with older messages. Specially those that are older than certain amount of seconds you provide in constructor.

This behaviour can not be used when you use `@Service("messageRepository")` on your repository.

Example:

    @Configuration
    public class AzeronConfiguration {
        private final MessageRepository myAzeronServerMessageRepository; //main message repository that uses DB and Disk
        private MapCacheMessageRepositoryDecorator mapCacheMessageRepositoryDecorator;
    
        @Autowired
        public AzeronConfiguration(MessageRepository myAzeronServerMessageRepository) {
            this.myAzeronServerMessageRepository = myAzeronServerMessageRepository;
        }
    
        @Bean
        public MessageRepository messageRepository(){
            mapCacheMessageRepositoryDecorator = new MapCacheMessageRepositoryDecorator(myAzeronServerMessageRepository, new ConcurrentHashMap<>(), 1000, 20);
            return mapCacheMessageRepositoryDecorator;
        }
    
        // Flushes cache into main repository
        @PreDestroy
        public void destroy(){
            mapCacheMessageRepositoryDecorator.flush();
        }
    
    }
