package com.taskmaster.managers;

import com.taskmaster.entities.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public Node<Task> first;
    public Node<Task> last;
    public HashMap<Integer, Node<Task>> node = new HashMap<>();

    private static class Node<T> {
        private final T task;
        private Node<T> prev;
        private Node<T> next;

        public Node(T task, Node<T> prev, Node<T> next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(node.get(id));
    }

    public void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task, last, null);
        node.put(task.getId(), newNode);
        if (last == null) {
            first = newNode;
        } else {
            newNode.prev = last;
            last.next = newNode;
        }
        last = newNode;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = first;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    public void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        if (node == first && node == last) {
            first = null;
            last = null;
            return;
        }
        if (node == first) {
            if (first.next != null) {
                first = first.next;
                first.prev = null;
            } else {
                first = null;
            }
            return;
        }
        if (node == last) {
            if (last.prev != null) {
                last = last.prev;
                last.next = null;
            } else {
                last = null;
            }
        } else {
            if (node.prev != null) {
                node.prev.next = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
        }
    }
}
