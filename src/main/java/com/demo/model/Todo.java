package com.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TODOタスクを表すエンティティクラス
 * アイゼンハワーマトリックスの4象限で管理
 */
public class Todo {
    private String id;
    private String title;
    private String description;
    private boolean important;
    private boolean urgent;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Todo() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.completed = false;
    }

    public Todo(String title, String description, boolean important, boolean urgent) {
        this();
        this.title = title;
        this.description = description;
        this.important = important;
        this.urgent = urgent;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 象限を取得（1-4）
     * 1: 重要 & 緊急
     * 2: 重要 & 緊急でない
     * 3: 重要でない & 緊急
     * 4: 重要でない & 緊急でない
     */
    public int getQuadrant() {
        if (important && urgent) return 1;
        if (important && !urgent) return 2;
        if (!important && urgent) return 3;
        return 4;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", important=" + important +
                ", urgent=" + urgent +
                ", completed=" + completed +
                ", quadrant=" + getQuadrant() +
                '}';
    }
}

// Made with Bob
