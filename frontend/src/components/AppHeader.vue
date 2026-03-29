<template>
    <el-header class="app-header">
        <div class="header-content">
            <div class="logo" @click="router.push('/')">
                <el-icon><Histogram /></el-icon>
                <span>在线投票系统</span>
            </div>

            <el-menu
                :default-active="activeMenu"
                mode="horizontal"
                :ellipsis="false"
                @select="handleMenuSelect"
            >
                <el-menu-item index="/">首页</el-menu-item>
                <el-menu-item index="/polls">投票列表</el-menu-item>
                <el-menu-item v-if="authStore.isAuthenticated" index="/my-polls"
                    >我的投票</el-menu-item
                >
                <el-menu-item v-if="authStore.isAdmin" index="/admin"
                    >管理后台</el-menu-item
                >
            </el-menu>

            <div class="user-actions">
                <template v-if="authStore.isAuthenticated">
                    <el-dropdown>
                        <span class="user-info">
                            <el-avatar :size="32" :icon="UserFilled" />
                            <span class="username">{{
                                authStore.user?.username
                            }}</span>
                            <el-tag
                                v-if="authStore.isAdmin"
                                type="danger"
                                size="small"
                                >管理员</el-tag
                            >
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item @click="handleLogout"
                                    >退出登录</el-dropdown-item
                                >
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </template>
                <template v-else>
                    <el-button type="primary" @click="router.push('/login')"
                        >登录</el-button
                    >
                    <el-button @click="router.push('/register')"
                        >注册</el-button
                    >
                </template>
            </div>
        </div>
    </el-header>
</template>

<script setup>
import { computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { Histogram, UserFilled } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const activeMenu = computed(() => route.path);

function handleMenuSelect(index) {
    router.push(index);
}

function handleLogout() {
    authStore.logout();
    ElMessage.success("已退出登录");
    router.push("/");
}
</script>

<style lang="scss" scoped>
.app-header {
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    position: sticky;
    top: 0;
    z-index: 100;
    height: 60px;
    padding: 0;

    .header-content {
        max-width: 1400px;
        margin: 0 auto;
        height: 100%;
        display: flex;
        align-items: center;
        padding: 0 20px;
    }

    .logo {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 20px;
        font-weight: 600;
        color: #409eff;
        cursor: pointer;
        margin-right: 40px;

        .el-icon {
            font-size: 28px;
        }
    }

    .el-menu {
        flex: 1;
        border-bottom: none;
    }

    .user-actions {
        display: flex;
        align-items: center;
        gap: 10px;

        .user-info {
            display: flex;
            align-items: center;
            gap: 8px;
            cursor: pointer;

            .username {
                font-size: 14px;
                color: #606266;
            }
        }
    }
}
</style>
