package com.taskmaster.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taskmaster.server.KVTaskClient;
import com.taskmaster.server.adapter.FileAdapter;
import com.taskmaster.server.adapter.HistoryManagerAdapter;
import com.taskmaster.server.adapter.LocalDateTimeAdapter;

import java.io.File;
import java.time.LocalDateTime;

public class HttpTaskManager extends FileBackedTasksManager {
    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(File.class, new FileAdapter())
            .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
            .serializeNulls().create();
    private final String key;
    private final String url;

    public HttpTaskManager(String url, String key) {
        this.url = url;
        this.key = key;
    }

    @Override
    public void save() {
        String manager = gson.toJson(this);
        new KVTaskClient(url).put(key, manager);
    }

    public static HttpTaskManager load(String url, String key) {
        String json = new KVTaskClient(url).load(key);
        if (json.isEmpty()) {
            return new HttpTaskManager(url, key);
        }
        return gson.fromJson(json, HttpTaskManager.class);
    }
}
