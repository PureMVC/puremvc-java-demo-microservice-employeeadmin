//
//  RoleService.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.role;

import com.google.protobuf.Empty;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

public class RoleService extends RoleServiceGrpc.RoleServiceImplBase {

    private MongoDatabase database;

    public RoleService(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void findAll(Empty request, StreamObserver<RoleList> responseObserver) {
        MongoCursor<Document> cursor = database.getCollection("role")
                .find()
                .iterator();

        RoleList.Builder roles = RoleList.newBuilder();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Role role = Role.newBuilder()
                    .setId(document.getObjectId("_id").toString())
                    .setName(document.getString("name")).build();
            roles.addRole(role);
        }
        responseObserver.onNext(roles.build());
        responseObserver.onCompleted();
    }

    @Override
    public void findById(Role request, StreamObserver<Role> responseObserver) {
        Document document = database.getCollection("role")
                .find(Filters.eq("_id", new ObjectId(request.getId())))
                .first();

        if(document != null) {
            responseObserver.onNext(Role.newBuilder()
                    .setId(document.getObjectId("_id").toString())
                    .setName(document.getString("name")).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("Not Found"));
        }
    }

    @Override
    public void save(Role request, StreamObserver<Role> responseObserver) {
        Document document = new Document()
                .append("_id", ObjectId.get())
                .append("name", request.getName());

        database.getCollection("role")
                .insertOne(document);

        responseObserver.onNext(Role.newBuilder()
                .setId(document.getObjectId("_id").toString())
                .setName(document.getString("name")).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateById(Role request, StreamObserver<Role> responseObserver) {
        Document document = new Document()
                .append("name", request.getName());

        UpdateResult result = database.getCollection("role")
                .updateOne(Filters.eq("_id", new ObjectId(request.getId())), new Document("$set", document));

        if(result.getMatchedCount() == 1) {
            responseObserver.onNext(Role.newBuilder()
                    .setId(request.getId())
                    .setName(document.getString("name")).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

    @Override
    public void deleteById(Role request, StreamObserver<Empty> responseObserver) {
        DeleteResult result = database.getCollection("role")
                .deleteOne(Filters.eq("_id", new ObjectId(request.getId())));

        if(result.getDeletedCount() == 1) {
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }

    }

}