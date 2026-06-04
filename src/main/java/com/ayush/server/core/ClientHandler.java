package com.ayush.server.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ayush.server.http.HttpRequest;
import com.ayush.server.http.HttpRequestParser;
import com.ayush.server.http.HttpResponse;
import com.ayush.server.routing.Router;

public class ClientHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final Router router;

    public ClientHandler(Socket clientSocket, Router router) {
        this.clientSocket = clientSocket;
        this.router = router;
    }

    public void handle() {
        log.info("Handling client {} on thread {}",
                clientSocket.getInetAddress(),
                Thread.currentThread().getName());
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));) {
            HttpRequestParser parser = new HttpRequestParser();
            HttpRequest request = parser.parse(reader);
            if (request == null) {
                return;
            }
            log.info("Incoming Request {} {}", request.getMethod(), request.getPath());
            HttpResponse response = router.route(request);
            response.send(writer);
        } catch (IOException e) {
            log.error("Error While Handling Client", e);
        } finally {
            try {
                // INFO: closing connection
                clientSocket.close();
                log.info("Connection Closed");
            } catch (IOException e) {
                log.warn("Failed To close Socket", e);
            }
        }
    }

}
