//
//  DepartmentServiceTest.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department;

import com.google.protobuf.Empty;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class DepartmentServiceTest {

    @Mock
    MongoClient mockMongoClient;

    @Mock
    private MongoDatabase mockMongoDatabase;

    @Mock
    MongoCollection mockMongoCollection;

    @Mock
    FindIterable<Document> mockFindIterable;

    @Mock
    MongoCursor<Document> mockMongoCursor;

    @Mock
    Document mockDocument;

    @Mock
    UpdateResult mockUpdateResult;

    @Mock
    DeleteResult mockDeleteResult;

    String id = "5d4dc8635dd32dbcba4ae0ae";
    String name = "Department A";

    public DepartmentServiceTest() throws IOException {
        MockitoAnnotations.initMocks(this);

        when(mockMongoClient.getDatabase(anyString())).thenReturn(mockMongoDatabase);
        when(mockMongoDatabase.getCollection(anyString())).thenReturn(mockMongoCollection);

        when(mockMongoCollection.find()).thenReturn(mockFindIterable);
        when(mockMongoCollection.find(any(Bson.class))).thenReturn(mockFindIterable);

        when(mockFindIterable.iterator()).thenReturn(mockMongoCursor);
        when(mockFindIterable.first()).thenReturn(mockDocument);
        when(mockFindIterable.projection(any(Bson.class))).thenReturn(mockFindIterable);

        when(mockDocument.getObjectId("_id")).thenReturn(new ObjectId(id));
        when(mockDocument.getString("name")).thenReturn(name);

        when(mockMongoCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockUpdateResult);
        when(mockUpdateResult.getMatchedCount()).thenReturn((long) 1);

        when(mockMongoCollection.deleteOne(any(Bson.class))).thenReturn(mockDeleteResult);
        when(mockDeleteResult.getDeletedCount()).thenReturn((long)1);
    }

    @Test
    public void testMocks() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8083).usePlaintext().build();
        DepartmentServiceGrpc.DepartmentServiceBlockingStub departmentService = DepartmentServiceGrpc.newBlockingStub(channel);
        EmployeeDepartmentServiceGrpc.EmployeeDepartmentServiceBlockingStub employeeDepartmentService = EmployeeDepartmentServiceGrpc.newBlockingStub(channel);

        String employeeId = ObjectId.get().toString();

        // save
        Department department = departmentService.save(Department.newBuilder()
                .setName(name).build());

        assertEquals(name, department.getName());

        // findById
        Department findByIdResult = departmentService.findById(Department.newBuilder().setId(department.getId()).build());
        assertEquals(department.getId(), findByIdResult.getId());
        assertEquals(name, findByIdResult.getName());

        // save employee_department
        EmployeeDepartment saveResult = employeeDepartmentService.save(EmployeeDepartment.newBuilder()
                .setId(employeeId)
                .setDepartment(Department.newBuilder().setId(department.getId())).build());

        assertEquals(employeeId, saveResult.getId());
        assertEquals(department.getId(), saveResult.getDepartment().getId());

        // find all
        departmentService.findAll(Empty.getDefaultInstance());

        // delete employee_department
        employeeDepartmentService.deleteById(EmployeeDepartment.newBuilder()
                .setId(saveResult.getId()).build());

        // delete department
        departmentService.deleteById(Department.newBuilder()
                .setId(department.getId()).build());

        channel.shutdown();
    }
}