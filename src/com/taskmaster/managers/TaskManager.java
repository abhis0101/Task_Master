package com.taskmaster.managers;

import com.taskmaster.entities.Epic;
import com.taskmaster.entities.Subtask;
import com.taskmaster.entities.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Epic epic, Subtask subtask);
    void updateTask(int id, Task updatedTask);
    void updateEpic(int id, Epic updatedEpic);
    void updateSubtask(int id, Subtask updatedSubtask);
    ArrayList<Task> getTasksList();
    ArrayList<Subtask> subtaskList(int id);
    ArrayList<Epic> getEpicsList();
    ArrayList<Subtask> getSubtaskList();
    void deleteTaskById(int id);
    void deleteSubtaskById(int id);
    void deleteEpicById(int id);
    void printAllTasks();
    void deleteEverything();
    Task getTaskById (int id);
    Epic getEpicById (int id);
    Subtask getSubtaskById (int id);
    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubtasks();
    void printSubtasksByEpicId(int epicId);
    void updateEpicStatus(int epicId);
    List<Task> getHistory();
    Set<Task> getPrioritizedTasks();
}
