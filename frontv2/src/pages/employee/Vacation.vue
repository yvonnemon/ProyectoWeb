<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 60vw; max-width: 60vw">

      <q-expansion-item
        expand-separator
        icon="perm_identity"
        label="Solicitar vacaciones"
        class="form-style"
        v-model="expanded"
      >
        <q-card>
          <q-card-section class="row overflow-auto">
            <q-form class="row" @submit="insert" @reset="onFormReset">
              <q-date class="form-input col-sm-4 offset-sm-4 col-xs-12 offset-xs-auto" 
              v-model="dates" range :options='optionsFn' subtitle="Solicitando"/>

              <q-input
                outlined
                v-model="comment"
                label="Comentario"
                stack-label
                autogrow
                class="form-input col-sm-4 col-xs-12 offset-sm-4"
                
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
                class="col-sm-1 form-buttons col-xs-12 offset-auto"
                glossy
                type="submit"
                label="Solicitar"
              />
            </q-form>
          </q-card-section>
        </q-card>
      </q-expansion-item>

      <div class="q-pa-md list-style self-center" >
        <div class="q-pa-md">
          <q-table
            title="Historial de vacaciones"
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
                    class="table-actions"
                    color="red-5"
                    round
                    dense
                    @click="deleteConfirmFunc(props.row.id)"
                    :disable="props.row.status =='CANCELED'"
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
              >¿Esta seguro de querer cancelar la peticion?</span
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
              @click="deleteVacation"
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
import { date } from 'quasar'
export default {
  data() {
    return {
      token: "",
      expanded: false,
      deleteConfirm: false,
      modifyingId: "",
      comment: "",
      dates: { from: '', to: '' },
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
          sortable: true,
          sortOrder: 'ad'

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
    this.listCalendar();
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

    optionsFn (fecha) {
      return fecha >= date.formatDate(Date.now(), 'YYYY/MM/DD') 
    },

    showNotif() {
      this.$q.notify({
        message: "Hubo un error",
        color: "negative"
      });
    },

    showNotifOK() {
      this.$q.notify({
        message: "Actualizado correctamente",
        color: "positive"
      });
    },

    onFormReset: function() {
      this.dates.to = '';
      this.dates.from = '';
      this.comment = '';
    },

    insert: async function() {
        let dateFrom = new Date();
        dateFrom = Date.parse(this.dates.from);

        let dateTo = new Date();
        dateTo = Date.parse(this.dates.to);

        const data = {
          comment: this.comment,
          startDate: dateFrom,
          endDate: dateTo
        };
        console.log(data);
        let url = "http://localhost:8080/calendar/insert";
        const axiospost = await axios.post(url, data, {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
        }).then(response => {
          console.log(response);
          this.listCalendar();
          document.getElementById("resetButton").click();
          this.expanded = false;
        })
        .catch(function(error) {
          fail = true;
          console.log(error);
        });
      if (fail) {
        this.showNotif();
      };
    },

    listCalendar: async function() {//TODO catch error
      let fail = false;
      let listarPosts = await axios.get("http://localhost:8080/calendar/users", {
        headers:{
          Authorization: "Bearer " + sessionStorage.getItem("Session")
        }
      }).then(response => {
        console.log(response.data);
          this.data = response.data;
          this.showNotifOK();
        })
        .catch(function(error) {
          fail = true;
          console.log(error);
        });
      if (fail) {
        this.showNotif();
      };
    },

    cancelDelete: function() {
      console.log("cancelado");
      this.modifyingId = "";
    },

    deleteConfirmFunc: function(id) {
      this.modifyingId = id;
      this.deleteConfirm = true;
    },

    deleteVacation: async function() {
      let fail = false;
      console.log(this.modifyingId);
      let borrado = await axios
        .delete("http://localhost:8080/calendar/delete", {
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

    update: async function(status){
      let fail = false;
      const data = {
        id: this.modifyingId,
        status: status
        };
      console.log(data);
      let url = "http://localhost:8080/calendar/update";
      const axiospost = await axios
        .put(url, data, {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
        })
        .then(response => {
          this.showNotifOK();
          this.listCalendar();
          this.modifyingId = "";
        })
        .catch(function(error) {
          fail = true;
        });
      if (fail) {
        this.showNotif();
      }

    },

    updateButton: async function(id){
      this.radio(status);
      this.modifyingId = id.id;
      console.log(id.status);
    }
  }
};
</script>

