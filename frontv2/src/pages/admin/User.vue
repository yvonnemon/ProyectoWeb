<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 70vw; max-width: 70vw">
      <q-expansion-item
        expand-separator
        icon="perm_identity"
        label="Añadir usuario"
        class="form-style"
        v-model="expanded"
      >
        <q-card>
          <q-card-section class="row">
            <q-form class="row" @submit="addUser" @reset="onFormReset">
              <q-input
                outlined
                v-model="name"
                label="Nombre"
                stack-label
                @keypress="userrandom"
                class="form-input col-sm-3 col-xs-12"
                :rules="[
                  val => !!val || 'Campo necesario',
                  val =>
                    !val.includes(' ') || 'No puede haber espacios en blanco'
                ]"
              />
              <q-input
                outlined
                v-model="lastname"
                label="Apellido"
                stack-label
                @keypress="userrandom"
                class="form-input col-sm-3 col-xs-12"
                :rules="[val => !!val || 'Campo necesario']"
              />
              <q-input
                outlined
                v-model="dni"
                label="Dni"
                stack-label
                @keypress="userrandom"
                class="form-input col-sm-3 col-xs-12"
                :rules="[
                  val => !!val || 'Campo necesario',
                  val =>
                    !val.includes(' ') || 'No puede haber espacios en blanco'
                ]"
              />

              <q-input
                outlined
                v-model="email"
                label="Email"
                type="email"
                stack-label
                class="form-input col-sm-3 col-xs-12"
                :rules="[
                  val => !!val || 'Campo necesario',
                  val =>
                    !val.includes(' ') || 'No puede haber espacios en blanco',
                  val => val.includes('@') || 'Dirección no válida'
                ]"
              />
              <q-input
                outlined
                v-model="telephone"
                label="Telefono"
                stack-label
                class="form-input col-sm-3 col-xs-12"
                mask="### ### ###"
                :rules="[val => !!val || 'Campo necesario']"
              />

              <q-input
                outlined
                v-model="user"
                label="Usuario"
                stack-label
                class="form-input col-sm-3 col-xs-12"
                readonly
              />

              <q-input
                outlined
                v-model="address"
                label="Direccion"
                stack-label
                class="form-input col-sm-3 col-xs-12"
                :rules="[val => !!val || 'Campo necesario']"
              />

              <q-select
                outlined
                class="form-input col-sm-3 col-xs-12"
                v-model="selectedRol"
                :options="roles"
                label="Rol"
                :rules="[val => !!val || 'Campo necesario']"
              />
              <q-btn
                label="Limpar"
                type="reset"
                color="amber-14"
                outline
                class="col-sm-1 offset-sm-9 form-buttons col-xs-12 offset-xs-auto"
                id="resetButton"
              />
              <q-btn
                color="green-8"
                class="col-sm-1 form-buttons col-xs-12 offset-xs-auto"
                glossy
                type="submit"
                label="Añadir"
              />
            </q-form>
          </q-card-section>
        </q-card>
      </q-expansion-item>

      <div class="q-pa-md list-style self-center">
        <div class="q-pa-md">
          <q-table
            title="Lista de usuarios"
            :data="data"
            :columns="columns"
            row-key="dni"
            :pagination="pagination"
          >
            <template v-slot:body="props">
              <q-tr :props="props">
                <q-td auto-width>
                  <q-btn
                    size="md"
                    color="amber-9"
                    round
                    dense
                    @click="updateData(props.row)"
                    icon="fas fa-pen"
                  />
                  <q-btn
                    size="md"
                    class="table-actions"
                    color="negative"
                    round
                    dense
                    @click="deleteConfirmFunc(props.row.id)"
                    icon="fas fa-trash-alt"
                  />
                </q-td>
                <q-td v-for="col in props.cols" :key="col.name" :props="props">
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
            <span class="q-ml-sm"
              >¿Esta seguro de querer borrar el usuario?</span
            >
          </q-card-section>

          <q-card-actions align="right">
            <q-btn
              flat
              label="Cancelar"
              color="red"
              @click="cancelDelete"
              v-close-popup
            />
            <q-btn
              flat
              label="Aceptar"
              color="positive"
              @click="deleteUser"
              v-close-popup
            />
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
      expanded: false,
      deleteConfirm: false,
      modifyingId: "",
      address: "",
      roles: ["Administrador", "Empleado"],
      selectedRol: "",
      columns: [
        {
          name: "",
          label: "Acciones",
          align: "center"
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
        {
          name: "email",
          label: "Email",
          field: "email",
          align: "center",
          sortable: true
        },
        {
          name: "username",
          label: "Usuario",
          field: "username",
          align: "center",
          sortable: true
        },
        {
          name: "role",
          label: "Rol",
          field: row => this.translate(row.role),
          align: "center",
          sortable: true
        }
      ],
      data: [],
      pagination: {
        sortBy: "desc",
        descending: false,
        rowsPerPage: 10
      }
    };
  },
  async created() {
    this.token = sessionStorage.getItem("Session");
    console.log(this.token);
    this.listUsers();
  },
  methods: {
    translate: function(texto) {
      console.log(texto);
      let result;
      if (texto === "ADMIN") {
        result = "Adminsitrador";
        return result;
      } else if (texto === "EMPLOYEE") {
        result = "Empleado";
        return result;
      }
    },

    onFormReset: function() {
      this.dni = "";
      this.name = "";
      this.lastname = "";
      this.telephone = "";
      this.user = "";
      this.password = "";
      this.email = "";
      this.selectedRol = "";
      this.address = "";
    },

    userrandom: function() {
      console.log(this.name.length);
      if (this.name.length >= 1) {
        //empeiza en 0, asi que esto son 2 caracteres
        this.user = this.name.substring(0, 2);
      }
      if (this.lastname.length >= 1) {
        this.user = this.user.concat(this.lastname.substring(0, 2));
      }
      if (this.dni.length >= 1) {
        this.user = this.user.concat(this.dni);
      }
    },

    addUser: async function() {
      let fail = false;

      if (this.modifyingId) {
        console.log("UPDATE");
        this.updateUser();
      } else {
        console.log(this.token);
        let phone = this.telephone.replaceAll(" ", "");
        phone = parseInt(phone, 10);
        console.log(phone);
        let rol;
        if (this.selectedRol == "Administrador") {
          rol = "ADMIN";
        } else {
          rol = "EMPLOYEE";
        }

        const data = {
          dni: this.dni,
          name: this.name,
          lastname: this.lastname,
          telephone: phone,
          username: this.user,
          password: "",
          email: this.email,
          address: this.address,
          role: rol
        };

        console.log(data);
        let url = "http://localhost:8080/user/insert";
        const axiospost = await axios
          .post(url, data, {
            headers: {
              Authorization: "Bearer " + this.token,
              "Content-Type": "application/json"
            }
          })
          .then(response => {
            console.log(response);
            this.listUsers();
            document.getElementById("resetButton").click();
          })
          .catch(function(error) {
            console.log(error);
            fail = true;
          });
        if (fail) {
          this.showNotif();
        }
      }
    },

    listUsers: async function() {
      let fail = false;

      let listarPosts = await axios
        .get("http://localhost:8080/user/users", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
        })
        .then(response => {
          this.data = response.data;
          console.log(response.data);
        })
        .catch(function(error) {
          console.log(error);
          fail = true;
        });
      if (fail) {
        this.showNotif();
      }
    },

    cancelDelete: function() {
      console.log("cancelado");
      this.modifyingId = "";
    },

    deleteConfirmFunc: function(id) {
      this.modifyingId = id;
      this.deleteConfirm = true;
    },

    showNotif() {
      this.$q.notify({
        message: "Hubo un error",
        color: "negative"
      });
    },

    deleteUser: async function() {
      let fail = false;
      console.log(this.modifyingId);
      let borrado = await axios
        .delete("http://localhost:8080/user/delete", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          },
          data: {
            id: this.modifyingId
          }
        })
        .then(response => {
          this.listUsers();
          this.modifyingId = "";
          console.log("borrasion");
        })
        .catch(function(error) {
          fail = true;
        });
      if (fail) {
        this.showNotif();
      }
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
      // this.selectedRol = data.role;

      if (data.role == "ADMIN") {
        this.selectedRol = "Administrador";
      } else {
        this.selectedRol = "Empleado";
      }
    },

    updateUser: async function() {
      let fail = false;
      let phone = this.telephone.replaceAll(" ", "");
      phone = parseInt(phone, 10);
      let rol;
      if (this.selectedRol == "Administrador") {
        rol = "ADMIN";
      } else {
        rol = "EMPLOYEE";
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
      const axiospost = await axios
        .put(url, data, {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
        })
        .then(response => {
          this.listUsers();
          this.modifyingId = "";
          document.getElementById("resetButton").click();
        })
        .catch(function(error) {
          console.log(error);
          fail = true;
        });
      if (fail) {
        this.showNotif();
      }
    }
  }
};
</script>
<style></style>
