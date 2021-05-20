<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 70vw; max-width: 70vw">
        <q-card>
          <q-card-section class="row user-form">
            <q-form
            class="row"
              @submit="updateUser"
                >
            <q-input
              outlined
              v-model="name"
              label="Nombre"
              stack-label
              @keypress="userrandom"
              class="form-input col-sm-6 col-xs-12"
              :rules="[
                val => !!val || 'Campo necesario',
                val => !val.includes(' ') || 'No puede haber espacios en blanco'
              ]"
            />
            <q-input
              outlined
              v-model="lastname"
              label="Apellido"
              stack-label
              @keypress="userrandom"
              class="form-input col-sm-6 col-xs-12"
              :rules="[val => !!val || 'Campo necesario']"
            />
            <q-input
              outlined
              v-model="dni"
              label="Dni"
              stack-label
              class="form-input col-sm-6 col-xs-12"
              :rules="[
                val => !!val || 'Campo necesario',
                val => !val.includes(' ') || 'No puede haber espacios en blanco'
              ]"
            />

            <q-input
              outlined
              v-model="email"
              label="Email"
              type="email"
              stack-label
              class="form-input col-sm-6 col-xs-12"
              :rules="[
                val => !!val || 'Campo necesario',
                val => !val.includes(' ') || 'No puede haber espacios en blanco',
                val => val.includes('@') || 'Dirección no válida',
              ]"
            />
            <q-input
              outlined
              v-model="telephone"
              label="Telefono"
              stack-label
              class="form-input col-sm-6 col-xs-12"
              mask="### ### ###"
              :rules="[val => !!val || 'Campo necesario']"
            />

            <q-input
              outlined
              v-model="user"
              label="Usuario"
              stack-label
              class="form-input col-sm-6 col-xs-12"
              readonly
            />

            <q-input
              v-model="password"
              outlined
              :type="isPwd ? 'password' : 'text'"
              label="Contraseña"
              stack-label
              class="form-input col-sm-6 col-xs-12"
              :rules="[val => !!val || 'Campo necesario']"
            >
              <template v-slot:append>
                <q-icon
                  :name="isPwd ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="isPwd = !isPwd"
                />
              </template>
            </q-input>

            <q-input
              outlined
              v-model="address"
              label="Dirección"
              stack-label
              @keypress="userrandom"
              class="form-input col-sm-6 col-xs-12"
              :rules="[val => !!val || 'Campo necesario']"
            />

            <q-btn
              color="green-8"
              class="col-sm-2 offset-sm-10 form-buttons col-xs-12 offset-xs-auto"
              glossy
              type="submit"
              label="Actualizar"
            />
            </q-form>
          </q-card-section>
        </q-card>
    </div>
  </div>
</template>

<script>
import jwt_decode from "jwt-decode";
const axios = require("axios");
export default {
  data() {
    return {
      password: "",
      name: "",
      lastname: "",
      dni: "",
      user: "",
      email: "",
      telephone: "",
      address:"",
      isPwd: true,
      token: "",
      tab: "list",
      token: "",
      modifyingId: "",
      roles: ["Administrador", "Empleado"],
      selectedRol: "",
      data: [],
    };
  },
  async created() {
    this.token = sessionStorage.getItem("Session");
    this.gtoken = sessionStorage.getItem("gtoken")
    console.log(this.token);
    this.getUser();
  },
  methods: {
    userrandom: function() {
      console.log(this.name.length);
      if (this.name.length >= 1) {
        //empeiza en 0, asi que esto son 2 caracteres
        this.user = this.name.substring(0, 2);
      }
      if (this.lastname.length >= 1) {
        this.user = this.user.concat(this.lastname.substring(0, 2));
      }
    },
    getUser: async function() {
      this.token = sessionStorage.getItem("Session");
      let theUser = jwt_decode(this.token);
      console.log(theUser.user.id);
      let userinfo = await axios.post("http://localhost:8080/user/user", {
        headers:{
            Authorization: "Bearer " + this.token,
            Gauth: "Bearer " + this.gtoken,
          "Content-Type": "application/json"
        },
        id: theUser.user.id
        
      });
      this.updateData(userinfo.data);
    },

    updateData: function(data) {
      console.log(data);
      let rol;
      this.modifyingId = data.id;
      this.expanded = true;
      this.name = data.name;
      this.lastname = data.lastname;
      this.dni = data.dni;
      this.email = data.email;
      this.telephone = data.telephone;
      this.user = data.username;
            this.address = data.address;

     // this.selectedRol = data.role;

      if(data.role == 'ADMIN') {
        this.selectedRol = 'Administrador';
      } else {
        this.selectedRol = 'Empleado';
      }
    },

    updateUser: async function(){
                let fail = false;

      let phone = this.telephone.replaceAll(" ", "");
        phone = parseInt(phone, 10);
        let rol;
        if(this.selectedRol == 'Administrador') {
          rol = 'ADMIN';
        } else {
          rol = 'EMPLOYEE';
        }

      const data = {
        id: this.modifyingId,
        dni: this.dni,
        name: this.name,
        lastname: this.lastname,
        telephone: phone,
        username: this.user,
        password: this.password,
        email: this.email,
        role: rol
      };

      let url = "http://localhost:8080/user/update";
      const axiospost = await axios.put(url, data, {
        headers: {
            Authorization: "Bearer " + this.token,
            Gauth: "Bearer " + this.gtoken,
          "Content-Type": "application/json"
        }
      }).then(response => {
        this.showNotifOk();
        })
        .catch(function(error) {
          console.log(error);
          fail = true;
        });
      if (fail) {
          console.log("delete error?");
        this.showNotif();
      };
        //this.onFormReset();
    },

    showNotif() {
      this.$q.notify({
        message: "Hubo un error",
        color: "negative"
      });
    },

    showNotifOk () {
      const notif = this.$q.notify({
        group: false, // required to be updatable
        timeout: 0, // we want to be in control when it gets dismissed
        spinner: true,
        type: "positive",
        message: 'Actualizando datos...',
        caption: '0%'
      })

      let percentage = 0
      const interval = setInterval(() => {
        percentage = Math.min(100, percentage + Math.floor(Math.random() * 22))

        notif({
          caption: `${percentage}%`
        })

        if (percentage === 100) {
          notif({
            icon: 'done', 
            spinner: false, // we reset the spinner setting so the icon can be displayed
            message: 'Datos actualizados',
            timeout: 2500 
          })
          clearInterval(interval)
          this.$router.push("/main");
        }
      }, 500)
    }
  }
};
</script>
<style></style>
