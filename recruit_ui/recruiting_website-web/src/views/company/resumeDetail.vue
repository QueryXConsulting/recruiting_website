<template>
  <div class="resume-detail">
    <el-card class="resume-card">
      <div class="resume-header">
        <div class="back-button" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
        </div>
        <h1>个人简历</h1>
      </div>

      <div class="basic-info">
        <div class="info-grid">
          <div class="info-item">
            <span class="label">姓名：</span>
            <span class="content">{{ resumeData.resumeName }}</span>
          </div>
          <div class="info-item">
            <span class="label">手机号：</span>
            <span class="content">{{ resumeData.resumePhone }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱：</span>
            <span class="content">{{ resumeData.resumeEmail }}</span>
          </div>
          <div class="info-item">
            <span class="label">性别：</span>
            <span class="content">{{ resumeData.resumeGender }}</span>
          </div>
          <div class="info-item">
            <span class="label">出生日期：</span>
            <span class="content">{{ formatDate(resumeData.resumeBirth) }}</span>
          </div>
          <div class="info-item">
            <span class="label">婚姻状况：</span>
            <span class="content">{{ resumeData.resumeMarriage }}</span>
          </div>
          <div class="info-item">
            <span class="label">政治背景：</span>
            <span class="content">{{ resumeData.resumePolitical }}</span>
          </div>
        </div>
      </div>

      <div class="education-section">
        <div class="section-title">
          <i class="el-icon-education" />
          <h2>教育背景</h2>
        </div>
        <div class="education-info">
          <div class="edu-grid">
            <div class="edu-item">
              <span class="label">毕业院校：</span>
              <span class="content">{{ resumeData.resumeCollege }}</span>
            </div>
            <div class="edu-item">
              <span class="label">专业：</span>
              <span class="content">{{ resumeData.resumeMajor }}</span>
            </div>
            <div class="edu-item">
              <span class="label">教育程度：</span>
              <span class="content">{{ resumeData.resumeEducation }}</span>
            </div>
            <div class="edu-item">
              <span class="label">在校时间：</span>
              <span class="content">{{ formatDate(resumeData.resumeEducationDate) }}</span>
            </div>
            <div class="edu-item">
              <span class="label">专业排名：</span>
              <span class="content">{{ resumeData.resumeMajorRank }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="resume-section">
        <div class="section-title">
          <i class="el-icon-user" />
          <h2>求职意向</h2>
        </div>
        <div class="section-content">
          <div class="job-info">
            <div class="job-item">
              <span class="label">工作经验：</span>
              <span class="content">{{ resumeData.resumeExperience }}</span>
            </div>
            <div class="job-item">
              <span class="label">意向工作：</span>
              <span class="content">{{ resumeData.resumeJob }}</span>
            </div>
            <div class="job-item">
              <span class="label">期望薪资：</span>
              <span class="content">{{ resumeData.resumeSalary }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="resume-section">
        <div class="section-title">
          <i class="el-icon-medal" />
          <h2>专业技能</h2>
        </div>
        <div class="section-content">
          <p class="skills">{{ resumeData.resumeSkills }}</p>
        </div>
      </div>


      <div class="resume-section">
        <div class="section-title">
          <i class="el-icon-office-building" />
          <h2>项目经验</h2>
        </div>
        <div class="section-content">
          <p class="project">{{ resumeData.resumeProject }}</p>
        </div>
      </div>



      <div class="resume-section">
        <div class="section-title">
          <i class="el-icon-suitcase" />
          <h2>工作经历</h2>
        </div>
        <div class="section-content">
          <p class="employment-history">{{ resumeData.resumeEmploymentHistory }}</p>
        </div>
      </div>

      <div class="resume-section">
        <div class="section-title">
          <i class="el-icon-document" />
          <h2>个人介绍</h2>
        </div>
        <div class="section-content">
          <p class="introduction">{{ resumeData.resumeIntroduction }}</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>

import { selectResume } from '@/api/company/companyApi'
import router from '@/router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'


const route = useRoute()
const resumeData = ref({})


onMounted(() => {
  getResumeDetail()
})

const goBack = () => {
  router.go(-1)
}


const getResumeDetail = async () => {
  try {
    const res = await selectResume({
      resumeId: route.query.resumeId,
      resumeType: '0'
    })
    if (res.code === 200) {
      resumeData.value = res.content
    }
  } catch (error) {
    console.error('获取简历详情失败：', error)
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

onMounted(() => {
  getResumeDetail()
})
</script>

<style scoped>
.resume-detail {
  padding: 40px;
  background-color: #f0f2f5;
  min-height: 100vh;
  display: flex;
  justify-content: center;
}

.resume-card {
  width: 210mm;
  min-height: 297mm;
  padding: 60px;
  background: #fff;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.resume-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 8px;
  background: linear-gradient(90deg, #409EFF, #67C23A, #E6A23C);
}

.resume-header {
  text-align: center;
  margin-bottom: 50px;
  position: relative;
  padding-bottom: 25px;
}

.resume-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 120px;
  height: 4px;
  background: linear-gradient(90deg, #409EFF, #67C23A, #E6A23C);
  border-radius: 4px;
}

.resume-header h1 {
  font-size: 36px;
  color: #1a1a1a;
  font-weight: 700;
  margin: 0;
  letter-spacing: 3px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.basic-info {
  margin-bottom: 45px;
  background: linear-gradient(145deg, #f8fafc, #f0f4f8);
  padding: 30px;
  border-radius: 12px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.03);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item,
.edu-item,
.job-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  transition: all 0.3s ease;
  border-radius: 8px;
}

.label {
  min-width: 110px;
  color: #4a5568;
  font-weight: 600;
  font-size: 15px;
  position: relative;
  padding-left: 15px;
}

.label::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 4px;
  background: #409EFF;
  border-radius: 50%;
}

.content {
  color: #2d3748;
  flex: 1;
  font-size: 15px;
  line-height: 1.6;
}

.resume-section {
  margin-bottom: 35px;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e8eef3;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 50px;
  height: 2px;
  background: #409EFF;
}

.section-title i {
  color: #409EFF;
  margin-right: 15px;
  font-size: 24px;
  background: #f0f7ff;
  padding: 8px;
  border-radius: 8px;
}

.section-title h2 {
  font-size: 20px;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
  letter-spacing: 1px;
}

.section-content {
  padding: 0 20px;
}

.education-info,
.job-info {
  display: grid;
  gap: 20px;
  background: linear-gradient(145deg, #f8fafc, #f0f4f8);
  padding: 25px;
  border-radius: 12px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.03);
}

.introduction,
.project,
.skills,
.employment-history {
  line-height: 2;
  color: #2d3748;
  margin: 0;
  text-align: justify;
  white-space: pre-wrap;
  background: linear-gradient(145deg, #f8fafc, #f0f4f8);
  padding: 25px;
  border-radius: 12px;
  font-size: 15px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.03);
}

.info-item:hover,
.edu-item:hover,
.job-item:hover {
  background: rgba(64, 158, 255, 0.08);
  transform: translateX(5px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.edu-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.edu-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background: linear-gradient(145deg, #f8fafc, #f0f4f8);
  border-radius: 8px;
  transition: all 0.3s ease;
}


.edu-item:last-child:nth-child(odd) {
  grid-column: 1 / -1;
}


@media print {
  .resume-detail {
    padding: 0;
    background: none;
  }

  .resume-card {
    box-shadow: none;
    width: 100%;
    padding: 30px;
  }

  .basic-info,
  .education-info,
  .job-info,
  .introduction,
  .project {
    background: none;
    padding: 15px 0;
  }
}


.resume-header {
  text-align: center;
  margin-bottom: 50px;
  position: relative;
  padding-bottom: 25px;
}

.back-button {
  position: absolute;
  left: 0;
  top: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #409EFF;
  font-size: 20px;
  transition: transform 0.3s ease;
}

.back-button:hover {
  transform: translateX(-5px);
}


</style>
