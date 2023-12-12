<template>
  <div class="container">
    <h1 class="text-center">Login</h1>
    <form @submit.prevent="login" class="mx-auto p-4 bg-light" style="max-width: 400px;">
      <div class="mb-3">
        <input v-model="username" placeholder="Username" class="form-control" @focus="hideError" required/>
      </div>
      <div class="mb-3">
        <input type="password" v-model="password" placeholder="Password" class="form-control" @focus="hideError" required/>
      </div>

      <div v-if="showError" class="alert alert-danger">
        {{ errorMessage }}
      </div>
      <button type="submit" class="btn btn-primary">Login</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',
      showError: false
    };
  },
  methods: {
    hideError() {
      this.showError = false;
    },
    async login() {
      try {
        const response = await axios.post('/auth/login', {
          username: this.username,
          password: this.password,
        });

        // Store the token in local storage
        localStorage.setItem('authToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);

        location.href = '/rooms';
        
        // Redirect to the room list
        //this.$router.push('/rooms');
        
      } catch (error) {


        if (error.response.status === 401 || error.response.status === 403) {
          this.errorMessage = 'Login failed. Please check your credentials.';
          this.showError = true;
        } else {
          console.error(error);
        }
        

        localStorage.removeItem('authToken');
        localStorage.removeItem('refreshToken');
      }
    },
  },
};
</script>
