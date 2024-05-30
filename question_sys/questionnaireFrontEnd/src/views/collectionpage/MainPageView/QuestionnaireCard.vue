<template>
    <div>
        <el-card shadow="hover" @click.native="dialogOutlineDataVisible = true">
            <div class="card-inner">
                <div class="card-title">
                    {{title}}
                </div>
                <div class="card-state">
                    {{translateLabel[status]}}
                </div>
                <div class="card-time">
                    {{createTime}}
                </div>
            </div>
        </el-card>
        <el-dialog :title="title" :visible.sync="dialogOutlineDataVisible">
            <div>
                Link of Survey：
                <el-link class="copy-link"
                         target="_blank" @click="copy"
                         :data-clipboard-text="this.servername+'/fillin/'+this.id"
                         data-clipboard-action="copy"
                         type="primary"
                >
                    {{this.title}}-click to copy
                </el-link>
            </div>
            <div class="description-box">
                <div>
                    <b>description：</b>{{description}}
                </div>
                <div>
                    <b>status：</b>{{translateLabel[status]}}
                </div>
                <div>
                    <b>participants：</b>{{fillCount}}
                </div>
                <div>
                    <b>release time：</b>{{createTime}}
                </div>
                <div>
                    <b>due：</b>{{endTime}}
                </div>
            </div>

            <el-dialog
                    width="30%"
                    title="Delete the survey？"
                    :visible.sync="deleteVisible"
                    append-to-body
                    center
            >
                <span slot="footer" class="dialog-footer">
                    <el-button @click="deleteVisible=false">cancel</el-button>
                    <el-button @click="deleteQuestionnaire" type="danger">delete</el-button>
                </span>

            </el-dialog>
            <el-button type="danger" @click="deleteVisible = true" icon="el-icon-delete">delete</el-button>

            <el-button v-if="status==='collecting'" @click="closeForm" icon="el-icon-close">close</el-button>

            <el-button v-if="status==='collecting'||status==='closed'" @click="gotoFillIn" icon="el-icon-search">preview
            </el-button>

            <el-button v-if="status==='collecting'||status==='closed'" type="primary" @click="gotoAnalysis"
                       icon="el-icon-s-data">analysis
            </el-button>
            <el-button v-if="status==='editing'" type="primary" @click="gotoEdit" icon="el-icon-edit">edit</el-button>

        </el-dialog>
    </div>
</template>

<script>
    import Clipboard from "clipboard";

    export default {
        name: "questionnaire-card",
        props: {
            id: Number,
            title: String,
            status: String,
            createTime: String,
            endTime: String,
            fillCount: Number,
            description: String,
        },
        methods: {
            gotoAnalysis() {
                const success = this.$router.push('/analysis/' + this.id);
                if (!success) {
                    console.log("analysis error");
                }
            },
            gotoEdit() {
                const success = this.$router.push('/create/' + this.id);
                if (!success) {
                    console.log("analysis error");
                }
            },

            deleteQuestionnaire() {
                this.deleteVisible = false;
                this.dialogOutlineDataVisible = false;
                this.axios.get('/api/deleteQuestionnaire', {params: {questionnaireId: this.id}}).then((response) => {
                    console.log(response);
                    this.$emit("deleteQuestionnaire");
                })
            },
            copy() {
                const clipboard = new Clipboard('.copy-link')
                clipboard.on('success', e => {
                    console.log('the link has been copied')
                    this.$message({message: "the link has been copie", duration: 1000})
                    // free memory
                    clipboard.destroy()
                })
                clipboard.on('error', e => {
                    
                    console.log('your brower does not support copying')
                    // free memory
                    clipboard.destroy()
                })
            },

            gotoFillIn() {
                const routeData = this.$router.resolve('/fillin/' + this.id);
                window.open(routeData.href, '_blank');
            },
            closeForm() {
                this.axios.post('/api/closeQuestionnaire?questionnaireId=' + this.id).then(() => {
                    this.$message({message: 'Your survey has been closed', duration: 1000})
                }).catch(() => {
                    this.$message.error({message: 'error！can not be close！', duration: 1000})
                });
            }
        },
        data: function () {
            return {
                dialogOutlineDataVisible: false,
                deleteVisible: false,
                translateLabel: {
                    'collecting': 'collecting',
                    'editing': 'editing',
                    'closed': 'closed'
                },
            }
        }
    }
</script>

<style scoped>
    .description-box {
        text-align: left;
        font-size: 14px;
        line-height: 35px;
        margin-left: 10%;
        margin-bottom: 15px;
        margin-top: 15px;
    }

    .el-card {
        width: 150px;
        height: 200px;
        margin: 10px;
        display: flex;
        justify-content: center;
        align-items: center;
        border-width: 2px;
        -webkit-user-select: none;
        cursor: pointer;
    }

    .card-inner {
        background-color: rgba(128, 128, 128, 0.1);
        width: 140px;
        height: 190px;
        position: relative;
    }

    .card-title {
        padding-top: 25px;
        font-size: 16px;
        font-weight: bold;
        color: black;
        overflow: hidden;
        width: 75%;
        margin: auto;
    }

    .card-time {
        position: absolute;
        font-size: 10px;
        color: black;
        bottom: 15px;
        width: 100%;
    }

    .card-state {
        position: absolute;
        font-size: 14px;
        color: black;
        bottom: 40px;
        width: 100%;
    }


</style>