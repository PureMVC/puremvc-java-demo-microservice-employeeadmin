//
//  ServiceRequest.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.service.model.request;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.json.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServiceRequest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Object requestData;
    private Object resultData;
    private int status;

    public ServiceRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public String getBody() throws IOException {
        if(requestData == null) {
            StringBuilder body = new StringBuilder();
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                char[] buffer = new char[1024];
                int bytes;
                while ((bytes = bufferedReader.read(buffer)) > 0) {
                    body.append(buffer, 0, bytes);
                }
            }
            requestData = body;
        }
        return requestData.toString();
    }

    public JsonObject getJsonObject() throws JsonException, IOException {
        if(requestData == null) {
            requestData = Json.createReader(request.getInputStream()).readObject();
        }
        return (JsonObject)requestData;
    }

    public JsonArray getJsonArray() throws JsonException, IOException {
        if(requestData == null) {
            requestData = Json.createReader(request.getInputStream()).readArray();
        }
        return (JsonArray)requestData;
    }

    public Document getXml() throws ParserConfigurationException, IOException, SAXException {
        if(requestData == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            requestData = builder.parse(request.getInputStream());
        }
        return (Document)requestData;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Object getResultData() {
        return resultData;
    }

    public void setResultData(int status, Object resultData) {
        this.status = status;
        this.resultData = resultData;
    }

    public int getStatus() {
        return status;
    }

}