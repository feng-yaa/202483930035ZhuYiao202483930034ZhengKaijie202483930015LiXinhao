<script>
import { inject } from 'vue';
import request from "@/utils/request.js";

export default {
    setup() {
        const globalVar = inject('globalVar');

        const changeParentVar = () => {
            globalVar.globalVar = true;
        };

        return { globalVar, changeParentVar };
    },
    data() {
        return {
            username: '',
            password: '',
            loginError: false
        };
    },
    methods: {
        handleLogin() {
            /*request.get('/players',{
              params:{
                wid: 1
              }
            }).then(res=>{
              console.log(res)
            })*/
          request.get('/validate',{
            params:{
              username: this.username,
              password: this.password
            }
          }).then(res=>{
            console.log(res)
            if(res === true) {
              alert('登录成功！');
              this.changeParentVar();
              this.loginError = false;
            }
          })
        },
    }
};
</script>

<template>
    <div class="login-container">
        <h2>登录</h2>
        <form @submit.prevent="handleLogin">
            <div class="form-group">
                <label for="username">用户名：</label>
                <input type="text" v-model="username" id="username" placeholder="请输入用户名" required />
            </div>

            <div class="form-group">
                <label for="password">密码：</label>
                <input type="password" v-model="password" id="password" placeholder="请输入密码" required />
            </div>

            <div class="form-group">
                <button type="submit">登录</button>
            </div>

            <div v-if="loginError" class="error-message">
                <p>用户名或密码错误！</p>
            </div>
        </form>
    </div>
</template>

<style scoped>
.login-container {
    min-width: 400px;
    margin: 50px auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
}

h2 {
    text-align: center;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    font-size: 14px;
    margin-bottom: 5px;
}

input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

button {
    width: 100%;
    padding: 10px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:hover {
    background-color: #45a049;
}

.error-message {
    color: red;
    font-size: 14px;
    margin-top: 10px;
}
</style>