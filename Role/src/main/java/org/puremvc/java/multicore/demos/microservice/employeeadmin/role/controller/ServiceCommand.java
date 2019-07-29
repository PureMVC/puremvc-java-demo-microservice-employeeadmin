//
//  ServiceCommand.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.role.controller;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.ApplicationFacade;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.model.ServiceProxy;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.model.request.ServiceRequest;
import org.puremvc.java.patterns.command.SimpleCommand;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAllowedException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceCommand extends SimpleCommand {

    @Override
    public void execute(INotification notification) {
        ServiceRequest serviceRequest = (ServiceRequest) notification.getBody();
        ServiceProxy serviceProxy = (ServiceProxy) facade.retrieveProxy(ServiceProxy.NAME);
        HttpServletRequest request = serviceRequest.getRequest();

        try {
            switch (request.getPathInfo()) {

                case "/roles":
                    if(request.getMethod().equals("GET")) {
                        Object result = serviceProxy.findAll();
                        serviceRequest.setResultData(200, result);
                    } else {
                        throw new NotAllowedException("HTTP 405 Method Not Allowed");
                    }
                    break;

                default:
                    Matcher matcher = Pattern.compile("/users/(.*)/roles").matcher(request.getPathInfo()); // users/:id/roles
                    if (matcher.find()) {
                        if (request.getMethod().equals("GET")) {
                            Object result = serviceProxy.getById(matcher.group(1));
                            serviceRequest.setResultData(200, result);
                        } else if(request.getMethod().equals("PUT")) {
                            Object result = serviceProxy.updateById(matcher.group(1), serviceRequest.getBody());
                            serviceRequest.setResultData(200, result);
                        } else {
                            throw new NotAllowedException("HTTP 405 Method Not Allowed");
                        }
                    } else {
                        throw new NotAllowedException("HTTP 405 Method Not Allowed");
                    }
            }
        } catch (Exception exception) {
            serviceRequest.setResultData(500, exception);
            sendNotification(ApplicationFacade.SERVICE_FAULT, serviceRequest);
            return;
        }

        sendNotification(ApplicationFacade.SERVICE_RESULT, serviceRequest);
    }

}