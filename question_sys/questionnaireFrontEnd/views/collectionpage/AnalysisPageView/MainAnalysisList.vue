<template>
    <div>
        <div class="main-analysis-list">
            <el-card>
                <h1>{{questionnaire.title}}</h1>
                <div class="description-box">
                    <div>
                        <b>description：</b>{{questionnaire.description}}
                    </div>
                    <div>
                        <b>status：</b>{{translateLabel[questionnaire.status]}}
                    </div>
                    <div>
                        <b>participants：</b>{{questionnaire.fillCount}}
                    </div>
                    <div>
                        <b>release time：</b>{{questionnaire.createTime}}
                    </div>
                    <div>
                        <b>due：</b>{{questionnaire.endTime}}
                    </div>
                </div>

            </el-card>
            <AnalysisCard
                    v-for="(item,index) in questionList"
                    :key="index"
                    :question-index="index+1"
                    :question-id="item.questionId"
                    :question-title="item.questionTitle"
                    :question-type="item.questionType"
            >
            </AnalysisCard>

        </div>
    </div>

</template>

<script>
    import AnalysisCard from "./AnalysisCard";

    export default {
        name: "MainAnalysisList",
        components: {AnalysisCard},
        methods: {
            fetchData() {
                this.axios.get("/api/getQuestionList", {
                    params: {
                        questionnaireId: this.$route.params.id,
                    }
                }).then((res) => {
                    this.questionList = res.data['questionList'];
                    this.$message({message: "Survey has been loaded", duration: 1000});
                }).catch(() => {
                    this.$message({message: "error！failed to load survey！", duration: 1000});
                })

                this.axios.get("/api/getQuestionnaireOutline", {
                    params: {
                        questionnaireId: this.$route.params.id
                    }
                }).then((res) => {
                    this.questionnaire = res.data['questionnaire'];
                }).catch(() => {
                    this.$message({message: "error！failed to load survey！", duration: 1000})
                })
            }
        },
        mounted() {
            this.fetchData();
        },
        data: function () {
            return {
                questionnaire: {
                    title: '',
                    description: 'description',
                    status: 'finished',
                    createTime: null,
                    endTime: null,
                    fillCount: 299,
                },
                questionList: [],
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
    .main-analysis-list {
        width: 70%;
        margin: auto;
    }

    .description-box {
        text-align: left;
        padding-left: 10%;
        line-height: 30px;
        font-size: 14px;
    }
</style>