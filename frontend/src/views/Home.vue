<template>
  <AppLayout>
    <div class="home-page">
      <div class="hero-section">
        <h1>欢迎使用在线投票系统</h1>
        <p>创建、参与投票，实时查看结果，让决策更加民主透明</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="router.push('/polls')">
            浏览投票
          </el-button>
          <el-button 
            v-if="authStore.isAdmin" 
            size="large" 
            @click="router.push('/admin/polls/create')"
          >
            创建投票
          </el-button>
        </div>
      </div>
      
      <div class="features-section">
        <h2>系统特点</h2>
        <div class="features-grid">
          <div class="feature-card">
            <el-icon class="feature-icon"><Lock /></el-icon>
            <h3>安全可靠</h3>
            <p>JWT身份验证，防刷票机制，确保投票公平公正</p>
          </div>
          <div class="feature-card">
            <el-icon class="feature-icon"><DataLine /></el-icon>
            <h3>实时统计</h3>
            <p>投票结果实时更新，可视化图表展示</p>
          </div>
          <div class="feature-card">
            <el-icon class="feature-icon"><Setting /></el-icon>
            <h3>灵活配置</h3>
            <p>支持单选/多选，匿名投票，时间限制等多种规则</p>
          </div>
          <div class="feature-card">
            <el-icon class="feature-icon"><Iphone /></el-icon>
            <h3>响应式设计</h3>
            <p>完美支持PC端和移动端访问</p>
          </div>
        </div>
      </div>
      
      <div class="active-polls-section">
        <div class="section-header">
          <h2>活跃投票</h2>
          <el-button text @click="router.push('/polls')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="polls.length === 0" class="empty-container">
          <el-empty description="暂无活跃投票" />
        </div>
        
        <div v-else class="polls-grid">
          <div
            v-for="poll in polls"
            :key="poll.id"
            class="poll-card card"
            @click="router.push(`/polls/${poll.id}`)"
          >
            <div class="poll-header">
              <span class="poll-status active">进行中</span>
              <span class="poll-votes">{{ poll.totalVotes }} 票</span>
            </div>
            <h3 class="poll-title">{{ poll.title }}</h3>
            <p class="poll-desc">{{ poll.description || '暂无描述' }}</p>
            <div class="poll-footer">
              <span>
                <el-icon><Clock /></el-icon>
                {{ formatDate(poll.endTime) }} 结束
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { pollApi } from '@/api'
import AppLayout from '@/components/AppLayout.vue'
import { Lock, DataLine, Setting, Iphone, ArrowRight, Clock } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const polls = ref([])

onMounted(async () => {
  await fetchActivePolls()
})

async function fetchActivePolls() {
  loading.value = true
  try {
    const response = await pollApi.getActivePolls({ page: 0, size: 6 })
    if (response.success) {
      polls.value = response.data.content
    }
  } catch (error) {
    console.error('Failed to fetch polls:', error)
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
</script>

<style lang="scss" scoped>
.home-page {
  .hero-section {
    text-align: center;
    padding: 60px 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    color: #fff;
    margin-bottom: 40px;
    
    h1 {
      font-size: 36px;
      margin-bottom: 16px;
    }
    
    p {
      font-size: 18px;
      opacity: 0.9;
      margin-bottom: 30px;
    }
    
    .hero-actions {
      display: flex;
      gap: 16px;
      justify-content: center;
    }
  }
  
  .features-section {
    margin-bottom: 40px;
    
    h2 {
      font-size: 24px;
      margin-bottom: 24px;
      color: #303133;
    }
    
    .features-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
    }
    
    .feature-card {
      background: #fff;
      padding: 30px;
      border-radius: 12px;
      text-align: center;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      
      .feature-icon {
        font-size: 48px;
        color: #409eff;
        margin-bottom: 16px;
      }
      
      h3 {
        font-size: 18px;
        margin-bottom: 10px;
        color: #303133;
      }
      
      p {
        color: #909399;
        font-size: 14px;
        line-height: 1.6;
      }
    }
  }
  
  .active-polls-section {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      h2 {
        font-size: 24px;
        color: #303133;
      }
    }
    
    .polls-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;
    }
    
    .poll-card {
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
      
      .poll-footer {
        color: #909399;
        font-size: 13px;
        display: flex;
        align-items: center;
        gap: 5px;
      }
    }
  }
}

.loading-container,
.empty-container {
  padding: 40px;
  text-align: center;
}
</style>
