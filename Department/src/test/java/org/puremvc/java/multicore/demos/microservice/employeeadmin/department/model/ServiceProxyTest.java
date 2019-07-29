//
//  ServiceProxyTest.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department.model;

import com.mongodb.client.*;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
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

    private ServiceProxy serviceProxy;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockMongoClient.getDatabase(anyString())).thenReturn(mockMongoDatabase);
        when(mockMongoDatabase.getCollection(anyString())).thenReturn(mockMongoCollection);

        when(mockMongoCollection.find()).thenReturn(mockFindIterable);

        when(mockFindIterable.iterator()).thenReturn(mockMongoCursor);
        when(mockFindIterable.first()).thenReturn(mockDocument);

        serviceProxy = new ServiceProxy(() -> mockMongoClient.getDatabase("employeeadmin"));
    }

    @Test
    public void findAll() {
        MongoCursor cursor = serviceProxy.findAll();
        assertNotNull(cursor);
    }
}