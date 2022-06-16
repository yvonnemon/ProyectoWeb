<template>
  <q-page class="row items-center justify-evenly mainpage">
    <div class="q-gutter-y-md row justify-around" style="min-width: 70vw; max-width: 70vw">
      <q-card class="my-card bg-cyan-8 text-white col-sm-5 col-xs-12">
        <q-card-section>
          <div class="text-h6">Archivos recientes</div>
        </q-card-section>

        <q-card-section>
          <q-table
            title="Lista de archivos"
            :data="allFiles"
            :columns="columns"
            row-key="id"
            :pagination="pagination"
          >
          </q-table>
        </q-card-section>

        <q-separator dark />

        <q-card-actions>
          <q-btn flat @click="docs">Revisar</q-btn>
        </q-card-actions>
      </q-card>
      <q-card
        class="my-card bg-cyan-8 text-white col-sm-5 offset-sm-2 col-xs-12 offset-xs-auto"
      >
        <q-card-section>
          <div class="text-h6">Proximas vacaciones</div>
        </q-card-section>

        <q-card-section>
          <q-table
            title="Vacaciones aprobadas"
            :data="dates"
            :columns="columnsdate"
            row-key="id"
            :pagination="pagination"
          >
          </q-table>
          
        </q-card-section>

        <q-separator dark />

        <q-card-actions>
          <q-btn flat @click="vacation">Revisar</q-btn>
        </q-card-actions>
      </q-card>
    </div>
  </q-page>
</template>

<script>
const axios = require("axios");
export default {
  data() {
    return {
      allFiles: [],
      token: "",
      columns: [
        {
          name: "dni",
          required: true,
          label: "Dni",
          align: "center",
          field: row => row.user.dni,
          format: val => `${val}`,
          sortable: true
        },
        {
          name: "name",
          align: "center",
          label: "Nombre",
          field: row => this.cleanName(row.name),
          sortable: true
        },
        {
          name: "username",
          label: "Usuario",
          field: row => row.user.name + " " + row.user.lastname,
          format: val => `${val}`,
          align: "center",
          sortable: true
        }
      ],
      columnsdate: [
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
          name: "status",
          label: "Estado",
          field: row => this.text(row.status),
          align: "center",
          sortable: true
        },

      ],
      data: [],
      dates: [],
      pagination: {
        sortBy: "desc",
        descending: false,
        rowsPerPage: 5
      }
    };
  },

  async created() {
    this.token = sessionStorage.getItem("Session");
     await axios
        .get(process.env.BACKEND_URL+"document/employee", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
        })
        .then(response => {
          this.allFiles = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });

    this.listCalendar();
  },
  methods: {
    cleanName: function(string) {
      let result = string.split("_");
      return result[1];
    },
    listCalendar: async function() {
      let fail = false;
      let listarPosts = await axios.get(process.env.BACKEND_URL+"calendar/next", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
      }).then(response => {
        this.dates = response.data;
        console.log(this.dates);
       // this.vacationRanges(this.dates);
        })
        .catch(function(error) {
          console.log(error);
          fail = true;
        });

    },
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

      
      vacation: function(){
        this.$router.push("/main/vacation")
      },
      docs: function(){
        this.$router.push("/main/document")
      }

  }
};


</script>

