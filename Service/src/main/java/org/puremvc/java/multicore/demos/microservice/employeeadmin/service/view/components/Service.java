//
//  Service.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.service.view.components;

import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.Department;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.DepartmentList;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.employee.Employee;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.employee.EmployeeList;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.EmployeeRole;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.Role;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.RoleList;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.service.view.interfaces.IService;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.service.ApplicationFacade;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Service extends HttpServlet {

    public Service() {
        ApplicationFacade.getInstance().startup(this); // puremvc
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");

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

    public void result(HttpServletRequest request, HttpServletResponse response, int status, Object result) {
        response.setHeader("content-type", "application/json;charset=UTF-8");
        JsonGenerator jsonGenerator;

        try {
            jsonGenerator = Json.createGenerator(response.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        switch (request.getPathInfo()) {
            case "/employees":
                if(request.getMethod().equals("GET")) {
                    response.setStatus(status);
                    List<Employee> employees = ((EmployeeList) result).getEmployeeList();
                    jsonGenerator.writeStartArray();
                    employees.forEach(employee -> {
                        jsonGenerator.writeStartObject();
                        jsonGenerator.write("id", employee.getId());
                        jsonGenerator.write("username", employee.getEmail());
                        jsonGenerator.write("first", employee.getFirst());
                        jsonGenerator.write("last", employee.getLast());
                        jsonGenerator.write("email", employee.getEmail());
                        jsonGenerator.writeEnd();
                    });
                    jsonGenerator.writeEnd();
                    jsonGenerator.close();
                } else if(request.getMethod().equals("POST")) {
                    response.setStatus(status);
                    response.setHeader("Location", "/users/" + ((Employee) result).getId());
                }
                break;

            case "/roles":
                response.setStatus(status);
                List<Role> roles = ((RoleList) result).getRoleList();
                jsonGenerator.writeStartArray();
                roles.forEach(role -> {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.write("id", role.getId());
                    jsonGenerator.write("name", role.getName());
                    jsonGenerator.writeEnd();
                });
                jsonGenerator.writeEnd();
                jsonGenerator.close();
                break;

            case "/departments":
                response.setStatus(status);
                List<Department> departments = ((DepartmentList) result).getDepartmentList();
                jsonGenerator.writeStartArray();
                departments.forEach(department -> {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.write("id", department.getId());
                    jsonGenerator.write("name", department.getName());
                    jsonGenerator.writeEnd();
                });
                jsonGenerator.writeEnd();
                jsonGenerator.close();
                break;

            default:

                Matcher matcher = Pattern.compile("/employees/(.*)/roles").matcher(request.getPathInfo()); // users/:id/roles
                if (matcher.find()) {
                    if (request.getMethod().equals("GET")) {
                        response.setStatus(status);
                        roles = ((EmployeeRole) result).getRoleList().getRoleList();
                        jsonGenerator.writeStartArray();
                        roles.forEach(role -> {
                            jsonGenerator.writeStartObject();
                            jsonGenerator.write("id", role.getId());
                            jsonGenerator.write("name", role.getName());
                            jsonGenerator.writeEnd();
                        });
                        jsonGenerator.writeEnd();
                        jsonGenerator.close();
                    } else if(request.getMethod().equals("PUT")) {
                        response.setStatus(status);
                    }
                    return;
                }

                matcher = Pattern.compile("/employees/(.*)").matcher(request.getPathInfo()); // users/:id
                if (matcher.find()) {
                    if (request.getMethod().equals("GET")) {
                        response.setStatus(status);
                        Employee employee = (Employee) result;
                        jsonGenerator.writeStartObject();
                        jsonGenerator.write("id", employee.getId());
                        jsonGenerator.write("username", employee.getEmail());
                        jsonGenerator.write("first", employee.getFirst());
                        jsonGenerator.write("last", employee.getLast());
                        jsonGenerator.write("email", employee.getEmail());
                        jsonGenerator.writeEnd();
                        jsonGenerator.close();
                    } else if(request.getMethod().equals("PUT")) {
                        response.setStatus(status);
                    } else if (request.getMethod().equals("DELETE")) {
                        response.setStatus(status);
                    }
                    return;
                }
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

}