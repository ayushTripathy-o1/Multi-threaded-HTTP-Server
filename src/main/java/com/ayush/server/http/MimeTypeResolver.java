package com.ayush.server.http;

public class MimeTypeResolver {
    public static String resolve(String filename){
        if (filename.endsWith(".html")) {
            return "text/html";
        } 
        if (filename.endsWith(".css")) {
            return "text/css";
        }if (filename.endsWith(".js")) {
            return "application/javascript";
        } if (filename.endsWith(".json")) {
            return "application/json";
        }
        return "text/plain";
    }
}
