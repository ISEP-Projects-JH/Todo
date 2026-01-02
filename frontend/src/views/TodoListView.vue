<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTodoStore } from '../stores/todoStore'
import { storeToRefs } from 'pinia'

const store = useTodoStore()
const { todos, loading } = storeToRefs(store)

const newTitle = ref('')
const newDesc = ref('')
const newTags = ref('')

onMounted(() => {
  store.fetchTodos()
})

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
            <div class="tags">
              <span v-for="tag in todo.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
          <div v-if="todo.description" class="description">{{ todo.description }}</div>
        </div>
        
        <button @click="store.deleteTodo(todo.title)" class="delete-btn">Delete</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
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
