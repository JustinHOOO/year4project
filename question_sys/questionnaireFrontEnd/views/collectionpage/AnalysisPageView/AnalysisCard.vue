<template>
    <div>
        <el-card class="box-card" shadow="hover">
            <div class="analysis-title">{{questionIndex}}. {{questionTitle}}</div>

            <el-divider></el-divider>

            <div v-if="questionType==='date'||questionType==='single_line_text'||questionType==='multi_line_text'">
                <el-table
                        :data="writeValueList"
                        height="250"
                        border
                        style="width: 80%; margin: auto">
                    <el-table-column
                            prop="writeValue"
                            label="content">
                    </el-table-column>
                </el-table>
            </div>

            <div v-if="questionType==='single_check'||questionType==='multi_check'">

                <el-radio-group v-model="showBar" class="choose-show">
                    <el-radio :label="true">Bar chart</el-radio>
                    <el-radio :label="false">Pie chart</el-radio>
                </el-radio-group>

                <el-row>
                    <el-col :xs="24" :sm="18" :md="18" :lg="18" :xl="18">
                        <div v-show="showBar===true" :id="'barChart'+questionIndex" class="bar-chart"
                             :style="{height:barHeight}"></div>

                        <div v-show="showBar===false" :id="'pieChart'+questionIndex" class="pie-chart"></div>
                    </el-col>

                    <el-col :xs="24" :sm="6" :md="6" :lg="6" :xl="6">
                        <el-table
                                :data="questionValueList"
                                :default-sort="{prop:'value',order:'descending'}"
                                class="analysis-table">
                            <el-table-column
                                    prop="name"
                                    label="option name">
                            </el-table-column>
                            <el-table-column
                                    prop="value"
                                    label="number of selections">
                            </el-table-column>
                        </el-table>
                    </el-col>

                </el-row>

            </div>

            <div v-if="questionType==='number'||questionType==='grade'">

                <el-row>
                    <el-col :xs="24" :sm="18" :md="18" :lg="18" :xl="18">
                        <div v-show="showBar===true" :id="'barChart'+questionIndex" class="bar-chart"
                             :style="{height:barHeight}"></div>

                        <div v-show="showBar===false" :id="'pieChart'+questionIndex" class="pie-chart"></div>
                    </el-col>

                    <el-col :xs="24" :sm="6" :md="6" :lg="6" :xl="6">
                        <el-table
                                :data="questionValueList"
                                :default-sort="{prop:'value',order:'descending'}"
                                class="analysis-table">
                            <el-table-column
                                    prop="name"
                                    label="Descriptive">
                            </el-table-column>
                            <el-table-column
                                    prop="value"
                                    label="Value">
                            </el-table-column>
                        </el-table>
                    </el-col>

                </el-row>

            </div>

        </el-card>
    </div>
</template>

<script>
    import echarts from "echarts/lib/echarts"
    import "echarts/lib/chart/bar"
    import "echarts/lib/chart/pie"
    import "echarts/lib/component/tooltip"

    export default {
        name: "AnalysisCard",
        props: {
            questionIndex: Number,
            questionId: Number,
            questionTitle: String,
            questionType: String
        },
        created() {
            this.fetchData();
        },
        methods: {
            fetchData() {
                if (this.questionType === 'single_check' || this.questionType === 'multi_check' || this.questionType === 'number' || this.questionType === 'grade') {
                    this.axios.get("/api/getQuestionValueList", {
                        params: {
                            questionId: this.questionId
                        }
                    }).then((res) => {
                        const temp = res['data'];

                        const dataList = [];
                        for (const oneName in temp) {
                            dataList.push({'name': oneName, 'value': temp[oneName]})
                        }
                        dataList.sort((a, b) => {
                            return a.value - b.value
                        });
                        this.questionValueList = dataList;
                        this.barHeight = this.questionValueList.length*33+150+'px'
                        this.drawBar();
                        this.drawPie();

                    }).catch(() => {
                        this.$message({message: "error！failed to load data！", duration: 1000})
                    })
                } else {
                    this.axios.get("/api/getWriteValue", {
                        params: {
                            questionId: this.questionId
                        }
                    }).then((res) => {
                        this.writeValueList = res['data']
                    }).catch(() => {
                        this.$message({message: "error！fail to load data！", duration: 1000})
                    })
                }

            },
            drawBar() {
                const myBarChart = echarts.init(document.getElementById('barChart' + this.questionIndex), 'light');
                myBarChart.setOption({
                    tooltip: {},
                    grid: {left: '15%'},
                    xAxis: {},
                    yAxis: {
                        data: this.questionValueList.map(x => x['name'])
                    },
                    series: [{
                        name: 'Number of selections',
                        type: 'bar',
                        data: this.questionValueList
                    }]
                });
            },
            drawPie() {
                const myPieChart = echarts.init(document.getElementById('pieChart' + this.questionIndex), 'light');
                myPieChart.setOption({
                    tooltip: {},
                    grid: {left: 20},
                    series: [{
                        name: 'Number of selections',
                        type: 'pie',
                        data: this.questionValueList
                    }]
                });

            }
        },

        data: function () {

            return {
                showBar: true,
                writeValueList: null,
                questionValueList: [],
                barHeight: '250px'
            }
        }
    }
</script>

<style scoped>
    .bar-chart {
        width: 100%;
    }

    .pie-chart {
        margin-left: calc(25% - 100px);
        width: 400px;
        height: 300px;
    }

    .box-card {
        transition: all ease 300ms;
        text-align: left;
    }

    .analysis-table {
    }

    .choose-show {
        margin-left: 80px;
    }

    .analysis-title {
        margin: 15px;
        font-size: 14px;
    }
</style>