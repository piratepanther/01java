<template>
  <div class="app-layout">
    <AppHeader />
    <el-main class="app-main">
      <slot></slot>
    </el-main>
    <el-footer class="app-footer">
      <p>&copy; 2024 在线投票系统. All rights reserved.</p>
    </el-footer>
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
</script>

<style lang="scss" scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-main {
  flex: 1;
  padding: 20px;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
}

.app-footer {
  background: #fff;
  border-top: 1px solid #ebeef5;
  text-align: center;
  padding: 20px;
  color: #909399;
  font-size: 14px;
}
</style>
