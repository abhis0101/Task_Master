package com.taskmaster.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, TaskTypes type, String title, TaskStatus status, String description, long duration, LocalDateTime startTime, int epicId) {
        super(id, type, title, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask subTask)) return false;
        if (!super.equals(o)) return false;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask {" + "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                ", endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                ", epicId=" + getEpicId() +
                '}';
    }

    @Override
    public String getTaskInfo() {
        return getId() + "," + TaskTypes.SUBTASK + "," + getTitle() + "," + getStatus() + "," + getDescription() + "," + getDuration() +
                "," + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "," + getEpicId();
    }
}
