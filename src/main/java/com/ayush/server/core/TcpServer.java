package com.ayush.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServer {
    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);
    private final int port;

    public TcpServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket Serversocket = new ServerSocket(port)) {
            logger.info("Server Started At port {}", port);

            // Demo Client
            Socket client = Serversocket.accept();
            logger.info("Client Connected from {}", client.getInetAddress());

        } catch (IOException e) {
            logger.error("Failed To Start Server", e);
        }
    }
}
