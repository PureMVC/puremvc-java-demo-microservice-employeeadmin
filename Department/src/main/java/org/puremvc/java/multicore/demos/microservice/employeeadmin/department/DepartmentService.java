//
//  DepartmentService.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department;

import com.google.protobuf.Empty;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

public class DepartmentService extends DepartmentServiceGrpc.DepartmentServiceImplBase {

    private MongoDatabase database;

    public DepartmentService(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void findAll(Empty request, StreamObserver<DepartmentList> responseObserver) {
        MongoCursor<Document> cursor = database.getCollection("department")
                .find()
                .iterator();

        DepartmentList.Builder departments = DepartmentList.newBuilder();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Department department = Department.newBuilder()
                    .setId(document.getObjectId("_id").toString())
                    .setName(document.getString("name")).build();

            departments.addDepartment(department);
        }
        responseObserver.onNext(departments.build());
        responseObserver.onCompleted();
    }

    @Override
    public void findById(Department request, StreamObserver<Department> responseObserver) {
        Document document = database.getCollection("department")
                .find(Filters.eq("_id", new ObjectId(request.getId())))
                .first();

        if(document != null) {
            responseObserver.onNext(Department.newBuilder()
                    .setId(document.getObjectId("_id").toString())
                    .setName(document.getString("name")).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

    @Override
    public void save(Department request, StreamObserver<Department> responseObserver) {
        Document document = new Document()
                .append("_id", ObjectId.get())
                .append("name", request.getName());

        database.getCollection("department")
                .insertOne(document);

        Department department = Department.newBuilder()
                .setId(document.getObjectId("_id").toString())
                .setName(document.getString("name")).build();

        responseObserver.onNext(department);
        responseObserver.onCompleted();
    }

    @Override
    public void updateById(Department request, StreamObserver<Department> responseObserver) {
        Document document = new Document()
                .append("name", request.getName());

        UpdateResult result = database.getCollection("department")
                .updateOne(Filters.eq("_id", new ObjectId(request.getId())), new Document("$set", document));

        if(result.getMatchedCount() == 1) {
            responseObserver.onNext(Department.newBuilder()
                    .setId(request.getId())
                    .setName(document.getString("name")).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

    @Override
    public void deleteById(Department request, StreamObserver<Empty> responseObserver) {
        DeleteResult result = database.getCollection("department")
                .deleteOne(Filters.eq("_id", new ObjectId(request.getId())));

        if(result.getDeletedCount() == 1) {
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

}