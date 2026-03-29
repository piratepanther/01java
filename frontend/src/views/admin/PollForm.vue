<template>
  <div class="poll-form-page">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑投票' : '创建投票' }}</h2>
    </div>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      class="form-container card"
    >
      <el-form-item label="投票标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入投票标题" maxlength="200" show-word-limit />
      </el-form-item>
      
      <el-form-item label="投票描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          placeholder="请输入投票描述"
          :rows="4"
          maxlength="2000"
          show-word-limit
        />
      </el-form-item>
      
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="form.startTime"
          type="datetime"
          placeholder="选择开始时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:ss"
        />
      </el-form-item>
      
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="form.endTime"
          type="datetime"
          placeholder="选择结束时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:ss"
        />
      </el-form-item>
      
      <el-form-item label="投票类型" prop="voteType">
        <el-radio-group v-model="form.voteType">
          <el-radio label="SINGLE">单选</el-radio>
          <el-radio label="MULTIPLE">多选</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item v-if="form.voteType === 'MULTIPLE'" label="最大投票数" prop="maxVotesPerUser">
        <el-input-number v-model="form.maxVotesPerUser" :min="1" :max="10" />
      </el-form-item>
      
      <el-form-item label="投票选项" prop="options">
        <div class="options-container">
          <div
            v-for="(option, index) in form.options"
            :key="index"
            class="option-item"
          >
            <el-input
              v-model="form.options[index]"
              placeholder="请输入选项内容"
              maxlength="500"
            />
            <el-button
              v-if="form.options.length > 2"
              type="danger"
              :icon="Delete"
              circle
              @click="removeOption(index)"
            />
          </div>
          <el-button type="primary" plain @click="addOption" :disabled="form.options.length >= 20">
            <el-icon><Plus /></el-icon>
            添加选项
          </el-button>
        </div>
      </el-form-item>
      
      <el-form-item label="其他设置">
        <div class="settings-group">
          <el-checkbox v-model="form.allowMultipleVotes">允许多次投票</el-checkbox>
          <el-checkbox v-model="form.isAnonymous">匿名投票</el-checkbox>
          <el-checkbox v-model="form.showResultsRealtime">实时显示结果</el-checkbox>
        </div>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '创建投票' }}
        </el-button>
        <el-button @click="router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { adminApi, pollApi } from '@/api'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const submitting = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  voteType: 'SINGLE',
  maxVotesPerUser: 1,
  options: ['', ''],
  allowMultipleVotes: false,
  isAnonymous: false,
  showResultsRealtime: true
})

const rules = {
  title: [
    { required: true, message: '请输入投票标题', trigger: 'blur' },
    { max: 200, message: '标题长度不能超过200个字符', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  voteType: [
    { required: true, message: '请选择投票类型', trigger: 'change' }
  ],
  options: [
    {
      validator: (rule, value, callback) => {
        const validOptions = value.filter(opt => opt.trim() !== '')
        if (validOptions.length < 2) {
          callback(new Error('至少需要2个有效选项'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

onMounted(async () => {
  if (isEdit.value) {
    await fetchPoll()
  }
})

async function fetchPoll() {
  try {
    const response = await pollApi.getPollById(route.params.id)
    if (response.success) {
      const poll = response.data
      form.title = poll.title
      form.description = poll.description
      form.startTime = poll.startTime
      form.endTime = poll.endTime
      form.voteType = poll.voteType
      form.maxVotesPerUser = poll.maxVotesPerUser
      form.options = poll.options.map(opt => opt.text)
      form.allowMultipleVotes = poll.allowMultipleVotes
      form.isAnonymous = poll.isAnonymous
      form.showResultsRealtime = poll.showResultsRealtime
    }
  } catch (error) {
    console.error('Failed to fetch poll:', error)
  }
}

function addOption() {
  if (form.options.length < 20) {
    form.options.push('')
  }
}

function removeOption(index) {
  if (form.options.length > 2) {
    form.options.splice(index, 1)
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  const validOptions = form.options.filter(opt => opt.trim() !== '')
  if (validOptions.length < 2) {
    ElMessage.error('至少需要2个有效选项')
    return
  }
  
  if (new Date(form.endTime) <= new Date(form.startTime)) {
    ElMessage.error('结束时间必须晚于开始时间')
    return
  }
  
  submitting.value = true
  try {
    const data = {
      ...form,
      options: validOptions
    }
    
    let response
    if (isEdit.value) {
      response = await adminApi.updatePoll(route.params.id, data)
    } else {
      response = await adminApi.createPoll(data)
    }
    
    if (response.success) {
      ElMessage.success(isEdit.value ? '修改成功' : '创建成功')
      router.push('/admin/polls')
    }
  } catch (error) {
    console.error('Failed to save poll:', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.poll-form-page {
  .page-header {
    margin-bottom: 24px;
    
    h2 {
      font-size: 20px;
      color: #303133;
    }
  }
  
  .form-container {
    max-width: 800px;
    
    .options-container {
      width: 100%;
      
      .option-item {
        display: flex;
        gap: 10px;
        margin-bottom: 10px;
        
        .el-input {
          flex: 1;
        }
      }
    }
    
    .settings-group {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
  }
}
</style>
