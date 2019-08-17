//
//  StartupCommand.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.service.controller;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.Department;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.DepartmentServiceGrpc;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.EmployeeDepartmentServiceGrpc;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.employee.Employee;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.employee.EmployeeServiceGrpc;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.EmployeeRoleServiceGrpc;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.Role;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.RoleServiceGrpc;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.service.ApplicationFacade;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.service.model.ServiceProxy;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.service.view.ServiceMediator;
import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;

import javax.json.Json;
import javax.servlet.Servlet;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class StartupCommand extends SimpleCommand {

    @Override
    public void execute(INotification notification) {
        Map<String, String> env = new HashMap<String, String>() {
            {
                put("EMPLOYEE_HOST", System.getenv("EMPLOYEE_HOST"));
                put("ROLE_HOST", System.getenv("ROLE_HOST"));
                put("DEPARTMENT_HOST", System.getenv("DEPARTMENT_HOST"));
            }
        };

        env.forEach((k, v) -> {
            System.out.println(k + ": " + v);
            if(v == null) { throw new RuntimeException("Please set the " + k + " in env variables and try again"); }
        });

        System.out.println("before");

        ManagedChannel employeeChannel = ManagedChannelBuilder.forTarget(env.get("EMPLOYEE_HOST")).usePlaintext().build();
        EmployeeServiceGrpc.EmployeeServiceBlockingStub employee = EmployeeServiceGrpc.newBlockingStub(employeeChannel);

        ManagedChannel roleChannel = ManagedChannelBuilder.forTarget(env.get("ROLE_HOST")).usePlaintext().build();
        RoleServiceGrpc.RoleServiceBlockingStub role = RoleServiceGrpc.newBlockingStub(roleChannel);
        EmployeeRoleServiceGrpc.EmployeeRoleServiceBlockingStub employeeRole = EmployeeRoleServiceGrpc.newBlockingStub(roleChannel);

        ManagedChannel departmentChannel = ManagedChannelBuilder.forTarget(env.get("DEPARTMENT_HOST")).usePlaintext().build();
        DepartmentServiceGrpc.DepartmentServiceBlockingStub department = DepartmentServiceGrpc.newBlockingStub(departmentChannel);
        EmployeeDepartmentServiceGrpc.EmployeeDepartmentServiceBlockingStub employeeDepartment = EmployeeDepartmentServiceGrpc.newBlockingStub(departmentChannel);
//
        ServiceProxy serviceProxy = new ServiceProxy(employee, role, employeeRole, department, employeeDepartment);

        System.out.println("in");

//        if (serviceProxy.findAllDepartment().getDepartmentCount() == 0) { // initialize database
            Department accounting = serviceProxy.saveDepartment("Accounting");
//            Department sales = serviceProxy.saveDepartment("Sales");
//            Department plant = serviceProxy.saveDepartment("Plant");
//            Department shipping = serviceProxy.saveDepartment("Shipping");
//            Department qualityControl = serviceProxy.saveDepartment("Quality Control");

//            Role administrator = serviceProxy.saveRole("Administrator");
//            Role accountsPayable = serviceProxy.saveRole("Accounts Payable");
//            Role accountsReceivable = serviceProxy.saveRole("Accounts Receivable");
//            Role employeeBenefits = serviceProxy.saveRole("Employee Benefits");
//            Role generalLedger = serviceProxy.saveRole("General Ledger");
//            Role payroll = serviceProxy.saveRole("Payroll");
//            Role inventory = serviceProxy.saveRole("Inventory");
//            Role production = serviceProxy.saveRole("Production");
//            Role qualityControlRole = serviceProxy.saveRole("Quality Control");
//            Role salesRole = serviceProxy.saveRole("Sales");
//            Role orders = serviceProxy.saveRole("Orders");
//            Role customers = serviceProxy.saveRole("Customers");
//            Role shippingRole = serviceProxy.saveRole("Shipping");
//            Role returns = serviceProxy.saveRole("Returns");

//            String larryJSON = "{\"username\": \"lstooge\", \"first\": \"Larry\", \"last\": \"Stooge\", \"email\": \"larry@stooges.com\"}";
//            String curlyJSON = "{\"username\": \"cstooge\", \"first\": \"Curly\", \"last\": \"Stooge\", \"email\": \"curly@stooges.com\"}";
//            String moeJSON = "{\"username\": \"mstooge\", \"first\": \"Moe\", \"last\": \"Stooge\", \"email\": \"moe@stooges.com\"}";
//
//            Employee larry = serviceProxy.saveEmployee(Json.createReader(new StringReader(larryJSON)).readObject());
//            Employee curly = serviceProxy.saveEmployee(Json.createReader(new StringReader(curlyJSON)).readObject());
//            Employee moe = serviceProxy.saveEmployee(Json.createReader(new StringReader(moeJSON)).readObject());
//
//            serviceProxy.saveEmployeeDepartmentById(larry.getId(), accounting.getId());
//            serviceProxy.saveEmployeeDepartmentById(curly.getId(), sales.getId());
//            serviceProxy.saveEmployeeDepartmentById(moe.getId(), plant.getId());
//
//            larryJSON = "[\"" + payroll.getId() + "\",\"" + employeeBenefits.getId() + "\"]";
//            curlyJSON = "[\"" + accountsPayable.getId() + "\",\"" + accountsReceivable.getId() + "\",\"" + generalLedger.getId() + "\"]";
//            moeJSON = "[\"" + inventory.getId() + "\",\"" + production.getId() + "\",\"" + salesRole.getId() + "\",\"" + shippingRole.getId() + "\"]";
//
//            serviceProxy.saveEmployeeRoleById(larry.getId(), Json.createReader(new StringReader(larryJSON)).readArray());
//            serviceProxy.saveEmployeeRoleById(curly.getId(), Json.createReader(new StringReader(curlyJSON)).readArray());
//            serviceProxy.saveEmployeeRoleById(moe.getId(), Json.createReader(new StringReader(moeJSON)).readArray());
//        }

//        this.getFacade().registerCommand(ApplicationFacade.SERVICE, () -> new ServiceCommand());
//        this.getFacade().registerProxy(serviceProxy);
//        this.getFacade().registerMediator(new ServiceMediator((Servlet) notification.getBody()));

        System.out.println("startup");
    }
}