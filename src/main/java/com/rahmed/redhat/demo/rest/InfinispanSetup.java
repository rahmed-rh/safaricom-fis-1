package com.rahmed.redhat.demo.rest;

import java.io.IOException;

import org.apache.camel.component.infinispan.processor.idempotent.InfinispanIdempotentRepository;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "infinispan")
public class InfinispanSetup {
	final static Logger LOG = LoggerFactory.getLogger(InfinispanSetup.class);
	/**
	 * The host name of the Infinispan service.
	 */
	private String host;

	/**
	 * The port of the Infinispan service.
	 */
	private Integer port;

	/**
	 * The host name of the Infinispan service.
	 */
	private String username;

	/**
	 * The host name of the Infinispan service.
	 */
	private String password;

	/**
	 * The name of the Infinispan cache.
	 */
	private String cacheName ;


	@Bean(initMethod = "start", destroyMethod = "stop")
	public BasicCacheContainer remoteCacheContainer() {

		String hostPort = host + ":" + port;
		LOG.info("Connecting to the Infinispan service at {}", hostPort);


		RemoteCacheManager manager = new RemoteCacheManager(
											new ConfigurationBuilder()
											.addServers(hostPort)
											.forceReturnValues(true)
											.marshaller(new ProtoStreamMarshaller())
											.maxRetries(10)
											.security()
									        .authentication()
									            .enable()
									            .serverName("jdg-server")
									            .saslMechanism("DIGEST-MD5")
									            .callbackHandler(new LoginHandler(username, password.toCharArray(), "ApplicationRealm"))
												.build(), 
											true);
		registerSchemasAndMarshallers(manager);
		return manager;
	}

	@Bean
	public InfinispanIdempotentRepository infinispanRepository(BasicCacheContainer cacheContainer) {
		return InfinispanIdempotentRepository.infinispanIdempotentRepository(cacheContainer, cacheName);
	}
	
	private void registerSchemasAndMarshallers(RemoteCacheManager cacheManager) {
        try {


			String file = " package uaex;\n" 
					+ "message Payee {\n" 
					+ "    required int32 id = 1;\n"
					+ "    required string name = 2;\n" 
					+ "    required string bankName = 3;\n"
					+ "    required string accountNumber = 4;\n" + "}\n";

			// Register entity marshaller on the client side ProtoStreamMarshaller instance
			// associated with the remote cache manager.
			SerializationContext ctx = ProtoStreamMarshaller.getSerializationContext(cacheManager);

			ctx.registerProtoFiles(FileDescriptorSource.fromString("Payee.proto", file));
			ctx.registerMarshaller(new PayeeMarshaller());
            

        	/*ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
        	String payeeSchemaFile = protoSchemaBuilder
        	    .fileName("Payee.proto")
        	    .packageName("uaex")
        	    .addClass(Payee.class)
        	    .build(serCtx);

        	// the types can be marshalled now
        	LOG.info("Can marshal Incident.class now = "+serCtx.canMarshall(Payee.class) );
//        	assertTrue(serCtx.canMarshall(Incident.class));
//        	assertTrue(serCtx.canMarshall(Note.class));

        	// display the schema file
        	LOG.info(payeeSchemaFile);

            // register the schemas with the server too
            RemoteCache<String, String> metadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
            //metadataCache.put(PROTOBUF_DEFINITION_RESOURCE, readResource(PROTOBUF_DEFINITION_RESOURCE));
            metadataCache.put("payee.proto", payeeSchemaFile);
            String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
            if (errors != null) {
               throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
            }        	
      */
        } catch (IOException ioe) {
        	LOG.info("Could not register marshallers! Remote querying is not going to work properly.");
        	LOG.info(ioe.getMessage());
        }
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

}
