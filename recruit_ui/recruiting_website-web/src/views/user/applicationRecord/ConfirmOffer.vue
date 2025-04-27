<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import WBTable from '@/components/table/WBTable.vue';
import WBDialog from '@/components/WBDialog.vue';
import { offerFilePath, offerList, offerStatus, offerSignature, postMessage } from '@/api/user/UserApi';
import { ElMessage, ElMessageBox } from 'element-plus';

const currentPage = ref(1);// 当前页
const pageSize = ref(10);// 每页显示条数
const total = ref(0);// 总条数
const pages = ref(0);// 总页数
let jobName = null;// 职位名称
let companyId = null;// 公司ID

// 表格根数据
const rootData = reactive([
  { tag: '', prop: "jobId", label: "职位ID" },
  { tag: '', prop: "userId", label: "用户ID" },
  { tag: '', prop: "offerId", label: "offerID" },
  { tag: '', prop: "companyId", label: "公司ID" },
  { tag: '', prop: "jobPosition", label: "面试岗位" },
  { tag: '', prop: "companyInfoName", label: "公司名" },
  { tag: '', prop: "offersDate", label: "发送时间" },
  { tag: '', prop: "offersStatus", label: "offer状态" },
]);

// 表格列显示
const tableColumns = reactive([]);
(() => {
  const o = rootData.filter((item) => !item.prop.includes('Id'));
  for (let i = 0; i < o.length; i++) {
    tableColumns.push({ prop: o[i].prop, label: o[i].label });
  }
})()

// 表格数据
const tableData = ref([]);
const getOfferList = async () => {
  const res = await offerList(currentPage.value, pageSize.value);
  tableData.value = res.content.records;
  currentPage.value = res.content.current;
  pageSize.value = res.content.size;
  total.value = res.content.total;
  pages.value = res.content.pages;
  ElMessage.success(res.message);
}
onMounted(async () => {
  getOfferList();
})

// 获取选项label
const getOptionLabel = (options, value) => {
  const option = options.find((item) => item.value === value);
  return option ? option.label : value;
}
// 获取tag类型
const getTagType = (options, value) => {
  const option = options.find((item) => item.value === value);
  return option ? option.tag : 'default';
}


// 应聘状态选项
const statusOptions = [
  { tag: 'warning', value: '0', label: '待发送' },
  { tag: 'success', value: '1', label: '已接受' },
  { tag: 'danger', value: '2', label: '拒绝' },
  { tag: 'info', value: '3', label: '撤销' },
  { tag: 'primary', value: '4', label: '已发送' },
  { tag: 'danger', value: '5', label: '已打回' },
  { tag: 'success', value: '6', label: '通过' },
]

// 表格右侧操作栏
const hasOperation = ref(true);// 是否有操作栏
const tableOperation = [
  // { type: 'default', text: '查看' },
  { type: 'danger', text: '拒绝' },
  { type: 'success', text: '接受' },
]
let filteredOperation = tableOperation;
const getStatusOptionsLabel = (value) => {
  if (value === '0' || value === '1' || value == '2' || value == '3' || value == '6') {
    filteredOperation = [];
  } else {
    filteredOperation = tableOperation;
  }
  return getOptionLabel(statusOptions, value);
}


/*================= 预览弹窗 =================*/
const isShowPreview = ref(false);// 详情弹窗是否显示
const pdfUrl = ref('');// pdf地址

// 预览offer
const previewOffer = async (row, _, e) => {
  // 点击表格右侧按钮会触发表格行的点击事件，这里需要阻止冒泡
  if (e.target.innerText === '接受' || e.target.innerText === '拒绝') return;
  const _filePath = await offerFilePath(row.offerId);
  if (!_filePath) return;
  isShowPreview.value = true;
  pdfUrl.value = _filePath.content;
}

/*================= 签名弹窗 =================*/
const isShowSignature = ref(false);// 签名弹窗是否显示
let offerId = '';
// cavas绘制相关变量
let canvasRef, ctx, painting;
let lastX = 0;
let lastY = 0;

// 初始化canvas
const initCanvas = () => {
  canvasRef = document.querySelector('.signature-canvas');
  ctx = canvasRef.getContext('2d');
  painting = false;
  // 设定Canvas尺寸
  canvasRef.width = canvasRef.offsetWidth;
  canvasRef.height = canvasRef.offsetHeight;
  // 填充背景
  ctx.fillStyle = '#fff';
  ctx.fillRect(0, 0, canvasRef.width, canvasRef.height);
}

// 开始绘制
const startPainting = (e) => {
  painting = true;
  [lastX, lastY] = [e.offsetX, e.offsetY];
  ctx.beginPath();
  ctx.moveTo(lastX, lastY);
}
// 结束绘制
const stopPainting = () => {
  // 结束绘制
  painting = false;
  ctx.closePath();
}
// 绘制
const draw = (e) => {
  if (!painting) return;
  ctx.lineWidth = 2;
  ctx.lineTo(lastX, lastY);
  ctx.stroke();
  [lastX, lastY] = [e.offsetX, e.offsetY];
}

// 提交签名
const signatureSubmit = async () => {
  const dataURL = canvasRef.toDataURL('image/png');
  const byteString = atob(dataURL.split(',')[1]);
  const mimeType = dataURL.split(',')[0].split(':')[1].split(';')[0];
  const ab = new ArrayBuffer(byteString.length);
  const ia = new Uint8Array(ab);
  for (let i = 0; i < byteString.length; i++) {
    ia[i] = byteString.charCodeAt(i);
  }
  const blob = new Blob([ab], { type: mimeType });
  const formData = new FormData();
  const imgName = Date.now() + '.' + mimeType.split('/')[1];
  formData.append('image', blob, imgName);
  const _res = await offerSignature(offerId, formData);
  if (_res) {
    isShowSignature.value = false;
    if (_res.code !== 200) {
      return ElMessage.error(_res.message);
    }
    const _res2 = await offerStatus(offerId, '1');
    if (_res2) {
      getOfferList();
    }
  }
  if (companyId && jobName) {
    await postMessage({ userId: companyId, content: `我已接收您发布的${jobName}岗位的offer，请您及时查看。 ——此消息由系统自动发送，请勿回复。` });
    return;
  }
  ElMessage.error('未知错误，请联系管理员');
}

// 清空画布
const clearCanvas = () => {
  ctx.clearRect(0, 0, canvasRef.width, canvasRef.height);
}

// 表格点击事件
const handleOperationClick = async (btnIndex, row, text) => {
  switch (text) {
    case '拒绝': // 拒绝
      const _updateResult = await offerStatus(row.offerId, 2);

      if (_updateResult) {
        getOfferList();
      }
      break;
    case '接受': // 接受
      // ElMessageBox.confirm(
      //   '接受这个offer之后，其他offer都会自动拒绝，请慎重选择！',
      //   '确认offer',
      //   {
      //     confirmButtonText: '确定',
      //     cancelButtonText: '取消',
      //     type: 'warning',
      //   }
      // ).then(async () => {
        isShowSignature.value = true;
        await nextTick();// 等待DOM加载完成
        initCanvas(); // 初始化canvas
        offerId = row.offerId;
        // 初始化留言相关
        jobName = row.jobPosition;
        companyId = row.companyId;
      // })
      break;
    default:
      break;
  }

}


// 一页条数变化时触发
const handleSizeChange = (size) => {
  pageSize.value = size
}
// 当前页码变化时触发
const handleCurrentChange = (page) => {
  currentPage.value = page
}

</script>

<template>
  <div>
    <WBTable :total="total" @update:currentPage="handleCurrentChange" @update:page-size="handleSizeChange"
      :table-columns="tableColumns" v-model:tableData="tableData" v-model:currentPage="currentPage"
      @row-click="previewOffer" :has-operation="hasOperation" v-model:pageSize="pageSize" border style="cursor: pointer;">
      <template #default="scope">
        <el-tag v-if="scope.prop === 'offersStatus'"
          :type="getTagType(statusOptions, tableData[scope.$index][scope.prop])">
          {{ getStatusOptionsLabel(tableData[scope.$index][scope.prop]) }}
        </el-tag>
        <span v-else>{{ tableData[scope.$index][scope.prop] ? tableData[scope.$index][scope.prop] : '-' }}</span>
      </template>
      <!-- 表格右侧操作栏 -->
      <template #operation="scope">
        <el-button v-for="(item, index) in filteredOperation" :type="item.type" :key="index" size="small"
          @click="handleOperationClick(scope.$index, scope.row, item.text)">
          {{ item.text }}
        </el-button>
      </template>
    </WBTable>

    <!-- 签名弹窗 -->
    <WBDialog v-model="isShowSignature" @cancel="clearCanvas" @submit="signatureSubmit" cancel-text="清空" width="50%"
      title="offer签名" draggable>
      <fieldset style="display: flex; justify-content: center; align-items: center; height: 300px;">
        <canvas @mousedown="startPainting" @mousemove="draw" @mouseleave="stopPainting" @mouseup="stopPainting"
          class="signature-canvas"></canvas>
      </fieldset>
    </WBDialog>

    <!-- pdf预览弹窗 -->
    <WBDialog v-model="isShowPreview" fullscreen title="offer预览">
      <iframe :src="pdfUrl" class="pdf-preview" frameborder="0"></iframe>
      <template #footer><i></i></template>
    </WBDialog>
  </div>
</template>

<style lang="scss" scoped>
/* 为iframe设置一些基本样式 */
.pdf-preview {
  width: 100%;
  /* 以适应其父容器 */
  height: 600px;
  /* 高度可以根据需要调整 */
  border: none;
  /* 去掉边框 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  /* 添加阴影效果 */
  margin: 5px auto;
  /* 上下外边距为20px，左右自动居中 */
  display: block;
  /* 将iframe显示为块级元素 */
  background-color: #f5f5f5;
  /* 背景颜色，当PDF加载失败时会显示 */

}

.signature-canvas {
  width: 100%;
  height: 100%;
  border: 2px solid #269eeeef;
  border-radius: 10px;
}
</style>