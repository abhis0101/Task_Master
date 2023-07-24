package com.taskmaster.server.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.taskmaster.entities.Epic;
import com.taskmaster.entities.Subtask;
import com.taskmaster.entities.Task;
import com.taskmaster.managers.HistoryManager;
import com.taskmaster.managers.InMemoryHistoryManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class HistoryManagerAdapter extends TypeAdapter<HistoryManager> {
    public Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(File.class, new FileAdapter()).serializeNulls().create();

    @Override
    public void write(JsonWriter jsonWriter, HistoryManager historyManager) throws IOException {
        if (historyManager == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.beginObject();
            for (Task task : historyManager.getHistory()) {
                if (task instanceof Epic) {
                    jsonWriter.name("Epic" + task.getId());
                    jsonWriter.value(gson.toJson(task));
                } else if (task instanceof Subtask) {
                    jsonWriter.name("Subtask" + task.getId());
                    jsonWriter.value(gson.toJson(task));
                } else {
                    jsonWriter.name("Task" + task.getId());
                    jsonWriter.value(gson.toJson(task));
                }
            }
            jsonWriter.endObject();
        }
    }

    @Override
    public HistoryManager read(JsonReader jsonReader) throws IOException {
        HistoryManager manager;
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        } else {
            manager = new InMemoryHistoryManager();
            jsonReader.beginObject();
            String field = null;
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (token.equals(JsonToken.NAME)) {
                    field = jsonReader.nextName();
                }
                if (field == null) {
                    jsonReader.peek();
                } else if (field.contains("Epic")) {
                    jsonReader.peek();
                    manager.add(gson.fromJson(jsonReader.nextString(), Epic.class));
                } else if (field.contains("Subtask")) {
                    jsonReader.peek();
                    manager.add(gson.fromJson(jsonReader.nextString(), Subtask.class));
                } else {
                    jsonReader.peek();
                    manager.add(gson.fromJson(jsonReader.nextString(), Task.class));
                }
            }
        }
        jsonReader.endObject();
        return manager;
    }
}
