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
      <q-btn color="deep-orange" glossy label="Login" @click="login" />
            <q-btn color="deep-orange" glossy label="asd" @click="login2" />

      <a :href="google"><q-btn color="deep-orange" glossy label="Google"/></a>
    </div>
  </div>
</template>

<script>
import { log } from 'util';
const axios = require("axios");
export default {
  data() {
    return {
      user: "",
      password: "",
      token: "",
      google: "http://localhost:3000/auth/google"
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
      debugger;
      console.log("hola");
      const data = {
        username: this.user,
        password: this.password
      };
      console.log(data);
      let url = "http://localhost:8080/user/login";
      const axiospost = await this.$axios.post(url, data, {
        headers: {
          //Authorization: "Bearer " + this.token,
          "Content-Type": "application/x-www-form-urlencoded"
        }
      });
      //this.token = axiospost.data.jwt;
      console.log(axiospost.data);
      //sessionStorage.setItem("token", this.token);
      //sessionStorage.setItem("userid", axiospost.data.userId);
      //this.$router.push("/posts");
    }
  }
};
</script>
<style></style>
