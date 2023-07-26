package com.taskmaster.test;

import com.taskmaster.managers.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @Override
    FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(new File("src/com/taskmaster/testresources/file.csv"));
    }
    @AfterEach
    public void deleteEverything(){

        testManager.deleteAllTasks();
        testManager.deleteAllEpics();
        testManager.deleteAllSubtasks();

    }

    @Test
    public void testLoadFromEmptyFile() {
        FileBackedTasksManager managerWithEmptyFile = FileBackedTasksManager.loadFromFile(new File(
                "src/com/taskmaster/testresources/file1.csv"));

        assertEquals(0, managerWithEmptyFile.getTasksList().size());
        assertEquals(0, managerWithEmptyFile.getEpicsList().size());
        assertEquals(0, managerWithEmptyFile.getSubtaskList().size());

    }

    @Test
    public void testLoadFromFileWithoutEpicAndSubtask() {

        testManager.createTask(task1);
        testManager.createTask(task2);

        testManager.getTaskById(1);
        testManager.getTaskById(2);

        FileBackedTasksManager managerWithEmptyFile = FileBackedTasksManager.loadFromFile(new File(
                "src/com/taskmaster/testresources/file.csv"));

        assertEquals(2, managerWithEmptyFile.getTasksList().size());
        assertEquals(0, managerWithEmptyFile.getEpicsList().size());
        assertEquals(0, managerWithEmptyFile.getSubtaskList().size());
        assertEquals(2, managerWithEmptyFile.getHistory().size());

    }

    @Test
    public void testLoadFromFileWithoutTask() {

        testManager.createEpic(epic1);
        testManager.createEpic(epic2);
        testManager.createEpic(epic3);
        testManager.createSubtask(epic1,subTask1);
        testManager.createSubtask(epic1,subTask2);
        testManager.createSubtask(epic3,subTask3);

        testManager.getEpicById(1);
        testManager.getEpicById(2);
        testManager.getEpicById(3);
        testManager.getSubtaskById(4);
        testManager.getSubtaskById(5);
        testManager.getSubtaskById(6);

        FileBackedTasksManager managerWithEmptyFile = FileBackedTasksManager.loadFromFile(new File(
                "src/com/taskmaster/testresources/file.csv"));

        assertEquals(0, managerWithEmptyFile.getTasksList().size());
        assertEquals(3, managerWithEmptyFile.getEpicsList().size());
        assertEquals(3, managerWithEmptyFile.getSubtaskList().size());
        assertEquals(6, managerWithEmptyFile.getHistory().size());
    }

    @Test
    public void testLoadFromFileWithoutHistory() {

        testManager.createTask(task1);
        testManager.createTask(task2);
        testManager.createEpic(epic1);
        testManager.createEpic(epic2);
        testManager.createEpic(epic3);
        testManager.createSubtask(epic1,subTask1);
        testManager.createSubtask(epic2,subTask2);
        testManager.createSubtask(epic3,subTask3);

        FileBackedTasksManager managerWithEmptyFile = FileBackedTasksManager.loadFromFile(new File(
                "src/com/taskmaster/testresources/file.csv"));

        assertEquals(2, managerWithEmptyFile.getTasksList().size());
        assertEquals(3, managerWithEmptyFile.getEpicsList().size());
        assertEquals(3, managerWithEmptyFile.getSubtaskList().size());
        assertEquals(0, managerWithEmptyFile.getHistory().size());
    }

}
