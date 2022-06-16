<template>
  <div class="column items-center">
    <div class="q-pa-md" style="min-width: 300px">
      <q-form class="row" @submit="login">
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
        <q-btn
          color="indigo-13"
          glossy
          label="Login"
          type="submit"
          class="col-sm-6 offset-sm-3 col-xs-12"
        />
      </q-form>
      <div id="g-signin2" class="googlebutton" @data-onsuccess="onSignIn"></div>

      <q-dialog v-model="alert">
        <q-card>
          <q-card-section>
            <div class="text-h6">Error</div>
          </q-card-section>

          <q-card-section class="q-pt-none">
            Las credenciales no son correctas
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="OK" color="primary" v-close-popup />
          </q-card-actions>
        </q-card>
      </q-dialog>
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
      token: "",
      alert: false
    };
  },
  async created() {
    sessionStorage.removeItem("Session");
    sessionStorage.removeItem("gtoken");
  },
  mounted() {
    this.init();
  },
  methods: {
    login: async function(mail, nombre, apellido) {
     // console.log(user.ts);
      let alert = false;
      console.log("login to back");
      let data = {};
      if(mail && nombre && apellido){
        data = {
          username: this.user,
          password: this.password,
          email: mail,
          name: nombre,
          lastname: apellido
        };
      } else {
        data = {
          username: this.user,
          password: this.password,
          email: "null",
          name: "null",
          lastname: "null"

        };
      }
      let body = {string: "string"};
      let url = process.env.BACKEND_URL + "auth/login";
      const axiospost = await this.$axios
        .post(url, data, body, {
          headers: {
            "Content-Type": "application/json"
          }
        })
        .then(response => {
          let decodedtoken = jwt_decode(response.data);
          this.$token = "asd";
          let split = response.data.split("&");
          console.log(split);
          sessionStorage.setItem("Session", split[0]);
          if (decodedtoken.role === "ADMIN") {
            this.$router.push("/admin");
          } else if (decodedtoken.role === "EMPLOYEE") {
            this.$router.push("/main");
          }
        })
        .catch(function(error) {
          alert = true;
        });
      this.alert = alert;
    },

    onSignIn(user) {
      //el login de google puede fallar si los valores para conseguir los datos (user.Ju) no estan actualizados recientemnete
      //console.log("user")
      console.log(user)
      let objetogoogle = user.Av;
      let mail = objetogoogle.mw;
      let nombre = objetogoogle.LZ;
      let apellido = objetogoogle.ZX;
      this.login(mail, nombre, apellido);
    },

    init: function() {
      if (!localStorage.getItem("access_token")) {
        window.gapi.load("auth2", () => {
          this.render();
          let auth2 = window.gapi.auth2.getAuthInstance({
            client_id: process.env.GOOGLE_ID,
            scope:
              "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile"
          });
        });
      } else {
        document.querySelector("#g-signin2").style.display = "none";
        sessionStorage.setItem("authok", true);
      }
    },

    render: async function() {
      window.gapi.signin2.render("g-signin2", {
        scope:
          "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile",
        width: 250,
        height: 50,
        longtitle: true,
        theme: "dark",
        onsuccess: this.onSignIn
      });
      console.log("render gapi");

      //console.log(window.gapi);
    }
  }
};
</script>
<style></style>
