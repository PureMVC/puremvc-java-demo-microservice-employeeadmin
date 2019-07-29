//
//  ServiceProxyTest.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.role.model;

import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ServiceProxyTest {

    @Mock
    MongoClient mockMongoClient;

    @Mock
    private MongoDatabase mockMongoDatabase;

    @Mock
    MongoCollection mockMongoCollection;

    @Mock
    FindIterable<Document> mockFindIterable;

    @Mock MongoCursor<Document> mockMongoCursor;

    @Mock
    Document mockDocument;

    @Mock
    UpdateResult mockUpdateResult;

    private ServiceProxy serviceProxy;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockMongoClient.getDatabase(anyString())).thenReturn(mockMongoDatabase);
        when(mockMongoDatabase.getCollection(anyString())).thenReturn(mockMongoCollection);

        when(mockMongoCollection.find()).thenReturn(mockFindIterable);
        when(mockMongoCollection.find(any(Bson.class))).thenReturn(mockFindIterable);

        when(mockFindIterable.iterator()).thenReturn(mockMongoCursor);
        when(mockFindIterable.first()).thenReturn(mockDocument);
        when(mockFindIterable.projection(any(Bson.class))).thenReturn(mockFindIterable);

        when(mockMongoCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(mockUpdateResult);
        when(mockUpdateResult.getMatchedCount()).thenReturn((long) 1);

        serviceProxy = new ServiceProxy(() -> mockMongoClient.getDatabase("employeeadmin"));
    }

    @Test
    public void getRoles() {
        MongoCursor cursor = serviceProxy.findAll();
        assertNotNull(cursor);
    }

    @Test
    public void getUserRoles() {
        Document document = serviceProxy.getById("5d3d6253156d4f6085f37407");
        assertNotNull(document);
    }

    @Test
    public void updateUserRoles() {
        Document document = serviceProxy.updateById("5d3d6253156d4f6085f37407", "{\"roles\": [{\"_id\": \"5d3bd0f8454e59408cffdb4e\", \"name\": \"Inventory\"}]}");
        assertNotNull(document);
    }
}