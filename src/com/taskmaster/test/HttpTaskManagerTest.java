package com.taskmaster.test;

import com.taskmaster.managers.HttpTaskManager;
import com.taskmaster.server.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kvServer;

    @Override
    public HttpTaskManager createTaskManager() {
        return new HttpTaskManager("http://localhost:8078", "key");
    }

    @BeforeEach
    public void startServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void loadFromEmptyServer() {

        createAllTask();

        testManager.deleteEverything();

        HttpTaskManager emptyManager = HttpTaskManager.load("http://localhost:8078", "key");

        assertEquals(0, emptyManager.getTasksList().size());
        assertEquals(0, emptyManager.getSubtaskList().size());
        assertEquals(0, emptyManager.getEpicsList().size());
        assertEquals(0, emptyManager.getHistory().size());

        assertNotNull(emptyManager);
    }

    @Test
    public void loadEpicWithSubtasks() {

        createAllTask();

        testManager.getEpicById(3);
        testManager.getEpicById(4);

        HttpTaskManager epicManager = HttpTaskManager.load("http://localhost:8078", "key");

        assertEquals(3, epicManager.getEpicsList().size());
        assertEquals(2, epicManager.getHistory().size());
        assertEquals(2, epicManager.getEpicById(3).getSubtasksIds().size());
    }

    @Test
    public void loadWithEmptyHistory() {

        createAllTask();

        HttpTaskManager emptyHistoryManager = HttpTaskManager.load("http://localhost:8078", "key");

        assertEquals(2, emptyHistoryManager.getTasksList().size());
        assertEquals(3, emptyHistoryManager.getEpicsList().size());
        assertEquals(3, emptyHistoryManager.getSubtaskList().size());
        assertEquals(0, emptyHistoryManager.getHistory().size());
    }
}
