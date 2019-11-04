//
//  EmployeeDepartmentService.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department;

import com.google.protobuf.Empty;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;

public class EmployeeDepartmentService extends EmployeeDepartmentServiceGrpc.EmployeeDepartmentServiceImplBase {

    private MongoDatabase database;

    public EmployeeDepartmentService(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void findById(EmployeeDepartment request, StreamObserver<EmployeeDepartment> responseObserver) {
        List<Document> aggregates = Arrays.asList(
                new Document("$match", new Document("_id", new ObjectId(request.getId()))),
                new Document("$lookup", new Document("from", "department")
                        .append("localField", "department._id")
                        .append("foreignField", "_id")
                        .append("as", "department")),
                new Document("$unwind", "$department"));

        Document document = database.getCollection("employee_department")
                .aggregate(aggregates)
                .first();

        if(document != null) {
            Document department = (Document)document.get("department");
            EmployeeDepartment employeeDepartment = EmployeeDepartment.newBuilder()
                    .setId(document.getObjectId("_id").toString())
                    .setDepartment(Department.newBuilder()
                            .setId(department.getObjectId("_id").toString())
                            .setName(department.getString("name")).build()).build();

            responseObserver.onNext(employeeDepartment);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

    @Override
    public void save(EmployeeDepartment request, StreamObserver<EmployeeDepartment> responseObserver) {
        Document document = new Document()
                .append("_id", new ObjectId(request.getId()))
                .append("department", new Document().append("_id", new ObjectId(request.getDepartment().getId())));

        database.getCollection("employee_department")
                .insertOne(document);

        responseObserver.onNext(EmployeeDepartment.newBuilder()
                .setId(document.getObjectId("_id").toString())
                .setDepartment(Department.newBuilder()
                        .setId(((Document)document.get("department")).getObjectId("_id").toString()).build()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateById(EmployeeDepartment request, StreamObserver<EmployeeDepartment> responseObserver) {
        Document dept = new Document().append("_id", new ObjectId(request.getDepartment().getId()));
        Document document = new Document()
                .append("department", dept);

        UpdateResult result = database.getCollection("employee_department")
                .updateOne(Filters.eq("_id", new ObjectId(request.getId())), new Document("$set", document));

        if(result.getMatchedCount() == 1) {
            responseObserver.onNext(EmployeeDepartment.newBuilder()
                    .setId(request.getId())
                    .setDepartment(Department.newBuilder()
                            .setId(dept.getObjectId("_id").toString())).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

    @Override
    public void deleteById(EmployeeDepartment request, StreamObserver<Empty> responseObserver) {
        DeleteResult result = database.getCollection("employee_department")
                .deleteOne(Filters.eq("_id", new ObjectId(request.getId())));

        if(result.getDeletedCount() == 1) {
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

}