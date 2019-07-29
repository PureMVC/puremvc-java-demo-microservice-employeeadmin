//
//  EmployeeAdmin.java
//  PureMVC Java Demo - EmployeeAdmin Microservice
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package integration;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmployeeAdmin {

    public String url = "http://localhost:8080/User";

    @Test
    public void getUsers() throws IOException {
        URL url = new URL("http://localhost:8080/User/users");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder body = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(connection.getResponseCode() == 200 ?
                connection.getInputStream() : connection.getErrorStream());
        try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
                while((line = bufferedReader.readLine()) != null) {
                body.append(line);
            }
        }

        try(JsonReader jsonReader = Json.createReader(new StringReader(body.toString()))) {
            JsonArray jsonArray = jsonReader.readArray();
            assertNotNull(jsonArray);
        }

        if(connection.getResponseCode() != 200) System.out.println(body);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    public void insertUser() throws IOException {
        URL url = new URL("http://localhost:8080/User/users");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.getOutputStream().write("{\"username\": \"test\"}".getBytes());

        StringBuilder body = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(connection.getResponseCode() == 201 ?
                connection.getInputStream() : connection.getErrorStream());
        try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                body.append(line);
            }
        }

        assertEquals(201, connection.getResponseCode());
        assertNotNull(connection.getHeaderField("Location"));

        getUser(connection.getHeaderField("Location"));
        updateUser(connection.getHeaderField("Location"));
        updateUserRoles(connection.getHeaderField("Location"));
        deleteUser(connection.getHeaderField("Location"));
    }

    public void getUser(String location) throws IOException {
        URL url = new URL("http://localhost:8080/User" + location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder body = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(connection.getResponseCode() == 200 ?
                connection.getInputStream() : connection.getErrorStream());
        try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                body.append(line);
            }

            if(connection.getResponseCode() == 200) {
                try(JsonReader jsonReader = Json.createReader(new StringReader(body.toString()))) {
                    JsonObject jsonObject = jsonReader.readObject();
                    assertNotNull(jsonObject);
                }
            } else {
                System.out.println(connection.getResponseCode() + ": " + body);
            }
        }
        assertEquals(200, connection.getResponseCode());
    }

    public void updateUser(String location) throws IOException {
        URL url = new URL("http://localhost:8080/User" + location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.getOutputStream().write("{\"username\": \"test2\"}".getBytes());

        StringBuilder body = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(connection.getResponseCode() == 200 ?
                connection.getInputStream() : connection.getErrorStream());
        try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                body.append(line);
            }
        }

        assertEquals(200, connection.getResponseCode());
    }

    public void deleteUser(String location) throws IOException {
        URL url = new URL("http://localhost:8080/User" + location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        StringBuilder body = new StringBuilder();
        if(connection.getResponseCode() == 404 || connection.getResponseCode() == 405) {
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
            try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    body.append(line);
                }
            }
            System.out.println(connection.getResponseCode() + ": " + body);
        }
        assertEquals(204, connection.getResponseCode());
    }

    public void updateUserRoles(String location) throws IOException {
        URL url = new URL("http://localhost:8080/User/" + location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.getOutputStream().write("{\"roles\": [{\"_id\": \"5d3bd0f8454e59408cffdb4e\",\"name\": \"Inventory\"}]}".getBytes());
        assertEquals(200, connection.getResponseCode());

        getUserRoles(location);
    }

    public void getUserRoles(String location) throws IOException {
        System.out.println("get user roles");
        URL url = new URL("http://localhost:8080/User/" + location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder body = new StringBuilder();
        if(connection.getResponseCode() == 200 || connection.getResponseCode() == 404 || connection.getResponseCode() == 405) {
            System.out.println("in");
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
            try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    body.append(line);
                }
            }
            System.out.println(connection.getResponseCode() + ": " + body);
        }

        assertEquals(200, connection.getResponseCode());
    }

}



