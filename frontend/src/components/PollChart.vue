<template>
  <div class="poll-chart">
    <Bar
      v-if="chartData"
      :data="chartData"
      :options="chartOptions"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js'
import { Bar } from 'vue-chartjs'

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
)

const props = defineProps({
  options: {
    type: Array,
    required: true
  }
})

const colors = [
  '#409eff',
  '#67c23a',
  '#e6a23c',
  '#f56c6c',
  '#909399',
  '#00d4aa',
  '#ff6b6b',
  '#4ecdc4',
  '#45b7d1',
  '#96ceb4'
]

const chartData = computed(() => {
  if (!props.options || props.options.length === 0) return null
  
  return {
    labels: props.options.map(opt => opt.text),
    datasets: [
      {
        label: '票数',
        data: props.options.map(opt => opt.voteCount),
        backgroundColor: props.options.map((_, index) => colors[index % colors.length]),
        borderRadius: 8
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: true,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          const total = context.dataset.data.reduce((a, b) => a + b, 0)
          const percentage = total > 0 ? ((context.raw / total) * 100).toFixed(1) : 0
          return `${context.raw} 票 (${percentage}%)`
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        stepSize: 1
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.poll-chart {
  width: 100%;
  max-height: 400px;
}
</style>
