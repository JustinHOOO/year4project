<template>
    <div>
        <el-form
                v-if="registerChoose===false"
                :rules="rules"
                ref="loginForm"
                v-loading="loading"
                element-loading-text="logining..."
                element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.8)"
                :model="loginForm"
                class="loginContainer">
            <h3 class="loginTitle">Log in</h3>
            <el-form-item prop="username">
                <el-input size="normal" type="text" v-model="loginForm.username" auto-complete="off"
                          placeholder="enter username here"></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input size="normal" type="password" v-model="loginForm.password" auto-complete="off"
                          placeholder="enter password here"></el-input>
            </el-form-item>
            <el-link type="primary" @click="registerChoose = true" class="link">
                Sign up
            </el-link>

            <el-button size="normal" type="primary" style="width: 100%;" @click="submitLogin">Confirm</el-button>
        </el-form>
        <el-form
                v-if="registerChoose===true"
                :rules="rules"
                ref="registerForm"
                v-loading="loading"
                element-loading-text="signing..."
                element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.8)"
                :model="registerForm"
                class="loginContainer">
            <h3 class="loginTitle">Sign up</h3>
            <el-form-item prop="username">
                <el-input size="normal" type="text" v-model="registerForm.username" auto-complete="off"
                          placeholder="set username"></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input size="normal" type="password" v-model="registerForm.password" auto-complete="off"
                          placeholder="set password"></el-input>
            </el-form-item>
            <el-link type="primary" @click="registerChoose = false" class="link">
                Log in
            </el-link>

            <el-button size="normal" type="primary" style="width: 100%;" @click="submitRegister">Sign up</el-button>
        </el-form>
    </div>
</template>

<script>

    export default {
        name: "Login",
        data() {
            return {
                loading: false,
                vcUrl: '/verifyCode?time=' + new Date(),
                loginForm: {
                    username: 'user',
                    password: '123',
                },
                registerForm: {
                    username: '',
                    password: '',
                },
                rules: {
                    username: [{required: true, message: 'please enter username', trigger: 'blur'}],
                    password: [{required: true, message: 'please enter password', trigger: 'blur'}],
                },
                registerChoose: false,
            }
        },
        methods: {

            submitLogin() {
                this.$refs.loginForm.validate((valid) => {
                    if (valid) {
                        this.axios.post('/api/login?username=' + this.loginForm.username + '&password=' + this.loginForm.password).then((res) => {
                            this.loading = false;
                            if(res.data==='wrong'){
                                this.$message.error({message:'wrong username or password！',duration:1000});
                            }
                            else if(res.data==='success'){
                                this.$message.success({message:'logged in',duration:1000});
                                this.$router.replace('/collection');
                                this.$store.commit('login',this.loginForm.username);
                            }
                        })
                    } else {
                        return false;
                    }
                });
            },
            submitRegister() {
                this.$refs.registerForm.validate((valid) => {
                    if (valid) {
                        this.axios.post('/api/register?username=' + this.registerForm.username + '&password=' + this.registerForm.password).then((res) => {
                            this.loading = false;
                            if(res.data==='usernameAlready'){
                                this.$message.error({message:'username already existed！',duration:1000});
                            }else if(res.data==='success'){
                                this.$message.success({message:'successfully signed up！',duration:1000});
                                this.registerChoose = false;
                            }
                        })
                    } else {
                        return false;
                    }
                });
            }
        }
    }
</script>

<style>
    .loginContainer {
        border-radius: 15px;
        background-clip: padding-box;
        margin: 100px auto;
        width: 250px;
        padding: 15px 35px 15px 35px;
        background: #fff;
        border: 1px solid #eaeaea;
        box-shadow: 0 0 25px #cac6c6;
    }

    .loginTitle {
        margin: 15px auto 20px auto;
        text-align: center;
        color: #505458;
    }

    .loginRemember {
        text-align: left;
        margin: 0px 0px 15px 0px;
    }

    .el-form-item__content {
        display: flex;
        align-items: center;
    }

    .link {
        margin-bottom: 15px;
    }
</style>
