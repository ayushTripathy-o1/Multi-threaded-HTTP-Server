package com.ayush.server.routing;

import java.util.HashMap;
import java.util.Map;

import com.ayush.server.http.HttpRequest;
import com.ayush.server.http.HttpResponse;
import com.ayush.server.http.HttpStatus;

public class Router {
    private final Map<String, RouteHandler> routes = new HashMap<>();

    public void addRoute(String path, RouteHandler handler){
        routes.put(path, handler);
    }
    public HttpResponse route(HttpRequest request){
        RouteHandler handler = routes.get(request.getPath());
        if (handler == null) {
            HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND);
            response.setHeader("Content-Type", "text/plain");
            response.setBody("404 Not Found");
            return response;
        }
        return handler.handle(request);
    }
}
