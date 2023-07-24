import com.taskmaster.server.HttpTaskServer;
import com.taskmaster.server.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        KVServer kVServer = new KVServer();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        kVServer.start();
        httpTaskServer.start();

//        TaskManager manager = Managers.getInMemoryTaskManager();
//        Task task1 = new Task(1, TaskTypes.TASK,"Buy Flowers", TaskStatus.NEW,"Roses",10,
//                LocalDateTime.of(2023, 3, 20, 12, 10, 10));
//        Task task2 = new Task(2,TaskTypes.TASK,"Order Pizzas", TaskStatus.NEW,"Margarita",10,
//                LocalDateTime.of(2023, 3, 20, 12, 0, 0));
//        Epic epic1 = new Epic(3,TaskTypes.EPIC,"Build a house", TaskStatus.NEW,"One-story house with a flat roof" ,10,
//                LocalDateTime.of(2023, 3, 20, 12, 0, 0) );
//        Subtask subTask1 = new Subtask(4,TaskTypes.SUBTASK, "Establish Foundation",TaskStatus.NEW, "Strip Foundation",
//                15, LocalDateTime.of(2023, 3, 20, 12, 10, 0),3);
//        Subtask subTask2 = new Subtask(5,TaskTypes.SUBTASK, "Build Walls", TaskStatus.NEW,"Wall material - red brick",
//                10, LocalDateTime.of(2023, 3, 20, 12, 15, 0),3);
//        Subtask subTask3 = new Subtask(6, TaskTypes.SUBTASK,"Dig a landing hole",TaskStatus.NEW, "Depth of the pit - 1m",
//                20, LocalDateTime.of(2023, 3, 20, 12, 20, 0),3);
//
//        manager.createTask(task1);
//        manager.createTask(task2);
//        manager.createEpic(epic1);
//        manager.createSubtask(epic1, subTask1);
//        manager.createSubtask(epic1, subTask2);
//        manager.createSubtask(epic1, subTask3);
//
//        manager.getTaskById(1);
//        manager.getTaskById(2);
//        manager.getEpicById(3);
//        manager.getSubtaskById(4);
//        manager.getSubtaskById(5);
//        manager.getSubtaskById(6);
//
//        for (Task task : manager.getHistory()) {
//            System.out.println(task);
//        }
//
//        System.out.println(" ");
//        manager.deleteTaskById(1);
//
//        for (Task task : manager.getHistory()) {
//            System.out.println(task);
//        }
//
//        System.out.println(" ");
//
//        manager.deleteEpicById(3);
//
//        for (Task task : manager.getHistory()) {
//            System.out.println(task);
//        }
    }
}
