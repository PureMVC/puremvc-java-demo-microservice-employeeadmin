//
//  ServiceTest.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.role;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    @Test
    public void testIntegration() {
        // create channel
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8082").usePlaintext().build();
        RoleServiceGrpc.RoleServiceBlockingStub roleService = RoleServiceGrpc.newBlockingStub(channel);
        EmployeeRoleServiceGrpc.EmployeeRoleServiceBlockingStub employeeRoleService = EmployeeRoleServiceGrpc.newBlockingStub(channel);

        // generate roles and employee id
        String employee_id = ObjectId.get().toString();

        Role role1 = roleService.save(Role.newBuilder().setName("testRole").build());
        assertEquals("testRole", role1.getName());
        Role role2 = roleService.save(Role.newBuilder().setName("testRole2").build());
        assertEquals("testRole2", role2.getName());
        Role role3 = roleService.save(Role.newBuilder().setName("testRole3").build());
        assertEquals("testRole3", role3.getName());

        // get a role by id
        Role roleServiceById = roleService.findById(Role.newBuilder().setId(role1.getId()).build());
        assertEquals(role1.getId(), roleServiceById.getId());

        // update role
        Role updateResult = roleService.updateById(Role.newBuilder().setId(role1.getId()).setName("testRole1").build());
        assertEquals("testRole1", updateResult.getName());

        // insert roles of an employee
        EmployeeRole employeeRole = employeeRoleService.save(EmployeeRole.newBuilder()
                .setId(employee_id)
                .setRoleList(RoleList.newBuilder()
                        .addRole(Role.newBuilder().setId(role1.getId()).build())
                        .addRole(Role.newBuilder().setId(role2.getId()).build())).build());
        assertNotNull(employeeRole);

        // get roles of an employee
        EmployeeRole employeeRole1 = employeeRoleService.findById(EmployeeRole.newBuilder()
                .setId(employee_id).build());

        List<Role> roleList = employeeRole1.getRoleList().getRoleList();
        assertEquals(2, roleList.size());
        assertEquals(role1.getId(), roleList.get(0).getId());
        assertEquals(role2.getId(), roleList.get(1).getId());

        // update roles of an employee
        EmployeeRole employeeRole2 = EmployeeRole.newBuilder()
                .setId(employee_id)
                .setRoleList(RoleList.newBuilder()
                        .addRole(Role.newBuilder().setId(role3.getId()).build())).build();
        employeeRoleService.updateById(employeeRole2);

        // get updated roles of an employee
        EmployeeRole employeeRole3 = employeeRoleService.findById(EmployeeRole.newBuilder()
                .setId(employee_id).build());

        List<Role> roleList1 = employeeRole3.getRoleList().getRoleList();
        assertEquals(1, roleList1.size());
        assertEquals(role3.getId(), roleList1.get(0).getId());

        // find all
        roleService.findAll(Empty.newBuilder().build());

        // delete roles of an employee
        employeeRoleService.deleteById(EmployeeRole.newBuilder()
                .setId(employee_id).build());

        // delete role
        roleService.deleteById(Role.newBuilder().setId(role1.getId()).build());
        roleService.deleteById(Role.newBuilder().setId(role2.getId()).build());
        roleService.deleteById(Role.newBuilder().setId(role3.getId()).build());

        channel.shutdown();
    }

    @Test
    public void testFindByInvalidId() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8082").usePlaintext().build();
        RoleServiceGrpc.RoleServiceBlockingStub roleService = RoleServiceGrpc.newBlockingStub(channel);

        try {
            roleService.findById(Role.newBuilder().setId(ObjectId.get().toString()).build());
            fail();
        } catch (Exception e) { }

        try {
            roleService.updateById(Role.newBuilder().setId(ObjectId.get().toString()).setName("temp").build());
            fail();
        } catch (Exception e) { }

        try {
            roleService.deleteById(Role.newBuilder().setId(ObjectId.get().toString()).build());
            fail();
        } catch (Exception e) { }

        channel.shutdown();
    }
}