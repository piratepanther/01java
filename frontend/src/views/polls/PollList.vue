<template>
  <AppLayout>
    <div class="poll-list-page">
      <div class="page-header">
        <h1>投票列表</h1>
        <p>浏览所有可参与的投票项目</p>
      </div>
      
      <div class="filter-section">
        <el-radio-group v-model="statusFilter" @change="handleFilterChange">
          <el-radio-button label="active">进行中</el-radio-button>
          <el-radio-button label="CLOSED">已结束</el-radio-button>
        </el-radio-group>
      </div>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      
      <div v-else-if="polls.length === 0" class="empty-container">
        <el-empty description="暂无投票" />
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
            <span class="poll-votes">{{ poll.totalVotes }} 票</span>
          </div>
          <h3 class="poll-title">{{ poll.title }}</h3>
          <p class="poll-desc">{{ poll.description || '暂无描述' }}</p>
          <div class="poll-meta">
            <span>
              <el-icon><User /></el-icon>
              {{ poll.creatorName }}
            </span>
            <span>
              <el-icon><Clock /></el-icon>
              {{ formatDate(poll.startTime) }} - {{ formatDate(poll.endTime) }}
            </span>
          </div>
          <div v-if="poll.hasVoted" class="voted-badge">
            <el-icon><Check /></el-icon>
            已投票
          </div>
        </div>
      </div>
      
      <div v-if="totalPages > 1" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="totalElements"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { pollApi } from '@/api'
import AppLayout from '@/components/AppLayout.vue'
import { User, Clock, Check } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const polls = ref([])
const statusFilter = ref('active')
const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

onMounted(async () => {
  await fetchPolls()
})

async function fetchPolls() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    let response
    if (statusFilter.value === 'active') {
      response = await pollApi.getActivePolls(params)
    } else {
      response = await pollApi.getPollsByStatus(statusFilter.value, params)
    }
    
    if (response.success) {
      polls.value = response.data.content
      totalElements.value = response.data.totalElements
      totalPages.value = response.data.totalPages
    }
  } catch (error) {
    console.error('Failed to fetch polls:', error)
  } finally {
    loading.value = false
  }
}

function handleFilterChange() {
  currentPage.value = 1
  fetchPolls()
}

function handlePageChange(page) {
  currentPage.value = page
  fetchPolls()
}

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
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
.poll-list-page {
  .filter-section {
    margin-bottom: 24px;
  }
  
  .polls-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
  }
  
  .poll-card {
    position: relative;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
    }
    
    .poll-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
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
        
        &.draft {
          background: #f4f4f5;
          color: #909399;
        }
      }
      
      .poll-votes {
        color: #909399;
        font-size: 14px;
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
      line-height: 1.5;
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .poll-meta {
      display: flex;
      gap: 20px;
      color: #909399;
      font-size: 13px;
      
      span {
        display: flex;
        align-items: center;
        gap: 5px;
      }
    }
    
    .voted-badge {
      position: absolute;
      top: 20px;
      right: 20px;
      background: #409eff;
      color: #fff;
      padding: 4px 12px;
      border-radius: 4px;
      font-size: 12px;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
  
  .pagination-container {
    margin-top: 30px;
    display: flex;
    justify-content: center;
  }
}

.loading-container,
.empty-container {
  padding: 40px;
  text-align: center;
}
</style>
