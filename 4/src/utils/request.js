import axios from "axios"
import axiosRetry from "axios-retry";
const request = axios.create({
    baseURL: 'http://localhost:8080',  //服务器地址和端口
    timeout: 30000 //后台接口超时时间
})

request.interceptors.request.use(config=>{
    config.headers['Content-Type']='application/json;charset=utf-8';
    //使用JSON和utf-8编码进行数据传输
    return config
},error=>{
    return Promise.reject(error)
})

request.interceptors.response.use(
    response=> {
        let res = response.data;
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        return res;
    },
    error=> {
        console.error(error.message)
        return Promise.reject(error)
    }
)

axiosRetry(request, {
    retries: 10, // 最大重试次数
    retryDelay: axiosRetry.exponentialDelay, // 使用指数延迟的方式重试
    shouldRetry: (error) => {
        // 只有在请求失败（如服务器未启动）时才重试
        return error.response === undefined || error.response.status >= 500;
    },
});
export default request