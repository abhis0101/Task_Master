package com.taskmaster.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.taskmaster.entities.Epic;
import com.taskmaster.entities.Subtask;
import com.taskmaster.entities.Task;
import com.taskmaster.managers.HistoryManager;
import com.taskmaster.managers.Managers;
import com.taskmaster.managers.TaskManager;
import com.taskmaster.server.adapter.FileAdapter;
import com.taskmaster.server.adapter.HistoryManagerAdapter;
import com.taskmaster.server.adapter.LocalDateTimeAdapter;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager = Managers.getDefault("http://localhost:8078", "key");
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(File.class, new FileAdapter())
            .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
            .serializeNulls().create();

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handle);
    }

    public void handle(HttpExchange exchange) throws IOException {
        String response;
        String path = exchange.getRequestURI().getPath();
        String param = exchange.getRequestURI().getQuery();
        switch (path) {
            case "/tasks/task" -> handleTask(exchange);
            case "/tasks/subtask" -> handleSubtask(exchange);
            case "/tasks/epic" -> handleEpic(exchange);
            case "/tasks/subtask/epic" -> {
                int id = Integer.parseInt(param.split("=")[1]);
                List<Subtask> subtasks = manager.subtaskList(id);
                if (subtasks == null) {
                    exchange.sendResponseHeaders(404, 0);
                    response = "Epic not found.";
                } else {
                    response = GSON.toJson(subtasks);
                    exchange.sendResponseHeaders(200, 0);
                }
                sendResponse(exchange, response);
                exchange.close();
            }
            case "/tasks/history" -> {
                response = GSON.toJson(manager.getHistory());
                exchange.sendResponseHeaders(200, 0);
                sendResponse(exchange, response);
                exchange.close();
            }
            case "/tasks" -> {
                response = GSON.toJson(manager.getPrioritizedTasks());
                exchange.sendResponseHeaders(200, 0);
                sendResponse(exchange, response);
                exchange.close();
            }
        }
    }

    private void handleTask(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET" -> {
                response = handleTaskGet(exchange);
                sendResponse(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleTaskPost(exchange);
                sendResponse(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleTaskDelete(exchange);
                sendResponse(exchange, response);
                exchange.close();
            }
        }
    }

    private void handleEpic(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET" -> {
                response = handleEpicGet(exchange);
                sendResponse(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleEpicPost(exchange);
                sendResponse(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleEpicDelete(exchange);
                sendResponse(exchange, response);
                exchange.close();
            }
        }
    }

    private void handleSubtask(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET" -> {
                response = handleSubtaskGet(exchange);
                sendResponse(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleSubtaskPost(exchange);
                sendResponse(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleSubtaskDelete(exchange);
                sendResponse(exchange, response);
                exchange.close();
            }
        }
    }

    private String handleTaskGet(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = GSON.toJson(manager.getTasksList());
            h.sendResponseHeaders(200, 0);
        } else {
            Task task = manager.getTaskById(id);
            if (task == null) {
                h.sendResponseHeaders(404, 0);
                response = "Task not found.";
            } else {
                response = GSON.toJson(task);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleTaskPost(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readRequest(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "Task is missing from the request body.";
        } else {
            Task task = GSON.fromJson(body, Task.class);
            if (param == null) {
                manager.createTask(task);
                h.sendResponseHeaders(201, 0);
                response = "Task has been created.";
            } else {
                task.setId(id);
                manager.updateTask(id, task);
                h.sendResponseHeaders(201, 0);
                response = "Task with " + id + " has been updated.";
            }
        }
        return response;
    }


    private String handleTaskDelete(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.deleteAllTasks();
            h.sendResponseHeaders(200, 0);
            response = "All Task have been deleted.";
        } else {
            manager.deleteTaskById(id);
            h.sendResponseHeaders(200, 0);
            response = "Task with " + id + " has been deleted.";
        }
        return response;
    }


    private String handleEpicGet(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = GSON.toJson(manager.getEpicsList());
            h.sendResponseHeaders(200, 0);
        } else {
            Epic epic = manager.getEpicById(id);
            if (epic == null) {
                h.sendResponseHeaders(404, 0);
                response = "Epic not found.";
            } else {
                response = GSON.toJson(epic);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleEpicPost(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readRequest(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "Epic is missing from the request body.";
        } else {
            Epic epic = GSON.fromJson(body, Epic.class);
            if (param == null) {
                manager.createEpic(epic);
                h.sendResponseHeaders(201, 0);
                response = "Epic has been created.";
            } else {
                epic.setId(id);
                manager.updateEpic(id, epic);
                h.sendResponseHeaders(201, 0);
                response = "Epic with " + id + " has been updated.";
            }
        }
        return response;
    }


    private String handleEpicDelete(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        if (param == null) {
            manager.deleteAllEpics();
            h.sendResponseHeaders(200, 0);
            response = "All Epic has been deleted.";
        } else {
            int id = Integer.parseInt(param.split("=")[1]);
            manager.deleteEpicById(id);
            h.sendResponseHeaders(200, 0);
            response = "Epic with " + id + " has been deleted .";
        }
        return response;
    }


    private String handleSubtaskGet(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = GSON.toJson(manager.getSubtaskList());
            h.sendResponseHeaders(200, 0);
        } else {
            Subtask subtask = manager.getSubtaskById(id);
            if (subtask == null) {
                h.sendResponseHeaders(404, 0);
                response = "Subtask not found.";
            } else {
                response = GSON.toJson(subtask);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleSubtaskPost(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readRequest(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "Subtask is missing from the request body.";
        } else {
            Subtask subtask = GSON.fromJson(body, Subtask.class);
            if (param == null) {
                int epicId = subtask.getEpicId();
                Epic epic = manager.getEpicById(epicId);
                if (epic == null) {
                    h.sendResponseHeaders(400, 0);
                    response = "Epic with ID: " + epicId + " not found.So subtask cannot be created.";
                } else {
                    manager.createSubtask(epic, subtask);
                    h.sendResponseHeaders(201, 0);
                    response = "Subtask has been created.";
                }
            } else {
                subtask.setId(id);
                manager.updateSubtask(id, subtask);
                h.sendResponseHeaders(201, 0);
                response = "Subtask with " + id + " updated.";
            }
        }
        return response;
    }

    private String handleSubtaskDelete(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.deleteAllSubtasks();
            h.sendResponseHeaders(200, 0);
            response = "All Subtask has been deleted.";
        } else {
            manager.deleteSubtaskById(id);
            h.sendResponseHeaders(200, 0);
            response = "Subtask with " + id + " deleted.";
        }
        return response;
    }


    public void start() {
        System.out.println("Starting the server on a port " + PORT);
        System.out.println("Link in browser http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private String readRequest(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void sendResponse(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseBody().write(resp);
    }
}
