<template>
  <div class="container">
    <h1 class="text-center">My Reservations</h1>
    <!-- Display reservations -->
    <table class="table table-striped table-bordered mt-5">
      <thead class="thead-dark">
        <tr>
          <th scope="col">#</th>
          <th scope="col">Reservation ID</th>
          <th scope="col">Room Booked</th>
          <th scope="col">Name</th>
          <th scope="col">Check-in Date</th>
          <th scope="col">Check-out Date</th>
          <th scope="col">Status</th>
          <th scope="col">Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(reservation, index) in reservations" :key="reservation.reservationId">
          <th scope="row">{{ index + 1 }}</th>
          <td>{{ reservation.reservationId }}</td>
          <td>{{ reservation.rooms ? reservation.rooms.map(room => `${room.roomType}(${room.capacity})`).join(', ') : '' }}</td>
          <td>{{ reservation.guestName }}</td>
          <td>{{ reservation.startDate }}</td>
          <td>{{ reservation.endDate }}</td>
          <td>{{ reservation.status }}</td>
          <td><button class="btn btn-danger" v-if="canCancel(reservation)" @click="cancelReservation(reservation.reservationId)">Cancel</button></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>


<script>
import axios from 'axios';

export default {
  data() {
    return {
      reservations: []
    }
  },
  methods: {
    async getReservations() {
      try {
        const response = await axios.get(`/api/reservations`)
        this.reservations = response.data;
      } catch(error) { 

        if (error.response.status === 401) {
          location.reload()
        }

        alert("Error when loading results")
        this.reservations = [];
      }

    },
    canCancel(reservation) {
      const now = new Date();
      const checkIn = new Date(reservation.startDate);
      const diffTime = Math.abs(checkIn - now);
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
      return diffDays >= 1 && reservation.status === 'ACTIVE';
    },
    cancelReservation(id) {
      axios.put(`/api/reservations/${id}/cancel`)
      .then(() => {
        this.getReservations();
      });
    }
  },
  mounted() {
    this.getReservations();
  }
}
</script>

<style scoped>
/* Add your styles here */
</style>
