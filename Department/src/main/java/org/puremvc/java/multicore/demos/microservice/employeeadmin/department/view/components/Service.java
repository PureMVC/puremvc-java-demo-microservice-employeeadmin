//
//  Service.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department.view.components;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.ApplicationFacade;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.PrintWriter;

public class Service extends HttpServlet {

    public Service() {
        ApplicationFacade.getInstance().startup(this); // start the puremvc apparatus
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        if(delegate != null) {
            if(request.getPathInfo() != null) {
                delegate.service(request, response);
            } else {
                response.setStatus(200);
            }
        } else {
            this.fault(request, response, 500, new Exception("Microservice failed to start."));
        }
    }

    public void result(HttpServletRequest request, HttpServletResponse response, int status, Object data) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");

        response.setHeader("content-type", "application/json;charset=UTF-8");
        JsonWriterSettings writerSettings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();

        PrintWriter responseWriter;
        try {
            responseWriter = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        switch (request.getPathInfo()) {
            case "/departments":
                response.setStatus(status);
                try (MongoCursor<Document> cursor = (MongoCursor)data) {
                    responseWriter.write("[");
                    while (cursor.hasNext()) {
                        responseWriter.write(cursor.next().toJson(writerSettings) + (cursor.hasNext() ? "," : ""));
                    }
                    responseWriter.write("]");
                }
                break;
        }
    }

    public void fault(HttpServletRequest request, HttpServletResponse response, int status, Exception exception) {
        response.setHeader("content-type", "application/json;charset=UTF-8");
        JsonGenerator jsonGenerator;

        try {
            jsonGenerator = Json.createGenerator(response.getWriter());
        } catch(Exception ex) {
            ex.printStackTrace();
            return;
        }

        int code = exception instanceof WebApplicationException ? ((WebApplicationException)exception).getResponse().getStatus() : status;
        String message = exception instanceof WebApplicationException ? exception.getMessage() : exception.toString();

        response.setStatus(code);
        jsonGenerator.writeStartObject();
        jsonGenerator.write("code", code);
        jsonGenerator.write("message", message);
        jsonGenerator.writeEnd();
        jsonGenerator.close();
    }

    public void setDelegate(IService delegate) {
        this.delegate = delegate;
    }

    private IService delegate;

    public interface IService {
        void service(HttpServletRequest request, HttpServletResponse response);
    }

}