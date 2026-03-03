<template>
  <div class="play-page">
    <!-- 播放器区域 -->
    <div class="player-wrapper">
      <video ref="videoRef" class="video-js vjs-big-play-centered vjs-fluid"></video>
    </div>

    <!-- 底部控制栏 -->
    <div class="player-bar">
      <div class="player-bar-left">
        <el-button text @click="$router.push(`/movie/${movieId}`)">‹ 返回详情</el-button>
        <el-button text @click="$router.push('/')">首页</el-button>
        <span class="now-playing">{{ movieTitle }}</span>
      </div>
      <div class="player-bar-right">
        <!-- 切换资源 -->
        <el-select v-model="currentSourceId" placeholder="切换线路" size="small" style="width:160px" @change="switchSource">
          <el-option v-for="s in sources" :key="s.id" :label="s.sourceName" :value="s.id">
            <span>{{ s.sourceName }}</span>
            <span v-if="s.isRecommended" style="color:var(--gold);margin-left:4px">推荐</span>
          </el-option>
        </el-select>
      </div>
    </div>

    <!-- 无资源提示 -->
    <div v-if="!loading && sources.length === 0" class="no-source">
      <p>暂无可用播放资源</p>
      <el-button type="primary" @click="$router.push(`/movie/${movieId}`)">返回详情</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'
import api from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const userStore = useUserStore()
const movieId = ref(parseInt(route.params.id))
const movieTitle = ref('')
const sources = ref([])
const currentSourceId = ref(null)
const loading = ref(true)
const videoRef = ref(null)
let player = null

// 保存观看进度的定时器
let progressTimer = null

function initPlayer(src) {
  if (player) {
    player.src({ src, type: 'video/mp4' })
    player.play()
    return
  }

  nextTick(() => {
    player = videojs(videoRef.value, {
      controls: true,
      autoplay: true,
      preload: 'auto',
      fluid: true,
      playbackRates: [0.5, 1, 1.25, 1.5, 2],
      sources: [{ src, type: 'video/mp4' }],
      controlBar: {
        children: [
          'playToggle', 'volumePanel', 'currentTimeDisplay',
          'timeDivider', 'durationDisplay', 'progressControl',
          'playbackRateMenuButton', 'fullscreenToggle'
        ]
      }
    })

    player.on('error', () => {
      console.warn('播放失败，该资源可能无效')
    })

    // 定时保存进度
    progressTimer = setInterval(() => {
      if (player && !player.paused() && userStore.isLoggedIn) {
        const progress = Math.floor(player.currentTime())
        api.post('/user/history', {
          userId: userStore.user.id,
          movieId: movieId.value,
          progress,
          sourceId: currentSourceId.value
        }).catch(() => {})
      }
    }, 15000)
  })
}

function switchSource(sourceId) {
  const source = sources.value.find(s => s.id === sourceId)
  if (source) {
    initPlayer(source.sourceUrl)
  }
}

onMounted(async () => {
  try {
    const res = await api.get(`/movie/${movieId.value}`)
    const data = res.data.data
    movieTitle.value = data.movie?.title || ''
    sources.value = data.sources || []

    // 记录历史
    if (userStore.isLoggedIn) {
      api.post('/user/history', {
        userId: userStore.user.id,
        movieId: movieId.value,
        progress: 0
      }).catch(() => {})
    }

    // 选择初始资源
    if (sources.value.length > 0) {
      const queryId = route.query.sourceId ? parseInt(route.query.sourceId) : null
      const recommended = sources.value.find(s => s.isRecommended)
      const initial = queryId ? sources.value.find(s => s.id === queryId) : recommended || sources.value[0]
      currentSourceId.value = initial.id
      initPlayer(initial.sourceUrl)
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

onBeforeUnmount(() => {
  if (progressTimer) clearInterval(progressTimer)
  if (player) {
    // 离开时保存进度
    if (userStore.isLoggedIn && player.currentTime) {
      api.post('/user/history', {
        userId: userStore.user.id,
        movieId: movieId.value,
        progress: Math.floor(player.currentTime()),
        sourceId: currentSourceId.value
      }).catch(() => {})
    }
    player.dispose()
    player = null
  }
})
</script>

<style scoped>
.play-page {
  background: #000;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.player-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  max-height: calc(100vh - 56px);
}
.player-wrapper .video-js {
  width: 100%;
  max-height: calc(100vh - 56px);
}
.player-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 24px;
  background: rgba(10, 14, 23, 0.95);
  border-top: 1px solid var(--border);
  height: 56px;
}
.player-bar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.now-playing {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 600;
  margin-left: 16px;
}
.player-bar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.no-source {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  gap: 16px;
}
</style>
