import 'bootstrap/dist/css/bootstrap.css'
import * as Vue  from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import VueCardFormat from 'vue-credit-card-validation';
import VuePaycard from "vue-paycard";
import refreshTokenService from './refreshTokenService';

axios.defaults.baseURL = process.env.VUE_APP_BACKEND_URL || 'http://localhost:8080';

// Add a request interceptor
axios.interceptors.request.use(async config => {

    if (config.url.includes('/auth/login') || config.url.includes('/auth/token')) {
        return config;
    }

    // Get the token from storage
    const token = localStorage.getItem('authToken');

    // If the token is present, add it to the request
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token;
    }

    return config;
}, error => {
    Promise.reject(error)
});

axios.interceptors.response.use(function (response) {
    // If the response is OK, return it
    return response;
}, function (error) {
    // If the response has a status of 401 (Unauthorized), try to refresh the token
    const originalRequest = error.config;
    if (error.response.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        if (originalRequest.url.includes('/auth/token') || originalRequest.url.includes('/auth/login')) {
            refreshTokenService.cleanToken()
            return Promise.reject(error);
        }

        return refreshTokenService.refreshToken().then(res => {
            if (res.status === 200) {
                // If the refresh was successful, update the token in the header and retry the original request
                axios.defaults.headers.common['Authorization'] = 'Bearer ' + res.data.accessToken;
                originalRequest.headers['Authorization'] = 'Bearer ' + res.data.accessToken;
                return axios(originalRequest);
            }
        });
    }

    // If the response has another error status, reject the promise with the error
    return Promise.reject(error);
});

Vue.createApp({
    render: () => Vue.h(App),
})
.use(VueCardFormat)
.use(router)
.use(VuePaycard)
.mount('#app')

