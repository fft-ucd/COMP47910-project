<template>
  <div id="app">
    <nav>
      <router-link v-if="!isLoggedIn" to="/login">Login</router-link>
      |
      <router-link v-if="!isLoggedIn" to="/register">Register</router-link>
      |
      <router-link v-if="isLoggedIn" to="/profile">Update Profile</router-link>
      |
      <router-link to="/rooms">Rooms</router-link>
      |
      <router-link to="/reservations">My Reservations</router-link>
      |
      <router-link to="/book-room">Book a Room</router-link>
      |
      <router-link v-if="isLoggedIn" to="/card-register">Manage Cards</router-link>
      |
      <router-link v-if="isLoggedIn" to="/unregister">Unregister</router-link>
      |
      <router-link v-if="isLoggedIn" to="" @click.capture="logout">Logout</router-link>
    </nav>
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
  computed: {
    isLoggedIn() {
      return localStorage.getItem('authToken') !== null;
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('authToken')
      localStorage.removeItem('refreshToken')

      location.href = '/login';
    }
  }  
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
