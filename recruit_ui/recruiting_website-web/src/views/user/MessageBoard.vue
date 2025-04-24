<template>
    <!-- 顶部导航栏 -->
    <div style="height: 73px;width: 100%;">
      <nav class="nav-bar">
        <div class="logo">
          <!-- <img src="/public/logo.png" alt="问呗" class="logo-img">
            <span class="divider">|</span>
            <span class="recruit-text">招聘</span> -->
        </div>
        <div class="nav-items">
          <a href="/users/index" class="nav-item">首页</a>
          <a href="/users/search" class="nav-item">校园招聘</a>
          <a href="#" class="nav-item">社会招聘</a>
          <a href="/users/application" class="nav-item" v-if="userStore().role == '5'">应聘历史</a>
          <a href="/users/message" class="nav-item active" v-if="userStore().role == '5'" style="padding-top: 15px;"
            alt="留言板">
            <el-icon>
              <component :is="Bell"></component>
            </el-icon>
            <!-- 未读消息提示(小圆点) -->
            <i v-if="hasMessage" class="message-flag"></i>
          </a>
          <!-- 用户头像 -->
          <el-dropdown v-if="userStore().token != null" @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar size="large" :src="userStore().userInfo.userAvatar" @error="() => { }">
                <!-- 头像加载失败时显示默认头像 -->
                <img src="/public/default_user.png" alt="用户头像">
              </el-avatar>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="index" v-if="userStore().role != '5' && userStore().role != null">
                  <el-icon>
                    <component :is="iconMapping['house']" />
                  </el-icon>
                  公司端
                </el-dropdown-item>

                <el-dropdown-item command="userInfo" v-if="userStore().role == '5'">
                  <el-icon>
                    <component :is="iconMapping['user']" />
                  </el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon>
                    <component :is="iconMapping['switch']" />
                  </el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <router-link to="/auth/login" class="login-btn" v-else-if="userStore().token == null">登录</router-link>
        </div>
      </nav>
    </div>
  <div class="message-board-container">
    <!-- 左侧对话列表 -->
    <div class="conversation-list">
      <div v-for="conv in conversations" :key="conv.id"
        :class="['conversation-item', { active: currentConvId === conv.id, unread: !conv.isRead }]"
        @click="switchConversation(conv.id)">
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
          <div v-if="hasMore" class="load-more">
            <el-button type="text" @click="loadMore" :loading="loading">
              加载更多
            </el-button>
          </div>
          <el-timeline-item v-for="msg in currentMessages" :key="msg.id" :timestamp="msg.createTime"
            :hide-timestamp="true" placement="top" type="primary">
            <template #dot>
              <div class="custom-dot"></div>
            </template>
            <div class="message-time">{{ msg.createTime }}</div>
            <div class="message-content" :class="{ 'message-self': msg.ownerUser === '1' }">
              <div class="message-header">
                <div class="message-header-left">
                  <span class="sender-name">{{ msg.user }}</span>
                  <span class="sender-type">{{ msg.ownerUser === '1' ? '招聘者' : '应聘者' }}</span>
                </div>
                <div v-if="msg.ownerUser == '1'" class="message-status" :class="{ 'status-unread': !msg.isRead }">
                  {{ msg.isRead ? '已读' : '未读' }}
                </div>
              </div>
              <div class="content-text">{{ msg.content }}</div>
            </div>
          </el-timeline-item>
          <div v-if="loading" class="loading-more">加载中...</div>
        </el-timeline>

        <div class="post-message">
          <el-input v-model="newMessage" type="textarea" :rows="4" placeholder="输入留言(应聘者得到回复之后才能发送下一条)" resize="none"
            class="message-input" />
          <div class="button-wrapper">
            <el-button @click="postMessage" class="submit-button" type="primary" :loading="posting">
              留 言
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import router from '@/router';
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { iconMapping } from '@/utils/iconList';
import { ElMessage, ElMessageBox } from 'element-plus';
import { userLogout } from '@/api/company/companyApi';
import { Bell } from '@element-plus/icons-vue';
import { getLastMessage, getMessageData, postMessage as postMessageApi } from '@/api/user/UserApi'
import userStore from '@/store/user'


const hasMessage = ref(false); // 是否有未读消息


const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userLogout().then(() => {
        userStore().$reset();
        window.location.reload();
        // 跳转到登录页
        router.push('/auth/login');
        ElMessage.success('已安全退出登录');
      })
    }).catch(() => { })
  } else if (command === 'userInfo') {
    router.push('/users/userInfo');
  } else if (command === 'index') {
    router.push('/home');
  }
}


const conversations = ref([])

const fetchLastMessages = async () => {
  try {
    const res = await getLastMessage()
    if (res.code === 200 && res.content) {

      conversations.value = res.content.map(msg => ({
        id: msg.userId,
        title: msg.userName,
        lastMessage: msg.content,
        lastTime: msg.createTime,
        isRead: msg.isRead == 1
      }))
    }
  } catch (error) {
    ElMessage.error('获取留言列表失败')
  }
}


const allMessages = ref({})
const currentPage = ref(1)
const pageSize = ref(4)
const loading = ref(false)
const hasMore = ref(true)

const fetchMessageData = async (userId, loadMore = false) => {
  try {
    console.log('fetchMessageData', typeof userId);
    loading.value = true
    const res = await getMessageData({
      page: currentPage.value,
      size: pageSize.value,
      id: userId
    })

    // const companyId = userStore().userInfo.companyInfoId
    // const res = await getMessageData(userId, companyId)
    if (res.code === 200 && res.content) {

      const newMessages = res.content.map(msg => ({
      // allMessages.value[userId] = res.content.map(msg => ({
        id: msg.messageId,
        content: msg.content,
        createTime: msg.createTime,
        isRead: msg.isRead == 0 ? false : true,
        user: msg.user,
        ownerUser: msg.ownerUser,
        userId: msg.userId,
        companyUserId: msg.companyUserId
      }))

      if (loadMore) {
        allMessages.value[userId] = [...newMessages, ...(allMessages.value[userId] || [])]
      } else {
        // 如果是切换对话或首次加载，则直接替换消息列表
        allMessages.value[userId] = newMessages;
      }

      // 如果返回的消息数量小于页面大小，说明没有更多消息了
      hasMore.value = newMessages.length >= pageSize.value;
    } else {
      if (!loadMore) {
        allMessages.value[userId] = []
      }
      hasMore.value = false
    }
  } catch (error) {
    console.log(error);
    ElMessage.error('获取留言数据失败')
  } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  if (!hasMore.value || loading.value) return
  currentPage.value++;
  await fetchMessageData(currentConvId.value, true)
}

const switchConversation = async (convId) => {
  currentConvId.value = convId;
  currentPage.value = 1;
  hasMore.value = true;
  newMessage.value = '';
  await fetchMessageData(convId)
}


onMounted(async () => {
  await fetchLastMessages()
  if (conversations.value.length > 0) {
    currentConvId.value = conversations.value[0].id
    await fetchMessageData(currentConvId.value)
  }
})


const refreshInterval = ref(null)

onMounted(() => {
  refreshInterval.value = setInterval(async () => {
    await fetchLastMessages()
    if (currentConvId.value) {
      await fetchMessageData(currentConvId.value)
    }
  }, 60000)
})

onUnmounted(() => {
  if (refreshInterval.value) {
    clearInterval(refreshInterval.value)
  }
})

const currentConvId = ref(1)
const newMessage = ref('')
const posting = ref(false)


const currentMessages = computed(() => {
  return allMessages.value[currentConvId.value] || []
})


const postMessage = async () => {
  if (!newMessage.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }

  posting.value = true
  try {
    await postMessageApi({
      userId: currentConvId.value,
      content: newMessage.value,
    })
    // await fetchMessageData(currentConvId.value)
    // await fetchLastMessages()

    // 获取最新一条消息，但保持当前页码
    const currentMessages = allMessages.value[currentConvId.value] || []
    const res = await getMessageData({
      page: 1,
      size: 1,
      id: currentConvId.value
    })

    if (res.code === 200 && res.content) {
      const newMsg = res.content.map(msg => ({
        id: msg.messageId,
        content: msg.content,
        createTime: msg.createTime,
        isRead: msg.isRead == 0 ? false : true,
        user: msg.user,
        ownerUser: msg.ownerUser,
        userId: msg.userId,
        companyUserId: msg.companyUserId
      }))[0]

      // 将新消息添加到现有消息列表末尾
      allMessages.value[currentConvId.value] = [...currentMessages, newMsg]
    }

    await fetchLastMessages() // 更新左侧会话列表
    ElMessage.success('留言发表成功')
    newMessage.value = ''
  } catch (error) {
    ElMessage.error('发表留言失败')
  } finally {
    posting.value = false
  }
}


</script>

<style scoped lang="scss">
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
  position: relative;
}

.conversation-item:hover {
  background-color: #fafafa;
}

.conversation-item.active {
  background-color: #f5f5f5;
}

.conversation-item::after {
  content: '';
  position: absolute;
  top: 50%;
  right: 10px;
  width: 8px;
  height: 8px;
  background-color: #f56c6c;
  border-radius: 50%;
  transform: translateY(-50%);
  display: none;
}

.conversation-item.unread::after {
  display: block;
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
  margin-left: 0;
  margin-right: auto;
  max-width: 80%;
}



.message-self .sender-type {
  background-color: #c8e6c9;
  color: #2e7d32;
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

.message-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.message-header-left {
  display: flex;
  align-items: center;
}

.sender-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  margin-right: 8px;
}

.sender-type {
  font-size: 12px;
  color: #909399;
  background-color: #f4f4f5;
  padding: 2px 6px;
  border-radius: 4px;
}

.message-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  background-color: #f0f9eb;
  color: #67c23a;
}

.message-status.status-unread {
  background-color: #fef0f0;
  color: #f56c6c;
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

  .message-status {
    background-color: rgba(103, 194, 58, 0.1);
  }

  .message-status.status-unread {
    background-color: rgba(245, 108, 108, 0.1);
  }

  :deep(.el-timeline) {
    &::-webkit-scrollbar-thumb {
      background-color: rgba(144, 147, 153, 0.1);
      /* 深色模式下更淡 */
    }
  }
}

/* 头部导航栏 */
.loading-more {
  text-align: center;
  padding: 10px 0;
  color: #909399;
}

.load-more {
  text-align: center;
  margin-top: 10px;
}


.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 4rem;
  background: rgba(255, 255, 255, 0.95);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.logo {
  display: flex;
  align-items: center;
}

// .logo-img {
//   height: 32px;
//   width: auto;
// }

.divider {
  margin: 0 10px;
  color: #FF7427;
}

.recruit-text {
  font-size: 16px;
  color: #FF7427;
}

.nav-items {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.nav-item {
  color: #333;
  text-decoration: none;
  font-size: 16px;
  padding: 0.5rem 0;
  position: relative;
}

.nav-item:hover,
.nav-item.active {
  color: #FF7427;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #FF7427;
}

.message-flag {
  position: absolute;
  width: 5px;
  height: 5px;
  right: 1%;
  background: #FF7427;
  border-radius: 50%;
}

.login-btn {
  background: #FF7427;
  color: white;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background: #FFAA66;
}
</style>
