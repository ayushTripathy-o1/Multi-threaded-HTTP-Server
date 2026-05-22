package com.ayush.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ayush.server.http.HttpResponse;
import com.ayush.server.http.HttpStatus;
import com.ayush.server.routing.Router;

public class TcpServer {
    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);
    private final int port;
    private final ExecutorService threadPool;
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    public TcpServer(int port) {
        this.port = port;
        // get the current runtime and the total no. of available cpu cores
        int cores = Runtime.getRuntime().availableProcessors();
        // poolSize = 2 * cores
        int poolSize = 2 * cores;
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        log.info("Thread Pool Initialized With Size {}", poolSize);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            log.info("Tcp server Listening on Port {}", port);
            // routing
            Router router = new Router();
            router.addRoute("GET","/hello", req ->{
                HttpResponse response = new HttpResponse(HttpStatus.OK);
                response.setHeader("Content-Type", "text/plain");
                String body = "Hello Route\n";
                response.setBody(body.getBytes());
                return response;
            });
            router.addRoute("GET","/test", req ->{
                HttpResponse response = new HttpResponse(HttpStatus.OK);
                response.setHeader("Content-Type", "text/plain");
                String body = "Test Route\n";
                response.setBody(body.getBytes());
                return response;
            });
            router.addRoute("POST","/user", req ->{
                HttpResponse response = new HttpResponse(HttpStatus.OK);
                response.setHeader("Content-Type", "application/json");
                String body = "Received: " + req.getBody()+"\r\n";
                response.setBody(body.getBytes());
                return response;
            });
            router.addRoute("DELETE", "/user", req -> {
                HttpResponse response = new HttpResponse(HttpStatus.OK);
                response.setHeader("Content-Type", "application/json");
                String body = "User Deleted\n";
                response.setBody(body.getBytes());
                return response;
            });
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    log.info("Accepted Connection from {}", client.getInetAddress());

                    ClientHandler handler = new ClientHandler(client,router);
                    threadPool.submit(handler::handle);
                } catch (IOException e) {
                    if (running) {
                        log.error("Error Accepting Connection", e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Server Failed to Start", e);
        }
    }

    public void stop() {
        log.info("Shutting down Server....");
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // breaks accept()
            }
        } catch (IOException e) {
            log.warn("Error closing server socket", e);
        }
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("Forcing Shutdown");
                threadPool.shutdown();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Server Shutdown Complete");
    }
}
