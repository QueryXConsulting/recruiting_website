<template>
  <div class="message-board-container">
    <!-- 左侧对话列表 -->
    <div class="conversation-list">
      <div v-for="conv in conversations" :key="conv.id"
        :class="['conversation-item', { active: currentConvId === conv.id }]" @click="switchConversation(conv.id)">
        <div class="conv-info">
          <div class="conv-title">{{ conv.title }}</div>
          <div class="conv-last-message">{{ conv.lastMessage }}</div>
        </div>
        <div class="conv-time">{{ conv.lastTime }}</div>
      </div>
    </div>

    <div class="message-area">
      <div class="message-board">
        <el-timeline>
          <el-timeline-item v-for="msg in currentMessages" :key="msg.id" :timestamp="formatDate(msg.createTime)"
            :hide-timestamp="true" placement="top" type="primary">
            <template #dot>
              <div class="custom-dot"></div>
            </template>
            <div class="message-time">{{ formatDate(msg.createTime) }}</div>
            <div class="message-content">
              <div class="visitor-label">求职者：</div>
              <div class="content-text">{{ msg.content }}</div>
            </div>
          </el-timeline-item>
        </el-timeline>

        <div class="post-message">
          <el-input v-model="newMessage" type="textarea" :rows="4" placeholder="输入留言(应聘者最多只能发三次留言)" resize="none"
            class="message-input" />
          <div class="button-wrapper">
            <el-button class="submit-button" type="primary" @click="postMessage" :loading="posting">
              留 言
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'


const conversations = ref([
  {
    id: 1,
    title: '求职者',
    lastMessage: '好的，我明白了，谢谢',
    lastTime: '2024/01/07'
  },
  {
    id: 2,
    title: '求职者',
    lastMessage: '请问这个功能怎么使用？',
    lastTime: '2024/01/06'
  },
  {
    id: 3,
    title: '求职者',
    lastMessage: '产品质量很好',
    lastTime: '2024/01/05'
  }
])


const allMessages = ref({
  1: [
    {
      id: 1,
      content: '这个留言板感觉实现很简单，还舒服。',
      createTime: '2021/01/07 23:21'
    },
    {
      id: 2,
      content: '我现在的五笔练习已经很棒了',
      createTime: '2021/01/07 23:23'
    }
  ],
  2: [
    {
      id: 1,
      content: '请问这个功能怎么使用？',
      createTime: '2021/01/07 23:24'
    }
  ],
  3: [
    {
      id: 1,
      content: '产品质量很好，很满意',
      createTime: '2021/01/07 23:25'
    }
  ]
})

const currentConvId = ref(1)
const newMessage = ref('')
const posting = ref(false)


const currentMessages = computed(() => {
  return allMessages.value[currentConvId.value] || []
})


const switchConversation = (convId) => {
  currentConvId.value = convId
  newMessage.value = ''
}


const postMessage = async () => {
  if (!newMessage.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }

  posting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))

    const now = new Date()
    const newMsg = {
      id: currentMessages.value.length + 1,
      content: newMessage.value,
      createTime: `${now.getFullYear()}/${String(now.getMonth() + 1).padStart(2, '0')}/${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
    }

    allMessages.value[currentConvId.value].unshift(newMsg)


    const currentConv = conversations.value.find(c => c.id === currentConvId.value)
    if (currentConv) {
      currentConv.lastMessage = newMessage.value
      currentConv.lastTime = `${now.getFullYear()}/${String(now.getMonth() + 1).padStart(2, '0')}/${String(now.getDate()).padStart(2, '0')}`
    }

    ElMessage.success('留言发表成功')
    newMessage.value = ''
  } catch (error) {
    ElMessage.error('发表留言失败')
  } finally {
    posting.value = false
  }
}

// 添加日期格式化函数
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.message-board-container {
  display: flex;
  height: 100%;

  background-color: #f5f7fa;
  padding: 20px;
  gap: 20px;
}

.conversation-list {
  width: 240px;
  background-color: #fff;
  border-radius: 4px;
  overflow-y: auto;
  height: 100%;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8e8e8;
}

.conversation-item {
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f0f2f5;
  transition: all 0.3s;
  display: flex;
  justify-content: space-between;
}

.conversation-item:hover {
  background-color: #fafafa;
}

.conversation-item.active {
  background-color: #f5f5f5;
}

.conv-info {
  flex: 1;
  overflow: hidden;
  max-width: 160px;
}

.conv-title {
  font-size: 14px;
  color: #333333;
  margin-bottom: 6px;
  font-weight: normal;
}

.conv-last-message {
  font-size: 12px;
  color: #999999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.conv-time {
  font-size: 12px;
  color: #999999;
  margin-left: 10px;
}

.message-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.message-board {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

:deep(.el-timeline) {
  flex: 1;
  padding: 20px;
  margin-bottom: 20px;
  overflow-y: auto;
  max-height: calc(100vh - 300px);

  /* 自定义滚动条样式 */
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: rgba(144, 147, 153, 0.15);
    border-radius: 3px;
  }

  &::-webkit-scrollbar-track {
    background-color: transparent;
  }
}

:deep(.el-timeline-item__node) {
  left: 0;
}

:deep(.el-timeline-item__tail) {
  border-left: 2px solid #909399;
  left: 5px;
}

.custom-dot {
  width: 10px;
  height: 10px;
  background-color: #fff;
  border: 2px solid #909399;
  border-radius: 50%;
  box-shadow: 0 0 0 2px rgba(144, 147, 153, 0.1);
}

.message-time {
  color: #606266;
  font-size: 13px;
  margin-bottom: 8px;
  margin-left: -5px;
  font-weight: 500;
}

:deep(.el-timeline-item__wrapper) {
  padding-left: 25px;
}

:deep(.el-timeline-item__content) {
  margin-left: 0;
}

.message-content {
  background-color: #ffffff;
  padding: 16px 20px;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8e8e8;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.visitor-label {
  color: #666666;
  font-weight: normal;
  margin-bottom: 8px;
  font-size: 14px;
}

.content-text {
  color: #333333;
  line-height: 1.6;
  font-size: 14px;
  word-break: break-all;
}

.post-message {
  padding: 20px;
  background-color: #ffffff;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8e8e8;
}

.message-input {
  margin-bottom: 15px;
}

:deep(.el-textarea__inner) {
  resize: none;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 12px;
  font-size: 14px;
  transition: all 0.3s;
  background-color: #ffffff;
  color: #333333;
  min-height: 80px !important;
  height: 80px !important;
}

:deep(.el-textarea__inner:focus) {
  border-color: #40a9ff;
  box-shadow: 0 0 0 2px rgba(64, 169, 255, 0.2);
}

.button-wrapper {
  display: flex;
  justify-content: flex-end;
}

.submit-button {
  width: 120px;
  height: 36px;
  font-size: 14px;
  background-color: #f0f2f5;
  border: none;
  color: #666;
  letter-spacing: 2px;
  border-radius: 4px;
}

.submit-button:hover {
  background-color: #e6e8eb;
  color: #333;
}

@media (prefers-color-scheme: dark) {
  .conversation-list {
    background-color: #1f1f1f;
    border-color: #434343;
  }

  .conversation-item {
    border-bottom-color: #2d2d2d;
  }

  .conversation-item:hover {
    background-color: #2d2d2d;
  }

  .conversation-item.active {
    background-color: #363636;
  }

  .conv-title {
    color: #e0e0e0;
  }

  .conv-last-message,
  .conv-time {
    color: #a6a6a6;
  }

  .message-content {
    background-color: #1f1f1f;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
    border-color: #434343;
    animation: fadeIn 0.3s ease-in-out;
  }

  .visitor-label {
    color: #a6a6a6;
  }

  .content-text {
    color: #e0e0e0;
  }

  .post-message {
    background-color: #1f1f1f;
    border-color: #434343;
  }

  :deep(.el-textarea__inner) {
    background-color: #1f1f1f;
    border-color: #434343;
    color: #e0e0e0;
  }

  :deep(.el-textarea__inner:focus) {
    border-color: #177ddc;
    box-shadow: 0 0 0 2px rgba(23, 125, 220, 0.2);
  }

  .submit-button {
    background-color: #2d2d2d;
    color: #a6a6a6;
  }

  .submit-button:hover {
    background-color: #363636;
    color: #e0e0e0;
  }

  :deep(.el-timeline-item__tail) {
    border-left: 2px solid #666;
  }

  .custom-dot {
    background-color: #1f1f1f;
    border-color: #666;
    box-shadow: 0 0 0 2px rgba(102, 102, 102, 0.1);
  }

  .message-time {
    color: #909399;
  }

  :deep(.el-timeline) {
    &::-webkit-scrollbar-thumb {
      background-color: rgba(144, 147, 153, 0.1);
      /* 深色模式下更淡 */
    }
  }
}
</style>
