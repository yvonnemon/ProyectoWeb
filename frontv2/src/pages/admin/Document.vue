<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 60vw; max-width: 60vw">
      <q-expansion-item
        expand-separator
        icon="perm_identity"
        label="Subir archivo"
        class="form-style"
        v-model="expanded"
      >
        <q-card>
          <q-card-section >
            <q-form class="row overflow-auto" @submit="addFile" @reset="onFormReset">
              <q-file
                v-model="files"
                label="Seleccionar archivos"
                filled
                counter
                :counter-label="counterLabelFn"
                max-files="5"
                multiple
                class="form-input col-sm-3 offset-sm-3  col-xs-12 offset-xs-auto"
              >
                <template v-slot:prepend>
                  <q-icon name="attach_file" />
                </template>
              </q-file>

              <q-select
                outlined
                class="form-input col-sm-3 col-xs-12"
                v-model="employee"
                :options="employees"
                :option-label="'fullName'"
                :option-value="'id'"
                label="Empleado"
                emit-name
                map-options
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
      tab: "list",
      token: "",
      expanded: false,
      deleteConfirm: false,
      modifyingId: "",
      selectedRol: "",
      files: null,
      employee: "",
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
      employees: [""],
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
    this.listEmployees();
  },
  methods: {
    asd: function(algo) {
      console.log(algo);
    },
    counterLabelFn({ totalSize, filesNumber, maxFiles }) {
      return `${filesNumber} files of ${maxFiles} | ${totalSize}`;
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
    },
    showNotifOK() {
      this.$q.notify({
        message: "Actualizado correctamente",
        color: "positive"
      });
    },

    addFile: async function() {
      console.log(this.files);
      const data = {
          userid: this.employee,
          files: this.files
      };

      console.log(data);
      /*  console.log(data);
        let url = "http://localhost:8080/user/insert";
        const axiospost = await axios.post(url, data, {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
        });
        console.log(axiospost);
        this.listUsers();
        document.getElementById("resetButton").click();*/
    },

    listEmployees: async function() {
      let fail = false;

      let listarPosts = await axios
        .get("http://localhost:8080/user/employee", {
          method: "GET",
          headers: new Headers({
            Authorization: "Bearer " + sessionStorage.getItem("Session")
          })
        })
        .then(response => {
          this.employees = response.data;
          console.log(this.employees);
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
          console.log(error);
          fail = true;
        });
      if (fail) {
          console.log("delete error?");
        this.showNotif();
      }
    }
  }
};
</script>
<style></style>
