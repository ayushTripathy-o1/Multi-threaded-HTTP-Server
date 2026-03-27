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
        try (ServerSocket serversocket = new ServerSocket(port)) {
            logger.info("Server Started At port {}", port);

            while (true) {
                Socket client = serversocket.accept();
                logger.info("Accepted connection from: {}", client.getInetAddress());

                ClientHandler handler = new ClientHandler(client);
                // NOTE: Each client in it's own thread
                Thread thread = new Thread(handler::handel);
                thread.start();
            }

        } catch (IOException e) {
            logger.error("Failed To Start Server", e);
        }
    }
}
