package com.rahmed.redhat.demo.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.stereotype.Component;

import com.rahmed.redhat.demo.rest.Order;

@Component
public class OrderREST extends RouteBuilder{

	private String ds = "?dataSource=dataSource";
	private String selectAll = "sql:select * from orders" + ds;
	private String selectCount = "sql:select count(*) from orders" + ds;
	private String selectById = "sql:select * from orders where " +
								"id = :#${header.id}&" +
								"outputClass=com.rramalho.rest.Order" + ds;
	private String insertOrder = "sql:insert into orders (id,item, amount, description, processed) values " +
    	                		"(:#${body.id},:#${body.item}, :#${body.amount}, :#${body.description}, false)"+ ds;
	private String updateOrder = "sql:update orders set "
						+ "item = :#${body.item}, "
						+ "amount = :#${body.amount}, "
						+ "description = :#${body.description}, "
						+ "processed = :#${body.processed} " +
						"where id = :#${body.id}" + ds;		  
	private String deleteOrder = "sql:delete from orders where id = :#${header.id}" + ds;              

	
    @Override
    public void configure() {       
    	onException(java.sql.SQLIntegrityConstraintViolationException.class)
	    	.handled(true)
	    	.setBody(simple("Order id already registered"));
    	
    	rest("/orders").description("Orders service")

	    	.get("/").description("List orders")
				.route().routeId("orders-list")
				.to(selectAll)
				.endRest()
			
			.get("order/{id}").outType(Order.class).description("Details of an order by id")
				.route().routeId("order-api")
				.to(selectById)
				.endRest()

    		.post("/").type(Order.class).description("Create a new Order")
    		
    			.route().routeId("insert-order").tracing()
    			.log("Order Id is ${body.id}")
    			.setHeader("id",simple("${body.id}"))
    			.log("Order Id is ${header.id} whole body is ${body}")
    			.idempotentConsumer(header("id"),
                        MemoryIdempotentRepository.memoryIdempotentRepository(200)).skipDuplicate(true)
//    			.to("bean:orderService?method=createOrder")
				// .setHeader("CamelSqlRetrieveGeneratedKeys", constant(true)) // For some reason it doesn't work
				// if it works the sql to retrieve the count will be not necessary
    			.log("inserting new order ${body}")			
    			.to(insertOrder)
				// this lines are necessary because the CamelSqlRetrieveGeneratedKeys was not set.
				.to(selectCount)							
				.to("sql:select * from orders where id=:#${header.id}" +ds)
    			.endRest()
    			
			.put("/").type(Order.class).description("Update a order")
	    		.route().routeId("update-order")
				.log("Updating order ${body}")
				.to(updateOrder).endRest()
    			
    		.delete("/{id}").description("Delete a order")
	    		.route().routeId("delete-order")
				.log("deleting order ${header.id}")
				.to(deleteOrder)
				.endRest();
	}
}
