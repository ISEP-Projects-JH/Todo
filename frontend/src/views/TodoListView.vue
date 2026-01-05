<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTodoStore } from '../stores/todoStore'
import { storeToRefs } from 'pinia'

const store = useTodoStore()
const { todos, loading } = storeToRefs(store)

const newTitle = ref('')
const newDesc = ref('')
const newTags = ref('')

const searchQuery = ref('')
const searchDate = ref('')

onMounted(() => {
  store.currentFilter = { type: 'all' }
  store.fetchTodos()
})

async function handleTextSearch() {
  if (!searchQuery.value.trim()) return
  searchDate.value = '' // Clear other filter
  store.currentFilter = { type: 'search', query: searchQuery.value }
  await store.searchTodos(searchQuery.value)
}

async function handleDateSearch() {
  if (!searchDate.value) return
  searchQuery.value = '' // Clear other filter
  const date = new Date(searchDate.value)
  date.setHours(23, 59, 59, 999)
  const dateStr = date.toISOString()
  store.currentFilter = { type: 'date', date: dateStr }
  await store.listTodosBefore(dateStr)
}

async function handleReset() {
  searchQuery.value = ''
  searchDate.value = ''
  store.currentFilter = { type: 'all' }
  await store.fetchTodos()
}

// Edit state
const editingTodo = ref<string | null>(null)
const editForm = ref({
  description: '',
  tags: ''
})

function startEdit(todo: any) {
  editingTodo.value = todo.title
  editForm.value = {
    description: todo.description || '',
    tags: todo.tags ? todo.tags.join(', ') : ''
  }
}

function cancelEdit() {
  editingTodo.value = null
  editForm.value = { description: '', tags: '' }
}

async function saveEdit(todo: any) {
  if (!editingTodo.value) return
  
  const tags = editForm.value.tags.split(',').map(t => t.trim()).filter(t => t)
  await store.updateTodo(
    todo.title,
    todo.title, // Keep title same
    editForm.value.description,
    tags
  )
  cancelEdit()
}

async function handleCreate() {
  if (!newTitle.value.trim()) return
  
  const tags = newTags.value.split(',').map(t => t.trim()).filter(t => t)
  await store.createTodo(newTitle.value, newDesc.value, tags)
  
  newTitle.value = ''
  newDesc.value = ''
  newTags.value = ''
}
</script>

<template>
  <div class="todo-list-view">
    <h1>Todos</h1>
    
    <div class="search-section">
      <div class="search-group">
        <label>Search by Text:</label>
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="Type and press Enter..." 
          class="input-field"
          @keyup.enter="handleTextSearch"
          @focus="searchDate = ''"
        >
      </div>

      <div class="divider">- OR -</div>

      <div class="search-group">
        <label>Filter by Date:</label>
        <input 
          v-model="searchDate" 
          type="date" 
          class="date-input"
          @change="handleDateSearch"
          @focus="searchQuery = ''"
        >
      </div>
      
      <button @click="handleReset" class="reset-btn">Reset All</button>
    </div>

    <div class="create-form">
      <input 
        v-model="newTitle" 
        type="text" 
        placeholder="Task title" 
        class="input-field"
        @keyup.enter="handleCreate"
      >
      <input 
        v-model="newDesc" 
        type="text" 
        placeholder="Description (optional)" 
        class="input-field"
      >
      <input 
        v-model="newTags" 
        type="text" 
        placeholder="Tags (comma separated)" 
        class="input-field"
      >
      <button @click="handleCreate" class="add-btn">Add Task</button>
    </div>

    <div v-if="loading" class="loading">Loading...</div>
    
    <div v-else class="todo-list">
      <div v-for="todo in todos" :key="todo.title" class="todo-item" :class="{ completed: todo.completed }">
        <div class="todo-checkbox">
          <input 
            type="checkbox" 
            :checked="todo.completed"
            @change="store.toggleStatus(todo)"
          >
        </div>
        
        <div class="todo-content">
          <div class="todo-header">
            <span class="title">{{ todo.title }}</span>
            <div class="tags" v-if="editingTodo !== todo.title">
              <span v-for="tag in todo.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
          
          <div v-if="editingTodo === todo.title" class="edit-form">
            <input v-model="editForm.description" placeholder="Description" class="edit-input">
            <input v-model="editForm.tags" placeholder="Tags (comma separated)" class="edit-input">
            <div class="edit-actions">
              <button @click="saveEdit(todo)" class="save-btn">Save</button>
              <button @click="cancelEdit" class="cancel-btn">Cancel</button>
            </div>
          </div>
          <div v-else>
            <div v-if="todo.description" class="description">{{ todo.description }}</div>
            <div class="meta">
              <small v-if="todo.createdAt">Created: {{ new Date(todo.createdAt).toLocaleString() }}</small>
              <small v-if="todo.updatedAt">Updated: {{ new Date(todo.updatedAt).toLocaleString() }}</small>
            </div>
          </div>
        </div>
        
        <div class="actions">
          <button v-if="editingTodo !== todo.title" @click="startEdit(todo)" class="edit-btn">Edit</button>
          <button @click="store.deleteTodo(todo.title)" class="delete-btn">Delete</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.search-section {
  background: var(--card-bg);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.search-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-group label {
  font-weight: 600;
  min-width: 100px;
}

.divider {
  text-align: center;
  font-weight: bold;
  color: #666;
  font-size: 0.9rem;
}

.date-input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-btn {
  background-color: var(--secondary-color, #2c3e50);
  color: white;
}

.reset-btn {
  background-color: #95a5a6;
  color: white;
}

.create-form {
  background: var(--card-bg);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.input-field {
  flex: 1;
  min-width: 200px;
}

.add-btn {
  background-color: var(--primary-color);
  color: white;
}

.todo-item {
  background: var(--card-bg);
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 15px;
  transition: opacity 0.2s;
}

.todo-item.completed {
  opacity: 0.7;
}

.todo-item.completed .title {
  text-decoration: line-through;
}

.todo-content {
  flex: 1;
}

.todo-header {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.title {
  font-weight: 600;
  font-size: 1.1rem;
}

.tags {
  display: flex;
  gap: 5px;
}

.tag {
  background-color: #e1f0fa;
  color: var(--primary-color);
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
}

.description {
  color: #666;
  font-size: 0.9rem;
  margin-top: 4px;
}

.meta {
  margin-top: 8px;
  display: flex;
  gap: 15px;
  color: #999;
  font-size: 0.8rem;
}

.load-more {
  text-align: center;
  margin-top: 20px;
  padding-bottom: 20px;
}

.load-more-btn {
  background-color: var(--secondary-color, #2c3e50);
  color: white;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.actions {
  display: flex;
  gap: 5px;
}

.edit-btn {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.edit-form {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.edit-input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.edit-actions {
  display: flex;
  gap: 5px;
}

.save-btn {
  background-color: var(--success-color, #27ae60);
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #95a5a6;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.delete-btn {
  background-color: transparent;
  color: var(--danger-color);
  border: 1px solid var(--danger-color);
}

.delete-btn:hover {
  background-color: var(--danger-color);
  color: white;
}

.todo-checkbox input {
  width: 20px;
  height: 20px;
  cursor: pointer;
}
</style>
