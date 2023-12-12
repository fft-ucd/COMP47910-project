<template>
  <div class="container">
    <h1>Retrieve Reservations</h1>

    <div class="mb-3">
      <label for="reservationId" class="form-label">Reservation ID:</label>
      <input type="text" class="form-control" id="reservationId" placeholder="Reservation Id" v-model="reservationId">
    </div>

    <div class="mb-3">
      <label for="name" class="form-label">Name:</label>
      <input type="text" class="form-control" id="name"  placeholder="Guest Name" v-model="name">
    </div>

    <div class="mb-3">
      <label for="surname" class="form-label">Surname:</label>
      <input type="text" class="form-control" id="surname"  placeholder="Guest Surname" v-model="surname">
    </div>

    <div class="mb-3">
      <label for="email" class="form-label">Email:</label>
      <input type="email" class="form-control" id="email" placeholder="Reservation Email" v-model="email">
    </div>

    <button class="btn btn-primary" :disabled="!isValidForm" @click="getReservations">Submit</button>

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
          <td>{{ reservation.rooms.map(room => `${room.roomType}(${room.capacity})`).join(', ') }}</td>
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
      reservationId: '',
      name: '',
      surname: '',
      email: '',
      reservations: []
    }
  },
  computed: {
    isValidForm() {
      return this.reservationId ||
        (this.name && this.surname && this.validateEmail(this.email));
    }
  },
  watch: {
    reservationId() {
      if (this.reservationId) {
        this.name = '';
        this.surname = '';
        this.email = '';
      }
    },
    name() {
      if (this.name) {
        this.reservationId = '';
      }
    },
    surname() {
      if (this.surname) {
        this.reservationId = '';
      }
    },
    email() {
      if (this.email) {
        this.reservationId = '';
      }
    },
  },
  methods: {
    validateEmail(email) {
      var re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(String(email).toLowerCase());
    },
    async getReservations() {

      if (this.reservationId) {
        try {
          const response = await axios.get(`/api/reservations/${this.reservationId}`)
          this.reservations = [response.data];
        } catch(e) {
          alert("Error or search didn't produced results")
          this.reservations = [];
        }
      } else if (this.name && this.surname && this.email) {
        try {
          const response = await axios.get(`/api/reservations/guest`, { params: {
            name: this.name,
            surname: this.surname,
            email: this.email
          }})

          this.reservations = response.data;
        } catch(e) {
          alert("Error or search didn't produced results")
          this.reservations = [];
        }
      }
    },
    canCancel(reservation) {
      const now = new Date();
      const checkIn = new Date(reservation.startDate);
      const diffTime = Math.abs(checkIn - now);
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
      return diffDays >= 1 && reservation.status === 'ACTIVE';
    },
    async cancelReservation(id) {
      
      try {
        await axios.put(`/api/reservations/${id}/cancel`)
        this.getReservations();
      } catch(error) {
 
        if (error.response.status === 401) {
            location.href = '/login';
        } else if (error.response.status === 400 || error.response.status === 409) {
          alert("It is not possible to cancel this reservation!")
          return
        }

        console.error(error);
      } 
      
    }
  }
}
</script>
