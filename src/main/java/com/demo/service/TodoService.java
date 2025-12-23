package com.demo.service;

import com.demo.model.Todo;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * TODOタスクを管理するサービスクラス
 * メモリ内でデータを保持（ConcurrentHashMap使用）
 */
@ApplicationScoped
public class TodoService {
    
    private final Map<String, Todo> todoStore = new ConcurrentHashMap<>();

    public TodoService() {
        // サンプルデータを初期化
        initializeSampleData();
    }

    /**
     * サンプルデータを初期化
     */
    private void initializeSampleData() {
        Todo todo1 = new Todo("緊急の顧客対応", "重要な顧客からの問い合わせに対応する", true, true);
        Todo todo2 = new Todo("プロジェクト計画", "次四半期のプロジェクト計画を立てる", true, false);
        Todo todo3 = new Todo("会議の準備", "明日の定例会議の資料を準備する", false, true);
        Todo todo4 = new Todo("メールチェック", "一般的なメールをチェックする", false, false);
        
        todoStore.put(todo1.getId(), todo1);
        todoStore.put(todo2.getId(), todo2);
        todoStore.put(todo3.getId(), todo3);
        todoStore.put(todo4.getId(), todo4);
    }

    /**
     * 全てのTODOを取得
     */
    public List<Todo> getAllTodos() {
        return new ArrayList<>(todoStore.values());
    }

    /**
     * IDでTODOを取得
     */
    public Optional<Todo> getTodoById(String id) {
        return Optional.ofNullable(todoStore.get(id));
    }

    /**
     * 象限別にTODOを取得
     * @param quadrant 象限番号（1-4）
     */
    public List<Todo> getTodosByQuadrant(int quadrant) {
        return todoStore.values().stream()
                .filter(todo -> todo.getQuadrant() == quadrant)
                .collect(Collectors.toList());
    }

    /**
     * 新しいTODOを作成
     */
    public Todo createTodo(Todo todo) {
        if (todo.getId() == null || todo.getId().isEmpty()) {
            todo.setId(UUID.randomUUID().toString());
        }
        todoStore.put(todo.getId(), todo);
        return todo;
    }

    /**
     * TODOを更新
     */
    public Optional<Todo> updateTodo(String id, Todo updatedTodo) {
        if (!todoStore.containsKey(id)) {
            return Optional.empty();
        }
        
        Todo existingTodo = todoStore.get(id);
        existingTodo.setTitle(updatedTodo.getTitle());
        existingTodo.setDescription(updatedTodo.getDescription());
        existingTodo.setImportant(updatedTodo.isImportant());
        existingTodo.setUrgent(updatedTodo.isUrgent());
        existingTodo.setCompleted(updatedTodo.isCompleted());
        
        return Optional.of(existingTodo);
    }

    /**
     * TODOを削除
     */
    public boolean deleteTodo(String id) {
        return todoStore.remove(id) != null;
    }

    /**
     * 完了状態を切り替え
     */
    public Optional<Todo> toggleCompleted(String id) {
        Todo todo = todoStore.get(id);
        if (todo == null) {
            return Optional.empty();
        }
        todo.setCompleted(!todo.isCompleted());
        return Optional.of(todo);
    }

    /**
     * 全てのTODOを削除（テスト用）
     */
    public void clearAll() {
        todoStore.clear();
    }

    /**
     * TODOの総数を取得
     */
    public int getTodoCount() {
        return todoStore.size();
    }

    /**
     * 完了済みTODOの数を取得
     */
    public long getCompletedCount() {
        return todoStore.values().stream()
                .filter(Todo::isCompleted)
                .count();
    }

    /**
     * 象限別の統計情報を取得
     */
    public Map<Integer, Long> getQuadrantStatistics() {
        Map<Integer, Long> stats = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            final int quadrant = i;
            long count = todoStore.values().stream()
                    .filter(todo -> todo.getQuadrant() == quadrant)
                    .count();
            stats.put(quadrant, count);
        }
        return stats;
    }
}

// Made with Bob
