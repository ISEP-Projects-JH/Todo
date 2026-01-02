import axios from 'axios'

const api = axios.create({
  baseURL: '/api'
})

export interface Todo {
  title: string
  description: string
  completed: boolean
  tags: string[]
}

export interface DashboardData {
  completedCount: number
  pendingCount: number
}

export const todoApi = {
  ping: () => api.get('/ping'),
  
  getDashboard: () => api.get<DashboardData>('/dashboard'),
  
  listTodos: () => api.get<Todo[]>('/todos'),
  
  createTodo: (title: string, description: string, tags: string[]) => 
    api.post<Todo>('/todos', { title, description, tags }),
    
  updateTodo: (title: string, newTitle: string, description: string, tags: string[]) =>
    api.put<Todo>(`/todos/${title}`, { newTitle, description, tags }),
    
  deleteTodo: (title: string) => api.delete(`/todos/${title}`),
  
  markCompleted: (title: string) => api.put<Todo>(`/todos/${title}/completed`),
  
  markPending: (title: string) => api.put<Todo>(`/todos/${title}/pending`),
  
  addTags: (title: string, tags: string[]) => 
    api.post<Todo>(`/todos/${title}/tags`, { tags }),
    
  removeTags: (title: string, tags: string[]) => 
    api.delete<Todo>(`/todos/${title}/tags`, { data: { tags } })
}
