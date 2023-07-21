package com.taskmaster.managers;

import com.taskmaster.entities.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void remove (int id);
}
