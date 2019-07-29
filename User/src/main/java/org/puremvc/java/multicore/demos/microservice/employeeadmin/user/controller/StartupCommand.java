//
//  StartupCommand.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.demos.microservice.employeeadmin.user.controller;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.puremvc.java.interfaces.INotification;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.user.ApplicationFacade;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.user.model.ServiceProxy;
import org.puremvc.java.multicore.demos.microservice.employeeadmin.user.view.ServiceMediator;
import org.puremvc.java.patterns.command.SimpleCommand;

import javax.servlet.Servlet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StartupCommand extends SimpleCommand {

    @Override
    public void execute(INotification notification) {

        Map<String, String> env = new HashMap<String, String>() {
            {
                put("MONGO_HOST", System.getenv("MONGO_HOST"));
                put("MONGO_PORT", System.getenv("MONGO_PORT"));
                put("MONGO_USERNAME", System.getenv("MONGO_USERNAME"));
                put("MONGO_PASSWORD", System.getenv("MONGO_PASSWORD"));
                put("MONGO_DATABASE", System.getenv("MONGO_DATABASE"));
                put("MONGO_AUTHDB", System.getenv("MONGO_AUTHDB"));
            }
        };

        env.forEach((k, v) -> {
            if(v == null) {
                throw new RuntimeException("Please set the " + k + " in env variables and try again");
            }
        });

        MongoCredential credential = MongoCredential.createCredential(env.get("MONGO_USERNAME"), env.get("MONGO_AUTHDB"), env.get("MONGO_PASSWORD").toCharArray());

        while(true) { // wait for mongo to boot up, mongoclient waits 30000 ms before timing out
            try {

                MongoClient mongoClient = MongoClients.create(
                        MongoClientSettings.builder()
                                .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress(env.get("MONGO_HOST"), Integer.parseInt(env.get("MONGO_PORT"))))))
                                .credential(credential)
                                .build());

                if(mongoClient.getDatabase(env.get("MONGO_DATABASE")).getCollection("user").find().iterator() != null) {
                    this.facade.registerCommand(ApplicationFacade.SERVICE, () -> new ServiceCommand());
                    this.facade.registerProxy(new ServiceProxy(() -> mongoClient.getDatabase(env.get("MONGO_DATABASE"))));
                    this.facade.registerMediator(new ServiceMediator((Servlet) notification.getBody()));
                    break;
                }

            } catch (Exception e) {
                System.err.println("failed to create");
            }

        }
    }

}
