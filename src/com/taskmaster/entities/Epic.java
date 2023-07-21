package com.taskmaster.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected final List<Integer> subtasksIds;
    protected LocalDateTime endTime;


    public Epic(int id, TaskTypes type, String title, TaskStatus status, String description, long duration, LocalDateTime startTime) {
        super(id, type, title, status, description, duration, startTime);
        this.subtasksIds = new ArrayList<>();
        this.endTime=getEndTime();
    }


    public void addSubtasksId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void removeSubtasksId(int subtaskId) {
        subtasksIds.removeIf(integer -> integer == subtaskId);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic epic)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(subtasksIds, epic.subtasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds);
    }

    @Override
    public String toString() {

        return "Epic {" + "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                ", endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                '}';
    }

    @Override
    public String getTaskInfo(){
        return getId() + "," + TaskTypes.EPIC   + "," + getTitle()  + "," + getStatus()  + "," + getDescription() +"," + getDuration() +
                "," + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public void setDuration(long duration) {
        super.setDuration(duration);
    }

    @Override
    public long getDuration() {
        return super.getDuration();
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        super.setStartTime(startTime);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

