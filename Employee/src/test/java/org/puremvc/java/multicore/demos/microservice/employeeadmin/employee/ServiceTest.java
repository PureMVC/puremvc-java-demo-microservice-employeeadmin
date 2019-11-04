//
//  ServiceTest.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.employee;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    @Test
    public void testIntegration() {
        // create channel
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8081").usePlaintext().build();
        EmployeeServiceGrpc.EmployeeServiceBlockingStub stub = EmployeeServiceGrpc.newBlockingStub(channel);

        // insert employee
        Employee result = stub.save(Employee.newBuilder()
                .setUsername("username")
                .setFirst("first")
                .setLast("last")
                .setEmail("email@mail.com").build());

        // find by id
        Employee result2 = stub.findById(Employee.newBuilder().setId(result.getId()).build());
        assertEquals(result.getId(), result2.getId());
        assertEquals(result.getUsername(), result2.getUsername());
        assertEquals(result.getFirst(), result2.getFirst());
        assertEquals(result.getLast(), result2.getLast());
        assertEquals(result.getEmail(), result2.getEmail());

        // update employee
        Employee result3 = stub.updateById(Employee
                .newBuilder()
                .setId(result.getId())
                .setUsername("username1")
                .setFirst("first1")
                .setLast("last1")
                .setEmail("email1@mail.com").build());

        // find employee by id again
        Employee result4 = stub.findById(Employee.newBuilder().setId(result.getId()).build());
        assertNotNull(result);
        assertEquals(result4.getId(), result3.getId());
        assertEquals(result4.getUsername(), result3.getUsername());
        assertEquals(result4.getFirst(), result3.getFirst());
        assertEquals(result4.getLast(), result3.getLast());
        assertEquals(result4.getEmail(), result3.getEmail());

        // find all
        stub.findAll(Empty.newBuilder().build());

        // delete employee
        Employee request = Employee.newBuilder().setId(result.getId()).build();
        stub.deleteById(request);

        // find employee after deletion
        try {
            stub.findById(Employee.newBuilder().setId(result.getId()).build());
            fail();
        } catch (Exception exception) {}

    }

    @Test
    public void testByInvalidId() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8081").usePlaintext().build();
        EmployeeServiceGrpc.EmployeeServiceBlockingStub stub = EmployeeServiceGrpc.newBlockingStub(channel);

        String id = "5d505412f5cb0669053e034a";
        try {
            stub.findById(Employee.newBuilder()
                    .setId(id).build());
            fail();
        } catch (Exception exception) { }

        try {
            Employee employee = Employee.newBuilder().setId(id).setUsername("temp").build();
            stub.updateById(employee);
            fail();
        } catch (Exception exception) { }

        try {
            Employee request = Employee.newBuilder().setId(id).build();
            stub.deleteById(request);
            fail();
        } catch(Exception e) { }
    }

}