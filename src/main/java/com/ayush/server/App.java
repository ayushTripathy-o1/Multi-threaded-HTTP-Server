package com.ayush.server;

import com.ayush.server.core.TcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            })
        );

        log.info("Starting HTTP Server....");
        server.start();
    }
}
