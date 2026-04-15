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

public class ClientHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void handel() {
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
            log.info("Incomming Request {} {}", request.getMethod(), request.getPath());

            String body = "Hello From Http Server";

            // TEMP: simple response
            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("Content-Type: text/plain\r\n");
            writer.write("Content-Length:" + body.length() + "\r\n");
            writer.write("\r\n");
            writer.write(body);
            writer.flush();
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
