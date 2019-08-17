package org.puremvc.java.multicore.demos.microservice.employeeadmin.department;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    @Test
    public void testIntegration() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:9083").usePlaintext().build();
        DepartmentServiceGrpc.DepartmentServiceBlockingStub departmentService = DepartmentServiceGrpc.newBlockingStub(channel);
        EmployeeDepartmentServiceGrpc.EmployeeDepartmentServiceBlockingStub employeeDepartmentService = EmployeeDepartmentServiceGrpc.newBlockingStub(channel);

        String employeeId = ObjectId.get().toString();

        // save the department
        Department departmenta = departmentService.save(Department.newBuilder().setName("Department").build());
        Department departmentb = departmentService.save(Department.newBuilder().setName("DepartmentB").build());
        assertEquals("Department", departmenta.getName());
        assertEquals("DepartmentB", departmentb.getName());

        // find the department by id
        Department department2 = departmentService.findById(Department.newBuilder()
                .setId(departmenta.getId()).build());
        assertEquals(departmenta.getId(), department2.getId());
        assertEquals(departmenta.getName(), department2.getName());

        // update the department
        Department department3 = departmentService.updateById(Department.newBuilder()
                .setId(departmenta.getId())
                .setName("DepartmentA").build());
        assertEquals(departmenta.getId(), department3.getId());
        assertEquals("DepartmentA", department3.getName());

        // assign department to an employee
        EmployeeDepartment department4 = employeeDepartmentService.save(EmployeeDepartment.newBuilder()
                .setId(employeeId)
                .setDepartment(Department.newBuilder().setId(departmenta.getId()).build()).build());
        assertNotNull(department4.getId());

        // retrieve department of an employee
        EmployeeDepartment department5 = employeeDepartmentService.findById(EmployeeDepartment.newBuilder()
                .setId(employeeId).build());
        assertEquals(department3.getName(), department5.getDepartment().getName());

        // update department of an employee
        EmployeeDepartment department6 = employeeDepartmentService.updateById(EmployeeDepartment.newBuilder()
                .setId(employeeId)
                .setDepartment(departmentb).build());

        // retrieve again department of an employee
        EmployeeDepartment department7 = employeeDepartmentService.findById(EmployeeDepartment.newBuilder().setId(employeeId).build());
        assertEquals(department6.getDepartment().getId(), department7.getDepartment().getId());

        // delete department of an employee by id
        employeeDepartmentService.deleteById(EmployeeDepartment.newBuilder()
                .setId(employeeId).build());

        // delete the department by id
        departmentService.deleteById(Department.newBuilder().setId(departmenta.getId()).build());
        departmentService.deleteById(Department.newBuilder().setId(departmentb.getId()).build());

        // findById again will throw an error
        try {
            departmentService.findById(Department.newBuilder()
                    .setId(departmenta.getId()).build());
            fail();
        } catch (Exception e) {}

        channel.shutdown();
    }

    @Test
    public void testByInvalidId() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8083").usePlaintext().build();
        DepartmentServiceGrpc.DepartmentServiceBlockingStub stub = DepartmentServiceGrpc.newBlockingStub(channel);

        try {
            stub.findById(Department.newBuilder().setId(ObjectId.get().toString()).build());
            fail();
        } catch (Exception e) { }

        try {
            stub.updateById(Department.newBuilder().setId(ObjectId.get().toString()).setName("temp").build());
            fail();
        } catch (Exception e) { }

        try {
            stub.deleteById(Department.newBuilder().setId(ObjectId.get().toString()).build());
            fail();
        } catch (Exception e) { }

        channel.shutdown();
    }

}