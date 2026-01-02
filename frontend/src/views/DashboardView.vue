<script setup lang="ts">
import { onMounted } from 'vue'
import { useTodoStore } from '../stores/todoStore'
import { storeToRefs } from 'pinia'

const store = useTodoStore()
const { dashboard } = storeToRefs(store)

onMounted(() => {
  store.fetchDashboard()
})
</script>

<template>
  <div class="dashboard">
    <h1>Dashboard</h1>
    <div class="stats-grid">
      <div class="stat-card pending">
        <h2>Pending</h2>
        <div class="number">{{ dashboard.pendingCount }}</div>
      </div>
      <div class="stat-card completed">
        <h2>Completed</h2>
        <div class="number">{{ dashboard.completedCount }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 20px 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.stat-card {
  background: var(--card-bg);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  text-align: center;
}

.stat-card h2 {
  margin: 0;
  color: var(--secondary-color);
  font-size: 1.2rem;
}

.number {
  font-size: 3rem;
  font-weight: bold;
  margin-top: 10px;
}

.pending .number {
  color: var(--primary-color);
}

.completed .number {
  color: var(--success-color);
}
</style>
