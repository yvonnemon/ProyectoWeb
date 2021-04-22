<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 350px">
      <q-expansion-item
        expand-separator
        icon="perm_identity"
        label="Añadir usuario"
        class="form-style"
      >
        <q-card>
          <q-card-section class="row">
            <q-input
              filled
              v-model="user"
              label="Nombre"
              stack-label
              class="form-input col-6"
            />
            <q-input
              filled
              v-model="user"
              label="Apellido"
              stack-label
              class="form-input col-6"
            />
            <q-input
              filled
              v-model="user"
              label="Email"
              stack-label
              class="form-input col-6"
            />

            <q-input
              filled
              v-model="password"
              label="Contraseña"
              stack-label
              type="password"
              class="form-input col-6"
            />
            <q-btn
              color="deep-orange"
              glossy
              label="Añadir"
              @click="registrar"
            />
          </q-card-section>
        </q-card>
      </q-expansion-item>

      <div class="q-pa-md list-style self-center" style="min-width: 350px">
        <div class="q-pa-md">
          <q-table
            title="Treats"
            :data="data"
            :columns="columns"
            row-key="dni"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const axios = require("axios");
export default {
  data() {
    return {
      user: "",
      password: "",
      token: "",
      tab: "list",
      token: "",
      columns: [
        {
          name: "dni",
          required: true,
          label: "Dni",
          align: "left",
          field: row => row.dni,
          format: val => `${val}`,
          sortable: true
        },
        {
          name: "name",
          align: "center",
          label: "Nombre",
          field: "name",
          sortable: true
        },
        { name: "email", label: "Email", field: "email", align: "center", sortable: true },
        { name: "username", label: "Usuario", field: "username", align: "center", sortable: true }
      ],
      data: []
    };
  },
  async created() {
    this.token = sessionStorage.getItem("Session")
    console.log(this.token);
    this.listUsers();
  },
  methods: {
    registrar: function() {
      console.log("aosdijkands");
      console.log(this.user);
      if (this.user == "pepe" && this.password == "123") {
        sessionStorage.setItem("Session", "asb123");
        this.$router.push("/main");
      }
    },

    listUsers: async function() {

      let listarPosts = await axios.get("http://localhost:8080/user/users", {
        method: "GET",
        headers: new Headers({
          Authorization: "Bearer " + sessionStorage.getItem("Session")
        })
      });
      this.data = listarPosts.data
      console.log(listarPosts.data);
    },

    login2: async function() {
      console.log("hola");
      const data = {
        user: this.user,
        pass: this.password
      };
      let url = "http://localhost:3000/auth/login";
      const axiospost = await axios.post(url, data, {
        headers: {
          Authorization: "Bearer " + this.token,
          "Content-Type": "application/json"
        }
      });
      this.token = axiospost.data.jwt;
      console.log(axiospost.data);
      sessionStorage.setItem("token", this.token);
      sessionStorage.setItem("userid", axiospost.data.userId);
      this.$router.push("/posts");
    }
  }
};
</script>
<style></style>
