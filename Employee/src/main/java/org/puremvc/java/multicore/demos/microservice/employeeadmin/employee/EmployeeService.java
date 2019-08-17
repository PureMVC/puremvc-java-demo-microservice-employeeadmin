//
//  Data.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.employee;

import com.google.protobuf.Empty;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

public class EmployeeService extends EmployeeServiceGrpc.EmployeeServiceImplBase {

    private MongoDatabase database;

    public EmployeeService(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void findAll(Empty request, StreamObserver<EmployeeList> responseObserver) {
        MongoCursor<Document> cursor = database.getCollection("employee")
                .find()
                .iterator();

        EmployeeList.Builder employees = EmployeeList.newBuilder();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            employees.addEmployee(Employee.newBuilder()
                    .setId(document.getObjectId("_id").toString())
                    .setUsername(document.getString("username"))
                    .setFirst(document.getString("first"))
                    .setLast(document.getString("last"))
                    .setEmail(document.getString("email")).build());
        }
        responseObserver.onNext(employees.build());
        responseObserver.onCompleted();
    }

    @Override
    public void findById(Employee request, StreamObserver<Employee> responseObserver) {
        Document document = database.getCollection("employee")
                .find(Filters.eq("_id", new ObjectId(request.getId())))
                .first();

        if(document != null) {
            responseObserver.onNext(Employee.newBuilder()
                    .setId(document.getObjectId("_id").toString())
                    .setUsername(document.getString("username"))
                    .setFirst(document.getString("first"))
                    .setLast(document.getString("last"))
                    .setEmail(document.getString("email")).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

    @Override
    public void save(Employee request, StreamObserver<Employee> responseObserver) {
        Document document = new Document()
                .append("_id", ObjectId.get())
                .append("username", request.getUsername())
                .append("first", request.getFirst())
                .append("last", request.getLast())
                .append("email", request.getEmail());

        database.getCollection("employee")
                .insertOne(document);

        responseObserver.onNext(Employee.newBuilder()
                .setId(document.getObjectId("_id").toString())
                .setUsername(document.getString("username"))
                .setFirst(document.getString("first"))
                .setLast(document.getString("last"))
                .setEmail(document.getString("email"))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateById(Employee request, StreamObserver<Employee> responseObserver) {
        Document document = new Document()
                .append("username", request.getUsername())
                .append("first", request.getFirst())
                .append("last", request.getLast())
                .append("email", request.getEmail());

        UpdateResult result = database
                .getCollection("employee")
                .updateOne(Filters.eq("_id", new ObjectId(request.getId())), new Document("$set", document));

        if(result.getMatchedCount() == 1) {
            responseObserver.onNext(Employee.newBuilder()
                    .setId(request.getId())
                    .setUsername(document.getString("username"))
                    .setFirst(document.getString("first"))
                    .setLast(document.getString("last"))
                    .setEmail(document.getString("email"))
                    .build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("Not Found"));
        }
    }

    @Override
    public void deleteById(Employee request, StreamObserver<Empty> responseObserver) {
        DeleteResult result = database
                .getCollection("employee")
                .deleteOne(Filters.eq("_id", new ObjectId(request.getId())));

        if(result.getDeletedCount() == 1) {
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("Not Found"));
        }
    }

}