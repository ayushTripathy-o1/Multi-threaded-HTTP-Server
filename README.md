# Multi-Threaded HTTP Server

[![Java](https://img.shields.io/badge/Java-21-%23ED8B00?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apache-maven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

A lightweight HTTP/1.1 server built from **zero dependencies** — no Spring Boot, no Tomcat, no servlet container. Just raw TCP sockets, a hand-crafted HTTP parser, and a clean routing engine written entirely in Java 21.

---

## Why This Exists

Most production servers abstract away the network layer. This project does the opposite: it implements the HTTP protocol at the socket level to demonstrate a deep understanding of TCP, HTTP semantics, concurrent I/O, and server architecture. Every byte sent over the wire is written and accounted for by the code in this repository.

---

## Architecture

```
Client (Browser/curl)
  -> TcpServer (accept loop, thread pool: 2xCPU)
    -> ClientHandler (per-connection, thread pool)
      -> HttpRequestParser (raw HTTP -> HttpRequest)
        -> Router (exact match | wildcard match)
          -> RouteHandler lambda / StaticFileResolver
            -> HttpResponse (status + headers + body -> wire)
              -> Client
```

---

## Features

- **Zero framework dependencies** — built on `java.net.ServerSocket` and `java.net.Socket`
- **Hand-crafted HTTP/1.1 parser** — parses request line, headers, and `Content-Length`-aware body
- **Concurrent request handling** — fixed thread pool sized at `2 × availableProcessors()`
- **Graceful shutdown** — drains in-flight requests with a 10-second timeout before force-stopping
- **Two-tier routing** — exact path matching plus wildcard (`/**`) prefix matching for static files
- **Static file serving** — serves HTML, CSS, JS, and JSON files with correct MIME types
- **Functional route handlers** — `@FunctionalInterface` enables lambda-based route definitions
- **Structured logging** — SLF4J + Logback with DEBUG-level visibility into every connection and request
- **HTTP status codes** — 200 OK, 404 Not Found, 500 Internal Server Error
- **JSON API support** — built-in health check, test, and users endpoints

---

## Quick Start

```bash
# Build
mvn clean package

# Run
mvn exec:java

# Or run directly
java -cp target/http-server-1.0-SNAPSHOT.jar com.ayush.server.App
```

The server starts on `http://localhost:8080`.

### Try It

```bash
# Health check
curl http://localhost:8080/health

# API endpoints
curl http://localhost:8080/test
curl http://localhost:8080/users

# Static files
curl http://localhost:8080/
```

---

## Project Structure

```
src/
├── main/java/com/ayush/server/
│   ├── App.java                  # Entry point, route registration
│   ├── core/
│   │   ├── TcpServer.java        # ServerSocket accept loop + thread pool
│   │   └── ClientHandler.java    # Per-connection request/response lifecycle
│   ├── http/
│   │   ├── HttpRequest.java      # Request model
│   │   ├── HttpRequestParser.java# Raw HTTP → object parser
│   │   ├── HttpResponse.java     # Response builder + wire formatter
│   │   ├── HttpStatus.java       # HTTP status code enum
│   │   └── MimeTypeResolver.java # Extension → MIME type mapping
│   └── routing/
│       ├── RouteHandler.java     # Functional interface for route lambdas
│       └── Router.java           # Route registry + dispatch
├── main/resources/static/        # Demo static assets (HTML/CSS/JS)
└── test/                         # Test suite
```

---

## Design Decisions

| Decision | Rationale |
|---|---|
| **No framework** | Demonstrates low-level understanding of TCP, HTTP, and I/O |
| **Fixed thread pool** | Avoids unbounded thread creation; `2×CPU` is a well-known starting point |
| **Blocking I/O** | Simpler than NIO for this scale; teaches fundamentals before async |
| **`@FunctionalInterface` routes** | Minimal ceremony for route handlers; enables clean lambda syntax |
| **SLF4J facade** | Industry-standard logging; easy to swap implementations |

---

## Code Highlights

**Server loop with graceful shutdown** (`TcpServer.java:32-53`):
```java
while (running) {
    Socket client = serverSocket.accept();
    ClientHandler handler = new ClientHandler(client, router);
    threadPool.submit(handler::handle);
}
```

**HTTP request parsing** (`HttpRequestParser.java:12-52`):
```java
String requestLine = reader.readLine();
String[] parts = requestLine.split(" ");
request.setMethod(parts[0]);
request.setPath(parts[1]);
request.setVersion(parts[2]);
```

**Lambda-based routing** (`App.java:40-52`):
```java
router.addRoute("GET", "/health", req -> {
    HttpResponse response = new HttpResponse(HttpStatus.OK);
    response.setHeader("Content-Type", "application/json");
    String body = "{\"status\":\"healthy\",\"timestamp\":\"" + LocalDateTime.now() + "\"}";
    response.setBody(body.getBytes(StandardCharsets.UTF_8));
    return response;
});
```

**Static file serving with wildcard routing** (`Router.java:21-53`):
```java
Path filePath = Path.of(staticDirectory, relativePath);
if (!Files.exists(filePath)) {
    return new HttpResponse(HttpStatus.NOT_FOUND);
}
byte[] fileBytes = Files.readAllBytes(filePath);
HttpResponse response = new HttpResponse(HttpStatus.OK);
response.setHeader("Content-Type", MimeTypeResolver.resolve(filename));
response.setBody(fileBytes);
```

---

## Roadmap

- [ ] HTTP/1.1 keep-alive connections
- [ ] Content-Type negotiation
- [ ] Binary file support (images, downloads)
- [ ] POST/PUT request body handling
- [ ] Middleware pipeline (logging, auth, CORS)
- [ ] HTTPS support (SSL/TLS)
- [ ] Request/response compression
- [ ] Full test coverage with integration tests
- [ ] NIO / non-blocking I/O variant

---

## License

MIT
