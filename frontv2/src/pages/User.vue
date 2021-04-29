<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 70vw; max-width: 70vw" >
      <q-expansion-item
        expand-separator
        icon="perm_identity"
        label="Añadir usuario"
        class="form-style"
      >
        <q-card>
          <q-card-section class="row">
            <q-input
              outlined 
              v-model="name"
              label="Nombre"
              stack-label
              @keypress="userrandom"
              class="form-input col-3"
              :rules="[val => !!val || 'Campo necesario', val => !val.includes(' ') || 'No puede haber espacios en blanco']"
            />
            <q-input
              outlined 
              v-model="lastname"
              label="Apellido"
              stack-label
              @keypress="userrandom"
              class="form-input col-3"
              :rules="[val => !!val || 'Campo necesario']"
            />
             <q-input
              outlined 
              v-model="dni"
              label="Dni"
              stack-label
              class="form-input col-3"
              :rules="[val => !!val || 'Campo necesario', val => !val.includes(' ') || 'No puede haber espacios en blanco']"
            />

            <q-input
              outlined 
              v-model="email"
              label="Email"
              type="email"
              stack-label
              class="form-input col-3"
              :rules="[val => !!val || 'Campo necesario', val => !val.includes(' ') || 'No puede haber espacios en blanco']"
            />
            <q-input
              outlined 
              v-model="telephone"
              label="Telefono"
              stack-label
              class="form-input col-3"
              mask="###-###-###"
              :rules="[val => !!val || 'Campo necesario']"
            />

            <q-input
              outlined 
              v-model="user"
              label="Usuario"
              stack-label
              class="form-input col-3"
              readonly 
            />

      <q-input v-model="password" outlined :type="isPwd ? 'password' : 'text'"
       label="Contraseña" stack-label class="form-input col-3" :rules="[val => !!val || 'Campo necesario']">
        <template v-slot:append>
          <q-icon
            :name="isPwd ? 'visibility_off' : 'visibility'"
            class="cursor-pointer"
            @click="isPwd = !isPwd"
          />
        </template>
      </q-input>
      
            <q-select outlined class="form-input col-3" v-model="selectedRol" :options="roles" label="Rol" />

            <q-btn
              color="deep-orange"
              class="col-1 offset-11"
              glossy
              label="Añadir"
              @click="addUser"
            />
          </q-card-section>
        </q-card>
      </q-expansion-item>

      <div class="q-pa-md list-style self-center" style="min-width: 350px">
        <div class="q-pa-md">
          <q-table
            title="Lista de usuarios"
            :data="data"
            :columns="columns"
            row-key="dni"

            
          >

      <template v-slot:body="props">
        <q-tr :props="props">
          <q-td auto-width>
            <q-btn size="md" color="amber-9" round dense @click="" icon="fas fa-pen" />
            <q-btn size="md" class="table-actions" color="negative" round dense @click="deleteConfirmFunc(props.row.id)" icon="fas fa-trash-alt" />
          </q-td>
          <q-td
            v-for="col in props.cols"
            :key="col.name"
            :props="props"
          >
            {{ col.value }}

          </q-td>
        </q-tr>
      </template>
      
      </q-table>
       </div>
      </div>
      <q-dialog v-model="deleteConfirm" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="fas fa-user" color="indigo" text-color="white" />
          <span class="q-ml-sm">¿Esta seguro de querer borrar el usuario?</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Aceptar" color="positive" @click="deleteUser" v-close-popup />
          <q-btn flat label="Cancelar" color="red" @click="cancelDelete" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
    </div>
  </div>
</template>

<script>
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
      isPwd: true,
      token: "",
      tab: "list",
      token: "",
      deleteConfirm: false,
      deletingId: "",
      roles: ["asdmin","asd"],
      selectedRol: "",
      columns: [
                {
          name: "",
          label: "Acciones",
          align: "center",
        },
        {
          name: "dni",
          required: true,
          label: "Dni",
          align: "center",
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
        { name: "username", label: "Usuario", field: "username", align: "center", sortable: true },
        { name: "role", label: "Rol", field: "role", align: "center", sortable: true }
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
    userrandom: function() {
      console.log(this.name.length);
      if(this.name.length >= 1){ //empeiza en 0, asi que esto son 2 caracteres
        this.user = this.name.substring(0,2);
      }
      if(this.lastname.length >= 1){
        this.user = this.user.concat(this.lastname.substring(0,2));
      }
    },

    addUser: async function() {
      console.log(this.token);
      let phone = this.telephone.replace('-','');
      phone = parseFloat(phone,10);
      const data = {
          dni: this.dni,
          name: this.name,
          lastname: this.lastname,
          telephone: phone,
          username: this.user,
          password: this.password,
          email: this.email,
          role: 'ADMIN'
        };
      console.log(data);
      let url = "http://localhost:8080/user/insert";
        const axiospost = await axios.post(url, data, {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
        });
        console.log(axiospost);
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

    cancelDelete: function(){
      console.log("cancelado");
      this.deletingId = "";
    },

    deleteConfirmFunc: function (id) {
      this.deletingId = id;
      this.deleteConfirm = true;
    },

    deleteUser: async function() {
        console.log(this.deletingId);
            let borrado = await axios.delete('http://localhost:8080/user/delete',{
              headers: {
                Authorization: "Bearer " + this.token,
              "Content-Type": "application/json"
              },
		          data: {
                id: this.deletingId
              }
            })        
      console.log('borrasion');
      this.listUsers();
    },
    
  }
};
</script>
<style></style>
