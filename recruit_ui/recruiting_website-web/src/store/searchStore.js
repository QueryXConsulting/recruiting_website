import { defineStore } from 'pinia';

const useSearchStore = defineStore('search', {
    state: () => {
        return {
            type: 'JOB', // 默认搜索类型
            conditions: {
                JOB: {
                    keyword: '', // 搜索关键字
                    page: 1, // 当前页
                    size: 10, // 每页显示条数
                    isAsc: false, // 排序方式
                },
                COMPANY: {
                    keyword: '', // 搜索关键字
                    page: 1, // 当前页
                    size: 10, // 每页显示条数
                    isAsc: false, // 排序方式
                },
            },
            result: [], // 列表数据
        }
    },
    actions: {
        setType(type) {
            this.type = type;
        },
        setConditions(type, condition) {
            switch (type) {
                case 'JOB':
                    this.conditions.JOB = condition;
                    break;
                case 'COMPANY':
                    this.conditions.COMPANY = condition;
                    break;
            }
        },
        getConditions(type) {
            switch (type) {
                case 'JOB':
                    return this.conditions.JOB;
                case 'COMPANY':
                    return this.conditions.COMPANY;
            }
        },
        setResult(res) {
            this.result = res;
        }
    },
    getters: {
        getType() {
            return this.type;
        },
        getResult() {
            return this.result;
        }
    },
    persist: {
        key: 'search',
        storage: sessionStorage,
        paths: ['type', 'conditions', 'result'],
    }
});

export default useSearchStore;