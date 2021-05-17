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
          <q-card-section>
            <q-form
              class="row overflow-auto"
              @submit="addFile"
              @reset="onFormReset"
            >
              <q-file
                v-model="files"
                label="Seleccionar archivos"
                filled
                counter
                :counter-label="counterLabelFn"
                max-files="5"
                multiple
                accept=".pdf"
                @rejected="onRejected"
                :rules="[val => !!val || 'Campo necesario']"
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
            title="Lista de archivos"
            :data="allFiles"
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
                    color="secondary"
                    round
                    dense
                    @click="downloadFile(props.row.id, props.row.name)"
                    icon="fas fa-file-download"
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
              @click="deleteFile"
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
      files: null,
      employee: "",
      allFiles: [],
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
    this.listFiles();
  },
  methods: {
    cleanName: function(string) {
      let result = string.split("_");
      return result[1];
    },

    //el texto debajo del input de archivos
    counterLabelFn({ totalSize, filesNumber, maxFiles }) {
      return `${filesNumber} files of ${maxFiles} | ${totalSize}`;
    },

    onFormReset: function() {
      this.files = null;
      this.employee = "";
    },

    showNotifOK() {
      this.$q.notify({
        message: "Actualizado correctamente",
        color: "positive"
      });
    },

    addFile: async function() {
      let fail = false;

      console.log(this.files);
      let fileArray = [];
      let thefile;
      for (let i = 0; i < this.files.length; i++) {
        const fileString = await this.getFile(this.files[i]);

        let extension = this.files[i].name.split(".");

        thefile = {
          docName: extension[0],
          ext: "." + extension[extension.length - 1],
          byteData: fileString
        };
        fileArray.push(thefile);
      }
      console.log(fileArray);
      let data = {
        fileArray,
        user: this.employee
      };

      console.log(data);
      let url = "http://localhost:8080/document/insert";
      const axiospost = await axios.post(url, data, {
        headers: {
          Authorization: "Bearer " + this.token,
          "Content-Type": "application/json"
        }
      }).then(response => {
          this.listFiles();
          document.getElementById("resetButton").click();
          this.expanded = false;
        })
        .catch(function(error) {
          console.log(error);
          fail = true;
        });
      if (fail) {
        this.showNotif();
      };
    },

    //esto convierte el archivo en un string de bytes
    getFile(file) {
      return new Promise(resolve => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function() {
          resolve(reader.result.toString());
        };
      });
    },

    listEmployees: async function() {
      let fail = false;

      let listar = await axios
        .get("http://localhost:8080/user/employee", {
          method: "GET",
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          }
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

    listFiles: async function() {
      let fail = false;

      let listar = await axios
        .get("http://localhost:8080/document/documents", {
          method: "GET",
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

    downloadFile: async function(id, name) {
      let cleanName = name.split(/([0-9]{17}_)/);
      let fail = false;
      let borrado = await axios
        .post("http://localhost:8080/document/download", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          },
          data: id
        }) 
        .then(res => {
          const byteCharacters = atob(res.data);
          const byteNumbers = new Array(byteCharacters.length);
          for (let i = 0; i < byteCharacters.length; i++) {
            byteNumbers[i] = byteCharacters.charCodeAt(i);
          }
          const byteArray = new Uint8Array(byteNumbers);
          const blob = new Blob([byteArray], {
            type: "application/octet-stream"
          });
          const fileUrl = window.URL.createObjectURL(blob);
          var anchor = document.createElement("a");
          anchor.download = cleanName[2];
          anchor.href = fileUrl;
          anchor.click();
        })
        .catch(function(error) {
          console.log(error);
          fail = true;
        });
      if (fail) {
        console.log("delete error?");
        this.showNotif();
      }
    },

    deleteFile: async function() {
      let fail = false;
      let borrado = await axios
        .delete("http://localhost:8080/document/delete", {
          headers: {
            Authorization: "Bearer " + this.token,
            "Content-Type": "application/json"
          },
          data: {
            id: this.modifyingId
          }
        })
        .then(response => {
          this.listFiles();
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
    },

    onRejected(rejectedEntries) {
      this.$q.notify({
        type: "negative",
        message: `Hay ${rejectedEntries.length} archivos extra.`
      });
    }
  }
};
</script>
<style></style>
