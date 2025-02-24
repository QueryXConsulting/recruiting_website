<template>
    <!-- 顶部导航栏 -->
    <div class="nav-bar">
        <div class="nav-item active">首页</div>
        <div class="nav-item">公司</div>
        <div class="nav-item" style="margin-left:  auto;">登录</div>
    </div>
</template>

<script>
export default {
    name: 'TopNav'
}
</script>

<style scoped lang="scss">
.nav-bar {
    display: flex;
    margin-bottom: 20px;
    background-color: white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
    // align-items: center;

    .nav-item {
        // text-align: center;
        padding: 8px 16px;
        margin-right: 10px;
        height: 50px;

        cursor: pointer;
        border-bottom: 2px solid transparent;

        &:hover {
            color: #409EFF;
            border-bottom: 2px solid #409EFF;
        }

        &.active {
            border-bottom: 2px solid #409EFF;
        }
    }
}
</style>
