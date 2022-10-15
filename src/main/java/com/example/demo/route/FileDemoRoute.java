package com.example.demo.route;

import com.example.demo.model.Employee;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class FileDemoRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
       /* from("timer://helloTimer?repeatCount=5").routeId("timer-rt")
                .log("HELLO WORLD!");*/
        BindyCsvDataFormat dataFormat = new BindyCsvDataFormat(Employee.class);
        /*onException(HttpOperationFailedException.class)
                .handled(true)
                .log("Something happened try again?");*/

        from("file://../in?fileName=employee.csv&moveFailed=ERROR")
                .routeId("file-to-somewhere")
                .unmarshal(dataFormat)
                .split(body()).stopOnException()
                    .marshal().json(JsonLibrary.Jackson)
                    .log(LoggingLevel.ERROR, "${body}")
                    .to("http://localhost:3001/employee")
                .end()
        ;
    }
}
