//
//  EmployeeServiceTest.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.employee;

import com.google.protobuf.Empty;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {

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

    @Rule
    public GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    EmployeeServiceGrpc.EmployeeServiceBlockingStub stub;

    public EmployeeServiceTest() throws IOException {
        MockitoAnnotations.initMocks(this);

        when(mockMongoClient.getDatabase(anyString())).thenReturn(mockMongoDatabase);
        when(mockMongoDatabase.getCollection(anyString())).thenReturn(mockMongoCollection);

        when(mockMongoCollection.find()).thenReturn(mockFindIterable);
        when(mockMongoCollection.find(any(Bson.class))).thenReturn(mockFindIterable);

        when(mockFindIterable.iterator()).thenReturn(mockMongoCursor);
        when(mockFindIterable.first()).thenReturn(mockDocument);
        when(mockFindIterable.projection(any(Bson.class))).thenReturn(mockFindIterable);

        when(mockDocument.getObjectId("_id")).thenReturn(new ObjectId("5d4dc8635dd32dbcba4ae0c3"));
        when(mockDocument.getString("username")).thenReturn("lstooge");
        when(mockDocument.getString("first")).thenReturn("Larry");
        when(mockDocument.getString("last")).thenReturn("Stooge");
        when(mockDocument.getString("email")).thenReturn("larry@stooges.com");

        when(mockMongoCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockUpdateResult);
        when(mockUpdateResult.getMatchedCount()).thenReturn((long) 1);

        doNothing().when(mockMongoCollection).insertOne(mockDocument);

        when(mockMongoCollection.deleteOne(any(Bson.class))).thenReturn(mockDeleteResult);
        when(mockDeleteResult.getDeletedCount()).thenReturn((long)1);

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(new EmployeeService(mockMongoDatabase))
                .build()
                .start());

        // Create a client channel and register for automatic graceful shutdown.
        stub = EmployeeServiceGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder
                        .forName(serverName)
                        .directExecutor()
                        .build()));
    }

    @Test
    public void testFindAll() {
        EmployeeList response = stub.findAll(Empty.getDefaultInstance());
        assertNotNull(response);
    }

    @Test
    void testFindById() {
        Employee response = stub.findById(Employee.newBuilder().setId("5d4dc8635dd32dbcba4ae0c3").build());
        assertNotNull(response);
    }

    @Test
    public void testSave() {
        Employee employee = Employee.newBuilder()
                .setUsername("username")
                .setFirst("first")
                .setLast("last")
                .setEmail("mail@email.com").build();

        Employee response = stub.save(employee);
        assertNotNull(response);
    }

    @Test
    public void testUpdateById() {
        Employee request = Employee.newBuilder()
                .setId("5d4dc8635dd32dbcba4ae0c3")
                .setUsername("username")
                .setFirst("first")
                .setLast("last")
                .setEmail("mail@email.com").build();

        try {
            stub.updateById(request);
        } catch (Exception exception) {
            System.out.println(exception);
            fail();
        }
    }

    @Test
    public void testDeleteById() {
        Employee request = Employee.newBuilder()
                .setId("5d4dc8635dd32dbcba4ae0c3")
                .build();

        try {
            stub.deleteById(request);
        } catch (Exception exception) {
            System.out.println(exception);
            fail();
        }
    }

}