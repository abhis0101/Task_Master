package com.taskmaster.test;

import com.taskmaster.managers.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    @Override
    public InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}
