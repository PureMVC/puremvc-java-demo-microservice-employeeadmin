package org.puremvc.java.multicore.demos.microservice.employeeadmin.role;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class EmployeeRoleServiceTest {

    @Mock
    MongoClient mockClient;

    @Mock
    private MongoDatabase mockDatabase;

    @Mock
    MongoCollection mockCollection;

    @Mock
    FindIterable<Document> mockFindIterable;

    @Mock
    MongoCursor<Document> mockCursor;

    @Mock
    AggregateIterable mockAggregateIterable;

    @Mock
    List<Document> mockListDocument;

    @Mock
    Document mockDocument;

    @Mock
    UpdateResult mockUpdateResult;

    @Mock
    DeleteResult mockDeleteResult;

    @Rule
    public GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    EmployeeRoleServiceGrpc.EmployeeRoleServiceBlockingStub stub;

    String employeeId = ObjectId.get().toString();
    RoleList roleList1 = RoleList.newBuilder()
            .addRole(Role.newBuilder()
                    .setId(ObjectId.get().toString())
                    .setId(ObjectId.get().toString())).build();

    public EmployeeRoleServiceTest() throws IOException {
        MockitoAnnotations.initMocks(this);

        // database, collection
        when(mockClient.getDatabase(anyString())).thenReturn(mockDatabase);
        when(mockDatabase.getCollection(anyString())).thenReturn(mockCollection);

        // findAll, findById
        when(mockCollection.find()).thenReturn(mockFindIterable);
        when(mockCollection.find(any(Bson.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.iterator()).thenReturn(mockCursor);
        when(mockFindIterable.first()).thenReturn(mockDocument);
        when(mockFindIterable.projection(any(Bson.class))).thenReturn(mockFindIterable);

        // aggregate
        when(mockCollection.aggregate(anyList())).thenReturn(mockAggregateIterable);
        when(mockAggregateIterable.first()).thenReturn(mockDocument);
        when(mockDocument.get("roles")).thenReturn(mockListDocument);
        when(mockDocument.getObjectId("_id")).thenReturn(new ObjectId(employeeId));

        // insert
        doNothing().when(mockCollection).insertOne(mockDocument);

        // update
        when(mockCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockUpdateResult);
        when(mockUpdateResult.getMatchedCount()).thenReturn((long) 1);

        // delete
        when(mockCollection.deleteOne(any(Bson.class))).thenReturn(mockDeleteResult);
        when(mockDeleteResult.getDeletedCount()).thenReturn((long)1);

        // update
        when(mockCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockUpdateResult);

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(new EmployeeRoleService(mockDatabase))
                .build()
                .start());

        // Create a client channel and register for automatic graceful shutdown.
        stub = EmployeeRoleServiceGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder
                        .forName(serverName)
                        .directExecutor()
                        .build()));
    }

    @Test
    void testMocks() {
        // insert a role
        stub.save(EmployeeRole.newBuilder()
                .setId(employeeId)
                .setRoleList(roleList1).build());

        // get a role by id
        EmployeeRole employeeRole = stub.findById(EmployeeRole.newBuilder().setId(employeeId).build());
        assertEquals(employeeId, employeeRole.getId());

        // update a role
        EmployeeRole employeeRole1 = stub.updateById(EmployeeRole.newBuilder().setId(employeeId).build());
        assertEquals(employeeId, employeeRole1.getId());

        // delete a role by id
        stub.deleteById(EmployeeRole.newBuilder().setId(employeeId).build());
    }
}