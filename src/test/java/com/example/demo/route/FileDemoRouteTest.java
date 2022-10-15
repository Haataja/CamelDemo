package com.example.demo.route;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CamelSpringBootTest
@UseAdviceWith
public class FileDemoRouteTest {

    @Autowired
    private ProducerTemplate template;

    @Autowired
    private CamelContext camelContext;

    @Test
    void testSuccessful() throws Exception {
        // make little changes to the route, so you can test with mocks
        AdviceWith.adviceWith(camelContext, "file-to-somewhere", r -> {
            r.replaceFromWith("direct:in"); // replace file component, for easier testing
            r.interceptSendToEndpoint("http://localhost:3001/employee").skipSendToOriginalEndpoint().to("mock:http");
        });
        camelContext.start(); // start camel context manually, because of the use of advice with

        // define what you expect to happen
        MockEndpoint httpMock = camelContext.getEndpoint("mock://http", MockEndpoint.class);
        httpMock.expectedMessageCount(2);
        httpMock.expectedBodiesReceived("{\"id\":1,\"firstname\":\"Rogerio\",\"lastname\":\"Deporte\",\"email\":\"rdeporte0@economist.com\",\"gender\":\"Male\",\"ipAddress\":\"235.236.144.148\"}",
                "{\"id\":2,\"firstname\":\"Amy\",\"lastname\":\"Rickford\",\"email\":\"arickford1@mayoclinic.com\",\"gender\":\"Female\",\"ipAddress\":\"141.163.55.251\"}" );

        // send something to the route
        template.sendBody("direct:in", """
                id,first_name,last_name,email,gender,ip_address
                1,Rogerio,Deporte,rdeporte0@economist.com,Male,235.236.144.148
                2,Amy,Rickford,arickford1@mayoclinic.com,Female,141.163.55.251""");

        // check for success
        httpMock.assertIsSatisfied();
    }




}
