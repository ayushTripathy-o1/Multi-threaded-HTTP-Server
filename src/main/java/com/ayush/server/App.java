package com.ayush.server;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ayush.server.core.TcpServer;
import com.ayush.server.http.HttpResponse;
import com.ayush.server.http.HttpStatus;
import com.ayush.server.http.MimeTypeResolver;
import com.ayush.server.routing.Router;

/*
 * main
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        TcpServer server = new TcpServer(8080);

        // adding shutdown hook
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    log.info("Shutdown Signal Recieved");
                    server.stop();
                }));

        log.info("Starting HTTP Server....");
        // routing
        Router router = new Router();
        router.addRoute("GET", "/health", req -> {
            HttpResponse response = new HttpResponse(HttpStatus.OK);
            response.setHeader("Content-Type", "text/plain");
            Map<String, String> body = new HashMap<>();
            body.put("status", "healthy");
            body.put("timestamp", LocalDateTime.now().toString());
            String jsonString = String.format(
                    "{\"status\":\"%s\",\"timestamp\":\"%s\"}\n",
                    body.get("status"),
                    body.get("timestamp"));
            response.setBody(jsonString.getBytes(StandardCharsets.UTF_8));
            return response;
        });
        router.addRoute("GET", "/test", req -> {
            HttpResponse response = new HttpResponse(HttpStatus.OK);
            response.setHeader("Content-Type", "text/plain");
            String body = "Test Route\n";
            response.setBody(body.getBytes());
            return response;
        });
        router.addRoute("GET", "/users", req -> {
            HttpResponse response = new HttpResponse(HttpStatus.OK);
            response.setHeader("Content-Type", "application/json");
            User u = new User("Ayush", "ayush@hx.com");
            String body = String.format("{\"message\":\"%s\",\"data\":\"%s\"}\n", "Users Found",u.toString());              
            response.setBody(body.getBytes());
            return response;
        });

        router.addStaticRoute("GET", "/", "src/main/resources/static", "index.html"); 
        server.start(router);
    }
}

// dummy user class 
class User {
    private UUID id = UUID.randomUUID();
    private String name;
    private String email;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
