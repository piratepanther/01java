<template>
  <div class="dashboard-page">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: #e6f7ff;">
          <el-icon style="color: #1890ff;"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalPolls }}</div>
          <div class="stat-label">总投票数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: #f6ffed;">
          <el-icon style="color: #52c41a;"><Check /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.activePolls }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: #fff7e6;">
          <el-icon style="color: #fa8c16;"><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalVotes }}</div>
          <div class="stat-label">总票数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: #fff1f0;">
          <el-icon style="color: #f5222d;"><CircleClose /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.closedPolls }}</div>
          <div class="stat-label">已结束</div>
        </div>
      </div>
    </div>
    
    <div class="quick-actions card">
      <h3>快捷操作</h3>
      <div class="actions">
        <el-button type="primary" @click="router.push('/admin/polls/create')">
          <el-icon><Plus /></el-icon>
          创建投票
        </el-button>
        <el-button @click="router.push('/admin/polls')">
          <el-icon><List /></el-icon>
          管理投票
        </el-button>
      </div>
    </div>
    
    <div class="recent-polls card">
      <h3>最近投票</h3>
      <el-table :data="recentPolls" style="width: 100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalVotes" label="票数" width="80" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button text type="primary" @click="router.push(`/admin/polls/${row.id}/edit`)">
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import { Document, Check, User, CircleClose, Plus, List } from '@element-plus/icons-vue'

const router = useRouter()

const stats = reactive({
  totalPolls: 0,
  activePolls: 0,
  totalVotes: 0,
  closedPolls: 0
})

const recentPolls = ref([])

onMounted(async () => {
  await fetchDashboardData()
})

async function fetchDashboardData() {
  try {
    const response = await adminApi.getAllPolls({ page: 0, size: 10 })
    if (response.success) {
      const polls = response.data.content
      recentPolls.value = polls.slice(0, 5)
      
      stats.totalPolls = response.data.totalElements
      stats.activePolls = polls.filter(p => p.status === 'ACTIVE').length
      stats.closedPolls = polls.filter(p => p.status === 'CLOSED').length
      stats.totalVotes = polls.reduce((sum, p) => sum + p.totalVotes, 0)
    }
  } catch (error) {
    console.error('Failed to fetch dashboard data:', error)
  }
}

function getStatusText(status) {
  const statusMap = {
    ACTIVE: '进行中',
    CLOSED: '已结束',
    DRAFT: '草稿'
  }
  return statusMap[status] || status
}

function getStatusType(status) {
  const typeMap = {
    ACTIVE: 'success',
    CLOSED: 'danger',
    DRAFT: 'info'
  }
  return typeMap[status] || 'info'
}
</script>

<style lang="scss" scoped>
.dashboard-page {
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
    margin-bottom: 24px;
  }
  
  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    
    .stat-icon {
      width: 56px;
      height: 56px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .el-icon {
        font-size: 28px;
      }
    }
    
    .stat-info {
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
      }
      
      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }
  
  .quick-actions {
    margin-bottom: 24px;
    
    h3 {
      margin-bottom: 16px;
      color: #303133;
    }
    
    .actions {
      display: flex;
      gap: 12px;
    }
  }
  
  .recent-polls {
    h3 {
      margin-bottom: 16px;
      color: #303133;
    }
  }
}
</style>
