package com.ayush.server.routing;

import java.util.HashMap;
import java.util.Map;

import com.ayush.server.http.HttpRequest;
import com.ayush.server.http.HttpResponse;
import com.ayush.server.http.HttpStatus;

public class Router {
    private final Map<String, RouteHandler> routes = new HashMap<>();

    public void addRoute(String method,String path, RouteHandler handler){
        routes.put(
                createKey(method, path),handler
                );
    }
    public String createKey(String method, String path){
        return method +":"+path;
    }
    public HttpResponse route(HttpRequest request){
        String routeKey = createKey(request.getMethod(), request.getPath());
        RouteHandler handler = routes.get(routeKey);
        if (handler == null) {
            HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND);
            response.setHeader("Content-Type", "text/plain");
            String body = "404 Route Not Found\n";
            response.setBody(body.getBytes());
            return response;
        }
        return handler.handle(request);
    }
}
