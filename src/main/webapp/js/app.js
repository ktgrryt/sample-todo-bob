// API Base URL
const API_BASE = '/bob-test/api/todos';

// State
let todos = [];
let editingTodoId = null;

// DOM Elements
const modal = document.getElementById('todoModal');
const todoForm = document.getElementById('todoForm');
const addTodoBtn = document.getElementById('addTodoBtn');
const closeModalBtn = document.getElementById('closeModal');
const cancelBtn = document.getElementById('cancelBtn');
const loading = document.getElementById('loading');
const toast = document.getElementById('toast');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadTodos();
    setupEventListeners();
});

// Event Listeners
function setupEventListeners() {
    addTodoBtn.addEventListener('click', () => openModal());
    closeModalBtn.addEventListener('click', closeModal);
    cancelBtn.addEventListener('click', closeModal);
    todoForm.addEventListener('submit', handleFormSubmit);
    
    // モーダル外クリックで閉じる
    modal.addEventListener('click', (e) => {
        if (e.target === modal) closeModal();
    });

    // ドラッグ&ドロップのセットアップ
    setupDragAndDrop();
}

// API Functions
async function loadTodos() {
    showLoading(true);
    try {
        const response = await fetch(API_BASE);
        if (!response.ok) throw new Error('Failed to load todos');
        todos = await response.json();
        renderTodos();
        updateStats();
    } catch (error) {
        showToast('タスクの読み込みに失敗しました', 'error');
        console.error('Error loading todos:', error);
    } finally {
        showLoading(false);
    }
}

async function createTodo(todoData) {
    try {
        const response = await fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(todoData)
        });
        if (!response.ok) throw new Error('Failed to create todo');
        const newTodo = await response.json();
        todos.push(newTodo);
        renderTodos();
        updateStats();
        showToast('タスクを作成しました', 'success');
        return newTodo;
    } catch (error) {
        showToast('タスクの作成に失敗しました', 'error');
        console.error('Error creating todo:', error);
        throw error;
    }
}

async function updateTodo(id, todoData) {
    try {
        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(todoData)
        });
        if (!response.ok) throw new Error('Failed to update todo');
        const updatedTodo = await response.json();
        const index = todos.findIndex(t => t.id === id);
        if (index !== -1) todos[index] = updatedTodo;
        renderTodos();
        updateStats();
        showToast('タスクを更新しました', 'success');
        return updatedTodo;
    } catch (error) {
        showToast('タスクの更新に失敗しました', 'error');
        console.error('Error updating todo:', error);
        throw error;
    }
}

async function deleteTodo(id) {
    if (!confirm('このタスクを削除しますか？')) return;
    
    try {
        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Failed to delete todo');
        todos = todos.filter(t => t.id !== id);
        renderTodos();
        updateStats();
        showToast('タスクを削除しました', 'success');
    } catch (error) {
        showToast('タスクの削除に失敗しました', 'error');
        console.error('Error deleting todo:', error);
    }
}

async function toggleTodoCompleted(id) {
    try {
        const response = await fetch(`${API_BASE}/${id}/toggle`, {
            method: 'PATCH'
        });
        if (!response.ok) throw new Error('Failed to toggle todo');
        const updatedTodo = await response.json();
        const index = todos.findIndex(t => t.id === id);
        if (index !== -1) todos[index] = updatedTodo;
        renderTodos();
        updateStats();
    } catch (error) {
        showToast('タスクの更新に失敗しました', 'error');
        console.error('Error toggling todo:', error);
    }
}

// Render Functions
function renderTodos() {
    // 各象限をクリア
    for (let i = 1; i <= 4; i++) {
        const quadrantEl = document.getElementById(`quadrant-${i}`);
        quadrantEl.innerHTML = '';
    }

    // タスクを象限ごとに配置
    todos.forEach(todo => {
        const quadrant = getQuadrant(todo);
        const quadrantEl = document.getElementById(`quadrant-${quadrant}`);
        const taskEl = createTaskElement(todo);
        quadrantEl.appendChild(taskEl);
    });

    // 各象限のタスク数を更新
    updateQuadrantCounts();
}

function createTaskElement(todo) {
    const div = document.createElement('div');
    div.className = `task-card ${todo.completed ? 'completed' : ''}`;
    div.draggable = true;
    div.dataset.todoId = todo.id;

    const createdDate = new Date(todo.createdAt).toLocaleDateString('ja-JP');

    div.innerHTML = `
        <div class="task-header">
            <label class="checkbox-container">
                <input type="checkbox" ${todo.completed ? 'checked' : ''} 
                       onchange="toggleTodoCompleted('${todo.id}')">
                <span class="checkmark"></span>
            </label>
            <h3 class="task-title">${escapeHtml(todo.title)}</h3>
        </div>
        ${todo.description ? `<p class="task-description">${escapeHtml(todo.description)}</p>` : ''}
        <div class="task-footer">
            <span class="task-date">${createdDate}</span>
            <div class="task-actions">
                <button class="btn-icon" onclick="editTodo('${todo.id}')" title="編集">
                    ✏️
                </button>
                <button class="btn-icon" onclick="deleteTodo('${todo.id}')" title="削除">
                    🗑️
                </button>
            </div>
        </div>
    `;

    // ドラッグイベント
    div.addEventListener('dragstart', handleDragStart);
    div.addEventListener('dragend', handleDragEnd);

    return div;
}

function getQuadrant(todo) {
    if (todo.important && todo.urgent) return 1;
    if (todo.important && !todo.urgent) return 2;
    if (!todo.important && todo.urgent) return 3;
    return 4;
}

function updateQuadrantCounts() {
    for (let i = 1; i <= 4; i++) {
        const quadrantEl = document.querySelector(`.quadrant-${i}`);
        const count = document.getElementById(`quadrant-${i}`).children.length;
        quadrantEl.querySelector('.task-count').textContent = count;
    }
}

function updateStats() {
    const total = todos.length;
    const completed = todos.filter(t => t.completed).length;
    document.getElementById('totalCount').textContent = `総タスク: ${total}`;
    document.getElementById('completedCount').textContent = `完了: ${completed}`;
}

// Modal Functions
function openModal(todo = null) {
    editingTodoId = todo ? todo.id : null;
    document.getElementById('modalTitle').textContent = todo ? 'タスクを編集' : '新しいタスク';
    
    if (todo) {
        document.getElementById('todoId').value = todo.id;
        document.getElementById('todoTitle').value = todo.title;
        document.getElementById('todoDescription').value = todo.description || '';
        document.getElementById('todoImportant').checked = todo.important;
        document.getElementById('todoUrgent').checked = todo.urgent;
        document.getElementById('todoCompleted').checked = todo.completed;
    } else {
        todoForm.reset();
        document.getElementById('todoId').value = '';
    }
    
    modal.style.display = 'flex';
}

function closeModal() {
    modal.style.display = 'none';
    todoForm.reset();
    editingTodoId = null;
}

async function handleFormSubmit(e) {
    e.preventDefault();
    
    const todoData = {
        title: document.getElementById('todoTitle').value.trim(),
        description: document.getElementById('todoDescription').value.trim(),
        important: document.getElementById('todoImportant').checked,
        urgent: document.getElementById('todoUrgent').checked,
        completed: document.getElementById('todoCompleted').checked
    };

    try {
        if (editingTodoId) {
            await updateTodo(editingTodoId, todoData);
        } else {
            await createTodo(todoData);
        }
        closeModal();
    } catch (error) {
        // エラーは各関数内で処理済み
    }
}

// Drag and Drop
function setupDragAndDrop() {
    const quadrants = document.querySelectorAll('.task-list');
    
    quadrants.forEach(quadrant => {
        quadrant.addEventListener('dragover', handleDragOver);
        quadrant.addEventListener('drop', handleDrop);
        quadrant.addEventListener('dragleave', handleDragLeave);
    });
}

function handleDragStart(e) {
    e.target.classList.add('dragging');
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/html', e.target.innerHTML);
}

function handleDragEnd(e) {
    e.target.classList.remove('dragging');
}

function handleDragOver(e) {
    if (e.preventDefault) e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    e.currentTarget.classList.add('drag-over');
    return false;
}

function handleDragLeave(e) {
    e.currentTarget.classList.remove('drag-over');
}

async function handleDrop(e) {
    if (e.stopPropagation) e.stopPropagation();
    e.preventDefault();
    
    e.currentTarget.classList.remove('drag-over');
    
    const draggingElement = document.querySelector('.dragging');
    if (!draggingElement) return;
    
    const todoId = draggingElement.dataset.todoId;
    const targetQuadrant = parseInt(e.currentTarget.id.split('-')[1]);
    
    const todo = todos.find(t => t.id === todoId);
    if (!todo) return;
    
    // 象限に基づいて重要度と緊急度を更新
    const updates = {
        ...todo,
        important: targetQuadrant === 1 || targetQuadrant === 2,
        urgent: targetQuadrant === 1 || targetQuadrant === 3
    };
    
    await updateTodo(todoId, updates);
    
    return false;
}

// Utility Functions
function showLoading(show) {
    loading.style.display = show ? 'flex' : 'none';
}

function showToast(message, type = 'info') {
    toast.textContent = message;
    toast.className = `toast toast-${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Global functions for inline event handlers
window.editTodo = function(id) {
    const todo = todos.find(t => t.id === id);
    if (todo) openModal(todo);
};

window.deleteTodo = deleteTodo;
window.toggleTodoCompleted = toggleTodoCompleted;

// Made with Bob
