//
//  ServiceMediator.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department.view;

import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.ApplicationFacade;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.model.request.ServiceRequest;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.view.components.Service;
import org.puremvc.java.patterns.mediator.Mediator;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceMediator extends Mediator implements Service.IService {

    public static String NAME = "ServiceMediator";

    private Service service;

    public ServiceMediator(Servlet service) {
        super(NAME, service);
    }

    @Override
    public void onRegister() {
        service = ((Service)this.getViewComponent());
        service.setDelegate(this);
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        sendNotification(ApplicationFacade.SERVICE, new ServiceRequest(request, response));
    }

    @Override
    public String[] listNotificationInterests() {
        return new String[]{
                ApplicationFacade.SERVICE_RESULT,
                ApplicationFacade.SERVICE_FAULT
        };
    }

    @Override
    public void handleNotification(INotification notification) {
        ServiceRequest request = (ServiceRequest) notification.getBody();
        switch (notification.getName()) {
            case ApplicationFacade.SERVICE_RESULT:
                service.result(request.getRequest(), request.getResponse(), request.getStatus(), request.getResultData());
                break;
            case ApplicationFacade.SERVICE_FAULT:
                service.fault(request.getRequest(), request.getResponse(), request.getStatus(), (Exception)request.getResultData());
                break;
        }
    }

}