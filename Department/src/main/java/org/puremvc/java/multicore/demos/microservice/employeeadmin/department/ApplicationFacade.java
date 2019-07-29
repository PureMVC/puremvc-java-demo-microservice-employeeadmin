//
//  ApplicationFacade.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department;

import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.controller.StartupCommand;
import org.puremvc.java.patterns.facade.Facade;

import javax.servlet.Servlet;

public class ApplicationFacade extends Facade {

    private static final String STARTUP = "startup";
    public static final String SERVICE = "service";
    public static final String SERVICE_RESULT = "serviceResult";
    public static final String SERVICE_FAULT = "serviceFault";

    @Override
    protected void initializeController() {
        super.initializeController();
        registerCommand(STARTUP, () -> new StartupCommand());
    }

    public static ApplicationFacade getInstance() {
        return (ApplicationFacade) Facade.getInstance(() -> new ApplicationFacade());
    }

    public void startup(Servlet servlet) {
        sendNotification(ApplicationFacade.STARTUP, servlet);
    }

}