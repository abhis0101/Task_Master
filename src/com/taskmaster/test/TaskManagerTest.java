package com.taskmaster.test;

import com.taskmaster.entities.*;
import com.taskmaster.managers.HistoryManager;
import com.taskmaster.managers.InMemoryHistoryManager;
import com.taskmaster.managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static com.taskmaster.entities.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> {
    public T testManager;

    abstract T createTaskManager();

    protected Task task1;
    protected Task task2;
    protected Epic epic1;
    protected Epic epic2;
    protected Epic epic3;
    protected Subtask subTask1;
    protected Subtask subTask2;
    protected Subtask subTask3;


    @BeforeEach
    public void allTasksForTest() {
        testManager = createTaskManager();
        task1 = new Task(1, TaskTypes.TASK, "Buy Flowers", NEW, "Roses", 10,
                LocalDateTime.of(2023, 3, 1, 12, 0, 0));

        task2 = new Task(2, TaskTypes.TASK, "Order Pizza", NEW, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0));

        epic1 = new Epic(3, TaskTypes.EPIC, "Build a house", NEW, "One-story house with a flat roof",
                10, LocalDateTime.of(2023, 3, 21, 12, 0, 0));

        epic2 = new Epic(4, TaskTypes.EPIC, "Feed the Dog", NEW, "Morning at 8:00",
                10, LocalDateTime.of(2023, 3, 21, 8, 0, 0));

        epic3 = new Epic(5, TaskTypes.EPIC, "Feed the Cat", NEW, "Morning at 10:00",
                10, LocalDateTime.of(2023, 3, 21, 10, 0, 0));

        subTask1 = new Subtask(6, TaskTypes.SUBTASK, "Set the foundation", DONE, "Strip foundation",
                10, LocalDateTime.of(2023, 3, 28, 12, 0, 0), 3);

        subTask2 = new Subtask(7, TaskTypes.SUBTASK, "Build walls", NEW, "Wall material - red brick",
                10, LocalDateTime.of(2023, 3, 2, 15, 0, 0), 3);

        subTask3 = new Subtask(8, TaskTypes.SUBTASK, "Dig a landing hole", NEW, "Pit depth - 1m",
                10, LocalDateTime.of(2023, 3, 25, 10, 0, 0), 4);
    }


    public void createAllTask() {
        testManager.createTask(task1);
        testManager.createTask(task2);
        testManager.createEpic(epic1);
        testManager.createEpic(epic2);
        testManager.createEpic(epic3);
        testManager.createSubtask(epic1, subTask1);
        testManager.createSubtask(epic1, subTask2);
        testManager.createSubtask(epic2, subTask3);

    }

    public void clearFile(){
        testManager.deleteEverything();
    }

    @Test
    public void getTaskById() {

        createAllTask();
        //With standard behavior
        assertEquals(testManager.getTaskById(1).getType(), task1.getType());
        assertEquals(testManager.getTaskById(1).getTitle(), task1.getTitle());
        assertEquals(testManager.getTaskById(1).getStatus(), task1.getStatus());
        assertEquals(testManager.getTaskById(1).getDescription(), task1.getDescription());
        assertEquals(testManager.getTaskById(1).getDuration(), task1.getDuration());
        assertEquals(testManager.getTaskById(1).getStartTime(), task1.getStartTime());

        //With invalid task ID (empty and/or non-existent ID)
        assertNull(testManager.getTaskById(100));

        // With an empty list
        testManager.deleteAllTasks();
        assertNull(testManager.getTaskById(2));

    }

    @Test
    public void getEpicById() {

        createAllTask();
        //With standard behavior
        assertEquals(testManager.getEpicById(3).getType(), epic1.getType());
        assertEquals(testManager.getEpicById(3).getTitle(), epic1.getTitle());
        assertEquals(testManager.getEpicById(3).getStatus(), epic1.getStatus());
        assertEquals(testManager.getEpicById(3).getDescription(), epic1.getDescription());
        assertEquals(testManager.getEpicById(3).getDuration(), epic1.getDuration());
        assertEquals(testManager.getEpicById(3).getStartTime(), epic1.getStartTime());

        //With invalid task ID (empty and/or non-existent ID)
        assertNull(testManager.getEpicById(100));

        // With an empty list
        testManager.deleteAllEpics();
        assertNull(testManager.getEpicById(3));

    }

    @Test
    public void getSubtaskById() {

        createAllTask();
        //With standard behavior
        assertEquals(testManager.getSubtaskById(6).getType(), subTask1.getType());
        assertEquals(testManager.getSubtaskById(6).getTitle(), subTask1.getTitle());
        assertEquals(testManager.getSubtaskById(6).getStatus(), subTask1.getStatus());
        assertEquals(testManager.getSubtaskById(6).getDescription(), subTask1.getDescription());
        assertEquals(testManager.getSubtaskById(6).getDuration(), subTask1.getDuration());
        assertEquals(testManager.getSubtaskById(6).getStartTime(), subTask1.getStartTime());

        assertEquals(testManager.getEpicById(3).getId(), subTask1.getEpicId());
        Subtask testSubtask = new Subtask(9, TaskTypes.SUBTASK, "Order Pizza", NEW, "Margarita",
                10, LocalDateTime.of(2023, 3, 25, 10, 0, 0), 5);
        testManager.createSubtask(epic3, testSubtask);
        assertEquals(testManager.getEpicById(5).getStatus(), testSubtask.getStatus());
        assertEquals(testManager.getEpicById(5).getDuration(), testSubtask.getDuration());
        assertEquals(testManager.getEpicById(5).getStartTime(), testSubtask.getStartTime());


        //With invalid task ID (empty and/or non-existent ID)
        assertNull(testManager.getSubtaskById(100));

        // With an empty list
        testManager.deleteAllSubtasks();
        assertNull(testManager.getSubtaskById(6));

    }

    @Test
    void deleteTaskById() {

        createAllTask();

        //With invalid task ID (empty and/or non-existent ID)
        testManager.deleteTaskById(10);
        assertEquals(2, testManager.getTasksList().size());

        //With standard behavior
        testManager.deleteTaskById(task1.getId());
        testManager.deleteTaskById(task2.getId());
        assertEquals(0, testManager.getTasksList().size());

        // With an empty list
        testManager.deleteTaskById(100);
        clearFile();

    }

    @Test
    void deleteEpicById() {

        createAllTask();
        //With invalid task ID (empty and/or non-existent ID)
        testManager.deleteEpicById(12);
        assertEquals(3, testManager.getEpicsList().size());

        //With standard behavior
        testManager.deleteEpicById(epic1.getId());
        testManager.deleteEpicById(epic2.getId());
        testManager.deleteEpicById(epic3.getId());
        assertEquals(0, testManager.getEpicsList().size());

        // With an empty list
        testManager.deleteEpicById(100);
        clearFile();

    }


    @Test
    void deleteSubtaskById() {

        createAllTask();
        //With invalid task ID (empty and/or non-existent ID)
        testManager.deleteSubtaskById(10);
        assertEquals(3, testManager.getSubtaskList().size());

        //With standard behavior
        testManager.deleteSubtaskById(subTask1.getId());
        testManager.deleteSubtaskById(subTask2.getId());
        testManager.deleteSubtaskById(subTask3.getId());
        assertEquals(0, testManager.getSubtaskList().size());

        //Check epic status after deleting all subtasks
        assertEquals(testManager.getEpicById(5).getStatus(), NEW);
        assertEquals(testManager.getEpicById(3).getStatus(), NEW);

        // Subtask does not exist
        testManager.deleteSubtaskById(100);
        clearFile();

    }

    @Test
    void manipulatingTasksListTest() {

        createAllTask();
        //With standard behavior
        List<Task> testList1 = new ArrayList<>(List.of(task1, task2));
        List<Task> testList2 = testManager.getTasksList();
        assertEquals(testList1, testList2);

        //With invalid ID (empty and/or non-existent ID)
        testManager.getTaskById(100);
        testManager.getTaskById(200);

        //Check for an empty list of tasks
        testManager.deleteAllTasks();
        assertEquals(0, testManager.getTasksList().size());

    }

    @Test
    void manipulatingEpicsListTest() {

        createAllTask();
        //With standard behavior
        List<Epic> testList1 = new ArrayList<>(List.of(epic1, epic2, epic3));
        List<Epic> testList2 = testManager.getEpicsList();
        assertEquals(testList1, testList2);

        //With invalid ID (empty and/or non-existent ID)
        testManager.getEpicById(100);
        testManager.getEpicById(200);

        //Check if the list of epics and subtasks is empty
        testManager.deleteAllEpics();
        assertEquals(0,testManager.getEpicsList().size());
        assertEquals(0,testManager.getSubtaskList().size());

    }

    @Test
    void manipulatingSubtaskListTest() {

        createAllTask();
        //With standard behavior
        List<Subtask> testList1 = new ArrayList<>(List.of(subTask1, subTask2, subTask3));
        List<Subtask> testList2 = testManager.getSubtaskList();
        assertEquals(testList1, testList2);

        //With invalid ID (empty and/or non-existent ID)
        testManager.getSubtaskById(100);
        testManager.getSubtaskById(200);

        //Check if the list of subtasks is empty
        testManager.deleteAllSubtasks();
        assertEquals(0, testManager.getSubtaskList().size());

        //Check epic status after deleting subtasks
        assertEquals(NEW,testManager.getEpicById(epic1.getId()).getStatus());
        assertEquals(NEW,testManager.getEpicById(epic2.getId()).getStatus());


    }

    @Test
    void updatingTaskWithNewTask() {

        testManager.createTask(task1);
        //With standard behavior
        Task testTask = new Task(2, TaskTypes.TASK, "Order Pizza", NEW, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0));

        testManager.updateTask(1, testTask);
        assertEquals(testManager.getTaskById(1).getId(), testTask.getId());
        assertEquals(testManager.getTaskById(1).getTitle(), testTask.getTitle());
        assertEquals(testManager.getTaskById(1).getType(), testTask.getType());
        assertEquals(testManager.getTaskById(1).getStatus(), testTask.getStatus());
        assertEquals(testManager.getTaskById(1).getDescription(), testTask.getDescription());
        assertEquals(testManager.getTaskById(1).getStartTime(), testTask.getStartTime());
        assertEquals(testManager.getTaskById(1).getDuration(), testTask.getDuration());

        //With invalid ID (empty and/or non-existent ID)
        assertNull(testManager.getTaskById(100));
        testManager.updateTask(100,testTask);

        //Update non-existent task
        testManager.deleteAllTasks();
        testManager.updateTask(1,testTask);
    }

    @Test
    void updatingEpicWithNewEpic() {

        testManager.createEpic(epic1);
        Epic testEpic = new Epic(2, TaskTypes.EPIC, "Order Pizza", NEW, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0));

        testManager.createEpic(testEpic);
        testManager.createSubtask(testEpic,subTask3);

        testManager.updateEpic(1, testEpic);
        assertEquals(testManager.getEpicById(1).getId() , testEpic.getId());
        assertEquals(testManager.getEpicById(1).getTitle(), testEpic.getTitle());
        assertEquals(testManager.getEpicById(1).getType(), testEpic.getType());
        assertEquals(testManager.getEpicById(1).getStatus(), testEpic.getStatus());
        assertEquals(testManager.getEpicById(1).getDescription(), testEpic.getDescription());
        assertEquals(testManager.getEpicById(1).getStartTime(), testEpic.getStartTime());
        assertEquals(testManager.getEpicById(1).getDuration(), testEpic.getDuration());

        //With invalid ID (empty and/or non-existent ID)
        assertNull(testManager.getEpicById(100));
        testManager.updateEpic(100,testEpic);

        //Update non-existent task
        testManager.deleteAllEpics();
        testManager.updateEpic(5,testEpic);
    }

    @Test
    void updatingSubtaskWithNewSubtask() {

        createAllTask();
        Subtask testSubTask = new Subtask(9, TaskTypes.SUBTASK, "Build Walls", NEW, "Wall material - red brick", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 3);

        testManager.updateSubtask(6, testSubTask);
        assertEquals(testManager.getSubtaskById(6).getId(), testSubTask.getId());
        assertEquals(testManager.getSubtaskById(6).getTitle(), testSubTask.getTitle());
        assertEquals(testManager.getSubtaskById(6).getType(), testSubTask.getType());
        assertEquals(testManager.getSubtaskById(6).getStatus(), testSubTask.getStatus());
        assertEquals(testManager.getSubtaskById(6).getDescription(), testSubTask.getDescription());
        assertEquals(testManager.getSubtaskById(6).getStartTime(), testSubTask.getStartTime());
        assertEquals(testManager.getSubtaskById(6).getDuration(), testSubTask.getDuration());
        assertEquals(testManager.getSubtaskById(6).getEpicId(), testSubTask.getEpicId());

        //Epic status changes if subtask statuses change
        assertEquals(NEW,testManager.getEpicById(3).getStatus());
        Subtask testSubTask2 = new Subtask(9, TaskTypes.SUBTASK, "Order Pizza", DONE, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 3);
        testManager.createSubtask(epic1,testSubTask2);
        testManager.updateTask(6,testSubTask2);
        assertEquals(IN_PROGRESS,testManager.getEpicById(3).getStatus());

        //With invalid ID (empty and/or non-existent ID)
        assertNull(testManager.getSubtaskById(100));
        testManager.updateSubtask(100,testSubTask);

        //Update non-existent subtask
        testManager.deleteAllSubtasks();
        testManager.updateSubtask(6,testSubTask);
    }


    @Test
    void createNewTask() {

        //With standard behavior
        Task testTask = new Task(1, TaskTypes.TASK, "Buy Flowers", NEW, "Roses", 10,
                LocalDateTime.of(2023, 3, 1, 12, 0, 0));

        testManager.createTask(testTask);
        assertEquals(testManager.getTaskById(1).getTitle(), testTask.getTitle());
        assertEquals(testManager.getTaskById(1).getType(), testTask.getType());
        assertEquals(testManager.getTaskById(1).getStatus(), testTask.getStatus());
        assertEquals(testManager.getTaskById(1).getDescription(), testTask.getDescription());
        assertEquals(testManager.getTaskById(1).getStartTime(), testTask.getStartTime());
        assertEquals(testManager.getTaskById(1).getDuration(), testTask.getDuration());

        //Check for an empty task list
        testManager.deleteAllTasks();
        assertEquals(0,testManager.getTasksList().size());

    }

    @Test
    void createNewEpic() {

        //With standard behavior
        Epic testEpic = new Epic(4, TaskTypes.EPIC, "Feed the Cat", NEW, "Morning at 10:00",
                10, LocalDateTime.of(2023, 3, 21, 10, 0, 0));
        testManager.createEpic(testEpic);
        assertEquals(testManager.getEpicById(1).getType(), epic3.getType());
        assertEquals(testManager.getEpicById(1).getTitle(), epic3.getTitle());
        assertEquals(testManager.getEpicById(1).getStatus(), epic3.getStatus());
        assertEquals(testManager.getEpicById(1).getDescription(), epic3.getDescription());
        assertEquals(testManager.getEpicById(1).getDuration(), epic3.getDuration());
        assertEquals(testManager.getEpicById(1).getStartTime(), epic3.getStartTime());

        //Check for an empty list of epics
        testManager.deleteAllEpics();
        assertEquals(0,testManager.getEpicsList().size());
        assertEquals(0,testManager.getSubtaskList().size());

    }

    @Test
    void createNewSubtask() {

        createAllTask();
        Subtask testSubtask = new Subtask(9, TaskTypes.SUBTASK, "Build walls", NEW, "Wall material - red brick",
                10, LocalDateTime.of(2023, 3, 2, 15, 0, 0), 6);

        testManager.createSubtask(epic3,testSubtask);
        assertEquals(testManager.getSubtaskById(9).getTitle(), testSubtask.getTitle());
        assertEquals(testManager.getSubtaskById(9).getType(), testSubtask.getType());
        assertEquals(testManager.getSubtaskById(9).getStatus(), testSubtask.getStatus());
        assertEquals(testManager.getSubtaskById(9).getDescription(), testSubtask.getDescription());
        assertEquals(testManager.getSubtaskById(9).getStartTime(), testSubtask.getStartTime());
        assertEquals(testManager.getSubtaskById(9).getDuration(), testSubtask.getDuration());

        //Check if the epic contains the same values as its only subtask
        assertEquals(testManager.getSubtaskById(9).getStatus(), testManager.getEpicById(5).getStatus());
        assertEquals(testManager.getSubtaskById(9).getStartTime(), testManager.getEpicById(5).getStartTime());
        assertEquals(testManager.getSubtaskById(9).getDuration(), testManager.getEpicById(5).getDuration());
        assertEquals(testManager.getSubtaskById(9).getEndTime(), testManager.getEpicById(5).getEndTime());

        //Check if subtasks are removed when an epic is deleted
        testManager.deleteEpicById(5);
        assertNull(testManager.getSubtaskById(9));

        clearFile();
    }

    @Test
    void updateEpicStatusIfAllSubtasksNew() {
        createAllTask();
        //With standard behavior
        Subtask testSubTask1 = new Subtask(9, TaskTypes.SUBTASK, "Order Pizza", NEW, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 5);
        testManager.createSubtask(epic3, testSubTask1);

        Subtask testSubTask2 = new Subtask(10, TaskTypes.SUBTASK, "Buy Flowers", NEW, "Roses", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 5);
        testManager.createSubtask(epic3, testSubTask2);

        testManager.updateEpicStatus(epic3.getId());
        assertEquals(NEW, testManager.getEpicById(5).getStatus());

        //With invalid ID (empty and/or non-existent ID)
        assertNull(testManager.getEpicById(100));
        testManager.updateEpicStatus(100);

        //Remove all subtasks and check the status of epics
        testManager.deleteAllSubtasks();
        assertEquals(NEW,epic1.getStatus());
        assertEquals(NEW,epic2.getStatus());
        assertEquals(NEW,epic3.getStatus());

    }

    @Test
    void updateEpicStatusIfAllSubtasksDone() {

        createAllTask();
        //With standard behavior
        Subtask testSubTask1 = new Subtask(9, TaskTypes.SUBTASK, "Order Pizza", DONE, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 5);
        testManager.createSubtask(epic3, testSubTask1);

        Subtask testSubTask2 = new Subtask(10, TaskTypes.SUBTASK, "Buy Flowers", DONE, "Roses", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 5);
        testManager.createSubtask(epic3, testSubTask2);

        testManager.updateEpicStatus(epic3.getId());
        assertEquals(DONE, testManager.getEpicById(5).getStatus());

        //With invalid ID (empty and/or non-existent ID)
        assertNull(testManager.getEpicById(100));
        testManager.updateEpicStatus(100);

        //Remove all subtasks and check the status of epics
        testManager.deleteAllSubtasks();
        assertEquals(NEW,epic1.getStatus());
        assertEquals(NEW,epic2.getStatus());
        assertEquals(NEW,epic3.getStatus());
    }

    @Test
    void updateEpicStatusIfAllSubtasksDoneAndNew() {

        createAllTask();
        Subtask testSubTask1 = new Subtask(9, TaskTypes.SUBTASK, "Order Pizza", NEW, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 5);
        testManager.createSubtask(epic3, testSubTask1);

        Subtask testSubTask2 = new Subtask(10, TaskTypes.SUBTASK, "Buy Flowers", DONE, "Roses", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 5);
        testManager.createSubtask(epic3, testSubTask2);

        testManager.updateEpicStatus(epic3.getId());
        assertEquals(IN_PROGRESS, testManager.getEpicById(5).getStatus());

        //With invalid ID (empty and/or non-existent ID)
        assertNull(testManager.getEpicById(100));
        testManager.updateEpicStatus(100);

        //Remove all subtasks and check the status of epics
        testManager.deleteAllSubtasks();
        assertEquals(NEW,epic1.getStatus());
        assertEquals(NEW,epic2.getStatus());
        assertEquals(NEW,epic3.getStatus());

    }

    @Test
    void updateEpicStatusIfAllSubtasksNewAndInProgress() {

        createAllTask();
        Subtask testSubTask1 = new Subtask(9, TaskTypes.SUBTASK, "Order Pizza", NEW, "Margarita", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 5);
        testManager.createSubtask(epic3, testSubTask1);

        Subtask testSubTask2 = new Subtask(10, TaskTypes.SUBTASK, "Buy Flowers", IN_PROGRESS, "Roses", 15,
                LocalDateTime.of(2023, 3, 2, 12, 0, 0), 5);
        testManager.createSubtask(epic3, testSubTask2);

        testManager.updateEpicStatus(epic3.getId());
        assertEquals(IN_PROGRESS, testManager.getEpicById(5).getStatus());

        //With invalid ID (empty and/or non-existent ID)
        assertNull(testManager.getEpicById(100));
        testManager.updateEpicStatus(100);

        //Remove all subtasks and check the status of epics
        testManager.deleteAllSubtasks();
        assertEquals(NEW,epic1.getStatus());
        assertEquals(NEW,epic2.getStatus());
        assertEquals(NEW,epic3.getStatus());


    }

    @Test
    void getHistory() {

        createAllTask();
        HistoryManager historyManager = new InMemoryHistoryManager();

        //Empty list of tasks
        assertTrue(historyManager.getHistory().isEmpty());

        //Add tasks
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        assertEquals(6, historyManager.getHistory().size());

        //Delete tasks and check for repetition
        historyManager.remove(1);
        historyManager.remove(3);
        historyManager.remove(6);
        assertEquals(3, historyManager.getHistory().size());


    }
    @Test
    void testDeleteAllTasks() {

        createAllTask();
        testManager.deleteAllTasks();
        List<Task> testList = testManager.getTasksList();
        assertEquals(0, testList.size());

        assertTrue(testManager.getTasksList().isEmpty());

    }

    @Test
    void testDeleteAllEpics() {
        createAllTask();
        testManager.deleteAllEpics();
        List<Epic> testList = testManager.getEpicsList();
        assertEquals(0, testList.size());

        assertTrue(testManager.getEpicsList().isEmpty());

    }

    @Test
    void testDeleteAllSubtasks() {

        createAllTask();
        testManager.deleteAllSubtasks();
        List<Subtask> testList = testManager.getSubtaskList();
        assertEquals(0, testList.size());

        assertEquals(testManager.getEpicById(3).getStatus(), NEW);
        assertEquals(testManager.getEpicById(5).getStatus(), NEW);

        assertTrue(testManager.getSubtaskList().isEmpty());

    }

    @Test
    void testGetPrioritizedTasks() {

        createAllTask();
        Set<Task> expectedPrioritizedList = new TreeSet<>(Comparator.nullsLast(Comparator.comparing(Task::getStartTime)));
        expectedPrioritizedList.addAll(testManager.getTasksList());
        expectedPrioritizedList.addAll(testManager.getSubtaskList());

        Set<Task> actualPrioritizedList = testManager.getPrioritizedTasks();

        assertEquals(expectedPrioritizedList, actualPrioritizedList);
        clearFile();
    }
}
