<template>
  <AppLayout>
    <div class="my-polls-page">
      <div class="page-header">
        <h1>我的投票</h1>
        <p>查看您参与的投票记录</p>
      </div>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      
      <div v-else-if="polls.length === 0" class="empty-container">
        <el-empty description="您还没有参与任何投票">
          <el-button type="primary" @click="router.push('/polls')">
            去投票
          </el-button>
        </el-empty>
      </div>
      
      <div v-else class="polls-list">
        <div
          v-for="poll in polls"
          :key="poll.id"
          class="poll-card card"
          @click="router.push(`/polls/${poll.id}`)"
        >
          <div class="poll-header">
            <span :class="['poll-status', poll.status.toLowerCase()]">
              {{ getStatusText(poll.status) }}
            </span>
          </div>
          <h3 class="poll-title">{{ poll.title }}</h3>
          <p class="poll-desc">{{ poll.description || '暂无描述' }}</p>
          <div class="poll-meta">
            <span>
              <el-icon><Clock /></el-icon>
              {{ formatDate(poll.endTime) }} 结束
            </span>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { pollApi } from '@/api'
import AppLayout from '@/components/AppLayout.vue'
import { Clock } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const polls = ref([])

onMounted(async () => {
  await fetchMyPolls()
})

async function fetchMyPolls() {
  loading.value = true
  try {
    const response = await pollApi.getMyPolls({ page: 0, size: 20 })
    if (response.success) {
      polls.value = response.data.content
    }
  } catch (error) {
    console.error('Failed to fetch my polls:', error)
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function getStatusText(status) {
  const statusMap = {
    ACTIVE: '进行中',
    CLOSED: '已结束',
    DRAFT: '草稿'
  }
  return statusMap[status] || status
}
</script>

<style lang="scss" scoped>
.my-polls-page {
  .polls-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }
  
  .poll-card {
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
    }
    
    .poll-header {
      margin-bottom: 12px;
      
      .poll-status {
        padding: 4px 12px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 500;
        
        &.active {
          background: #e1f3d8;
          color: #67c23a;
        }
        
        &.closed {
          background: #fef0f0;
          color: #f56c6c;
        }
      }
    }
    
    .poll-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }
    
    .poll-desc {
      color: #909399;
      font-size: 14px;
      margin-bottom: 16px;
    }
    
    .poll-meta {
      color: #909399;
      font-size: 13px;
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }
}

.loading-container,
.empty-container {
  padding: 40px;
  text-align: center;
}
</style>
