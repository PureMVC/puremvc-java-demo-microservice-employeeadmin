//
//  ServiceProxy.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.user.model;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
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
                .getCollection("user")
                .find()
                .iterator();
    }

    public Document findById(String id) {
        Document document = databaseSupplier.get()
                .getCollection("user")
                .find(Filters.eq("_id", new ObjectId(id)))
                .first();

        if(document != null) {
            return document;
        } else {
            throw new NotFoundException("Not Found");
        }
    }

    public Document save(String user) {
        Document document = Document.parse(user);
        databaseSupplier.get()
                .getCollection("user")
                .insertOne(document);

        return document;
    }

    public Document updateById(String id, String user) {
        Document document = Document.parse(user);
        UpdateResult result = databaseSupplier.get()
                .getCollection("user")
                .updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", document));

        if(result.getMatchedCount() == 1) {
            return document;
        } else {
            throw new NotFoundException("Not Found");
        }
    }

    public DeleteResult deleteById(String id) {
        DeleteResult result = databaseSupplier.get()
                .getCollection("user")
                .deleteOne(Filters.eq("_id", new ObjectId(id)));

        if(result.getDeletedCount() != 0) {
            return result;
        } else {
            throw new NotFoundException("Not Found");
        }
    }

}