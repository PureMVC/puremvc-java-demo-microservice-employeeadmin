//
//  ServiceProxy.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.service.model;

import com.google.protobuf.Empty;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.department.*;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.employee.Employee;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.employee.EmployeeList;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.employee.EmployeeServiceGrpc;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.role.*;
import org.puremvc.java.multicore.patterns.proxy.Proxy;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class ServiceProxy extends Proxy {

    public static String NAME = "ServiceProxy";

    EmployeeServiceGrpc.EmployeeServiceBlockingStub employee;
    DepartmentServiceGrpc.DepartmentServiceBlockingStub department;
    EmployeeDepartmentServiceGrpc.EmployeeDepartmentServiceBlockingStub employeeDepartment;
    RoleServiceGrpc.RoleServiceBlockingStub role;
    EmployeeRoleServiceGrpc.EmployeeRoleServiceBlockingStub employeeRole;

    public ServiceProxy(EmployeeServiceGrpc.EmployeeServiceBlockingStub employee,
                        RoleServiceGrpc.RoleServiceBlockingStub role,
                        EmployeeRoleServiceGrpc.EmployeeRoleServiceBlockingStub employeeRole,
                        DepartmentServiceGrpc.DepartmentServiceBlockingStub department,
                        EmployeeDepartmentServiceGrpc.EmployeeDepartmentServiceBlockingStub employeeDepartment) {
        super(NAME, null);
        this.employee = employee;
        this.role = role;
        this.employeeRole = employeeRole;
        this.department = department;
        this.employeeDepartment = employeeDepartment;
    }

    public EmployeeList findAllUser() {
        return employee.findAll(Empty.getDefaultInstance());
    }

    public Employee findEmployeeById(String id) {
        return employee.findById(Employee.newBuilder().setId(id).build());
    }

    public Employee saveEmployee(JsonObject body) {
        return employee.save(Employee.newBuilder().setUsername(body.getString("username"))
                .setFirst(body.getString("first")).setLast(body.getString("last"))
                .setEmail(body.getString("email")).build());
    }

    public Employee updateEmployeeById(JsonObject body) {
        return employee.updateById(Employee.newBuilder().setUsername(body.getString("username"))
                .setFirst(body.getString("first")).setLast(body.getString("last"))
                .setEmail(body.getString("email")).build());
    }

    public void deleteEmployeeById(String id) {
        employee.deleteById(Employee.newBuilder().setId(id).build());
    }

    public RoleList findAllRole() {
        return role.findAll(Empty.getDefaultInstance());
    }

    public Role saveRole(String name) {
        return role.save(Role.newBuilder().setName(name).build());
    }

    public EmployeeRole findEmployeeRoleById(String id) {
        return employeeRole.findById(EmployeeRole.newBuilder().setId(id).build());
    }

    public EmployeeRole saveEmployeeRoleById(String id, JsonArray roles) {
        RoleList.Builder roleList = RoleList.newBuilder();
        for (int i = 0; i < roles.size(); i++) {
            roleList.addRole(Role.newBuilder().setId(roles.getString(i)));
        }
        return employeeRole.save(EmployeeRole.newBuilder()
                .setId(id)
                .setRoleList(roleList).build());
    }

    public EmployeeRole updateEmployeeRoleById(String id, JsonArray roles) {
        RoleList.Builder roleList = RoleList.newBuilder();
        for (int i = 0; i < roles.size(); i++) {
            roleList.addRole(Role.newBuilder().setId(roles.getString(i)));
        }
        return employeeRole.updateById(EmployeeRole.newBuilder()
                .setId(id)
                .setRoleList(roleList).build());
    }

    public DepartmentList findAllDepartment() {
        return department.findAll(Empty.getDefaultInstance());
    }

    public Department saveDepartment(String name) {
        return department.save(Department.newBuilder().setName(name).build());
    }

    public EmployeeDepartment saveEmployeeDepartmentById(String userId, String departmentId) {
        return employeeDepartment.save(EmployeeDepartment.newBuilder()
                .setId(userId)
                .setDepartment(Department.newBuilder().setId(departmentId)).build());
    }

}