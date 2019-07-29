//
//  ServiceProxy.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department.model;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.puremvc.java.patterns.proxy.Proxy;

import java.util.function.Supplier;

public class ServiceProxy extends Proxy {

    public static String NAME = "ServiceProxy";

    private Supplier<MongoDatabase> databaseSupplier;

    public ServiceProxy(Supplier<MongoDatabase> databaseSupplier) {
        super(NAME, null);
        this.databaseSupplier = databaseSupplier;
    }

    public MongoCursor<Document> findAll() {
        return databaseSupplier.get()
                .getCollection("department")
                .find()
                .iterator();
    }

}