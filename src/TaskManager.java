import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private static int nextId = 0;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public void createTask(Task task) {
        nextId++;
        task.setId(nextId);
        if (!tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            System.out.println("Task with ID: " + task.getId() + " saved.");
        } else {
            System.out.println("Task with ID: " + task.getId() + " has been saved already.");
        }
    }
    public void createEpic(Epic epic) {
        nextId++;
        epic.setId(nextId);
        if (!epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            System.out.println("Epic with ID: " + epic.getId() + " saved.");
        } else {
            System.out.println("Epic with ID: " + epic.getId() + " has been saved already.");
        }
    }

    public void createSubtask(Epic epic, Subtask subtask) {
        nextId++;
        subtask.setId(nextId);
        subtask.setEpicId(epic.getId());
        if (epics.containsKey(epic.getId()) && !subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtasksId(subtask.getId());
            System.out.println("Subtask with ID: " + subtask.getId() + " saved.");
        } else {
            System.out.println("Epic not found. The subtask could not be created.");
        }
    }

    public void updateTask(int id, Task updatedTask) {
        if (tasks.containsKey(id)) {
            Task oldTask = tasks.get(id);
            oldTask.setTitle(updatedTask.getTitle());
            oldTask.setDescription(updatedTask.getDescription());
            oldTask.setTaskStatus(updatedTask.getTaskStatus());
            tasks.put(id, oldTask);
            System.out.println("Task with ID: " + id + " updated.");
        } else {
            System.out.println("Task with ID: " + id + " not found.");
        }
    }

    public void updateEpic(int id, Epic updatedEpic) {
        if (epics.containsKey(id)) {
            Epic oldEpic = epics.get(id);
            oldEpic.setTitle(updatedEpic.getTitle());
            oldEpic.setDescription(updatedEpic.getDescription());
            oldEpic.setTaskStatus(updatedEpic.getTaskStatus());
            epics.put(id, oldEpic);
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                if (entry.getValue().getEpicId() == id) {
                    Subtask subtask = entry.getValue();
                    subtask.setEpicId(id);
                    subtasks.put(subtask.getId(), subtask);
                }
            }
            System.out.println("Epic with ID: " + id + " updated.");
        } else {
            System.out.println("Epic with ID: " + id + " not found.");
        }
    }

    public void updateSubtask(int id, Subtask updatedSubtask) {
        if (subtasks.containsKey(id)) {
            Subtask oldSubtask = subtasks.get(id);
            oldSubtask.setTitle(updatedSubtask.getTitle());
            oldSubtask.setDescription(updatedSubtask.getDescription());
            oldSubtask.setTaskStatus(updatedSubtask.getTaskStatus());
            subtasks.put(id, oldSubtask);
            updateEpicStatus(oldSubtask.getEpicId());
            System.out.println("Subtask with ID: " + id + " updated.");
        } else {
            System.out.println("Subtask with ID: " + id + " not found.");
        }
    }

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

    public void clearAllSubtasksId() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
        }
    }
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        clearAllSubtasksId();
        System.out.println("All Tasks, Epics and Subtasks deleted.");
    }

    public void getById(int id) {
        if (tasks.containsKey(id)) {
            System.out.println(tasks.get(id));
        } else if (epics.containsKey(id)) {
            System.out.println(epics.get(id));
        } else if (subtasks.containsKey(id)) {
            System.out.println(subtasks.get(id));
        } else {
            System.out.println("Task, Epics and Subtasks with ID: " + id + " not found.");
        }
    }

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

    public void deleteById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Task with ID: " + id + " deleted.");
        } else if (epics.containsKey(id)) {
            epics.remove(id);
            clearAllSubtasksId();
            System.out.println("Epic with ID: " + id + " deleted.");
        } else if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            for (Epic epic : epics.values()){
                epic.removeSubtaskId(id);
            }
            System.out.println("Subtask with ID: " + id + " deleted.");
        } else {
            System.out.println("Task, Epic and Subtask with ID: " + id + " not found.");
        }
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        } List<Integer> subtasksId = epic.getSubtasksIds();
        if (subtasksId.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            int newCount = 0, inProgressCount = 0, doneCount = 0;
            for (int id : subtasksId) {
                Subtask subtask = subtasks.get(id);
                TaskStatus status = subtask.getTaskStatus();
                if (status == TaskStatus.NEW) {
                    newCount++;
                } else if (status == TaskStatus.IN_PROGRESS) {
                    inProgressCount++;
                } else {
                    doneCount++;
                }
            }
            if (doneCount == subtasksId.size()) {
                epic.setTaskStatus(TaskStatus.DONE);
            } else if (newCount == subtasksId.size()) {
                epic.setTaskStatus(TaskStatus.NEW);
            } else {
                epic.setTaskStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}

