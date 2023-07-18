public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task( "Buy flowers", "Roses", TaskStatus.NEW);
        Task task2 = new Task( "Order pizza", "Margarita", TaskStatus.NEW);

        Epic epic1 = new Epic( "Build a house", "One-story house with a flat roof", TaskStatus.NEW);
        Subtask subTask1 = new Subtask(epic1, "Set the foundation", "Buy foundation", TaskStatus.NEW);
        Subtask subTask2 = new Subtask(epic1, "Build Walls", "Wall material - red brick", TaskStatus.NEW);

        Epic epic2 = new Epic( "To plant a tree", "fruit apple tree", TaskStatus.NEW);
        Subtask subTask3 = new Subtask(epic2, "Dig a landing hole", "Depth of the pit - 1m", TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(epic1, subTask1);
        taskManager.createSubtask(epic1, subTask2);

        taskManager.createEpic(epic2);
        taskManager.createSubtask(epic2, subTask3);

        taskManager.printAllTasks();

        Task task3 = new Task( "Buy flowers", "Roses", TaskStatus.IN_PROGRESS);
        Subtask subTask4 = new Subtask(epic1, "Set the foundation", "Buy foundation", TaskStatus.DONE);
        Subtask subTask5 = new Subtask(epic1, "Build Walls", "Wall material - red brick", TaskStatus.DONE);

        taskManager.updateTask(1, task3);
        taskManager.updateSubtask(4, subTask4);
        taskManager.updateSubtask(5, subTask5);

        taskManager.printAllTasks();

        taskManager.deleteById(2);
        taskManager.deleteById(3);

        taskManager.printAllTasks();
    }
}