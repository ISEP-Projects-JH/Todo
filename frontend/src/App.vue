<script setup lang="ts">
</script>

<template>
  <div class="app-container">
    <nav class="nav-bar">
      <router-link to="/" class="nav-link">Dashboard</router-link>
      <router-link to="/todos" class="nav-link">Todos</router-link>
    </nav>
    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
}

.nav-bar {
  background-color: var(--card-bg);
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
}

.nav-link {
  text-decoration: none;
  color: var(--secondary-color);
  font-weight: 600;
  font-size: 1.1rem;
}

.nav-link.router-link-active {
  color: var(--primary-color);
  border-bottom: 2px solid var(--primary-color);
}

.main-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>
