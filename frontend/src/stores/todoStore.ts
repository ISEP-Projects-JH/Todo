import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { todoApi, type Todo, type DashboardData } from '../api/todoApi'

export const useTodoStore = defineStore('todo', () => {
  const todos = ref<Todo[]>([])
  const dashboard = ref<DashboardData>({ completedCount: 0, pendingCount: 0 })
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchDashboard() {
    try {
      const response = await todoApi.getDashboard()
      dashboard.value = response.data
    } catch (e) {
      console.error(e)
    }
  }

  async function fetchTodos() {
    loading.value = true
    try {
      const response = await todoApi.listTodos()
      todos.value = response.data
    } catch (e) {
      error.value = 'Failed to fetch todos'
    } finally {
      loading.value = false
    }
  }

  async function createTodo(title: string, description: string, tags: string[]) {
    try {
      await todoApi.createTodo(title, description, tags)
      await Promise.all([fetchTodos(), fetchDashboard()])
    } catch (e) {
      error.value = 'Failed to create todo'
      throw e
    }
  }

  async function toggleStatus(todo: Todo) {
    try {
      if (todo.completed) {
        await todoApi.markPending(todo.title)
      } else {
        await todoApi.markCompleted(todo.title)
      }
      await Promise.all([fetchTodos(), fetchDashboard()])
    } catch (e) {
      error.value = 'Failed to update status'
    }
  }

  async function deleteTodo(title: string) {
    try {
      await todoApi.deleteTodo(title)
      await Promise.all([fetchTodos(), fetchDashboard()])
    } catch (e) {
      error.value = 'Failed to delete todo'
    }
  }

  return {
    todos,
    dashboard,
    loading,
    error,
    fetchDashboard,
    fetchTodos,
    createTodo,
    toggleStatus,
    deleteTodo
  }
})
