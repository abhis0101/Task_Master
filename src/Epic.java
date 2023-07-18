import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksIds;
    public Epic(String title, String description, TaskStatus taskStatus) {
        super(title, description, taskStatus);
        this.subtasksIds = new ArrayList<>();
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtasksId (int subtaskId){
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId (int subtaskId){
        for(Integer id : subtasksIds){
            if (id == subtaskId){
                subtasksIds.remove(subtaskId);
            }
        }
    }
}

