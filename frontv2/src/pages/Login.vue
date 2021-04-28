<template>
  <div class="column items-center">
    <div class="q-pa-md" style="min-width: 300px">
      <q-input
        filled
        v-model="user"
        label="Usuario"
        stack-label
        class="form-input"
      />
      <q-input
        filled
        v-model="password"
        label="Contraseña"
        stack-label
        class="form-input"
        type="password"
      />
      <q-btn color="deep-orange" glossy label="Login" @click="login2" />

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
    sessionStorage.removeItem("token");
    let separar;
    if (window.location.href.includes("?")) {
      separar = window.location.href.split("?");
    }

    if (separar != undefined) {
      let datos = separar[1];
      console.log(datos);

      let datos2 = datos.split("&");
      console.log(datos2);

      let usergoogle = datos2[1];
      let tokengoogle = datos2[0];
      this.token = tokengoogle;
      sessionStorage.setItem("token", this.token);
      sessionStorage.setItem("user", usergoogle.replace("%40", "@"));
      this.$router.push("/posts");
    }
  },
  methods: {
    login: function() {
      console.log("aosdijkands");
      console.log(this.user);
      if (this.user == "pepe" && this.password == "123") {
        sessionStorage.setItem("Session", 'asb123');
        this.$router.push("/main");
      }

    },
    login2: async function() {
      console.log("login to back");      
      const data = {
        username: this.user,
        password: this.password
      };
      let url = "http://localhost:8080/user/login";
      const axiospost = await this.$axios.post(url, data, {
        headers: {
          //Authorization: "Bearer " + this.token,
          "Content-Type": "application/json"
        }
      });
      //this.token = axiospost.data.jwt;
      console.log(axiospost.data);
      let asd = jwt_decode(axiospost.data);
      console.log(asd);
      this.$token = "asd"
      let split = axiospost.data.split('&');
      sessionStorage.setItem("Session", split[1]);
      //sessionStorage.setItem("userid", axiospost.data.userId);
      this.$router.push("/admin");
    }
  }
};
</script>
<style></style>
