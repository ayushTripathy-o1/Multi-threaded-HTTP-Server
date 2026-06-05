package com.ayush.server.routing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.ayush.server.http.HttpRequest;
import com.ayush.server.http.HttpResponse;
import com.ayush.server.http.HttpStatus;
import com.ayush.server.http.MimeTypeResolver;

public class Router {
    private final Map<String, RouteHandler> routes = new HashMap<>();

    public void addRoute(String method,String path, RouteHandler handler){
        routes.put(
                createKey(method, path),handler
                );
    }
    public void addStaticRoute(String method,String mountPath, String staticDirectory, String defaultPage){
        addRoute(method, mountPath+"**", req -> {
            try {
                String requestPath = req.getPath();
                String relativePath;
                if(requestPath.equals(mountPath)){
                    relativePath = defaultPage;
                }
                else {
                    relativePath = requestPath.substring(mountPath.length());
                    if(relativePath.startsWith("/")){
                        relativePath = relativePath.substring(1);
                    }
                }
                Path filePath = Path.of(staticDirectory,relativePath);
                if(!Files.exists(filePath)){
                    HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND);
                    response.setHeader("Content-Type", "text/plain");
                    response.setBody("404 Resource Not Found".getBytes());
                    return response;
                }
                byte[] fileBytes = Files.readAllBytes(filePath);
                HttpResponse response = new HttpResponse(HttpStatus.OK);
                response.setHeader("Content-Type",MimeTypeResolver.resolve(filePath.getFileName().toString()));
                response.setBody(fileBytes);
                return response;
            } catch (Exception e) {
                HttpResponse response = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setHeader("Content-Type", "text/plain");
                response.setBody(e.getMessage().getBytes());
                return response;
            }
        });
    }
    public String createKey(String method, String path){
        return method +":"+path;
    }
    public HttpResponse route(
            HttpRequest request
            ){

        String routeKey =
            createKey(
                    request.getMethod(),
                    request.getPath()
                    );

        RouteHandler handler =
            routes.get(routeKey);

        if(handler != null){
            return handler.handle(request);
        }

        // wildcard matching

        for(
                Map.Entry<String, RouteHandler> route
                : routes.entrySet()
           ){

            String key =
                route.getKey();

            String wildcardSuffix =
                ":/**";

            if(
                    key.equals(
                        request.getMethod()
                        + wildcardSuffix
                        )
              ){
                return route
                    .getValue()
                    .handle(request);
              }

            if(key.contains("/**")){

                String prefix =
                    key.substring(
                            0,
                            key.length() - 2
                            );

                String requestKey =
                    request.getMethod()
                    + ":"
                    + request.getPath();

                if(
                        requestKey.startsWith(
                            prefix
                            )
                  ){
                    return route
                        .getValue()
                        .handle(request);
                  }
            }
           }

        HttpResponse response =
            new HttpResponse(
                    HttpStatus.NOT_FOUND
                    );

        response.setHeader(
                "Content-Type",
                "text/plain"
                );

        response.setBody(
                "404 Route Not Found"
                .getBytes()
                );

        return response;
            }
}
