<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 70vw; max-width: 70vw">
      <div class="q-pa-md list-style self-center" >
        <div class="q-pa-md">
          <q-table
            title="Vacaciones de los empleados"
            :data="data"
            :columns="columns"
            row-key="id"
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
      roles: ["Administrador", "Empleado"],
      selectedRol: "",
      columns: [
        {
          name: "",
          label: "Acciones",
          align: "center"
        },
        {
          name: "user",
          align: "center",
          label: "Empleado",
          field: row => row.user.name +' '+ row.user.lastname,
          sortable: true
        },

        {
          name: "startDate",
          align: "center",
          label: "Fecha inicio",
          field: "startDate",
          sortable: true
        },
        {
          name: "endDate",
          label: "Fecha fin",
          field: "endDate",
          align: "center",
          sortable: true
        },
        {
          name: "comment",
          label: "Comentarios",
          field: "comment",
          align: "center",
          sortable: true
        },
        {
          name: "approveDate",
          label: "Fecha aprobacion",
          field: "approveDate",
          align: "center",
          sortable: true
        },
        {
          name: "status",
          label: "Estado",
          field: row => this.text(row.status),
          align: "center",
          sortable: true
        },


      ],
      data: [],
      pagination: {
        sortBy: 'desc',
        descending: false,
        rowsPerPage: 10
      },
    };
  },
  async created() {
    this.token = sessionStorage.getItem("Session");
    console.log(this.token);
    this.listUsers();
  },
  methods: {

    text: function(filtro){
        console.log(filtro);
        let result;
        if(filtro === "PENDING"){
            result = "Pendiente";
        } else if(filtro === "APPROVED") {
            result = "Aprobado";
        } else if(filtro === "DENIED") {
            result = "Denegado";
        } else if (filtro === "CANCELED") {
            result = "Cancelado";
        }
       return result;
    },

    onFormReset: function(){
        this.dni = "";
        this.name = "";
        this.lastname = "";
        this.telephone = "";
        this.user = "";
        this.password = "";
        this.email = "";
        this.selectedRol = "";

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
    },

    addUser: async function() {
      if (this.modifyingId){
        console.log("UPDATE");
        this.updateUser();
      } else {
        console.log(this.token);
        let phone = this.telephone.replaceAll(" ", "");
        phone = parseInt(phone, 10);
        console.log(phone);
        let rol;
        if(this.selectedRol == 'Administrador') {
          rol = 'ADMIN';
        } else {
          rol = 'EMPLOYEE';
        }

        const data = {
          dni: this.dni,
          name: this.name,
          lastname: this.lastname,
          telephone: phone,
          username: this.user,
          password: "",
          email: this.email,
          role: rol
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
        this.listUsers();
                document.getElementById("resetButton").click();

      }
    },

    listUsers: async function() {
      let listarPosts = await axios.get("http://localhost:8080/calendar/vacations", {
        method: "GET",
        headers: new Headers({
          Authorization: "Bearer " + sessionStorage.getItem("Session")
        })
      });
      this.data = listarPosts.data;
      console.log(listarPosts.data);
    },

    cancelDelete: function() {
      console.log("cancelado");
      this.modifyingId = "";
    },

    deleteConfirmFunc: function(id) {
      this.modifyingId = id;
      this.deleteConfirm = true;
    },

    deleteUser: async function() {
      console.log(this.modifyingId);
      let borrado = await axios.delete("http://localhost:8080/user/delete", {
        headers: {
          Authorization: "Bearer " + this.token,
          "Content-Type": "application/json"
        },
        data: {
          id: this.modifyingId
        }
      });
      console.log("borrasion");
      this.listUsers();
      this.modifyingId = "";
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

      if(data.role == 'ADMIN') {
        this.selectedRol = 'Administrador';
      } else {
        this.selectedRol = 'Empleado';
      }
    },

    updateUser: async function(){
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
          "Content-Type": "application/json"
        }
      });
        this.listUsers();
              this.modifyingId = "";

        document.getElementById("resetButton").click();
        //this.onFormReset();
    }
  }
};
</script>

