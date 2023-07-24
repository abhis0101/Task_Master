package com.taskmaster.managers;

import com.taskmaster.entities.*;
import com.taskmaster.exceptions.ManagerSaveException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;
    protected int managerId;

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public FileBackedTasksManager() {
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,duration,startTime,epic\n");

            for (Task task : getTasksList()) {
                bufferedWriter.write(toString(task) + "\n");

            }
            for (Epic epic : getEpicsList()) {
                bufferedWriter.write(toString(epic) + "\n");

            }
            for (Subtask subTask : getSubtaskList()) {
                bufferedWriter.write(toString(subTask) + "\n");

            }
            bufferedWriter.write("\n" + historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage(), e.getCause());
        }
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskTypes type = TaskTypes.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        long duration = Long.parseLong(parts[5]);
        LocalDateTime startTime = LocalDateTime.parse(parts[6], FORMATTER);

        Task task;

        if (type.equals(TaskTypes.TASK)) {
            task = new Task(id, type, title, status, description, duration, startTime);
        } else if (type.equals(TaskTypes.EPIC)) {
            task = new Epic(id, type, title, status, description, duration, startTime);
        } else {
            task = new Subtask(id, type, title, status, description, duration, startTime, Integer.parseInt(parts[7]));
        }

        return task;
    }

    static String historyToString(HistoryManager historyManager) {

        List<String> history = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            history.add(String.valueOf(task.getId()));
        }
        return String.join(",", history);
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] parts = value.split(",");
        for (String part : parts) {
            history.add(Integer.valueOf(part));
        }
        return history;
    }

    static void updateEpicWithSubtask(FileBackedTasksManager manager, Subtask subtask) {
        if (!manager.epics.isEmpty()) {
            Epic epicInSub = manager.epics.get(subtask.getEpicId());
            epicInSub.getSubtasksIds().add(subtask.getId());
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        List<String> taskList;
        Map<Integer, Task> taskMap = new HashMap<>();
        int id = 0;

        try {
            taskList = Files.readAllLines(Path.of(file.getAbsolutePath()));
            for (int i = 1; i < taskList.size(); i++) {
                String data = taskList.get(i);
                if (data.isEmpty()) {
                    break;
                }
                Task task = fromString(taskList.get(i));
                taskMap.put(task.getId(), task);

                switch (task.getType()) {
                    case TASK -> fileBackedTasksManager.tasks.put(task.getId(), task);
                    case EPIC -> {
                        Epic epic = (Epic) task;
                        fileBackedTasksManager.epics.put(epic.getId(), epic);
                    }
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        fileBackedTasksManager.subtasks.put(subtask.getId(), subtask);
                        updateEpicWithSubtask(fileBackedTasksManager, subtask);
                    }
                    default -> throw new IllegalArgumentException("" + task.getType());
                }
                if (task.getId() > id) {
                    id = task.getId();
                }
            }
            if (taskList.size() > 1) {
                String history = taskList.get(taskList.size() - 1);
                if (!history.isEmpty()) {
                    List<Integer> list = historyFromString(history);
                    for (Integer newId : list) {
                        fileBackedTasksManager.historyManager.add(taskMap.get(newId));
                    }
                }
            }
            fileBackedTasksManager.managerId = id;
            return fileBackedTasksManager;


        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void updateTask(int id, Task updatedTask) {
        super.updateTask(id, updatedTask);
        save();
    }

    @Override
    public void updateEpic(int id, Epic updatedEpic) {
        super.updateEpic(id, updatedEpic);

        save();
    }

    @Override
    public void updateSubtask(int id, Subtask updatedSubtask) {
        super.updateSubtask(id, updatedSubtask);
        save();
    }

    @Override
    public void deleteEverything() {
        super.deleteEverything();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subTask = super.getSubtaskById(id);
        save();
        return subTask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        super.createSubtask(epic, subtask);
        save();
    }

    @Override
    public void updatedEpicTimeAndDate(Epic epic) {
        super.updatedEpicTimeAndDate(epic);
        save();
    }

    @Override
    public void getEpicEndTime(int epicId) {
        super.getEpicEndTime(epicId);
        save();
    }


    public String toString(Task task) {
        return task.getTaskInfo();
    }

    public static void main(String[] args) {
        File test = new File("src/com/taskmaster/resources/data.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(test);

        Task task1 = new Task(1, TaskTypes.TASK, "Buy Flowers", TaskStatus.NEW, "Roses", 10,
                LocalDateTime.of(2023, 3, 1, 12, 0, 0));

        Task task2 = new Task(2, TaskTypes.TASK, "Order pizza", TaskStatus.NEW, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0));

        Epic epic1 = new Epic(3, TaskTypes.EPIC, "Build a house", TaskStatus.NEW, "One-story house with a flat roof",
                10, LocalDateTime.of(2023, 3, 21, 12, 0, 0));

        Epic epic2 = new Epic(4, TaskTypes.EPIC, "Feed the dog", TaskStatus.NEW, "Morning at 8:00",
                10, LocalDateTime.of(2023, 3, 21, 8, 0, 0));

        Epic epic3 = new Epic(5, TaskTypes.EPIC, "Feed the cat", TaskStatus.NEW, "Morning at 10:00",
                10, LocalDateTime.of(2023, 3, 21, 10, 0, 0));

        Subtask subTask1 = new Subtask(6, TaskTypes.SUBTASK, "Establish Foundation", TaskStatus.NEW, "Strip Foundation",
                10, LocalDateTime.of(2023, 3, 28, 12, 0, 0), 3);

        Subtask subTask2 = new Subtask(7, TaskTypes.SUBTASK, "Build Walls", TaskStatus.NEW, "Wall material - red brick",
                10, LocalDateTime.of(2023, 3, 2, 15, 0, 0), 3);

        Subtask subTask3 = new Subtask(8, TaskTypes.SUBTASK, "Dig a landing hole", TaskStatus.NEW, "Depth of the pit - 1m",
                10, LocalDateTime.of(2023, 3, 25, 10, 0, 0), 4);

        fileBackedTasksManager.createTask(task1);
        fileBackedTasksManager.createTask(task2);
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createEpic(epic2);
        fileBackedTasksManager.createEpic(epic3);
        fileBackedTasksManager.createSubtask(epic1, subTask1);
        fileBackedTasksManager.createSubtask(epic1, subTask2);
        fileBackedTasksManager.createSubtask(epic2, subTask3);


        fileBackedTasksManager.getTaskById(1);
        fileBackedTasksManager.getTaskById(2);
        fileBackedTasksManager.getEpicById(3);
        fileBackedTasksManager.getEpicById(4);
        fileBackedTasksManager.getEpicById(5);
        fileBackedTasksManager.getSubtaskById(6);
        fileBackedTasksManager.getSubtaskById(7);
        fileBackedTasksManager.getSubtaskById(8);


        FileBackedTasksManager fileBackedTasksManager2 = loadFromFile(new File("src/com/taskmaster/resources/data.csv"));


        System.out.println(fileBackedTasksManager2.getTaskById(1));
        System.out.println(fileBackedTasksManager2.getTaskById(2));
        fileBackedTasksManager2.getEpicEndTime(3);
        System.out.println(fileBackedTasksManager2.getEpicById(3));
        fileBackedTasksManager2.getEpicEndTime(4);
        System.out.println(fileBackedTasksManager2.getEpicById(4));

        System.out.println(fileBackedTasksManager2.getSubtaskById(6));
        System.out.println(fileBackedTasksManager2.getSubtaskById(7));
        System.out.println(fileBackedTasksManager2.getSubtaskById(8));


    }
}
