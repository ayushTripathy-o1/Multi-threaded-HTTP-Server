package com.ayush.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ayush.server.core.TcpServer;

/**
 * Hello world!
 *
 */
public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        logger.info("Initiating Connection...");
        TcpServer server = new TcpServer(8080);
        server.start();
    }
}
