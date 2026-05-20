package com.ayush.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ayush.server.core.TcpServer;

/*
* main
*/
public class App {
    private final static Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        TcpServer server = new TcpServer(8080);

        // adding shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown Signal Recieved");
            server.stop();
        }));

        log.info("Starting HTTP Server....");
        server.start();
    }
}
