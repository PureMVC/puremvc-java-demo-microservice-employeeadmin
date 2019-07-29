package org.puremvc.java.multicore.demos.microservice.employeeadmin.user.view.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IService {
    void service(HttpServletRequest request, HttpServletResponse response);
}
