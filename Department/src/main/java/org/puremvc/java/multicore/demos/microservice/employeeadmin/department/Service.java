//
//  Service.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.department;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import grpc.health.v1.HealthGrpc;
import grpc.health.v1.HealthOuterClass;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Service extends DepartmentServiceGrpc.DepartmentServiceImplBase {

    private static MongoDatabase database;

    private static Map<String, String> env = new HashMap<String, String>() {
        {
            put("GRPC_PORT", System.getenv("GRPC_PORT"));
            put("CONSUL_HOST", System.getenv("CONSUL_HOST"));
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
        while(true) {
            try {
                MongoCredential credential = MongoCredential.createCredential(env.get("MONGO_INITDB_ROOT_USERNAME"), env.get("MONGO_AUTHDB"), env.get("MONGO_INITDB_ROOT_PASSWORD").toCharArray());
                MongoClient mongoClient = MongoClients.create(
                        MongoClientSettings.builder()
                                .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress(env.get("MONGO_HOST"), Integer.parseInt(env.get("MONGO_PORT"))))))
                                .credential(credential)
                                .build());

                mongoClient.getDatabase(env.get("MONGO_DATABASE")).getCollection(env.get("MONGO_AUTHDB")).find().iterator();
                database = mongoClient.getDatabase(env.get("MONGO_DATABASE"));
                break;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }

        System.out.println("Connecting Consul.");
        while(true) {
            try {
                String name = "department";
                String id = UUID.randomUUID().toString().substring(0, 8);
                String grpc = String.format("%s:%d/grpc.health.v1.Health", name, Integer.parseInt(env.get("GRPC_PORT")));
                String interval = "15s";

                String data = (String.format("{\"name\": \"%s\", \"id\": \"%s\", \"check\": { \"grpc\": \"%s\", \"interval\": \"%s\", \"name\": \"%s\", \"id\": \"%s\"}}",
                        name, id, grpc, interval, name, id));

                URL url = new URL(String.format("http://%s/v1/agent/service/register", env.get("CONSUL_HOST")));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("content-type", "application-json; charset=utf-8");
                connection.setDoOutput(true);
                connection.getOutputStream().write(data.getBytes());

                if (connection.getResponseCode() != 200) {
                    StringBuilder body = new StringBuilder();
                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                    try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        String line;
                        while((line = bufferedReader.readLine()) != null) {
                            body.append(line);
                        }
                    }
                    throw new Exception("Consul: " + body);
                }
                break;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    public static void main(String[] args) {
        Server server = ServerBuilder.forPort(Integer.parseInt(env.get("GRPC_PORT")))
                .addService(new DepartmentService(database))
                .addService(new EmployeeDepartmentService(database))
                .addService(new HealthGrpc.HealthImplBase() {
                    @Override
                    public void check(HealthOuterClass.HealthCheckRequest request, StreamObserver<HealthOuterClass.HealthCheckResponse> responseObserver) {
                        responseObserver.onNext(HealthOuterClass.HealthCheckResponse.newBuilder().setStatus(HealthOuterClass.HealthCheckResponse.ServingStatus.SERVING).build());
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void watch(HealthOuterClass.HealthCheckRequest request, StreamObserver<HealthOuterClass.HealthCheckResponse> responseObserver) {
                        responseObserver.onNext(HealthOuterClass.HealthCheckResponse.newBuilder().setStatus(HealthOuterClass.HealthCheckResponse.ServingStatus.SERVING).build());
                        responseObserver.onCompleted();
                    }
                })
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