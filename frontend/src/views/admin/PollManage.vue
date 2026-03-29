<template>
  <div class="poll-manage-page">
    <div class="page-header">
      <h2>投票管理</h2>
      <el-button type="primary" @click="router.push('/admin/polls/create')">
        <el-icon><Plus /></el-icon>
        创建投票
      </el-button>
    </div>
    
    <div class="filter-section">
      <el-radio-group v-model="statusFilter" @change="handleFilterChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="DRAFT">草稿</el-radio-button>
        <el-radio-button label="ACTIVE">进行中</el-radio-button>
        <el-radio-button label="CLOSED">已结束</el-radio-button>
      </el-radio-group>
    </div>
    
    <div class="table-container card">
      <el-table :data="polls" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="creatorName" label="创建者" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalVotes" label="票数" width="80" />
        <el-table-column label="时间" width="180">
          <template #default="{ row }">
            <div class="time-info">
              <div>开始: {{ formatDate(row.startTime) }}</div>
              <div>结束: {{ formatDate(row.endTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'DRAFT'"
              text
              type="success"
              @click="handleActivate(row.id)"
            >
              激活
            </el-button>
            <el-button
              v-if="row.status === 'ACTIVE'"
              text
              type="warning"
              @click="handleClose(row.id)"
            >
              关闭
            </el-button>
            <el-button
              text
              type="primary"
              @click="router.push(`/admin/polls/${row.id}/edit`)"
            >
              编辑
            </el-button>
            <el-button
              text
              type="danger"
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="totalElements"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const loading = ref(false)
const polls = ref([])
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)

onMounted(async () => {
  await fetchPolls()
})

async function fetchPolls() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      status: statusFilter.value || undefined
    }
    
    const response = await adminApi.getAllPolls(params)
    if (response.success) {
      polls.value = response.data.content
      totalElements.value = response.data.totalElements
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

async function handleActivate(id) {
  try {
    await ElMessageBox.confirm('确定要激活此投票吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.activatePoll(id)
    if (response.success) {
      ElMessage.success('投票已激活')
      await fetchPolls()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to activate poll:', error)
    }
  }
}

async function handleClose(id) {
  try {
    await ElMessageBox.confirm('确定要关闭此投票吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.closePoll(id)
    if (response.success) {
      ElMessage.success('投票已关闭')
      await fetchPolls()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to close poll:', error)
    }
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除此投票吗？此操作不可恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    const response = await adminApi.deletePoll(id)
    if (response.success) {
      ElMessage.success('投票已删除')
      await fetchPolls()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete poll:', error)
    }
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
.poll-manage-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h2 {
      font-size: 20px;
      color: #303133;
    }
  }
  
  .filter-section {
    margin-bottom: 20px;
  }
  
  .table-container {
    .time-info {
      font-size: 12px;
      color: #909399;
      line-height: 1.6;
    }
    
    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
