<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="220px">
        <div class="sidebar">
          <div class="logo">
            <el-icon><Setting /></el-icon>
            <span>管理后台</span>
          </div>
          <el-menu
            :default-active="activeMenu"
            router
          >
            <el-menu-item index="/admin">
              <el-icon><DataBoard /></el-icon>
              <span>仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/admin/polls">
              <el-icon><Document /></el-icon>
              <span>投票管理</span>
            </el-menu-item>
          </el-menu>
        </div>
      </el-aside>
      
      <el-container>
        <el-header>
          <div class="header-content">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin' }">管理后台</el-breadcrumb-item>
              <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
            <div class="user-info">
              <span>{{ authStore.user?.username }}</span>
              <el-button text @click="router.push('/')">返回前台</el-button>
            </div>
          </div>
        </el-header>
        
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Setting, DataBoard, Document } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  
  .el-container {
    height: 100%;
  }
  
  .el-aside {
    background: #001529;
    height: 100%;
    
    .sidebar {
      height: 100%;
      
      .logo {
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        color: #fff;
        font-size: 18px;
        font-weight: 600;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        
        .el-icon {
          font-size: 24px;
        }
      }
      
      .el-menu {
        border-right: none;
        background: transparent;
        
        .el-menu-item {
          color: rgba(255, 255, 255, 0.65);
          
          &:hover {
            background: rgba(255, 255, 255, 0.08);
            color: #fff;
          }
          
          &.is-active {
            background: #1890ff;
            color: #fff;
          }
        }
      }
    }
  }
  
  .el-header {
    background: #fff;
    border-bottom: 1px solid #f0f0f0;
    padding: 0 20px;
    
    .header-content {
      height: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 16px;
      
      span {
        color: #606266;
      }
    }
  }
  
  .el-main {
    background: #f5f7fa;
    padding: 20px;
  }
}
</style>
