<template>
  <div class="q-pa-md column items-center">
    <div class="q-gutter-y-md" style="min-width: 60vw; max-width: 60vw">

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
      tab: "list",
      token: "",
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
      pagination: {
        sortBy: "desc",
        descending: false,
        rowsPerPage: 10
      }
    };
  },
  async created() {
    this.token = sessionStorage.getItem("Session");
    this.gtoken = sessionStorage.getItem("gtoken")
    console.log(this.token);
    this.listFiles();
  },
  methods: {
    cleanName: function(string) {
      let result = string.split("_");
      return result[1];
    },

    showNotifOK() {
      this.$q.notify({
        message: "Archivo descargado",
        color: "positive"
      });
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

    listFiles: async function() {
      let fail = false;

      let listar = await axios
        .get("http://localhost:8080/document/userdocs", {
          headers: {
            Authorization: "Bearer " + this.token,
            Gauth: "Bearer " + this.gtoken,
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
            Gauth: "Bearer " + this.gtoken,
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
          this.showNotifOK();
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
  }
};
</script>
<style></style>
