<template>

    <el-row type="flex" justify="center" align="middle" class="header-row">
        <el-col :span="21">
            <el-menu mode="horizontal" :default-active="$route.path" router>
                <el-menu-item class="el-menu-item">Online Polling and Survey System</el-menu-item>
                <el-menu-item class="el-menu-item" index="/collection">All Surveys</el-menu-item>
                <el-menu-item class="el-menu-item" index="/create" @click="gotoCreate">Create Survey</el-menu-item>
                <el-menu-item class="el-menu-item" index="/help">Need Helps?</el-menu-item>
            </el-menu>
        </el-col>
        <el-col :span="3">
            <el-avatar v-if="!this.$store.state.login" @click.native="gotoLogin" style="cursor: pointer">Log Out</el-avatar>
            <el-dropdown class="user-profile" v-if="this.$store.state.login">
                <el-avatar><b>{{this.$store.state.username.substr(0,1)}}</b></el-avatar>
                <el-dropdown-menu slot="dropdown" >
                    <el-dropdown-item class="share-copy-link" icon="el-icon-share" @click="copyShare"
                                      :data-clipboard-text="this.servername"
                                      data-clipboard-action="copy"
                    >share the link</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-close" @click.native="logout"> Log Out</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </el-col>

    </el-row>

</template>

<script>
    import Clipboard from "clipboard";

    export default {
        name: "Header",

        methods: {
            gotoCreate() {
                this.axios.get('/api/createQuestionnaire').then((response) => {
                    const success = this.$router.push('/create/'+response.data['id']);
                })
            },
            copyShare() {
                const clipboard = new Clipboard('.share-copy-link')
                clipboard.on('success', e => {
                    console.log('the link has been copied')
                    this.$message({message: "the link has been copied", duration: 1000})
                    // free memory
                    clipboard.destroy()
                })
                clipboard.on('error', e => {
                    console.log('Your brower does not support copying')
                    // free memory
                    clipboard.destroy()
                })
            },
            logout() {
                this.axios.post("/api/logout").then(()=>{
                    this.$store.commit('logout');
                    this.$router.replace('/login')
                })
            },
            gotoLogin() {
                this.$router.replace('/login')
            }
        }

    }
</script>

<style scoped>
    .user-profile {
        justify-content: center;
        align-items: center;
    }
    .header-row{

    }
</style>