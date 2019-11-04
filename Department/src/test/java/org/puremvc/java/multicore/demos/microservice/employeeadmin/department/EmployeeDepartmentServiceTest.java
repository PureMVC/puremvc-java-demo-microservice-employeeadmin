//
//  EmployeeDepartmentServiceTest.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class EmployeeDepartmentServiceTest {

    @Mock
    MongoClient mockClient;

    @Mock
    private MongoDatabase mockDatabase;

    @Mock
    MongoCollection mockCollection;

    @Mock
    FindIterable<Document> mockFindIterable;

    @Mock
    MongoCursor<Document> mockMongoCursor;

    @Mock
    AggregateIterable mockAggregateIterable;

    @Mock
    List<Document> mockListDocument;

    @Mock
    Document mockDocument;

    @Mock
    Document mockDocument2;

    @Mock
    UpdateResult mockUpdateResult;

    @Mock
    DeleteResult mockDeleteResult;

    @Rule
    public GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    EmployeeDepartmentServiceGrpc.EmployeeDepartmentServiceBlockingStub stub;

    String employeeId = ObjectId.get().toString();
    Department departmenta = Department.newBuilder().setId(ObjectId.get().toString()).setName("DepartmentA").build();
    Department departmentb = Department.newBuilder().setId(ObjectId.get().toString()).setName("DepartmentB").build();

    public EmployeeDepartmentServiceTest() throws IOException {
        MockitoAnnotations.initMocks(this);

        when(mockClient.getDatabase(anyString())).thenReturn(mockDatabase);
        when(mockDatabase.getCollection(anyString())).thenReturn(mockCollection);

        when(mockCollection.find()).thenReturn(mockFindIterable);
        when(mockCollection.find(any(Bson.class))).thenReturn(mockFindIterable);

        when(mockFindIterable.iterator()).thenReturn(mockMongoCursor);
        when(mockFindIterable.first()).thenReturn(mockDocument);
        when(mockFindIterable.projection(any(Bson.class))).thenReturn(mockFindIterable);

        // aggregate
        when(mockCollection.aggregate(anyList())).thenReturn(mockAggregateIterable);
        when(mockAggregateIterable.first()).thenReturn(mockDocument);
        when(mockDocument.getObjectId("_id")).thenReturn(new ObjectId(employeeId));
        when(mockDocument.get("department")).thenReturn(mockDocument2);
        when(mockDocument2.getObjectId("_id")).thenReturn(ObjectId.get());
        when(mockDocument2.getString("name")).thenReturn("DepartmentA");

        // update
        when(mockCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockUpdateResult);
        when(mockUpdateResult.getMatchedCount()).thenReturn((long) 1);

        // delete
        when(mockCollection.deleteOne(any(Bson.class))).thenReturn(mockDeleteResult);
        when(mockDeleteResult.getDeletedCount()).thenReturn((long)1);

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(new EmployeeDepartmentService(mockDatabase))
                .build()
                .start());

        // Create a client channel and register for automatic graceful shutdown.
        stub = EmployeeDepartmentServiceGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder
                        .forName(serverName)
                        .directExecutor()
                        .build()));
    }

    @Test
    public void testMocks() {
        // save
        stub.save(EmployeeDepartment.newBuilder().setId(employeeId).setDepartment(departmenta).build());

        // findById
        EmployeeDepartment empdept1 = stub.findById(EmployeeDepartment.newBuilder()
                .setId(employeeId).build());

        // save employee_department
        stub.save(EmployeeDepartment.newBuilder().setId(employeeId).setDepartment(departmenta).build());

        // delete employee_department
        stub.deleteById(EmployeeDepartment.newBuilder().setId(employeeId).build());
    }
}