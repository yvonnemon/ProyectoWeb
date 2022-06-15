<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 70vw; max-width: 70vw">
      <q-expansion-item
        expand-separator
        icon="perm_identity"
        label="Fichar"
        class="form-style"
        v-model="expanded"
      >
        <div class="row justify-evenly fichaje flex-center">
          <q-btn-group spread class="col-11">
            <q-btn
              color="secondary"
              label="Entrada"
              icon="fas fa-clock"
              class="col-5"
              @click="insert"
            />
            <q-btn
              color="amber-4"
              label="Salida"
              icon="fas fa-clock"
              class="col-5"
              @click="update"
            />
          </q-btn-group>
        </div>
      </q-expansion-item>

      <div class="q-pa-md list-style self-center">
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
      expanded: false,
      columns: [
        {
          name: "user",
          align: "center",
          label: "Empleado",
          field: (row) => row.user.name + " " + row.user.lastname,
          sortable: true,
        },
        {
          name: "startDate",
          align: "center",
          label: "Fecha inicio",
          mask:"YYYY-MM-DD HH:mm",
          field: "startDate",
          sortable: true,
        },
        {
          name: "endDate",
          label: "Fecha fin",
          field: row => this.dateEmpty(row.endDate),
          align: "center",
          sortable: true,
        },
      ],
      data: [],
      pagination: {
        sortBy: "desc",
        descending: false,
        rowsPerPage: 10,
      },
    };
  },
  async created() {
    this.token = sessionStorage.getItem("Session");
    console.log(this.token);
    this.listClocin();
  },
  methods: {
    listClocin: async function () {
      let fail = false;
      let listarPosts = await axios
        .get(process.env.BACKEND_URL + "clock/userclockin", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json",
          },
        })
        .then((response) => {
          console.log(response);
          this.data = response.data;
          console.log(this.data);
        })
        .catch(function (error) {
          console.log(error);
          fail = true;
        });

      if (fail) {
        this.showNotif();
      }
    },
    
    insert: async function () {
      let fail = false;
      let datos = {
        data: 0
      };

      let url = process.env.BACKEND_URL + "clock/insert";
      const axiospost = await axios
        .post(url, datos, {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
        })
        .then((response) => {
          console.log(response);
          this.listClocin();
          this.expanded = false;
        })
        .catch(function (error) {
          fail = true;
          console.log(error);
        });
      if (fail) {
        this.showNotif();
      }
    },

    update: async function () {
      let fail = false;
      let datos = {
        data: 0
      };

      let url = process.env.BACKEND_URL + "clock/update";
      const axiospost = await axios
        .put(url, datos, {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
        })
        .then((response) => {
          console.log(response);
          this.listClocin();
          this.expanded = false;
        })
        .catch(function (error) {
          fail = true;
          console.log(error);
        });
      if (fail) {
        this.showNotif();
      }
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

    dateEmpty: function(empty) {
        if(empty == null) {
            return "Sin marcaje"
        } else {
            return empty;
        }
    },

  },
};
</script>
