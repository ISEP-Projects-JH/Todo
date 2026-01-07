import { defineStore } from 'pinia'
import { ref } from 'vue'
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
      // Sort by createdAt DESC (newest first)
      todos.value = response.data.sort((a, b) => {
        const t1 = a.createdAt ? new Date(a.createdAt).getTime() : 0
        const t2 = b.createdAt ? new Date(b.createdAt).getTime() : 0
        return t2 - t1
      })
    } catch (e) {
      error.value = 'Failed to fetch todos'
    } finally {
      loading.value = false
    }
  }

  async function searchTodos(query: string) {
    loading.value = true
    try {
      const response = await todoApi.searchTodos(query)
      // Sort by createdAt DESC (newest first)
      todos.value = response.data.sort((a, b) => {
        const t1 = a.createdAt ? new Date(a.createdAt).getTime() : 0
        const t2 = b.createdAt ? new Date(b.createdAt).getTime() : 0
        return t2 - t1
      })
    } catch (e) {
      error.value = 'Failed to search todos'
    } finally {
      loading.value = false
    }
  }

  async function findByTag(tag: string) {
    loading.value = true
    try {
      const response = await todoApi.findByTag(tag)
      // Sort by createdAt DESC (newest first)
      todos.value = response.data.sort((a, b) => {
        const t1 = a.createdAt ? new Date(a.createdAt).getTime() : 0
        const t2 = b.createdAt ? new Date(b.createdAt).getTime() : 0
        return t2 - t1
      })
    } catch (e) {
      error.value = 'Failed to find todos by tag'
    } finally {
      loading.value = false
    }
  }

  async function listTodosBefore(time: string, limit: number = 64) {
    loading.value = true
    try {
      const response = await todoApi.listTodosBefore(time, limit)
      // Backend returns ASC, user wants "later is higher" (DESC)
      todos.value = response.data.sort((a, b) => {
        const t1 = a.createdAt ? new Date(a.createdAt).getTime() : 0
        const t2 = b.createdAt ? new Date(b.createdAt).getTime() : 0
        return t2 - t1
      })
    } catch (e) {
      error.value = 'Failed to list todos before time'
    } finally {
      loading.value = false
    }
  }

  async function createTodo(title: string, description: string, tags: string[]) {
    try {
      await todoApi.createTodo(title, description, tags)
      await Promise.all([refreshCurrentView(), fetchDashboard()])
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
      await Promise.all([refreshCurrentView(), fetchDashboard()])
    } catch (e) {
      error.value = 'Failed to update status'
    }
  }

  async function updateTodo(originalTitle: string, newTitle: string, description: string, tags: string[]) {
    try {
      await todoApi.updateTodo(originalTitle, newTitle, description, tags)
      await Promise.all([refreshCurrentView(), fetchDashboard()])
    } catch (e) {
      error.value = 'Failed to update todo'
      throw e
    }
  }

  async function deleteTodo(title: string) {
    try {
      await todoApi.deleteTodo(title)
      await Promise.all([refreshCurrentView(), fetchDashboard()])
    } catch (e) {
      error.value = 'Failed to delete todo'
    }
  }

  // State to track current view parameters
  const currentFilter = ref<{
    type: 'all' | 'search' | 'date' | 'tag',
    query?: string,
    date?: string,
    tag?: string
  }>({ type: 'all' })

  async function refreshCurrentView() {
    if (currentFilter.value.type === 'search' && currentFilter.value.query) {
      await searchTodos(currentFilter.value.query)
    } else if (currentFilter.value.type === 'date' && currentFilter.value.date) {
      await listTodosBefore(currentFilter.value.date)
    } else if (currentFilter.value.type === 'tag' && currentFilter.value.tag) {
      await findByTag(currentFilter.value.tag)
    } else {
      await fetchTodos()
    }
  }

  return {
    todos,
    dashboard,
    loading,
    error,
    fetchDashboard,
    fetchTodos,
    searchTodos,
    findByTag,
    listTodosBefore,
    createTodo,
    updateTodo,
    toggleStatus,
    deleteTodo,
    currentFilter,
    refreshCurrentView
  }
})
