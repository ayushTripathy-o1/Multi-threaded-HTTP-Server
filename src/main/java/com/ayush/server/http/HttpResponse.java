package com.ayush.server.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpStatus status;
    private Map<String, String> headers = new HashMap<>();
    private String body = "";

    public HttpResponse(HttpStatus status){
        this.status =status;
    }

    public void setHeader(String key, String value){
        headers.put(key, value);
    }
    public void setBody(String body){
        this.body = body;
    }

    public void send(BufferedWriter writer) throws IOException {
        // Status line
        writer.write("HTTP/1.1 " + status.getCode() +" " + status.getReason()+"\r\n");
        // headers
        headers.putIfAbsent("Content-Length", String.valueOf(body.length()));
        for(Map.Entry<String,String> header: headers.entrySet()){
            writer.write(header.getKey()+": "+header.getValue()+"\r\n");
        }
        writer.write("\r\n");
        // body
        writer.write(body);
        writer.flush();
    }
}
