package com.taskmaster.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Task {
    private int id;
    private final TaskTypes type;
    private String title;
    private TaskStatus status;
    private String description;
    private long duration;
    private LocalDateTime startTime;

    public Task(int id, TaskTypes type, String title, TaskStatus status, String description, long duration, LocalDateTime startTime) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task {" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                ", endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                '}';
    }

    public TaskTypes getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    public String getTaskInfo(){
        return getId() + "," + TaskTypes.TASK   + "," + getTitle()  + "," + getStatus()  + "," + getDescription() + ","
                + getDuration() +
                "," + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
    public LocalDateTime getEndTime(){
        return startTime.plusMinutes(duration);
    }
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime =  startTime;
    }
}