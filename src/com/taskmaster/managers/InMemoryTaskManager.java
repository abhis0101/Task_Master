package com.taskmaster.managers;

import com.taskmaster.entities.Epic;
import com.taskmaster.entities.Subtask;
import com.taskmaster.entities.Task;
import com.taskmaster.entities.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public int nextId = 0;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.nullsLast(Comparator.comparing(Task::getStartTime)));

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void createTask(Task task) {
        nextId++;
        task.setId(nextId);
        if (!tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            System.out.println("Task with ID: " + task.getId() + " created.");
        } else {
            System.out.println("Task with ID: " + task.getId() + " already exists.");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        nextId++;
        epic.setId(nextId);
        if (!epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            System.out.println("Epic with ID: " + epic.getId() + " created.");
        } else {
            System.out.println("Epic with ID: " + epic.getId() + " already exists.");
        }

    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        if (epic == null) {
            return;
        }
        nextId++;
        subtask.setId(nextId);
        subtask.setEpicId(epic.getId());
        if (epics.containsKey(epic.getId()) && !subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtasksId(subtask.getId());
            prioritizedTasks.add(subtask);
            updatedEpicTimeAndDate(epic);
            updateEpicStatus(epic.getId());
            System.out.println("Subtask with ID: " + subtask.getId() + " created.");
        } else {
            System.out.println("Epic not found. So Subtask couldn't be created.");
        }

    }

    @Override
    public void updateTask(int id, Task updatedTask) {
        if (updatedTask.getTitle() == null || updatedTask.getDescription() == null || updatedTask.getStatus() == null ||
                updatedTask.getStartTime() == null || updatedTask.getDuration() == 0 || updatedTask.getEndTime() == null) {
            return;
        }
        if (tasks.containsKey(id)) {
            Task oldTask = tasks.get(id);
            oldTask.setTitle(updatedTask.getTitle());
            oldTask.setDescription(updatedTask.getDescription());
            oldTask.setStatus(updatedTask.getStatus());
            oldTask.setStartTime(updatedTask.getStartTime());
            oldTask.setDuration(updatedTask.getDuration());
            tasks.remove(id);
            tasks.put(id, updatedTask);
            prioritizedTasks.removeIf(task1 -> task1.getId() == updatedTask.getId());
            prioritizedTasks.add(updatedTask);
            System.out.println("Task with ID: " + id + " updated.");
        } else {
            System.out.println("Task with ID: " + id + " not found.");
        }
    }

    @Override
    public void updateEpic(int id, Epic updatedEpic) {
        if (updatedEpic.getTitle() == null || updatedEpic.getDescription() == null || updatedEpic.getStatus() == null ||
                updatedEpic.getStartTime() == null || updatedEpic.getDuration() == 0 || updatedEpic.getEndTime() == null) {
            return;
        }
        if (epics.containsKey(id)) {
            Epic oldEpic = epics.get(id);
            oldEpic.setTitle(updatedEpic.getTitle());
            oldEpic.setDescription(updatedEpic.getDescription());
            oldEpic.setStatus(updatedEpic.getStatus());
            oldEpic.setDuration(updatedEpic.getDuration());
            oldEpic.setStartTime(updatedEpic.getStartTime());
            oldEpic.setEndTime(updatedEpic.getEndTime());
            epics.remove(id);
            epics.put(id, updatedEpic);
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                if (entry.getValue().getEpicId() == id) {
                    Subtask subtask = entry.getValue();
                    subtask.setEpicId(id);
                    subtasks.put(subtask.getId(), subtask);
                }
            }
            System.out.println("Epic with  ID: " + id + " updated.");
        } else {
            System.out.println("Epic with ID: " + id + " not found.");
        }
    }


    @Override
    public void updateSubtask(int id, Subtask updatedSubtask) {
        if (updatedSubtask.getTitle() == null || updatedSubtask.getDescription() == null || updatedSubtask.getStatus() == null ||
                updatedSubtask.getStartTime() == null || updatedSubtask.getDuration() == 0 || updatedSubtask.getEndTime() == null) {
            return;
        }
        if (subtasks.containsKey(id)) {
            Subtask oldSubtask = subtasks.get(id);
            oldSubtask.setTitle(updatedSubtask.getTitle());
            oldSubtask.setDescription(updatedSubtask.getDescription());
            oldSubtask.setStatus(updatedSubtask.getStatus());
            oldSubtask.setStartTime(updatedSubtask.getStartTime());
            oldSubtask.setDuration(updatedSubtask.getDuration());
            subtasks.remove(id);
            subtasks.put(id, updatedSubtask);
            updateEpicStatus(oldSubtask.getEpicId());
            updatedEpicTimeAndDate(epics.get(oldSubtask.getEpicId()));
            prioritizedTasks.removeIf(task1 -> task1.getId() == updatedSubtask.getId());
            prioritizedTasks.add(updatedSubtask);
            System.out.println("Subtask with ID: " + id + " updated.");
        } else {
            System.out.println("Subtask with ID: " + id + " not found.");
        }
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            prioritizedTasks.remove(task);
            historyManager.remove(id);
            tasks.remove(id);
            System.out.println("Task with ID: " + id + " deleted.");
        } else {
            System.out.println("Task with ID: " + id + " not found.");
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.getSubtasksIds().remove(Integer.valueOf(id));
            subtasks.remove(id);
            if (epic.getSubtasksIds().isEmpty()) {
                epic.setStartTime(epic.getStartTime());
                epic.setDuration(epic.getDuration());
                epic.setEndTime(epic.getEndTime());
            }
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
            updatedEpicTimeAndDate(epic);
            updateEpicStatus(epicId);
            System.out.println("Subtask with ID - " + id + " deleted!");
        } else {
            System.out.println("Subtask with ID: " + id + " not found.");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Integer> subtaskIdOfEpic = epic.getSubtasksIds();
            for (Integer subtaskId : subtaskIdOfEpic) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
                Subtask subTask = subtasks.get(id);
                prioritizedTasks.remove(subTask);
            }
            prioritizedTasks.remove(epic);
            historyManager.remove(id);
            epics.remove(id);
            System.out.println("Epic with ID: " + id + " deleted.");
        } else {
            System.out.println("Epic with ID: " + id + " not found.");
        }
    }

    @Override
    public void printAllTasks() {
        System.out.println("Tasks: ");
        for (Task task : tasks.values()) {
            System.out.println(" " + task);
        }
        System.out.println("Epics: ");
        for (Epic epic : epics.values()) {
            System.out.println(" " + epic);

        }
        System.out.println("Subtasks: ");
        for (Subtask subtask : subtasks.values()) {
            System.out.println(" " + subtask);
        }
    }

    @Override
    public void deleteEverything() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
        nextId = 0;
        System.out.println("All Tasks, Epics and Subtasks has been deleted.");
    }

    public ArrayList<Subtask> subtaskList(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (int subtaskIdNumber : epic.getSubtasksIds()) {
            Subtask subtask = subtasks.get(subtaskIdNumber);
            listSubtasks.add(subtask);
        }
        return listSubtasks;
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Tasks with ID: " + id + " not found.");
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);

    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Epic with ID: " + id + " not found.");
        }
        historyManager.add(epics.get(id));
        return epics.get(id);

    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Subtask with ID: " + id + " not found.");
        }
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);

    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
        nextId = 0;
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            prioritizedTasks.remove(epic);
            historyManager.remove(epic.getId());
            epic.getSubtasksIds().clear();
        }
        epics.clear();

        for (Subtask subTask : subtasks.values()) {
            prioritizedTasks.remove(subTask);
            historyManager.remove(subTask.getEpicId());
        }
        subtasks.clear();
        nextId = 0;
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask sub : subtasks.values()) {
            prioritizedTasks.remove(sub);
            historyManager.remove(sub.getId());
            Subtask subtask = subtasks.get(sub.getId());
            if (subtask != null) {
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.getSubtasksIds().clear();
                    updateEpicStatus(epic.getId());
                    updatedEpicTimeAndDate(epic);
                }
            }
        }
        subtasks.clear();
        nextId = 0;
    }

    @Override
    public void printSubtasksByEpicId(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            for (int subtaskId : epic.getSubtasksIds()) {
                System.out.println(subtasks.get(subtaskId));
            }
        } else {
            System.out.println("Epic with ID: " + epicId + " not found.");
        }
    }


    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Integer> subtasksId = epic.getSubtasksIds();
        if (subtasksId.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            int newCount = 0, doneCount = 0;
            for (int id : subtasksId) {
                Subtask subtask = subtasks.get(id);
                TaskStatus status = subtask.getStatus();
                if (status == TaskStatus.NEW) {
                    newCount++;
                } else {
                    doneCount++;
                }
            }
            if (doneCount == subtasksId.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else if (newCount == subtasksId.size()) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void updatedEpicTimeAndDate(Epic epic) {
        getEpicStartTime(epic.getId());
        getEpicDuration(epic.getId());
        getEpicEndTime(epic.getId());
    }

    public void getEpicDuration(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subTasksIds = epic.getSubtasksIds();
        long duration = subTasksIds.stream()
                .mapToLong(id -> subtasks.get(id).getDuration())
                .sum();
        epic.setDuration(duration);
    }


    public void getEpicStartTime(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subTasksIds = epic.getSubtasksIds();
        if (!subTasksIds.isEmpty()) {
            LocalDateTime epicStartTime = subTasksIds.stream()
                    .map(subtasks::get)
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .get();
            epic.setStartTime(epicStartTime);
        }
    }


    public void getEpicEndTime(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subTasksId = epic.getSubtasksIds();
        if (subTasksId.size() > 0) {
            LocalDateTime epicEndTime = subTasksId.stream()
                    .map(subtasks::get)
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .get();
            epic.setEndTime(epicEndTime);
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}
