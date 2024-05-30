<template>
    <div>
        <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll"
                     @change="handleCheckAllChange">All Types
        </el-checkbox>
        <el-checkbox-group v-model="checkedList" @change="handleCheckedCitiesChange">
            <el-checkbox v-for="item in checkOptions" :label="item" :key="item">{{translateLabel[item]}}</el-checkbox>
        </el-checkbox-group>
    </div>
</template>
<script>
    const options = ['collecting', 'editing', 'closed'];
    export default {
        data() {
            return {
                checkAll: false,
                checkedList: ['collecting', 'editing', 'closed'],
                translateLabel: {
                    'collecting': 'collecting',
                    'editing': 'editing',
                    'closed': 'closed'
                },
                checkOptions: options,
                isIndeterminate: true
            };
        },
        methods: {
            handleCheckAllChange(val) {
                this.checkedList = val ? options : [];
                this.isIndeterminate = false;
            },
            handleCheckedCitiesChange(value) {
                const checkedCount = value.length;
                this.checkAll = checkedCount === this.checkOptions.length;
                this.isIndeterminate = checkedCount > 0 && checkedCount < this.checkOptions.length;
            }
        },
        updated() {
            console.log(this.checkedList)
            this.$emit('changeShow', this.checkedList);
        }
    };
</script>
<style scoped>

</style>