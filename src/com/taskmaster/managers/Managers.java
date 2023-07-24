package com.taskmaster.managers;

public class Managers {
    public static TaskManager getInMemoryTaskManager(){
        return new InMemoryTaskManager();
    }
    public static TaskManager getDefault(String url, String key) {
        return new HttpTaskManager(url, key);
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
