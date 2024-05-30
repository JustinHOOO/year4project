<template>
    <div style="position: relative;">
        <el-card class="box-card" shadow="hover" :body-style="{ padding: '0' }">

            <div class="box-is-not-selected-wrapper" v-if="!isBoxSelected" @click="clickUnSelected">
                <div class="box-is-not-selected">
                    <div class="question-title-div">
                        <div style="display: inline">{{questionIndex+1}}. {{questionTitleValue}}</div>
                        <div style="display: inline" v-if="questionNullableValue===false" class="nullable-star">*</div>
                    </div>
                    <div class="description-div">{{questionDescriptionValue}}</div>

                    <el-radio-group v-if="typeValue==='single_check'" v-model="optionsValue">
                        <el-radio v-for="(item,index) in optionsValue" :key="index">{{item}}</el-radio>
                    </el-radio-group>

                    <el-checkbox-group v-else-if="typeValue==='multi_check'" v-model="optionsValue">
                        <el-checkbox v-for="(item,index) in optionsValue" :key="index">{{item}}</el-checkbox>
                    </el-checkbox-group>

                    <el-input v-else-if="typeValue==='single_line_text'"
                              type="text" style="max-width: 80%"
                              placeholder="Enter your answer here"
                    ></el-input>

                    <el-input v-else-if="typeValue==='multi_line_text'"
                              type="textarea" style="max-width: 80%"
                              placeholder="Enter your answer here"
                    ></el-input>

                    <el-input v-else-if="typeValue==='number'" type="number"
                              style="max-width: 200px"
                              placeholder="Place a number"
                              step="1"
                    ></el-input>

                    <el-rate v-else-if="typeValue==='grade'" :max="gradeMaxValue">
                    </el-rate>

                    <el-date-picker v-else-if="typeValue==='date'" v-model="dateValue"></el-date-picker>

                    <div v-else-if="typeValue==='text_description'" class="description-div">
                        {{textDescriptionValue}}
                    </div>

                    <div v-if="frontChoose">
                        <el-divider></el-divider>
                        <div v-for="(item,index) in frontOptionsValue" :key="index">
                            previous option {{index+1}}：
                            <el-cascader
                                    :value="item"
                                    :options="frontOptions"
                                    :disabled="true"
                            ></el-cascader>
                        </div>
                    </div>


                </div>

                <div class="edit-icon"><i class="el-icon-edit-outline"></i></div>
            </div>


            <div class="box-is-selected" v-if="isBoxSelected">
                <div class="question-index">{{questionIndex+1}}.
                </div>
                <el-form>
                    <el-form-item label="Title：">
                        <el-input type="text" size="medium"
                                  style="max-width: 60%"
                                  v-model="questionTitleValue"
                                  placeholder="Survey title"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="Description：">
                        <el-input type="text" size="medium"
                                  class="description-div"
                                  v-model="questionDescriptionValue"
                                  placeholder="Survey description"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="Question Type：">
                        <el-select v-model="typeValue" placeholder="switch a question type by selecting an option from the menu." size="medium">
                            <el-option
                                    v-for="item in typeOptions"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="Question Mandatoriness:">
                        <el-select placeholder="Is this option mandatory?" v-model="questionNullableValue">
                            <el-option :value="true" :label="'No'"></el-option>
                            <el-option :value="false" :label="'Yes'"></el-option>
                        </el-select>
                    </el-form-item>

                    <el-form-item label="Appear with pervious option：">
                        <el-tooltip placement="top"
                                    effect="light"
                                    content="Appears after selecting the previous option."
                        >
                            <el-select placeholder="Appears after selecting the previous option." v-model="frontChooseValue">
                                <el-option :value="true" :label="'Yes'"></el-option>
                                <el-option :value="false" :label="'No'"></el-option>
                            </el-select>
                        </el-tooltip>

                    </el-form-item>
                    <div v-if="frontChooseValue===true">
                        <el-form-item v-for="(item,index) in frontOptionsValue" :key="index"
                                      :label="'pervious option'+(index+1)+'：'">
                            <div class="block">
                                <el-cascader
                                        v-model="frontOptionsValue[index]"
                                        :options="frontOptions"
                                        :props="{ expandTrigger: 'hover' }"></el-cascader>
                            </div>
                            <el-tooltip effect="light"
                                        content="add an option"
                                        :open-delay="200"
                                        :hide-after="1500"
                                        placement="top">
                                <el-button size="medium" round class="add-option-button"
                                           @click="addFrontOption"
                                ><i class="el-icon-plus"></i>
                                </el-button>
                            </el-tooltip>
                            <el-tooltip effect="light"
                                        content="delete this option"
                                        :open-delay="200"
                                        :hide-after="1500"
                                        placement="top">
                                <el-button size="medium" round class="delete-option-button"
                                           @click="deleteFrontOption(index)"
                                ><i class="el-icon-delete"></i>
                                </el-button>
                            </el-tooltip>
                        </el-form-item>
                    </div>


                    <el-divider class="divider"></el-divider>

                    <div v-if="typeValue==='single_check'||typeValue==='multi_check'">
                        <el-form-item v-for="(item,index) in optionsValue" :key="index">
                            Option {{index+1}}：
                            <el-input v-model="optionsValue[index]" style="max-width: 200px"></el-input>
                            <el-tooltip effect="light"
                                        content="Add an option"
                                        :open-delay="200"
                                        :hide-after="1500"
                                        placement="top">
                                <el-button size="medium" round class="add-option-button"
                                           @click="addOption"
                                ><i class="el-icon-plus"></i>
                                </el-button>
                            </el-tooltip>
                            <el-tooltip effect="light"
                                        content="Delete this option"
                                        :open-delay="200"
                                        :hide-after="1500"
                                        placement="top">
                                <el-button size="medium" round class="delete-option-button"
                                           @click="deleteOption(index)"
                                ><i class="el-icon-delete"></i>
                                </el-button>
                            </el-tooltip>
                        </el-form-item>
                    </div>

                    <div v-if="typeValue==='number'">
                        <el-form-item label="Number type：">
                            <el-select v-model="numberTypeValue" placeholder="Specify a number type" size="medium">
                                <el-option
                                        v-for="item in numberTypeOptions"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>

                        <el-form-item label="Default number：">
                            <el-input-number v-model="defaultNumberValue"></el-input-number>
                        </el-form-item>
                    </div>

                    <div v-if="typeValue==='grade'">
                        <el-form-item>
                            Maximum score : 
                            <el-input-number v-model="gradeMaxValue"></el-input-number>
                        </el-form-item>
                    </div>

                    <div v-if="typeValue==='text_description'">
                        <el-form-item label="Description：">
                            <el-input v-model="textDescriptionValue"
                                      type="textarea"
                                      style="max-width: 400px"
                                      size="medium"
                            ></el-input>
                        </el-form-item>
                    </div>

                    <el-form-item>
                        <el-button type="primary" @click="saveOneQuestion">Save</el-button>
                        <el-button @click="resetQuestion">Reset</el-button>
                    </el-form-item>
                </el-form>
            </div>


            <el-button class="delete-button" type="danger"
                       icon="el-icon-delete" circle
                       @click="clickDelete"
            ></el-button>


        </el-card>
    </div>
</template>

<script>
    export default {
        name: "Question",
        props: {
            isBoxSelected: Boolean,
            questionIndex: Number,
            questionTitle: String,
            questionNullable: Boolean,
            questionType: String,
            questionDescription: String,
            questionOptions: Array,
            frontChoose: Boolean,
            frontOptions: Array,
            frontOptionsInitValue: Array,
            numberType: String,
            defaultNumber: Number,
            gradeMax: Number,
            date: Date,
            textDescription: String,
        },
        methods: {
            clickUnSelected() {
                this.$emit('clickUnSelected');
            },
            saveOneQuestion() {
                this.$emit('saveOneQuestion', this.questionData);
                this.$emit('clickSelected');
            },
            resetQuestion() {
                this.$emit('resetQuestion');
                this.questionTitleValue = "Reset title";
                this.questionNullableValue = false;
                this.questionDescriptionValue = "Reset description";
                this.typeValue = 'not_selected';
                this.optionsValue = [];
                this.frontOptionsValue = [[]];
                this.frontChooseValue = false;
                this.numberTypeValue = 'integer';
                this.defaultNumberValue = 0;
                this.gradeMaxValue = 5;
                this.dateValue = new Date();
                this.textDescriptionValue = '';
            },
            clickDelete() {
                this.$emit('clickDelete');
            },
            addOption() {
                this.optionsValue.push('');
            },
            deleteOption(index) {
                this.optionsValue.splice(index, 1);
            },
            addFrontOption() {
                this.frontOptionsValue.push([]);
            },
            deleteFrontOption(index) {
                this.frontOptionsValue.splice(index, 1);
            },
        },

        data: function () {
            return {
                typeOptions: [
                    {
                        value: 'not_selected',
                        label: 'Choose a question type'
                    }, {
                        value: 'single_check',
                        label: 'Single-choice'
                    }, {
                        value: 'multi_check',
                        label: 'Multiple-choice'
                    }, {
                        value: 'single_line_text',
                        label: 'Single textbox'
                    }, {
                        value: 'multi_line_text',
                        label: 'Comment box'
                    }, {
                        value: 'number',
                        label: 'Number'
                    }, {
                        value: 'grade',
                        label: 'Rating'
                    }, {
                        value: 'date',
                        label: 'Date/time'
                    }, {
                        value: 'text_description',
                        label: 'Text description'
                    }],
                numberTypeOptions: [{
                    value: 'integer',
                    label: 'integer'
                }, {
                    value: 'fraction',
                    label: 'fraction'
                }],
                numberTypeValue: this.numberType,
                defaultNumberValue: this.defaultNumber,
                typeValue: this.questionType,
                optionsValue: this.questionOptions,
                questionDescriptionValue: this.questionDescription,
                questionTitleValue: this.questionTitle,
                gradeMaxValue: this.gradeMax,
                dateValue: this.date,
                textDescriptionValue: this.textDescription,
                frontChooseValue: this.frontChoose,
                frontOptionsValue: this.frontOptionsInitValue,
                questionNullableValue: this.questionNullable,
            }
        },
        computed: {
            questionData: function () {
                const questionData = {
                    questionIndex: this.questionIndex,
                    questionOptions: this.optionsValue,
                    questionDescription: this.questionDescriptionValue,
                    questionTitle: this.questionTitleValue,
                    questionType: this.typeValue,
                    frontChoose: this.frontChooseValue,
                    frontOptions: this.frontOptionsValue,
                    questionNullable: this.questionNullableValue,
                    numberType: this.numberTypeValue,
                    defaultNumber: this.defaultNumberValue,
                    gradeMax: this.gradeMaxValue,
                    date: this.dateValue,
                    textDescription: this.textDescriptionValue,
                };
                return questionData;

            },

        }
    }
</script>

<style scoped>
    .box-is-not-selected {
        position: relative;
        text-align: left;
        margin-left: 20%;
        line-height: 40px;
    }

    .nullable-star {
        color: red;
    }

    .delete-button {
        position: absolute;
        right: 10%;
        top: 20px;
    }

    .question-index {
        margin-bottom: 20px;
    }

    .box-card {
        transition: all ease 300ms;
    }

    .delete-button {
        position: absolute;
    }

    .box-is-selected {
        text-align: left;
        margin-top: 30px;
        margin-bottom: 30px;
        margin-left: 20%;
    }

    .box-is-not-selected-wrapper {
        padding-top: 20px;
        padding-bottom: 20px;
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

    .divider {
        max-width: 80%;
    }

    .add-option-button {
        margin-left: 10px;
    }

    .description-div {
        max-width: 60%;
    }
</style>