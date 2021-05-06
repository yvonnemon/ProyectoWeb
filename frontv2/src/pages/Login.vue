<template>
  <div class="column items-center">
    <div class="q-pa-md" style="min-width: 300px">
      <q-form
            class="row"
              @submit="login"
                >
      <q-input
        outlined
        v-model="user"
        label="Usuario"
        stack-label
        class="form-input col-12"
      />
      <q-input
        outlined
        v-model="password"
        label="Contraseña"
        stack-label
        class="form-input col-12"
        type="password"
      />
      <q-btn color="indigo-13" glossy label="Login" type="submit" class="col-sm-8 offset-sm-2 col-xs-12"/>
    </q-form>
    </div>
  </div>
</template>

<script>
import jwt_decode from "jwt-decode";
const axios = require("axios");
export default {
  data() {
    return {
      user: "",
      password: "",
      token: ""
    };
  },
  async created() {
    sessionStorage.removeItem("Session");
    
  },
  methods: {
    login: async function() {
      console.log("login to back");      
      const data = {
        username: this.user,
        password: this.password
      };
      let url = "http://localhost:8080/user/login";
      const axiospost = await this.$axios.post(url, data, {
        headers: {
          "Content-Type": "application/json"
        }
      });
      //this.token = axiospost.data.jwt;
      console.log(axiospost.data);
      let decodedtoken = jwt_decode(axiospost.data);
      console.log(decodedtoken);
      console.log(decodedtoken.role);
      this.$token = "asd"
      let split = axiospost.data.split('&');
      sessionStorage.setItem("Session", split[1]);
      if(decodedtoken.role === "ADMIN"){
          this.$router.push("/admin");
      } else if(decodedtoken.role === "EMPLOYEE"){
          this.$router.push("/main");

      }
      //sessionStorage.setItem("userid", axiospost.data.userId);
     // this.$router.push("/admin");
    }
  }
};
</script>
<style></style>
