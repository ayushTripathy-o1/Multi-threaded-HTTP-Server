package com.ayush.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServer {
    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);
    private final int port;
    private final ExecutorService threadPool;

    public TcpServer(int port) {
        this.port = port;

        // get the current runtime and the total no. of available cpu cores
        int cores = Runtime.getRuntime().availableProcessors();
        // poolSize = 2 * cores
        int poolSize = 2 * cores;
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        logger.info("Thread Pool Initialized With Size {}", poolSize);
    }

    public void start() {
        try (ServerSocket serversocket = new ServerSocket(port)) {
            logger.info("Server Started At port {}", port);

            while (true) {
                Socket client = serversocket.accept();
                logger.info("Accepted connection from: {}", client.getInetAddress());

                ClientHandler handler = new ClientHandler(client);
                // NOTE: using the thread pool
                threadPool.submit(handler::handel);
            }

        } catch (IOException e) {
            logger.error("Failed To Start Server", e);
        }
    }
}
