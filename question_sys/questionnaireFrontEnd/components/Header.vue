<template>
    <el-row type="flex" justify="center" align="middle" class="header-row">
        <el-col :span="21">
            <el-menu mode="horizontal" :default-active="$route.path" router class="header-menu">
                <el-menu-item index="/" class="menu-title">Polling & Survey</el-menu-item>
                <el-menu-item index="/collection" class="menu-item">All Surveys</el-menu-item>
                <el-menu-item index="/create" class="menu-item" @click="gotoCreate">Create Survey</el-menu-item>
                <el-menu-item index="/help" class="menu-item">Help</el-menu-item>
            </el-menu>
        </el-col>
        <el-col :span="3" class="user-col">
            <el-avatar v-if="!$store.state.login" @click.native="gotoLogin" class="login-avatar">Log In</el-avatar>
            <el-dropdown v-if="$store.state.login" class="user-profile">
                <el-avatar class="user-avatar">{{ $store.state.username.charAt(0) }}</el-avatar>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item class="share-link" icon="el-icon-share" @click="copyShare" :data-clipboard-text="servername">
                        Share Link
                    </el-dropdown-item>
                    <el-dropdown-item icon="el-icon-close" @click.native="logout">Log Out</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </el-col>
    </el-row>
</template>

<script>
import Clipboard from "clipboard";

export default {
    name: "Header",
    data() {
        return {
            servername: window.location.origin
        };
    },
    methods: {
        gotoCreate() {
            this.axios.get('/api/createQuestionnaire').then(response => {
                this.$router.push('/create/' + response.data.id);
            });
        },
        copyShare() {
            const clipboard = new Clipboard('.share-link');
            clipboard.on('success', () => {
                this.$message({ message: "Link copied", duration: 1000 });
                clipboard.destroy();
            });
            clipboard.on('error', () => {
                console.log('Copying not supported');
                clipboard.destroy();
            });
        },
        logout() {
            this.axios.post("/api/logout").then(() => {
                this.$store.commit('logout');
                this.$router.replace('/login');
            });
        },
        gotoLogin() {
            this.$router.replace('/login');
        }
    }
};
</script>

<style scoped>
.header-row {
    background-color: #f0f2f5;
    padding: 10px 0;
    border-bottom: 1px solid #dcdfe6;
}
.header-menu {
    background-color: transparent;
    border-bottom: none;
}
.menu-title {
    font-size: 18px;
    font-weight: bold;
    color: #409eff;
}
.menu-item {
    font-size: 16px;
    color: #606266;
}
.menu-item:hover {
    color: #409eff;
}
.user-col {
    display: flex;
    justify-content: flex-end;
    align-items: center;
}
.login-avatar {
    cursor: pointer;
    font-size: 16px;
    color: #409eff;
}
.user-profile {
    display: flex;
    align-items: center;
}
.user-avatar {
    background-color: #409eff;
    color: white;
    font-weight: bold;
}
.share-link {
    display: flex;
    align-items: center;
}
</style>
