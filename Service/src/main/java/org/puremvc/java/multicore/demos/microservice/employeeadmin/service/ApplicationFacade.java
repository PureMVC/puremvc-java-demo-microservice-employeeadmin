//
//  ApplicationFacade.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.service;

import org.puremvc.java.multicore.demos.microservice.employeeadmin.service.controller.StartupCommand;
import org.puremvc.java.multicore.patterns.facade.Facade;

import javax.servlet.Servlet;

public class ApplicationFacade extends Facade {

    private static final String STARTUP = "startup";
    public static final String SERVICE = "service";
    public static final String SERVICE_RESULT = "serviceResult";
    public static final String SERVICE_FAULT = "serviceFault";

    public ApplicationFacade(String key) {
        super(key);
    }

    @Override
    protected void initializeController() {
        super.initializeController();
        registerCommand(STARTUP, () -> new StartupCommand());
    }

    public static ApplicationFacade getInstance() {
        return (ApplicationFacade) Facade.getInstance("Service", () -> new ApplicationFacade("Service"));
    }

    public void startup(Servlet servlet) {
        sendNotification(ApplicationFacade.STARTUP, servlet);
    }

}