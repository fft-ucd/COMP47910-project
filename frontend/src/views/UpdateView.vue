<template>
    <div>
      <h1 class="text-center">Update Starwood Member</h1>
      <!-- Update form content here -->
      <form @submit.prevent="update" class="mx-auto p-4 bg-light" style="max-width: 400px;">
        <div class="mb-3">
            <input id="name" v-model="user.name" type="text" placeholder="Name" class="form-control" required/>
        </div>
        <div class="mb-3">
            <input id="surname" v-model="user.surname" type="text" placeholder="Surname" class="form-control" required/>
        </div>
        <div class="mb-3">
            <input id="address" v-model="user.street" type="text" placeholder="Street" class="form-control" maxlength="255" required>
        </div>
        <div class="mb-3">
            <input id="city" v-model="user.city" type="text" placeholder="City" class="form-control" maxlength="255" required>
        </div>
        <div class="mb-3">
            <input id="state" v-model="user.state" type="text" placeholder="State" class="form-control" maxlength="255" required>
        </div>
        <div class="mb-3">
            <input id="zipcode" v-model="user.zipcode" type="text" placeholder="Zipcode" class="form-control" maxlength="50" required>
        </div>
        <div class="mb-3">
            <input id="country" v-model="user.country" type="text" placeholder="Country" class="form-control" maxlength="2" required>
        </div>
        <div class="mb-3">
            <input id="phone" v-model="user.phone" type="text" placeholder="Phone" class="form-control" maxlength="17" required>
        </div>
        <div class="mb-3">
            <input id="email" v-model="user.email" type="email" placeholder="Email" maxlength="255" class="form-control" required>
        </div>
        <div class="mb-3">
            <input id="password" v-model="user.password" type="password" placeholder="Password" class="form-control"/>
        </div>
        <div class="mb-3">
            <input id="passwordRepeat" v-model="user.passwordRepeat" type="password" placeholder="Password Repeat" class="form-control" />
        </div>
        <ul class="requirements">
            <li
            v-for="(requirement, key) in passwordRequirements"
            :key="key"
            :class="requirement.predicate ? 'is-success' : 'is-error'"
            >
            {{ requirement.name }}
            </li>
        </ul>
        <p class="text-danger" v-if="!isValid">{{ error }}</p>
        
        <button type="submit" :disabled="!isValidForm" class="btn btn-primary">Register</button>
        </form>
    </div>
  </template>
  
  <script>
import axios from 'axios';

export default {
  data() {
    return {
      user: {
        name: '',
        surname: '',
        street: '',
        city: '',
        zipcode: '',
        country: '',
        phone: '',
        email: '',
        password: ''
      },
      error: null,
      isValid: false
    }
  },
  methods: {
    validateEmail(email) {
      var re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      this.error = re.test(String(email).toLowerCase()) ? '' : 'Invalid Email';
      this.isValid = !this.error
    },
    validatePhoneNumber() {
      let regex = /^\+?\d+(\(0\))?(x\d+)?$/;
      this.isValid = regex.test(this.user.phone) && this.user.phone.length > 4 && this.user.phone.length < 16;
      this.error = this.isValid ? '' : 'Invalid phone number, input only numbers and country code';
    },
    async load() {
        try {
            const response = await axios.get(`/api/members`)
            this.user.name = response.data.name;
            this.user.surname = response.data.surname;
            this.user.street = response.data.address.street;
            this.user.city = response.data.address.city;
            this.user.zipcode = response.data.address.zipcode;
            this.user.state = response.data.address.state;
            this.user.country = response.data.address.country;
            this.user.phone = response.data.contact.phone;
            this.user.email = response.data.contact.email;
            this.user.password = '';
            this.user.passwordRepeat = '';

        } catch(error) { 

            if (error.response.status === 401) {
                location.href = '/login';
            }

            alert("Error when loading results")
        }
    },
    async update() {
      try {
        await axios.put('/api/members', {
          name: this.user.name,
          surname: this.user.surname,
          address: {
            street: this.user.street,
            city: this.user.city,
            state: this.user.state,
            zipcode: this.user.zipcode,
            country: this.user.country
          },
          contact: {
            phone: this.user.phone,
            email: this.user.email
          },
          password: this.user.password,
        });
        alert("User Updated successfully!!!");
      } catch (error) {

        if (error.response && error.response.status === 409) {
          this.error = 'Password or username invalid';
          this.isValid = !this.error;
        } else {
          this.error = 'Unknow issue';
          this.isValid = !this.error;
          console.error('Error occurred during registration:', error);
        }
      }
    }
  },
  computed: {
    passwordRequirements() {
      return [
        {
          name: 'Must contain uppercase letters',
          predicate: this.user.password.toLowerCase() !== this.user.password,
        },
        {
          name: 'Must contain lowercase letters',
          predicate: this.user.password.toUpperCase() !== this.user.password,
        },
        {
          name: 'Must contain numbers',
          predicate: /\d/.test(this.user.password),
        },
        {
          name: 'Must contain symbols',
          predicate: /\W/.test(this.user.password),
        },
        {
          name: 'Must be at least 8 characters long',
          predicate: this.user.password.length >= 8,
        },
        {
          name: 'Must match',
          predicate: this.user.password === this.user.passwordRepeat,
        }
      ]
    },
    passwordValid() {

        if (this.user.password === this.user.passwordRepeat && this.user.password === '') {
            return true
        }

        return this.passwordRequirements.reduce((v, p) => v && p.predicate, true)
    },
    isValidForm() {
      return (this.user.name && this.user.surname && !this.error && this.user.email && this.user.phone && this.user.street && this.user.city && this.user.zipcode && this.user.country && this.passwordValid);
    }
  },
  watch: {
    passwordRequirements() {
    }
  },
  created() {
    this.load()
  }
}
</script>
  
  <style scoped>
  .requirements {
    font-weight: bold;
  }
  
  .is-success {
    color: #96CA2D;
  }
  
  .is-error {
    color: #BA3637;
  }
  </style>
  