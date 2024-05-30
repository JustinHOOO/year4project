<template>
    <el-row>
        <el-col class="main-answer-list" :sm="24" :md="{span:18,offset:3}" :lg="{span:16,offset:4}"
                :xl="{span:14,offset:5}">
            <el-card shadow="always" class="box-card">
                <div v-if="questionnaire.isBoxSelected===false">
                    <h1>{{questionnaire.questionnaireTitle}}</h1>
                    <h3>{{questionnaire.questionnaireDescription}}</h3>
                </div>
            </el-card>
            <el-form :disabled="cannotSubmit">
                <div v-for="(item,index) in questionList"
                     :key="index">
                    <el-card class="box-card" shadow="hover"
                             v-if="ifShowByCheckingFront(index)"
                    >

                        <el-form-item>
                            <div class="question-title-div">
                                <div style="display: inline"><b>{{index+1}}. {{item.questionTitle}}</b></div>
                                <div style="display: inline" v-if="item.questionNullable===false" class="nullable-star">
                                    *
                                </div>
                            </div>
                            <div class="description-div">{{item.questionDescription}}</div>

                        </el-form-item>

                        <el-form-item>
                            <el-radio-group v-if="item.questionType==='single_check'"
                                            v-model="answerList[index].answerSingleCheck">
                                <el-radio v-for="(optionItem,optionIndex) in item.questionOptions" :key="optionIndex"
                                          :label="optionItem">{{optionItem}}
                                </el-radio>
                            </el-radio-group>

                            <el-checkbox-group v-else-if="item.questionType==='multi_check'"
                                               v-model="answerList[index].answerMultiCheck">
                                <el-checkbox v-for="(optionItem,optionIndex) in item.questionOptions" :key="optionIndex"
                                             :label="optionItem">{{optionItem}}
                                </el-checkbox>
                            </el-checkbox-group>

                            <el-input v-else-if="item.questionType==='single_line_text'"
                                      type="text" style="max-width: 80%"
                                      placeholder="enter here-single text"
                                      v-model="answerList[index].answerText"
                            ></el-input>

                            <el-input v-else-if="item.questionType==='multi_line_text'"
                                      type="textarea" style="max-width: 80%"
                                      placeholder="enter here-comment box"
                                      v-model="answerList[index].answerText"
                            ></el-input>

                            <el-input v-else-if="item.questionType==='number'&&item.numberType==='integer'"
                                      type="number"
                                      style="max-width: 200px"
                                      placeholder="the number must be integer"
                                      step="1"
                                      oninput="this.value = this.value.replace(/[^0-9]/g, '');"
                                      v-model="answerList[index].answerNumber"
                            ></el-input>

                            <el-input v-else-if="item.questionType==='number'&&item.numberType!=='integer'"
                                      type="number"
                                      style="max-width: 200px"
                                      placeholder="Digits only - no letters or symbols"
                                      step="1"
                                      v-model="answerList[index].answerNumber"
                            ></el-input>

                            <el-rate v-else-if="item.questionType==='grade'"
                                     :max="item.gradeMax"
                                     v-model="answerList[index].answerGrade"
                            >
                            </el-rate>

                            <el-date-picker v-else-if="item.questionType==='date'"
                                            v-model="answerList[index].answerDate"
                            ></el-date-picker>

                            <div v-else-if="item.questionType==='text_description'" class="description-div">
                                {{item.textDescriptionValue}}
                            </div>

                        </el-form-item>
                    </el-card>
                </div>


                <el-form-item>
                    <el-card class="box-card" style="width: 100%">

                        <el-dialog
                                width="70%"
                                title="Confirm submission?"
                                :visible.sync="submitVisible"
                                append-to-body
                                center
                        >
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="submitVisible = false">cancel</el-button>
                                <el-button @click="submitAnswer" type="primary">submit</el-button>
                            </span>

                        </el-dialog>
                        <el-dialog
                                width="70%"
                                title="Confirm reset？"
                                :visible.sync="resetVisible"
                                append-to-body
                                center
                        >
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="resetVisible = false">cancel</el-button>
                                <el-button @click="resetAnswer" type="primary">reset</el-button>
                            </span>

                        </el-dialog>
                        <div>
                            <el-button type="primary" @click="submitVisible = true">sumbit</el-button>
                            <el-button @click="resetVisible = true">reset</el-button>
                        </div>

                    </el-card>
                </el-form-item>
            </el-form>

        </el-col>
    </el-row>

</template>

<script>
    export default {
        name: "FillIn",
        data: function () {
            return {
                questionList: [],
                answerList: [],
                questionnaire: {
                    isBoxSelected: false,
                    questionnaireDescription: "Description",
                    questionnaireTitle: "Title",
                    questionnaireId: this.$route.params.id,
                },
                submitVisible: false,
                resetVisible: false,
                ip: null,
                alreadySubmit: null,
                cannotSubmit: null,
            }
        },
        computed: {
            aaa: function () {
                return {
                    answerList: this.answerList,
                }
            }
        },
        methods: {
            fetchData() {
                if(localStorage.getItem('Ip')){
                    this.axios.get("http://api.tianapi.com/txapi/ipquery/index?key=68b512d1b0023c2a4db0818a4854700c").then((res) => {
                        const Ip = res.data['newslist'][0]['ip']
                        console.log(res,Ip)
                        localStorage.setItem('Ip', Ip)
                        this.ip = localStorage.getItem('Ip')
                    }).catch(() => {
                        this.$message({message: "error！IP地址检测失败！", duration: 1000})
                    });
                }
                this.axios.get("/api/fillin/getQuestionList", {
                    params: {
                        questionnaireId: this.$route.params.id,
                    }
                }).then((res) => {
                    const tempList = res.data['questionList'];
                    const resList = [];
                    const ansList = [];
                    for (const t of tempList) {
                        t['date'] = new Date(t['date']);
                        resList.push(t);
                        const oneAnswer = {
                            questionId: t['questionId'],
                            questionTitle: t['questionTitle'],
                            questionType: t['questionType'],
                            questionNullable: t['questionNullable'],
                            answerSingleCheck: '',
                            answerMultiCheck: [],
                            answerText: '',
                            answerNumber: t['defaultNumber'],
                            answerGrade: 0,
                            answerDate: t['date'],
                        };
                        ansList.push(oneAnswer);
                    }
                    this.questionList = resList;
                    this.answerList = ansList;
                    this.$message({message: "The survey has been loaded", duration: 1000});
                }).catch(() => {
                    this.$message({message: "error！failed to load survey！", duration: 1000});
                })

                this.axios.get("/api/fillin/getQuestionnaireOutline", {
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
                    if (res.data['questionnaire']['status'] === 'closed') {
                        this.$message.error({message: "error！The survey has been closed！", duration: 0});
                        this.cannotSubmit = true;
                    }
                    this.questionnaire = temp;
                }).catch(() => {
                    this.$message({message: "error!fail to load survey！", duration: 1000})
                })
            },
            submitAnswer() {
                console.log(this.answerList)
                if (this.checkValidate()) {
                    this.axios.post("/api/fillin/submitAnswer?questionnaireId=" + this.$route.params.id, {
                        answerList: this.answerList,
                        ip: this.ip
                    }).then(() => {
                        this.submitVisible = false;
                        this.alreadySubmit = true;
                        this.cannotSubmit = true;
                        this.$message({message: "submitted", duration: 1000});
                    }).catch(() => {
                        this.$message({message: "error！fail to submit！", duration: 1000})
                    });
                }
            },
            resetAnswer() {
                this.fetchData();
                this.resetVisible = false;
            },
            ifShowByCheckingFront(index) {
                const thisQuestion = this.questionList[index];
                if (thisQuestion.frontChoose === false)
                    return true;

                for (const oneFront of thisQuestion.frontOptions) {
                    const frontIndex = oneFront[0] - 1;
                    const frontValue = oneFront[1];
                    const frontQuestion = this.questionList[frontIndex];
                    let checkList = null;
                    if (frontQuestion.questionType === 'single_check') {
                        checkList = this.answerList[frontIndex].answerSingleCheck;
                        if (frontValue != checkList) return false;
                    } else if (frontQuestion.questionType === 'multi_check') {
                        checkList = this.answerList[frontIndex].answerMultiCheck;
                        for (const oneOfMulti of frontValue) {
                            if (checkList.indexOf(oneOfMulti) === -1)
                                return false;
                        }
                    }
                }
                return true;
            },
            checkValidate() {
                for (const oneAnswer of this.answerList) {
                    if (oneAnswer.questionNullable === true) {
                        console.log('nullable', oneAnswer);
                        continue;
                    } else {
                        console.log(oneAnswer.questionType === 'single_check')
                        if (oneAnswer.questionType === 'single_check' && oneAnswer.answerSingleCheck === '' ||
                            oneAnswer.questionType === 'multi_check' && oneAnswer.answerMultiCheck === [] ||
                            oneAnswer.questionType === 'single_line_text' && oneAnswer.answerText === '' ||
                            oneAnswer.questionType === 'multi_line_text' && oneAnswer.answerText === '' ||
                            oneAnswer.questionType === 'number' && oneAnswer.answerNumber == null ||
                            oneAnswer.questionType === 'grade' && oneAnswer.answerGrade === 0 ||
                            oneAnswer.questionType === 'date' && oneAnswer.answerGrade == null
                        ) {
                            console.log('error')
                            this.$message.error(oneAnswer.questionId % 1000 + 1 + ' ' + oneAnswer.questionTitle + '  was not filled in！');
                            return false;
                        }
                    }
                }
                return true;
            }
        },
        mounted() {

            this.fetchData();
            this.ip = localStorage.getItem('Ip')


            this.axios.get("/api/fillin/checkAlreadySubmit", {
                params: {
                    questionnaireId: this.$route.params.id,
                    ip: this.ip
                }
            }).then((res) => {
                this.alreadySubmit = res['data']
                this.cannotSubmit = this.alreadySubmit;
                if (this.alreadySubmit) {
                    this.$message.error({message: "You have done this survey before！", duration: 0, showClose: true})
                }
            }).catch(() => {
                this.$message({message: "error！failed to locate IP address！", duration: 1000})
            });
        }
    }
</script>

<style scoped>
    .main-answer-list {
        position: relative;
        text-align: left;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    .box-card {
        padding-left: 5%;
    }

    .nullable-star {
        color: red;
    }

</style>