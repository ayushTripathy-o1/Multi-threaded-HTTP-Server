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
import com.ayush.server.http.HttpStatus;

public class ClientHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
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
            HttpResponse response = new HttpResponse(HttpStatus.OK);
            response.setHeader("Content-Type", "text/plain");
            response.setBody("Hello From Clean HTTP Response\n");
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
