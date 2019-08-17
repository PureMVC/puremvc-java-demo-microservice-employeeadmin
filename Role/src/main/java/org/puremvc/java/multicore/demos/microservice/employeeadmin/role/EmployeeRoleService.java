package org.puremvc.java.multicore.demos.microservice.employeeadmin.role;

import com.google.protobuf.Empty;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeRoleService extends EmployeeRoleServiceGrpc.EmployeeRoleServiceImplBase {

    private MongoDatabase database;

    public EmployeeRoleService(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void findById(EmployeeRole request, StreamObserver<EmployeeRole> responseObserver) {
        List<Document> aggregates = Arrays.asList(
                new Document("$match", new Document("_id", new ObjectId(request.getId()))),
                new Document("$lookup", new Document("from", "role")
                        .append("localField", "roles")
                        .append("foreignField", "_id")
                        .append("as", "roles")));

        Document document = database.getCollection("employee_role")
                .aggregate(aggregates).first();

        if(document != null) {
            RoleList.Builder roleList = RoleList.newBuilder();
            ((List<Document>) document.get("roles")).forEach((role) -> {
                roleList.addRole(Role.newBuilder()
                        .setId(role.getObjectId("_id").toString())
                        .setName(role.getString("name")));
            });
            responseObserver.onNext(EmployeeRole.newBuilder()
                    .setId(document.getObjectId("_id").toString())
                    .setRoleList(roleList).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

    @Override
    public void save(EmployeeRole request, StreamObserver<EmployeeRole> responseObserver) {
        List<ObjectId> roles = new ArrayList<>();
        request.getRoleList().getRoleList().forEach(role -> roles.add(new ObjectId(role.getId())));

        Document document = new Document()
                .append("_id", new ObjectId(request.getId()))
                .append("roles", roles);

        database.getCollection("employee_role")
                .insertOne(document);

        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void updateById(EmployeeRole request, StreamObserver<EmployeeRole> responseObserver) {
        List<ObjectId> roles = new ArrayList<>();
        request.getRoleList().getRoleList().forEach(role -> roles.add(new ObjectId(role.getId())));

        Document document = new Document()
                .append("roles", roles);

        UpdateResult result = database.getCollection("employee_role")
                .updateOne(Filters.eq("_id", new ObjectId(request.getId())), new Document("$set", document));

        if(result.getMatchedCount() == 1) {
            responseObserver.onNext(request);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

    @Override
    public void deleteById(EmployeeRole request, StreamObserver<Empty> responseObserver) {
        DeleteResult result = database.getCollection("employee_role")
                .deleteOne(Filters.eq("_id", new ObjectId(request.getId())));

        if(result.getDeletedCount() == 1) {
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Throwable("NOT FOUND"));
        }
    }

}
