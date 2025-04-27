import { defineStore } from 'pinia';

export class SearchCondition {
    constructor(keyword = '', page = 1, size = 10, isAsc = false) {
        this.keyword = keyword; // 搜索关键字
        this.page = page; // 当前页
        this.size = size; // 每页显示条数
        this.isAsc = isAsc; // 排序方式
        // this.education = education; // 学历
        // this.nature = nature; // 性质
    }
}



export const useSearchStore = defineStore('search', {
    state: () => {
        return {
            type: 'JOB', // 默认搜索类型
            conditions: {
                JOB: {
                    ...new SearchCondition()
                },
                COMPANY: {
                    ...new SearchCondition()
                },
            },
            result: {
                JOB: [], // 职位列表数据
                COMPANY: [], // 公司列表数据
            },
        }
    },
    actions: {
        setType(type) {
            this.type = type;
        },

        setConditions(type, condition) {
            this.conditions[type] = condition;
        },
        getConditions(type) {
            return this.conditions[type];
        },
        resetCompanyId() {
            this.conditions.COMPANY.companyId = null;
            delete this.conditions.JOB.companyId;
        },
        
        setResult(type, res) {
            this.result[type] = res;
        },
        getResult(type) {
            return this.result[type];
        }
    },
    getters: {
        getType() {
            return this.type;
        },
    },
    persist: {
        key: 'search',
        storage: sessionStorage,
        paths: ['type', 'conditions', 'result'],
    }
});

