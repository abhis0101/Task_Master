package com.taskmaster.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data;

    public KVServer() throws IOException {
        this.data = new HashMap<>();
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) {

        try (h) {
            System.out.println("\n/load");
            if (!oAuth(h)) {
                System.out.println("Request not authorized, need parameter in query API_TOKEN with api-key value");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key to save empty. Key is specified in the path: /load/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                sendResponse(h, data.get(key));
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/load is waiting for a GET request, and received: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save(HttpExchange h) throws IOException {
        try (h) {
            System.out.println("\n/save");
            if (!oAuth(h)) {
                System.out.println("The request is not authorized, you need a parameter in the query API_TOKEN with the api-key value");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key to save empty. Key is specified in the path: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readRequest(h);
                if (value.isEmpty()) {
                    System.out.println("Value to save empty. Value is specified in the request body");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Key value " + key + " successfully updated!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save is waiting for a POST request, but received: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void register(HttpExchange h) throws IOException {
        try (h) {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendResponse(h, apiToken);
            } else {
                System.out.println("/register is waiting for a GET request, but received " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    public void start() {
        System.out.println("Starting the server on a port " + PORT);
        System.out.println("Open in browser http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    private boolean oAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    private String readRequest(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void sendResponse(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public void stop() {
        server.stop(1);
    }
}
