package com.ayush.server.routing;

import com.ayush.server.http.HttpRequest;
import com.ayush.server.http.HttpResponse;

@FunctionalInterface
public interface RouteHandler {
    HttpResponse handle(HttpRequest request);
} 
