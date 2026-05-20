package com.ayush.server.http;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestParser {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest parse(BufferedReader reader) throws IOException {
        HttpRequest request = new HttpRequest();

        // 1. parse the request line;
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }
        String[] parts = requestLine.split(" ");
        request.setMethod(parts[0]);
        request.setPath(parts[1]);
        request.setVersion(parts[2]);

        log.info("Parsed Request Line: {} {} {}", parts[0], parts[1], parts[2]);

        // 2. parse haeders
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headersParts = line.split(":", 2);
            if (headersParts.length == 2) {
                request.getHeaders().put(
                        headersParts[0].trim(),
                        headersParts[1].trim());
            }
        }
        log.info("Parsed {} headers", request.getHeaders().size());
        return request;
    }
}

