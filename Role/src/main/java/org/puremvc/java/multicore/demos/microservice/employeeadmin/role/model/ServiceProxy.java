//
//  ServiceProxy.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.role.model;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.puremvc.java.patterns.proxy.Proxy;

import javax.ws.rs.NotFoundException;
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

    public Document getById(String id) {
        Document document = databaseSupplier.get()
                .getCollection("user")
                .find(new BasicDBObject("_id", new ObjectId(id)))
                .projection(Projections.fields(Projections.include("roles"), Projections.excludeId()))
                .first();

        if(document != null) {
            return document;
        } else {
            throw new NotFoundException("Not Found");
        }
    }

    public Document updateById(String id, String roles) {
        Document document = Document.parse(roles);
        UpdateResult result = databaseSupplier.get()
                .getCollection("user")
                .updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", document));

        if(result.getMatchedCount() == 1) {
            return document;
        } else {
            throw new NotFoundException("Not Found");
        }
    }

}