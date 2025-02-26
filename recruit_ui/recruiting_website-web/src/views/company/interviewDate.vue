<template>
  <div class="interview-scheduler">
    <div class="scheduler-header">
      <h2>面试时间安排</h2>
      <div class="scheduler-tips">
        <el-alert title="温馨提示" type="info" :closable="false" description="请选择可用于面试的时间段，求职者将在这些时间段内选择合适的面试时间。" />
      </div>
    </div>

    <div class="scheduler-content">
      <div class="calendar-section">
        <div class="section-title">
          <span>选择日期和时间</span>
        </div>

        <div class="calendar-container">
          <el-calendar v-model="currentDate">
            <template #header="{ date }">
              <div class="calendar-header">
                <el-button-group>
                  <el-button @click="changeMonth(-1)">上个月</el-button>
                  <el-button @click="changeMonth(1)">下个月</el-button>
                </el-button-group>
                <span class="today-date">{{ currentDate.getFullYear() }} - {{ currentDate.getMonth() + 1 }}</span>
              </div>
            </template>
            <template #date-cell="{ data }">
              <div class="date-cell" :class="{
                'has-slots': hasTimeSlots(data.date),
                'disabled': isDateDisabled(data.date) || !isCurrentMonth(data.date)
              }" @click.stop="handleDateCellClick(data.date)">
                <span class="date-number">{{ formatDayOnly(data.day) }}</span>
                <div class="slot-indicator" v-if="hasTimeSlots(data.date)">
                  {{ getTimeSlotsCount(data.date) }}个时段
                </div>
              </div>
            </template>
          </el-calendar>
        </div>
      </div>

      <div class="time-slots-section">
        <div class="section-title">已设置的面试时段</div>
        <div class="time-slots-list">
          <el-collapse v-model="activeCollapse">
            <el-collapse-item v-for="(slots, date) in groupedTimeSlots" :key="date" :title="formatDate(date)"
              :name="date">
              <div class="slot-items">
                <div v-for="(slot, index) in slots" :key="index" class="slot-item">
                  <span>{{ formatTimeRange(slot.start, slot.end) }}</span>
                  <div class="slot-actions">
                    <el-tag size="small" :type="'success'">
                      可预约
                    </el-tag>
                    <el-button type="danger" size="small" circle @click="removeTimeSlot(date, index)"
                      :disabled="slot.status !== 'available'">
                      <el-icon>
                        <Delete />
                      </el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
    </div>

    <!-- 时间选择对话框 -->
    <el-dialog v-model="timeDialogVisible" title="设置面试时间段" width="400px" :close-on-click-modal="false">
      <div class="time-picker-container">
        <el-form :model="timeForm" label-width="100px">
          <!-- <el-form-item label="面试时长">
            <el-select v-model="timeForm.duration" placeholder="请选择面试时长">
              <el-option label="30分钟" :value="30" />
              <el-option label="45分钟" :value="45" />
              <el-option label="60分钟" :value="60" />
              <el-option label="90分钟" :value="90" />
            </el-select>
          </el-form-item> -->
          <el-form-item label="时间段">
            <div class="time-range-picker">
              <el-time-picker v-model="timeForm.startTime" format="HH:mm" placeholder="开始时间" class="time-picker" />
              <span class="time-separator">至</span>
              <el-time-picker v-model="timeForm.endTime" format="HH:mm" placeholder="结束时间" class="time-picker" />
            </div>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="timeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmTimeSelection">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';
import { addInterviewDate, selectInterviewDate } from '@/api/company/companyApi';
import userStore from '@/store/user';

const currentDate = ref(new Date());
const timeDialogVisible = ref(false);
const selectedDate = ref(null);
const activeCollapse = ref([]);
const timeForm = ref({
  startTime: null,
  endTime: null,
  duration: 30
});

const timeSlots = ref({});
const groupedTimeSlots = computed(() => {
  return timeSlots.value;
});

const interviewDates = ref([]);

const fetchInterviewDates = async () => {
  try {
    const response = await selectInterviewDate();
    interviewDates.value = response.data.map(date => ({
      id: date.interviewDateId,
      start: new Date(date.interviewDateStart),
      end: new Date(date.interviewDateEnd)
    }));
  } catch (error) {
    ElMessage.error('获取面试时间失败');
  }
};

onMounted(() => {
  fetchInterviewDates();
});

const hasTimeSlots = (date) => {
  const dateStr = formatDate(new Date(date));
  return timeSlots.value[dateStr] && timeSlots.value[dateStr].length > 0;
};

const getTimeSlotsCount = (date) => {
  const dateStr = formatDate(new Date(date));
  return timeSlots.value[dateStr]?.length || 0;
};

const isDateDisabled = (date) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return new Date(date) < today;
};

const handleDateClick = (date) => {
  if (isDateDisabled(date)) {
    ElMessage.warning('不能选择过去的日期');
    return;
  }

  selectedDate.value = new Date(date);
  timeDialogVisible.value = true;
  const defaultStart = new Date(date);
  defaultStart.setHours(9, 0, 0);
  const defaultEnd = new Date(date);
  defaultEnd.setHours(18, 0, 0);

  timeForm.value.startTime = defaultStart;
  timeForm.value.endTime = defaultEnd;
};

const confirmTimeSelection = () => {
  if (!timeForm.value.startTime || !timeForm.value.endTime) {
    ElMessage.warning('请选择完整的时间段');
    return;
  }

  const start = new Date(timeForm.value.startTime);
  const end = new Date(timeForm.value.endTime);

  if (end <= start) {
    ElMessage.warning('结束时间必须晚于开始时间');
    return;
  }

  const dateStr = formatDate(selectedDate.value);
  if (!timeSlots.value[dateStr]) {
    timeSlots.value[dateStr] = [];
  }

  timeSlots.value[dateStr].push({
    start: formatTime(start),
    end: formatTime(end),
    duration: timeForm.value.duration,
    status: 'available'
  });

  const interviewData = {
    companyId: userStore().userInfo.companyInfoId,
    interviewDateStart: start,
    interviewDateEnd: end
  };

  addInterviewDate(interviewData).then(() => {
    ElMessage.success('面试时间段设置成功');
  }).catch(() => {
    ElMessage.error('设置面试时间段失败');
  });

  timeDialogVisible.value = false;
  activeCollapse.value = [dateStr];
};

const removeTimeSlot = (date, index) => {
  timeSlots.value[date].splice(index, 1);
  if (timeSlots.value[date].length === 0) {
    delete timeSlots.value[date];
  }
  ElMessage.success('已删除该时间段');
};

const formatDate = (date) => {
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};

const formatTime = (date) => {
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

const formatTimeRange = (start, end) => {
  return `${start} - ${end}`;
};

const formatDayOnly = (dateString) => {
  return dateString.split('-')[2];
};

const handleDateCellClick = (date) => {
  if (!isCurrentMonth(date) || isDateDisabled(date)) {
    return;
  }
  handleDateClick(date);
};

const changeMonth = (delta) => {
  const newDate = new Date(currentDate.value);
  newDate.setMonth(newDate.getMonth() + delta);
  currentDate.value = newDate;
};

const isCurrentMonth = (date) => {
  const currentMonth = currentDate.value.getMonth();
  const dateToCheck = new Date(date);
  return dateToCheck.getMonth() === currentMonth;
};
</script>

<style scoped>
.interview-scheduler {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 48px);
}

.scheduler-header {
  margin-bottom: 24px;
}

.scheduler-header h2 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.scheduler-tips {
  margin-bottom: 24px;
  max-width: 800px;
}

.scheduler-content {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 24px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.calendar-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  padding: 16px;
}

.date-cell {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  padding: 12px 8px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.date-number {
  font-size: 15px;
  margin-bottom: 6px;
  font-weight: 500;
}

.slot-indicator {
  font-size: 12px;
  color: #409EFF;
  background: #ecf5ff;
  padding: 3px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.date-cell:hover {
  background-color: #f0f7ff;
  transform: translateY(-2px);
}

.time-slots-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.slot-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.slot-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.slot-item:hover {
  background: #f0f7ff;
  transform: translateX(4px);
}

.slot-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.time-picker-container {
  padding: 24px;
}

.time-range-picker {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.time-picker {
  width: 100%;
}

.time-separator {
  color: #606266;
  font-weight: 500;
  align-self: center;
  padding: 4px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
}

.date-cell.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: #f5f7fa;
}

.date-cell.disabled:hover {
  transform: none;
  background-color: #f5f7fa;
  box-shadow: none;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  margin-bottom: 12px;
}

.today-date {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
  background: #f4f4f5;
  padding: 6px 12px;
  border-radius: 6px;
}

:deep(.el-calendar) {
  background: transparent;
  border: none;
}

:deep(.el-calendar__header) {
  padding: 0;
}

:deep(.el-calendar-table) {
  border-spacing: 8px;
  border-collapse: separate;
}

:deep(.el-calendar-day) {
  padding: 0;
  height: auto;
}

:deep(.el-collapse-item__header) {
  font-size: 15px;
  font-weight: 500;
}

:deep(.el-tag) {
  border-radius: 6px;
  padding: 0 10px;
}
</style>
