<template>
    <div>
        <div class="main-question-list">
            <div style="padding: 0; display: inline">
                <el-card style="position: relative">
                    <div class="box-is-not-selected-wrapper" @click="editTitle">
                        <div v-if="questionnaire.isBoxSelected===false" class="box-is-not-selected">
                            <h1>{{questionnaire.questionnaireTitle}}</h1>
                            <h3>{{questionnaire.questionnaireDescription}}</h3>
                        </div>

                        <div class="edit-icon"><i class="el-icon-edit-outline"></i></div>
                    </div>
                    <div v-if="questionnaire.isBoxSelected===true">
                        <el-form>
                            <el-form-item>
                                <el-input
                                        type="text" style="max-width: 50%"
                                        placeholder="Survey title"
                                        v-model="questionnaire.questionnaireTitle"
                                ></el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-input
                                        type="textarea" style="max-width: 70%"
                                        placeholder="Survey description"
                                        v-model="questionnaire.questionnaireDescription"
                                ></el-input>
                            </el-form-item>


                            <el-form-item>
                                <el-button type="primary" @click="saveQuestionnaireTitle">Save</el-button>
                                <el-button @click="resetQuestionnaireTitle">Reset</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </el-card>
            </div>ss
            <div style="height: 10px"></div>


            <Question v-for="(item,index) in questionList"
                      :key="index"
                      :is-box-selected="item.isBoxSelected"
                      :question-index="index"
                      :question-title="item.questionTitle"
                      :question-nullable="item.questionNullable"
                      :question-description="item.questionDescription"
                      :question-type="item.questionType"
                      :front-choose="item.frontChoose"
                      :front-options-init-value="item.frontOptions"
                      :front-options="frontOptions(index)"
                      :date="item.date"
                      :default-number="item.defaultNumber"
                      :grade-max="item.gradeMax"
                      :number-type="item.numberType"
                      :question-options="item.questionOptions"
                      :text-description="item.textDescription"
                      @clickUnSelected="selectOneBox(index)"
                      @clickSelected="selectOneBox(index)"
                      @saveOneQuestion="saveOneQuestion"
                      @resetQuestion="resetQuestion(index)"
                      @clickDelete="deleteOneBox(index)"
            ></Question>
            <el-card class="add-question" :body-style="{ padding: '10px' }" shadow="hover">
                <div class="add-question-inner" @click="addNewQuestion('not_selected')"><i class="el-icon-plus"></i>
                    Add new question
                </div>
            </el-card>
            <el-card>

                <el-dialog
                        width="30%"
                        title="Confirm deletion？"
                        :visible.sync="deleteVisible"
                        append-to-body
                        center
                >
                <span slot="footer" class="dialog-footer">
                    <el-button @click="deleteVisible=false"> Cancel </el-button>
                    <el-button @click="deleteQuestionnaire" type="danger">Confirm deletion</el-button>
                </span>

                </el-dialog>
                <el-dialog
                        width="30%"
                        title="Released successfully"
                        :visible.sync="releaseVisible"
                        append-to-body
                        center
                >
                    Here is the link of your survey：
                    <el-link class="copy-link"
                             target="_blank" @click="copy"
                             :data-clipboard-text="this.servername+'/fillin/'+this.$route.params.id"
                             data-clipboard-action="copy"
                             type="primary"
                    >
                        {{this.questionnaire.questionnaireTitle}}-click to copy
                    </el-link>
                    <span slot="footer" class="dialog-footer">
                    <el-button @click="releaseEnd" type="primary">close</el-button>
                </span>

                </el-dialog>

                <el-button type="danger" @click="deleteVisible = true"> Delete </el-button>
                <el-button @click="saveQuestionnaire"> Save </el-button>
                <el-button @click="releaseQuestionnaire" type="primary"> Release </el-button>
            </el-card>


        </div>
    </div>
</template>

<script>
    import Question from "./Question";
    import Clipboard from 'clipboard';

    export default {
        name: "MainQuestionList",
        components: {Question},

        data: function () {
            return {
                questionList: [],
                questionnaire: {
                    isBoxSelected: false,
                    questionnaireDescription: "Survey description",
                    questionnaireTitle: "Survey title",
                    questionnaireId: this.$route.params.id,
                },
                deleteVisible: false,
                deleted: false,
                releaseVisible: false,
            }
        },

        methods: {
            fetchData() {
                this.axios.get("/api/getQuestionList", {
                    params: {
                        questionnaireId: this.$route.params.id,
                    }
                }).then((res) => {
                    const tempList = res.data['questionList'];
                    const resList = [];
                    for (const t of tempList) {
                        t['date'] = new Date(t['date']);
                        resList.push(t);
                    }
                    this.questionList = resList;
                    this.$message({message: "Survey loaded", duration: 1000});
                }).catch(() => {
                    this.$message({message: "error！ Survey fail to loaded！", duration: 1000});
                })

                this.axios.get("/api/getQuestionnaireOutline", {
                    params: {
                        questionnaireId: this.$route.params.id
                    }
                }).then((res) => {
                    const temp = {
                        isBoxSelected: false,
                        questionnaireDescription: res.data['questionnaire']['description'],
                        questionnaireTitle: res.data['questionnaire']['title'],
                        questionnaireId: res.data['questionnaire']['questionnaireId'],
                    };
                    this.questionnaire = temp;
                }).catch(() => {
                    this.$message({message: "error！ Survey overview failed to load！", duration: 1000})
                })

            },


            selectOneBox(index) {
                this.questionList[index].isBoxSelected = !this.questionList[index].isBoxSelected;
            },

            resetQuestion(index) {
                return;  // process in the question.vue
            },

            saveOneQuestion(data) {
                const index = data['questionIndex'];
                const oneQuestion = {
                    questionIndex: index,
                    isBoxSelected: true,
                    questionTitle: data['questionTitle'],
                    questionNullable: data['questionNullable'],
                    questionDescription: data['questionDescription'],
                    questionType: data['questionType'],
                    questionOptions: data['questionOptions'],
                    frontOptions: data['frontOptions'],
                    frontChoose: data['frontChoose'],
                    numberType: data['numberType'],
                    defaultNumber: data['defaultNumber'],
                    gradeMax: data['gradeMax'],
                    date: data['date'],
                    textDescription: data['textDescription'],
                };
                this.questionList.splice(index, 1, oneQuestion);
                this.axios.post("/api/saveOneQuestion?questionnaireId=" + this.$route.params.id, {
                    question: oneQuestion,
                }).then(() => {
                    this.$message({message: "Your survey has been saved", duration: 1000});
                }).catch(() => {
                    this.$message({message: "error！fail to save！", duration: 1000});
                });
            },

            deleteOneBox(index) {
                this.questionList.splice(index, 1);
            },

            addNewQuestion(type) {
                const newQuestion = {
                    questionIndex: this.questionList.length,
                    isBoxSelected: true,
                    questionTitle: "",
                    questionNullable: false,
                    questionDescription: "",
                    questionType: type,
                    questionOptions: [''],
                    frontOptions: [[]],
                    frontChoose: false,
                    numberType: 'integer',
                    defaultNumber: 0,
                    gradeMax: 5,
                    date: new Date(),
                    textDescription: '',
                };
                this.questionList.push(newQuestion);
                console.log('add', this.questionList)
            },
            saveQuestionnaireTitle() {
                this.questionnaire.isBoxSelected = false;
                this.axios.post("/api/saveQuestionnaireOutline", {
                    questionnaire: this.questionnaire,
                }).then(() => {
                    this.$message({message: "Your survey has been saved", duration: 1000});
                }).catch(() => {
                    this.$message({message: "error！fail to save！", duration: 1000});
                });
            },
            resetQuestionnaireTitle() {
                this.questionnaire = {
                    isBoxSelected: false,
                    questionnaireDescription: "Survey description",
                    questionnaireTitle: "Survey name",
                    questionnaireId: this.$route.params.id,
                };
            },
            editTitle() {
                this.questionnaire.isBoxSelected = true;
            },
            saveQuestionnaire() {
                this.axios.post("/api/saveQuestionnaire", {
                    questionnaire: this.questionnaire,
                    questionList: this.questionList,
                }).then(() => {
                    this.$message({message: "Your survey has been saved", duration: 1000});
                }).catch(() => {
                    this.$message({message: "error！fail to save！", duration: 1000});
                });
                this.$router.back();
            },
            deleteQuestionnaire() {
                this.deleteVisible = false;
                this.axios.get('/api/deleteQuestionnaire', {params: {questionnaireId: this.questionnaire.questionnaireId}}).then((response) => {
                    console.log(response);
                    this.$router.back();
                    this.deleted = true;
                    this.$message({message: "survey has been deleted", duration: 1000});
                })
            },
            frontOptions: function (index) {
                const res = [];

                for (let i = 0; i < index; i++) {
                    const temp = this.questionList[i];
                    if (temp != null) {
                        const oneQuestion = {
                            label: temp['questionTitle'],
                            value: index,
                            children: temp['questionOptions'].map((item) => {
                                return {value: item, label: item}
                            })
                        }
                        res.push(oneQuestion);
                    }
                }
                return res;
            },

            releaseQuestionnaire() {
                this.axios.post("/api/releaseQuestionnaire?questionnaireId=" + this.$route.params.id)
                    .then(() => {
                        this.$message({message: "Your survey has been released", duration: 1000});
                    }).catch(() => {
                    this.$message({message: "error！fail to release！", duration: 1000});
                });
                this.releaseVisible = true;
            },
            releaseEnd() {
                this.releaseVisible = false;
                this.$router.back();
            },
            copy() {
                const clipboard = new Clipboard('.copy-link')
                clipboard.on('success', e => {
                    console.log('copied')
                    this.$message({message: "copied", duration: 1000})
                    // Release memory
                    clipboard.destroy()
                })
                clipboard.on('error', e => {
                    // copy not support
                    console.log('Your browser does not support automatic copying')
                    // Release memory
                    clipboard.destroy()
                })
            }
        },
        created() {
            this.fetchData();
        },
    }
</script>

<style scoped>
    .main-question-list {
        height: 100%;
        background-color: white;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    .add-question-inner {
        width: 50%;
        height: 50px;
        padding-top: 25px;
        text-align: center;
        margin-left: auto;
        margin-right: auto;
        border-color: rgba(128, 128, 128, 0.5);
        color: rgba(128, 128, 128, 0.5);
        border-width: 2px;
        border-style: dashed;
        font-weight: bolder;
        cursor: pointer;
    }

    .box-is-not-selected {
        position: relative;
    }

    .box-is-not-selected-wrapper {
        cursor: pointer;
    }

    .box-is-not-selected-wrapper:hover .box-is-not-selected {
        filter: blur(8px);
    }

    .edit-icon {
        display: none;
    }

    .box-is-not-selected-wrapper:hover .edit-icon {
        display: inline;
        height: 100%;
        width: 100%;
        background-color: rgba(179, 229, 252, 0.3);
        position: absolute;
        top: 0;
        left: 0;
    }

    .el-icon-edit-outline {
        position: absolute;
        top: calc(50% - 30px);
        left: calc(50% - 30px);
        font-size: 60px;
        color: rgba(128, 0, 128, 0.6);
    }

</style>