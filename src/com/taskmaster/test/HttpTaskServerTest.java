package com.taskmaster.test;

import com.google.gson.reflect.TypeToken;
import com.taskmaster.entities.*;
import com.taskmaster.server.HttpTaskServer;
import com.taskmaster.server.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.taskmaster.managers.HttpTaskManager.gson;
import static com.taskmaster.server.HttpTaskServer.GSON;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {

    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    Task task1;
    Task task2;
    Epic epic1;
    Epic epic2;
    Epic epic3;
    Subtask subTask1;
    Subtask subTask2;
    Subtask subTask3;
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    @BeforeEach
    public void startServers() throws IOException {
        httpTaskServer = new HttpTaskServer();
        kvServer = new KVServer();
        httpTaskServer.start();
        kvServer.start();
    }

    @BeforeEach
    public void allTasksForTests() {

        task1 = new Task(1, TaskTypes.TASK, "Buy flowers", TaskStatus.NEW, "Roses", 10,
                LocalDateTime.of(2023, 3, 1, 12, 0, 0));

        task2 = new Task(2, TaskTypes.TASK, "Order pizza", TaskStatus.NEW, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0));

        epic1 = new Epic(3, TaskTypes.EPIC, "Build a house", TaskStatus.NEW, "One-story house with a flat roof",
                10, LocalDateTime.of(2023, 3, 21, 12, 0, 0));

        epic2 = new Epic(4, TaskTypes.EPIC, "Feed the dog", TaskStatus.NEW, "Morning at 8:00",
                10, LocalDateTime.of(2023, 3, 21, 8, 0, 0));

        epic3 = new Epic(5, TaskTypes.EPIC, "Feed the Cat", TaskStatus.NEW, "Morning at 10:00",
                10, LocalDateTime.of(2023, 3, 21, 10, 0, 0));

        subTask1 = new Subtask(6, TaskTypes.SUBTASK, "Set the foundation", TaskStatus.DONE, "Strip foundation",
                10, LocalDateTime.of(2023, 3, 28, 12, 0, 0), 3);

        subTask2 = new Subtask(7, TaskTypes.SUBTASK, "Build walls", TaskStatus.NEW, "Wall material - red brick",
                10, LocalDateTime.of(2023, 3, 2, 15, 0, 0), 3);

        subTask3 = new Subtask(8, TaskTypes.SUBTASK, "Dig a landing hole", TaskStatus.NEW, "Pit depth - 1m",
                10, LocalDateTime.of(2023, 3, 25, 10, 0, 0), 4);
    }


    @AfterEach
    public void stopAllServers() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    public HttpRequest createGetRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().GET().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
    }

    public HttpRequest createDeleteRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().DELETE().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
    }

    public HttpResponse<String> addTaskToServer(Task task, String path) throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        String body = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        return client.send(request, handler);
    }



    @Test
    public void emptyTaskNotSavedInServer() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/task");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("Task is missing from the request body.", response.body());
    }


    @Test
    public void getTaskByIdFromServer() throws IOException, InterruptedException {

        addTaskToServer(task1, "/task");
        task1.setId(1);

        HttpResponse<String> response = client.send(createGetRequest("/task?id=1"), handler);
        Task task = GSON.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task1.getId(), task.getId());
        assertEquals(task1.getTitle(), task.getTitle());
        assertEquals(task1.getDescription(), task.getDescription());
        assertEquals(task1.getStatus(), task.getStatus());
        assertEquals(task1.getStartTime(), task.getStartTime());
        assertEquals(task1.getDuration(), task.getDuration());
    }

    @Test
    public void taskWithIdNotExistsInServer() throws IOException, InterruptedException {

        addTaskToServer(task1, "/task");

        HttpResponse<String> response = client.send(createGetRequest("/task?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Task not found.", response.body());
    }
    @Test
    public void addingTaskToServer() throws IOException, InterruptedException {

        HttpResponse<String> response = addTaskToServer(task1, "/task");

        assertEquals(201, response.statusCode());
        assertEquals("Task has been created.", response.body());
    }
    @Test
    public void addingEpicToServer() throws IOException, InterruptedException {

        HttpResponse<String> response = addTaskToServer(epic1, "/epic");

        assertEquals(201, response.statusCode());
        assertEquals("Epic has been created.", response.body());
    }

    @Test
    public void emptyEpicNotSavedInServer() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/epic");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("Epic is missing from the request body.", response.body());
    }
    @Test
    public void updateTaskInServer() throws IOException, InterruptedException {

        addTaskToServer(task1, "/task");
        task2.setId(0);

        HttpResponse<String> response = addTaskToServer(task2, "/task?id=0");

        assertEquals(201, response.statusCode());
        assertEquals("Task with 0 has been updated.", response.body());
    }
    @Test
    public void updateEpicInServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");

        HttpResponse<String> response = addTaskToServer(epic1, "/epic?id=1");

        assertEquals(201, response.statusCode());
        assertEquals("Epic with 1 has been updated.", response.body());
    }
    @Test
    public void updateSubtaskInServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        addTaskToServer(subTask1, "/subtask");

        HttpResponse<String> response = addTaskToServer(subTask2, "/subtask?id=1");

        assertEquals(201, response.statusCode());
        assertEquals("Subtask with 1 updated.", response.body());
    }
    @Test
    public void deleteTaskFromServer() throws IOException, InterruptedException {

        addTaskToServer(task1, "/task");

        HttpResponse<String> response = client.send(createDeleteRequest("/task?id=1"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Task with 1 has been deleted.", response.body());
    }
    @Test
    public void deleteEpicFromServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");

        HttpResponse<String> response = client.send(createDeleteRequest("/epic?id=1"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Epic with 1 has been deleted .", response.body());
    }
    @Test
    public void getAllTaskFromServer() throws IOException, InterruptedException {

        addTaskToServer(task1, "/task");
        addTaskToServer(task2, "/task");

        HttpResponse<String> response = client.send(createGetRequest("/task"), handler);
        List<Task> tasks = GSON.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(2, tasks.size());
        assertEquals(200, response.statusCode());
    }
    @Test
    public void getAllEpicsFromServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        addTaskToServer(epic2, "/epic");

        HttpResponse<String> response = client.send(createGetRequest("/epic"), handler);
        List<Epic> taskList = GSON.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(2, taskList.size());
        assertEquals(200, response.statusCode());
    }
    @Test
    public void getAllSubtasksFromServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        addTaskToServer(subTask1, "/subtask");
        addTaskToServer(subTask2, "/subtask");

        HttpResponse<String> response = client.send(createGetRequest("/subtask"), handler);

        assertEquals(200, response.statusCode());
    }
    @Test
    public void getEpicByIdFromServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        epic1.setId(1);

        HttpResponse<String> response = client.send(createGetRequest("/epic?id=1"), handler);
        Epic epic = GSON.fromJson(response.body(), Epic.class);

        assertEquals(epic1.getId(), epic.getId());
        assertEquals(epic1.getTitle(), epic.getTitle());
        assertEquals(epic1.getDescription(), epic.getDescription());
        assertEquals(epic1.getStatus(), epic.getStatus());
        assertEquals(epic1.getStartTime(), epic.getStartTime());
        assertEquals(epic1.getDuration(), epic.getDuration());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void epicWithIdNotExistsInServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");

        HttpResponse<String> response = client.send(createGetRequest("/epic?id=10"), handler);

        assertEquals("Epic not found.", response.body());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void addingSubtaskToServer() throws IOException, InterruptedException {

        addTaskToServer(task1, "/task");
        addTaskToServer(task2, "/task");
        addTaskToServer(epic1, "/epic");

        HttpResponse<String> response = addTaskToServer(subTask1, "/subtask");

        assertEquals(201, response.statusCode());
        assertEquals("Subtask has been created.", response.body());
    }

    @Test
    public void emptySubtaskNotSavedInServer() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("Subtask is missing from the request body.", response.body());
    }
    @Test
    public void deleteAllTasksAddedToServer() throws IOException, InterruptedException {

        addTaskToServer(task1, "/task");
        addTaskToServer(task2, "/task");

        HttpResponse<String> response = client.send(createDeleteRequest("/task"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("All Task have been deleted.", response.body());
    }
    @Test
    public void deleteAllEpicsAddedToServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        addTaskToServer(epic2, "/epic");

        HttpResponse<String> response = client.send(createDeleteRequest("/epic"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("All Epic has been deleted.", response.body());
    }

    @Test
    public void deleteAllSubtasksAddedToServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        addTaskToServer(subTask1, "/subtask");
        addTaskToServer(subTask2, "/subtask");

        HttpResponse<String> response = client.send(createDeleteRequest("/subtask"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("All Subtask has been deleted.", response.body());
    }


    @Test
    public void subtaskWithIdNotExistsInServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        addTaskToServer(subTask1, "/subtask");

        HttpResponse<String> response = client.send(createGetRequest("/subtask?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Subtask not found.", response.body());
    }

    @Test
    public void getSubtaskOfParticularEpicFromServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        addTaskToServer(subTask1, "/subtask");
        addTaskToServer(subTask2, "/subtask");

        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=1"), handler);

        assertEquals(200, response.statusCode());
    }

    @Test
    public void subtaskNotBelongsToEpicAddedToServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        addTaskToServer(subTask1, "/subtask");
        addTaskToServer(subTask2, "/subtask");

        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=3"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Epic not found.", response.body());
    }

    @Test
    public void getHistoryFromServer() throws IOException, InterruptedException {

        addTaskToServer(epic1, "/epic");
        addTaskToServer(subTask1, "/subtask");
        addTaskToServer(task1, "/task");
        addTaskToServer(task2, "/task");
        client.send(createGetRequest("/task?id=3"), handler);
        client.send(createGetRequest("/task?id=4"), handler);

        HttpResponse<String> history = client.send(createGetRequest("/history"), handler);

        assertEquals(200, history.statusCode());
    }

    @Test
    public void getPrioritizedTasksFromServer() throws IOException, InterruptedException {

        task1.setStartTime(LocalDateTime.parse("21:00 - 07.03.2019", FORMATTER));
        task2.setStartTime(LocalDateTime.parse("09:30 - 07.03.2019", FORMATTER));
        addTaskToServer(task1, "/task");
        addTaskToServer(task2, "/task");
        task2.setId(2);

        HttpResponse<String> prioritized = client.send(createGetRequest(""), handler);
        List<Task> tasks = GSON.fromJson(prioritized.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(2, tasks.size());
        assertEquals(task2.getId(), tasks.get(0).getId());
        assertEquals(task2.getTitle(), tasks.get(0).getTitle());
        assertEquals(task2.getDescription(), tasks.get(0).getDescription());
        assertEquals(task2.getType(), tasks.get(0).getType());
        assertEquals(task2.getDuration(), tasks.get(0).getDuration());
        assertEquals(200, prioritized.statusCode());
    }
}
