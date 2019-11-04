//
//  Service.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.employee;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Service {

    private static MongoDatabase database;

    private static Map<String, String> env = new HashMap<String, String>() {
        {
            put("GRPC_PORT", System.getenv("GRPC_PORT"));
            put("MONGO_HOST", System.getenv("MONGO_HOST"));
            put("MONGO_PORT", System.getenv("MONGO_PORT"));
            put("MONGO_INITDB_ROOT_USERNAME", System.getenv("MONGO_INITDB_ROOT_USERNAME"));
            put("MONGO_INITDB_ROOT_PASSWORD", System.getenv("MONGO_INITDB_ROOT_PASSWORD"));
            put("MONGO_DATABASE", System.getenv("MONGO_DATABASE"));
            put("MONGO_AUTHDB", System.getenv("MONGO_AUTHDB"));
        }
    };

    static {
        env.forEach((key, value) -> {
            if(value == null)
                throw new RuntimeException("Please set the " + key + " in the environment variables and try again.");
        });

        System.out.println("Connecting MongoDB.");
        MongoCredential credential = MongoCredential.createCredential(env.get("MONGO_INITDB_ROOT_USERNAME"), env.get("MONGO_AUTHDB"), env.get("MONGO_INITDB_ROOT_PASSWORD").toCharArray());
        while(true) {
            try {
                MongoClient mongoClient = MongoClients.create(
                        MongoClientSettings.builder()
                                .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress(env.get("MONGO_HOST"), Integer.parseInt(env.get("MONGO_PORT"))))))
                                .credential(credential)
                                .build());
                if(mongoClient.getDatabase(env.get("MONGO_DATABASE")).getCollection("employee").find().iterator() != null) {
                    database = mongoClient.getDatabase(env.get("MONGO_DATABASE"));
                    break;
                }
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Server server = ServerBuilder.forPort(Integer.parseInt(env.get("GRPC_PORT")))
                .addService(new EmployeeService(database))
                .build();
        try {
            server.start();
            System.out.println("gRPC Server listening at: " + env.get("GRPC_PORT"));
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}