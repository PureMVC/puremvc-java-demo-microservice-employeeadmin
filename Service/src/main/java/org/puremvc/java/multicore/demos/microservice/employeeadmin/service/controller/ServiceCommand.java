//
//  ServiceCommand.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.service.controller;

import org.puremvc.java.multicore.demos.microservice.employeeadmin.service.ApplicationFacade;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.service.model.ServiceProxy;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.service.model.request.ServiceRequest;
import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAllowedException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceCommand extends SimpleCommand {

    @Override
    public void execute(INotification notification) {
        ServiceRequest serviceRequest = (ServiceRequest) notification.getBody();
        ServiceProxy serviceProxy = (ServiceProxy) getFacade().retrieveProxy(ServiceProxy.NAME);
        HttpServletRequest request = serviceRequest.getRequest();

        try {
            switch (request.getPathInfo()) {
                case "/employees":
                    if(request.getMethod().equals("GET")) {
                        Object result = serviceProxy.findAllUser();
                        serviceRequest.setResultData(200, result);
                    } else if(request.getMethod().equals("POST")) {
                        Object result = serviceProxy.saveEmployee(serviceRequest.getJsonObject());
                        serviceRequest.setResultData(201, result);
                    } else {
                        throw new NotAllowedException("HTTP 405 Method Not Allowed");
                    }
                    break;

                case "/roles":
                    if(request.getMethod().equals("GET")) {
                        Object result = serviceProxy.findAllRole();
                        serviceRequest.setResultData(200, result);
                    } else {
                        throw new NotAllowedException("HTTP 405 Method Not Allowed");
                    }
                    break;

                case "/departments":
                    if(request.getMethod().equals("GET")) {
                        Object result = serviceProxy.findAllDepartment();
                        serviceRequest.setResultData(200, result);
                    } else {
                        throw new NotAllowedException("HTTP 405 Method Not Allowed");
                    }
                    break;

                default:
                    Matcher matcher = Pattern.compile("/employees/(.*)/roles").matcher(request.getPathInfo()); // employees/:id/roles
                    if (matcher.find()) {
                        if (request.getMethod().equals("GET")) {
                            Object result = serviceProxy.findEmployeeRoleById(matcher.group(1));
                            serviceRequest.setResultData(200, result);
                        } else if(request.getMethod().equals("PUT")) {
                            Object result = serviceProxy.updateEmployeeRoleById(matcher.group(1), serviceRequest.getJsonArray());
                            serviceRequest.setResultData(200, result);
                        } else {
                            throw new NotAllowedException("HTTP 405 Method Not Allowed");
                        }
                        sendNotification(ApplicationFacade.SERVICE_RESULT, serviceRequest);
                        return;
                    }

                    matcher = Pattern.compile("/employees/((.*))").matcher(request.getPathInfo()); // employees/guid
                    if (matcher.find()) {
                        if (request.getMethod().equals("GET")) {
                            Object result = serviceProxy.findEmployeeById(matcher.group(1));
                            serviceRequest.setResultData(200, result);
                        } else if(request.getMethod().equals("PUT")) {
                            Object result = serviceProxy.updateEmployeeById(matcher.group(1), serviceRequest.getJsonObject());
                            serviceRequest.setResultData(200, result);
                        } else if (request.getMethod().equals("DELETE")) {
                            serviceProxy.deleteEmployeeById(matcher.group(1));
                            serviceRequest.setResultData(204, null);
                        } else {
                            throw new NotAllowedException("HTTP 405 Method Not Allowed");
                        }
                        sendNotification(ApplicationFacade.SERVICE_RESULT, serviceRequest);
                        return;
                    }

                    throw new NotAllowedException("HTTP 405 Method Not Allowed");
            }
        } catch (Exception exception) {
            serviceRequest.setResultData(500, exception);
            sendNotification(ApplicationFacade.SERVICE_FAULT, serviceRequest);
            return;
        }

        sendNotification(ApplicationFacade.SERVICE_RESULT, serviceRequest);
    }

}