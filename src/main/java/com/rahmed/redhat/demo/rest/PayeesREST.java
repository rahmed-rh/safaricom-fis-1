package com.rahmed.redhat.demo.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.processor.idempotent.InfinispanIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayeesREST extends RouteBuilder{
	
	
	private InfinispanIdempotentRepository infinispanRepository;
	
	
	@Autowired
	public void setInfinispanRepository(InfinispanIdempotentRepository infinispanRepository) {
		this.infinispanRepository = infinispanRepository;
	}
	
    @Override
    public void configure() {       
    	

    	rest("/payees").description("Payees service")

	    	.get("/").description("List Payees")
				.route().routeId("payees-list")
				.bean(PayeesService.class,"listPayees")
				.endRest()
				
			.get("payee/{id}").outType(Payee.class).description("Details of a Payee by id")
				.route().routeId("payee-by-id")
				.tracing()
//				.to("log:org.apache.camel.component.infinispan?level=INFO&showAll=true&multiline=true")
//				.setHeader(InfinispanConstants.OPERATION, constant(InfinispanConstants.GET))
//    	        .setHeader(InfinispanConstants.KEY, simple("payee_id_${header.id}"))
//    	        .to("infinispan:payees?cacheContainer=#remoteCacheContainer")
				.bean(PayeesService.class,"findPayeeById(${header.id})")
    	        .log("Received body: ${body}")
				.endRest()

    		.post("/").type(Payee.class).description("Create a new Payee")
    			.route().routeId("insert-payee")
    			.tracing()
    			.log("Order Id is ${body.id}")
    			.setHeader("id",simple("${body.id}"))
    			.log("Order Id is ${header.id} whole body is ${body}")
    			/*.idempotentConsumer(header("id"),
    					infinispanRepository).skipDuplicate(true)*/
    			.log("inserting new payee ${body}")
//    			.setHeader(InfinispanConstants.OPERATION, constant(InfinispanConstants.PUT))
//    			.setHeader(InfinispanConstants.KEY, simple("payee_id_${header.id}"))
//    	        .setHeader(InfinispanConstants.VALUE,constant("DummyValue"))
    			 .bean(PayeesService.class,"createPayee")
//    	        .to("log:org.apache.camel.component.infinispan?level=INFO&showAll=true&multiline=true")
//    	        .to("infinispan:payees?cacheContainer=#remoteCacheContainer")
    			.transform(constant("OK"))
    			.endRest();
    	
    	
    	
      
    			
	}
}
