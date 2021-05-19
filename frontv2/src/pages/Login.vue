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
          class="col-sm-6 offset-sm-2 col-xs-12"
        />
      </q-form>
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
          <div id="g-signin2" style="background-color: pink;" @data-onsuccess="onSignIn"></div>

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
    //this.init();
  },
   mounted() {
    window.onSignIn = this.onSignIn();

    //this.render();
      /*  window.gapi.signin2.render('g-signin2', {
        scope: 'https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile',
        width: 250,
        height: 50,
        longtitle: true,
        theme: 'dark',
        onsuccess: this.onSignIn
    })*/

    this.init();

  },
  methods: {
    login: async function() {
      let alert = false;
      console.log("login to back");
      const data = {
        username: this.user,
        password: this.password
      };
      let url = "http://localhost:8080/user/login";
      const axiospost = await this.$axios
        .post(url, data, {
          headers: {
            "Content-Type": "application/json"
          }
        })
        .then(response => {
          let decodedtoken = jwt_decode(response.data);
          this.$token = "asd";
          let split = response.data.split("&");
          sessionStorage.setItem("Session", split[1]);
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
    /******************************************************************************login? */

      onSignIn(user) {
        console.log("lasdfjkansdfnasd");
        console.log(user);
      /*const access_token = user.uc.access_token;
      //la linea esta de arriba, en el momendo que entregue la practica, era user.Zi.access_token, y por algun motivo
      //que desconozco, al hacerlo ahora para reentregarla, pasa a ser user.uc.access_token
      localStorage.setItem("access_token", access_token);
      document.querySelector("#g-signin2").style.visibility = "hidden";
      sessionStorage.setItem("authok",true);
      location.reload();*/
      },

//TODO
      init: function() { //onload esto 
      console.log(window.gapi);
      console.log(" ^^^window.gapi");
      //storageremove();
      if (!localStorage.getItem("access_token")) {
          window.gapi.load('auth2', () => {
            this.render()
              let auth2 = window.gapi.auth2.getAuthInstance({
                  client_id: process.env.GOOGLE_ID,
                  scope: 'https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile'
              });
          });
          //this.render();

      } else {
          document.querySelector("#g-signin2").style.display = "none";
          sessionStorage.setItem("authok",true);
      }
    },

   render: async function() {
        window.gapi.signin2.render('g-signin2', {
            scope: 'https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile',
            width: 250,
            height: 50,
            longtitle: true,
            theme: 'dark',
            onsuccess: this.onSignIn
        })
             console.log("render gapi");

        console.log(window.gapi);
    }

  }
};




function signOut() {
    let auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
    });
}

</script>
<style></style>
