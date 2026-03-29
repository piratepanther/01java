<template>
  <AppLayout>
    <div class="poll-detail-page">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <template v-else-if="poll">
        <div class="page-header">
          <el-button @click="router.back()" :icon="ArrowLeft">返回</el-button>
          <h1>{{ poll.title }}</h1>
          <div class="poll-meta">
            <span :class="['poll-status', poll.status.toLowerCase()]">
              {{ getStatusText(poll.status) }}
            </span>
            <span>
              <el-icon><User /></el-icon>
              {{ poll.creatorName }}
            </span>
            <span>
              <el-icon><Clock /></el-icon>
              {{ formatDate(poll.startTime) }} - {{ formatDate(poll.endTime) }}
            </span>
          </div>
        </div>
        
        <div class="poll-info card">
          <p class="description">{{ poll.description || '暂无描述' }}</p>
          <div class="info-items">
            <div class="info-item">
              <span class="label">投票类型</span>
              <span class="value">{{ poll.voteType === 'SINGLE' ? '单选' : '多选' }}</span>
            </div>
            <div class="info-item">
              <span class="label">总票数</span>
              <span class="value">{{ poll.totalVotes }} 票</span>
            </div>
            <div class="info-item">
              <span class="label">匿名投票</span>
              <span class="value">{{ poll.isAnonymous ? '是' : '否' }}</span>
            </div>
          </div>
        </div>
        
        <div class="vote-section card">
          <h2>投票选项</h2>
          
          <div v-if="hasVoted || poll.status !== 'ACTIVE'" class="results-view">
            <div
              v-for="option in poll.options"
              :key="option.id"
              class="option-result"
            >
              <div class="option-header">
                <span class="option-text">{{ option.text }}</span>
                <span class="option-stats">
                  {{ option.voteCount }} 票 ({{ option.percentage }}%)
                </span>
              </div>
              <div class="progress-bar">
                <div
                  class="progress-fill"
                  :style="{ width: `${option.percentage}%` }"
                ></div>
              </div>
            </div>
          </div>
          
          <div v-else class="vote-form">
            <div
              v-for="option in poll.options"
              :key="option.id"
              :class="['option-item', { selected: selectedOptions.includes(option.id) }]"
              @click="toggleOption(option.id)"
            >
              <el-checkbox
                v-if="poll.voteType === 'MULTIPLE'"
                :model-value="selectedOptions.includes(option.id)"
              />
              <el-radio
                v-else
                :model-value="selectedOptions[0]"
                :label="option.id"
              />
              <span class="option-text">{{ option.text }}</span>
            </div>
            
            <el-button
              type="primary"
              size="large"
              :loading="submitting"
              :disabled="selectedOptions.length === 0"
              @click="submitVote"
            >
              提交投票
            </el-button>
          </div>
        </div>
        
        <div v-if="poll.showResultsRealtime || poll.status === 'CLOSED'" class="chart-section card">
          <h2>投票结果图表</h2>
          <div class="chart-container">
            <PollChart :options="poll.options" />
          </div>
        </div>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { pollApi } from '@/api'
import AppLayout from '@/components/AppLayout.vue'
import PollChart from '@/components/PollChart.vue'
import { ArrowLeft, User, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const submitting = ref(false)
const poll = ref(null)
const selectedOptions = ref([])
const hasVoted = ref(false)

const pollId = computed(() => route.params.id)

onMounted(async () => {
  await fetchPoll()
})

async function fetchPoll() {
  loading.value = true
  try {
    const response = await pollApi.getPollById(pollId.value)
    if (response.success) {
      poll.value = response.data
      hasVoted.value = response.data.hasVoted
    }
  } catch (error) {
    console.error('Failed to fetch poll:', error)
  } finally {
    loading.value = false
  }
}

function toggleOption(optionId) {
  if (poll.value.voteType === 'SINGLE') {
    selectedOptions.value = [optionId]
  } else {
    const index = selectedOptions.value.indexOf(optionId)
    if (index > -1) {
      selectedOptions.value.splice(index, 1)
    } else {
      selectedOptions.value.push(optionId)
    }
  }
}

async function submitVote() {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  
  if (selectedOptions.value.length === 0) {
    ElMessage.warning('请选择投票选项')
    return
  }
  
  submitting.value = true
  try {
    const response = await pollApi.castVote({
      pollId: poll.value.id,
      optionIds: selectedOptions.value
    })
    
    if (response.success) {
      ElMessage.success('投票成功')
      hasVoted.value = true
      await fetchPoll()
    }
  } catch (error) {
    console.error('Failed to vote:', error)
  } finally {
    submitting.value = false
  }
}

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
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
.poll-detail-page {
  .page-header {
    margin-bottom: 24px;
    
    h1 {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
      margin: 16px 0;
    }
    
    .poll-meta {
      display: flex;
      gap: 20px;
      align-items: center;
      color: #909399;
      font-size: 14px;
      
      span {
        display: flex;
        align-items: center;
        gap: 5px;
      }
      
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
  }
  
  .poll-info {
    .description {
      font-size: 16px;
      line-height: 1.6;
      color: #606266;
      margin-bottom: 20px;
    }
    
    .info-items {
      display: flex;
      gap: 40px;
      
      .info-item {
        .label {
          color: #909399;
          font-size: 13px;
          display: block;
          margin-bottom: 4px;
        }
        
        .value {
          font-size: 16px;
          font-weight: 500;
          color: #303133;
        }
      }
    }
  }
  
  .vote-section {
    h2 {
      font-size: 20px;
      margin-bottom: 20px;
      color: #303133;
    }
    
    .option-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      border: 1px solid #dcdfe6;
      border-radius: 8px;
      margin-bottom: 12px;
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover {
        border-color: #409eff;
        background: #ecf5ff;
      }
      
      &.selected {
        border-color: #409eff;
        background: #ecf5ff;
      }
      
      .option-text {
        flex: 1;
        font-size: 15px;
      }
    }
    
    .option-result {
      margin-bottom: 20px;
      
      .option-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
        
        .option-text {
          font-size: 15px;
          color: #303133;
        }
        
        .option-stats {
          font-size: 14px;
          color: #909399;
        }
      }
      
      .progress-bar {
        height: 10px;
        background: #ebeef5;
        border-radius: 5px;
        overflow: hidden;
        
        .progress-fill {
          height: 100%;
          background: linear-gradient(90deg, #409eff, #67c23a);
          border-radius: 5px;
          transition: width 0.5s ease;
        }
      }
    }
  }
  
  .chart-section {
    h2 {
      font-size: 20px;
      margin-bottom: 20px;
      color: #303133;
    }
    
    .chart-container {
      max-width: 600px;
      margin: 0 auto;
    }
  }
}

.loading-container {
  padding: 40px;
}
</style>
