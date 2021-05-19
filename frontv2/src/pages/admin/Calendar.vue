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
                    color="indigo-6"
                    round
                    dense
                    @click="updateButton(props.row)"
                    :disable="props.row.status =='CANCELED'"
                    icon="fas fa-pen"
                  />
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
export default {
  data() {
    return {
      token: "",
      expanded: false,
      deleteConfirm: false,
      modifyingId: "",
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


    listCalendar: async function() {
      let fail = false;
      let listarPosts = await axios.get("http://localhost:8080/calendar/vacations", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
      }).then(response => {
        console.log(response);
        this.data = response.data;
        console.log(response.data);
        })
        .catch(function(error) {
          console.log(error);
          fail = true;
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

    radio: function(status) {
      this.$q.dialog({
        title: 'Cambiar estado',
        message: 'Elija el estado que quiere:',
        options: {
          type: 'radio',
          model: "DENIED",
          items: [
            { label: 'Denegado', value: 'DENIED' },
            { label: 'Aceptado', value: 'APPROVED' },
            { label: 'Pendiente', value: 'PENDING' }
          ]
        },
        cancel: true,
        persistent: true
      }).onOk(data => {
         console.log('>>>> OK, received', data)
         this.update(data);
      }).onCancel(() => {
          console.log('>>>> Cancel')
      })
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
          console.log(response);
          this.showNotifOK();
          this.listCalendar();
          this.modifyingId = "";
        })
        .catch(function(error) {
          fail = true;
          console.log(error);
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

