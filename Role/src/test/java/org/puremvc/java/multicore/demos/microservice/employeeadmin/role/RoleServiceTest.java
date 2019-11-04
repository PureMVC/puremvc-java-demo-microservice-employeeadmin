//
//  RoleServiceTest.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.role;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class RoleServiceTest {

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
    Document mockDocument;

    @Mock
    UpdateResult mockUpdateResult;

    @Mock
    DeleteResult mockDeleteResult;

    @Rule
    public GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    RoleServiceGrpc.RoleServiceBlockingStub stub;

    String roleId = "5d4dc8635dd32dbcba4ae0ae";
    String roleName = "Accounting";

    public RoleServiceTest() throws IOException {
        MockitoAnnotations.initMocks(this);

        // database, collection
        when(mockClient.getDatabase(anyString())).thenReturn(mockDatabase);
        when(mockDatabase.getCollection(anyString())).thenReturn(mockCollection);

        // findAll, findById
        when(mockCollection.find()).thenReturn(mockFindIterable);
        when(mockCollection.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockMongoCursor);
        when(mockFindIterable.first()).thenReturn(mockDocument);
        when(mockFindIterable.projection(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockDocument.getObjectId("_id")).thenReturn(new ObjectId(roleId));
        when(mockDocument.getString("name")).thenReturn(roleName);

        // insert
        doNothing().when(mockCollection).insertOne(mockDocument);

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
                .addService(new RoleService(mockDatabase))
                .build()
                .start());

        // Create a client channel and register for automatic graceful shutdown.
        stub = RoleServiceGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder
                        .forName(serverName)
                        .directExecutor()
                        .build()));
    }

    @Test
    void testMocks() {
        // insert a role
        Role role = stub.save(Role.newBuilder().setName(roleName).build());
        assertNotNull(role.getId());
        assertEquals(roleName, role.getName());

        // get a role by id
        Role temp = stub.findById(Role.newBuilder().setId(role.getId()).build());
        assertEquals(roleId, temp.getId());
        assertEquals(roleName, temp.getName());

        // update a role
        Role temp2 = stub.updateById(Role.newBuilder().setId(roleId).setName(roleName).build());
        assertEquals(roleId, temp2.getId());
        assertEquals(roleName, temp2.getName());

        // find all
        stub.findAll(Empty.getDefaultInstance());

        // delete a role by id
        stub.deleteById(Role.newBuilder().setId(roleId).build());
    }
}