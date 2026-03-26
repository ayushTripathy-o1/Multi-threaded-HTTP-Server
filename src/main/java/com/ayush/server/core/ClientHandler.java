package com.ayush.server.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void handel() {
        log.info("Handling client {}", clientSocket.getInetAddress());
        try (
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));) {
            // TEMP: read only the first Line (HTTP Request line)
            String requestLine = reader.readLine();
            log.info("Reading Line: {}", requestLine);

            // TEMP: simple response
            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("Content-Type: text/plain\r\n");
            writer.write("Content-Length: 12\r\n");
            writer.write("\r\n");
            writer.write("Hello World\n");
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
