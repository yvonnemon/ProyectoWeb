<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 70vw; max-width: 70vw">
      <div class="q-pa-md list-style self-center" >
        <div class="q-pa-md">
          <q-table
            title="Fichajes de hoy"
            :data="data"
            :columns="columns"
            row-key="id_clock"
            :pagination="pagination"
          >
            <template v-slot:body="props">
               <q-tr :props="props">
                <q-td auto-width>
                  <q-btn
                    size="md"
                    color="indigo-6"
                    round
                    dense
                    @click="stalk(props.row)"
                    icon="fas fa-eye"
                  >
                   <q-popup-proxy>
                    <div style="min-width: 40vw">
                    <q-banner>
                      
                      <q-table
                        title="Ultimos fichajes"
                        :data="userdata"
                        :columns="columnas"
                        row-key="id_clock"
                        :pagination="pagination"
                      >
                        <template v-slot:body="props">
                          <q-tr :props="props">
                            <q-td v-for="col in props.cols" :key="col.name" :props="props">
                              {{ col.value }}
                            </q-td>
                          </q-tr>
                        </template>
                      </q-table>
                    </q-banner>
                    </div>
                  </q-popup-proxy>
                  </q-btn>
                </q-td>
                <q-td v-for="col in props.cols" :key="col.name" :props="props">
                  {{ col.value }}
                </q-td>
              </q-tr>

            </template>
          </q-table>
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
      token: "",
      columns: [
        {
          name: "",
          label: "Acciones",
          align: "right"
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
          field: row => this.dateEmpty(row.endDate),
          align: "center",
          sortable: true,
        }],
      columnas: [
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
          field: row => this.dateEmpty(row.endDate),
          align: "center",
          sortable: true,
        }],

      data: [],
      userdata: [],
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
    this.listClocin();
  },
  methods: {
      listClocin: async function() {
      let fail = false;
      let listarPosts = await axios.get(process.env.BACKEND_URL+"clock/all", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
      }).then(response => {
        console.log(response);
        this.data = response.data;
        console.log(this.data[0].user);

        })
        .catch(function(error) {
          console.log(error);
          fail = true;
        });

      if (fail) {
        this.showNotif();
      };
    },

    deleteConfirm: function(id) {
      this.modifyingId = id;
      this.deleteConfirm = true;
    },

    stalk: async function (item) {
      console.log(item);
      let fail = false;
        let data = {
          id: item.user.id
        };

      let listarPosts = await axios
        .post(process.env.BACKEND_URL + "clock/stalk", data, {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json",
          },
        })
        .then((response) => {
          console.log(response);
          this.userdata = response.data;
          console.log(this.userdata);
        })
        .catch(function (error) {
          console.log(error);
          fail = true;
        });

      if (fail) {
        this.showNotif();
      }

    },

    dateEmpty: function(empty) {
        if(empty == null) {
            return "Sin marcaje"
        } else {
            return empty;
        }
    },


    deleteVacation: async function() {
      let fail = false;
      console.log(this.modifyingId);
      let borrado = await axios
        .delete(process.env.BACKEND_URL+"calendar/delete", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          },
          data: {
            id: this.modifyingId
          }
        })
        .then(response => {
          this.listCalendar();
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

    cancelDelete: function() {
      console.log("cancelado");
      this.modifyingId = "";
    },

    showNotif() {
      this.$q.notify({
        message: "Hubo un error",
        color: "negative",
      });
    },

    showNotifOk() {
      const notif = this.$q.notify({
        group: false, // required to be updatable
        timeout: 0, // we want to be in control when it gets dismissed
        spinner: true,
        type: "positive",
        message: "Actualizando datos...",
        caption: "0%"
      });

      let percentage = 0;
      const interval = setInterval(() => {
        percentage = Math.min(100, percentage + Math.floor(Math.random() * 22));

        notif({
          caption: `${percentage}%`
        });

        if (percentage === 100) {
          notif({
            icon: "done",
            spinner: false, // we reset the spinner setting so the icon can be displayed
            message: "Datos actualizados",
            timeout: 2500
          });
          clearInterval(interval);
          this.$router.push("/main");
        }
      }, 500);
    },

  }
};
</script>

