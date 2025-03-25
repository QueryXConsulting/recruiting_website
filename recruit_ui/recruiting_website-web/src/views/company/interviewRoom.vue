<template>
  <div class="video-room">
    <!-- 视频区域 -->
    <div class="video-container">
      <!-- 主视频窗口 -->
      <div class="main-video">
        <video ref="remoteVideo" autoplay playsinline></video>
        <!-- 远程用户离线时显示的占位符 -->
        <div v-if="!isRemoteConnected" class="offline-placeholder">
          <el-icon class="animate-pulse"><Avatar /></el-icon>
          <span>正在等待对方加入...</span>
        </div>
      </div>
      <!-- 小视频窗口（自己） -->
      <div class="self-video">
        <video ref="localVideo" autoplay playsinline muted></video>
      </div>
    </div>

    <!-- 控制栏 -->
    <div class="control-bar">
      <div class="control-buttons">
        <!-- 麦克风控制 -->
        <el-button
          class="control-btn"
          :class="{ 'is-active': isMicOn }"
          @click="toggleMic">
          <div class="btn-content">
            <el-icon>
              <Microphone v-if="isMicOn" />
              <Mute v-else />
            </el-icon>
            <span>{{ isMicOn ? '麦克风开启' : '麦克风关闭' }}</span>
          </div>
        </el-button>

        <!-- 摄像头控制 -->
        <el-button
          class="control-btn"
          :class="{ 'is-active': isCameraOn }"
          @click="toggleCamera">
          <div class="btn-content">
            <el-icon>
              <VideoCamera v-if="isCameraOn" />
              <Mute v-else />
            </el-icon>
            <span>{{ isCameraOn ? '摄像头开启' : '摄像头关闭' }}</span>
          </div>
        </el-button>

        <!-- 结束面试 -->
        <el-button
          class="control-btn end-btn"
          @click="endInterview">
          <div class="btn-content">
            <el-icon><Close /></el-icon>
            <span>结束面试</span>
          </div>
        </el-button>
      </div>
    </div>

    <!-- 面试信息侧边栏 -->
    <div class="interview-sidebar">
      <!-- 面试者信息 -->
      <div class="candidate-info">
        <h3>面试者信息</h3>
        <div class="info-item">
          <span class="label">姓名：</span>
          <span>{{ candidateInfo.name }}</span>
        </div>
        <div class="info-item">
          <span class="label">应聘职位：</span>
          <span>{{ candidateInfo.position }}</span>
        </div>
        <div class="info-item">
          <span class="label">面试时间：</span>
          <span>{{ candidateInfo.interviewDate }}</span>
        </div>
        <div class="info-item">
          <span class="label">面试方式：</span>
          <span>{{ candidateInfo.interviewType === '0' ? '线上' : '线下' }}</span>
        </div>
        <div class="info-item">
          <span class="label">预计时长：</span>
          <span>{{ candidateInfo.interviewTime }}分钟</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Close, Microphone, Mute, VideoCamera } from '@element-plus/icons-vue'

// 视频状态
const localVideo = ref(null)
const remoteVideo = ref(null)
const isMicOn = ref(true)
const isCameraOn = ref(true)
const isRemoteConnected = ref(false)

// 面试信息
const candidateInfo = ref({
  name: '张三',
  position: '前端开发工程师',
  interviewDate: '2024-03-21 14:30',
  interviewType: '0',
  interviewTime: 60
})

// 初始化WebRTC连接
let peerConnection = null
let localStream = null

onMounted(async () => {
  try {
    // 获取本地媒体流
    localStream = await navigator.mediaDevices.getUserMedia({
      video: true,
      audio: true
    })
    localVideo.value.srcObject = localStream

    // 初始化WebRTC - 这里需要根据实际的信令服务器来实现
    initializePeerConnection()
  } catch (error) {
    ElMessage.error('无法访问摄像头或麦克风')
    console.error('媒体设备访问错误:', error)
  }
})

onUnmounted(() => {
  // 清理资源
  if (localStream) {
    localStream.getTracks().forEach(track => track.stop())
  }
  if (peerConnection) {
    peerConnection.close()
  }
})

// 控制功能
const toggleMic = () => {
  if (localStream) {
    const audioTrack = localStream.getAudioTracks()[0]
    if (audioTrack) {
      audioTrack.enabled = !audioTrack.enabled
      isMicOn.value = audioTrack.enabled
    }
  }
}

const toggleCamera = () => {
  if (localStream) {
    const videoTrack = localStream.getVideoTracks()[0]
    if (videoTrack) {
      videoTrack.enabled = !videoTrack.enabled
      isCameraOn.value = videoTrack.enabled
    }
  }
}

const toggleScreenShare = async () => {
  try {
    const screenStream = await navigator.mediaDevices.getDisplayMedia({
      video: true
    })
    // 替换视频轨道
    const videoTrack = screenStream.getVideoTracks()[0]
    const sender = peerConnection
      .getSenders()
      .find(s => s.track.kind === 'video')
    sender.replaceTrack(videoTrack)
  } catch (error) {
    ElMessage.error('屏幕共享失败')
  }
}

const endInterview = async () => {
  try {
    await ElMessageBox.confirm('确定要结束面试吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 关闭连接
    if (peerConnection) {
      peerConnection.close()
    }
    if (localStream) {
      localStream.getTracks().forEach(track => track.stop())
    }

    // 跳转回列表页
    // router.push('/interview/list')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('结束面试失败')
    }
  }
}

// WebRTC相关函数
const initializePeerConnection = () => {
  // 这里需要实现WebRTC连接逻辑
  // 1. 创建RTCPeerConnection
  // 2. 添加本地流
  // 3. 设置信令处理
  // 4. 处理ICE候选
  // 具体实现需要根据你的信令服务器来定制
}
</script>

<style scoped>
.video-room {
  display: flex;
  height: 700px;
  background-color: #0a0a0a;
}

.video-container {
  flex: 1;
  position: relative;
  background-color: #000;
  border-radius: 12px;
  margin: 20px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.main-video {
  width: 100%;
  height: 100%;
}

.main-video video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.self-video {
  position: absolute;
  right: 24px;
  top: 24px;
  width: 240px;
  height: 180px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.self-video:hover {
  transform: scale(1.02);
}

.self-video video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.control-bar {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  padding: 16px 24px;
  background: rgba(0, 0, 0, 0.75);
  border-radius: 24px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.control-buttons {
  display: flex;
  gap: 16px;
}

.control-btn {
  width: 120px;
  height: 44px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  color: #fff;
  transition: all 0.3s ease;
  padding: 0;
  display: flex;
}

.btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.control-btn .el-icon {
  font-size: 18px;
}

.control-btn span {
  font-size: 14px;
  font-weight: 500;
}

.control-btn.is-active {
  background: #409EFF;
  color: #fff;
}

.control-btn.is-active:hover {
  background: #66b1ff;
}

.control-btn.end-btn {
  background: #F56C6C;
}

.control-btn.end-btn:hover {
  background: #f78989;
}

/* 添加动画效果 */
@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.control-btn.is-active .el-icon {
  animation: pulse 2s infinite;
}

.interview-sidebar {
  width: 340px;
  background: #1a1a1a;
  padding: 24px;
  overflow-y: auto;
  border-left: 1px solid rgba(255, 255, 255, 0.1);
}

.candidate-info {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.candidate-info h3 {
  color: #fff;
  font-size: 20px;
  margin-bottom: 20px;
  font-weight: 500;
}

.info-item {
  margin: 16px 0;
  display: flex;
  align-items: center;
}

.label {
  color: #888;
  margin-right: 12px;
  font-size: 14px;
  min-width: 80px;
}

.info-item span:not(.label) {
  color: #fff;
  font-size: 15px;
}

.offline-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #fff;
  font-size: 24px;
  background: rgba(0, 0, 0, 0.6);
}

.offline-placeholder .el-icon {
  font-size: 64px;
  margin-bottom: 20px;
  color: #409EFF;
}

/* 自定义滚动条 */
.interview-sidebar::-webkit-scrollbar {
  width: 6px;
}

.interview-sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.interview-sidebar::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .interview-sidebar {
    background: #1a1a1a;
  }

  .candidate-info {
    background: rgba(255, 255, 255, 0.05);
  }
}
</style>