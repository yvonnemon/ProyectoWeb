<template>
  <q-layout view="hHh lpR fFf" class="fondo">
    <q-header elevated class="bg-primary text-white" height-hint="98">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar>
            <img src="https://cdn.quasar.dev/logo/svg/quasar-logo.svg" />
          </q-avatar>
          Inter-net
        </q-toolbar-title>
      </q-toolbar>

      <q-tabs align="left">
        <q-route-tab to="/main" label="Inicio" />
        <q-route-tab to="/main/form" label="Mis Datos" />
        <q-route-tab to="/main/document" label="Mis Nominas" />
        <q-route-tab to="/main/vacation" label="Mis Vacaciones" />
        <q-route-tab to="/main/clock" label="Fichar" />

      </q-tabs>
            <div>
                <q-chip align="right" class="fixed-top-right avatar" >
                  <span class="material-icons">
                    person_outline
                    </span>
                {{ nombre }}
              </q-chip>
          </div>

    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>

    <q-footer reveal elevated class="bg-grey-8 text-white">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar>
            <img src="https://cdn.quasar.dev/logo/svg/quasar-logo.svg" />
          </q-avatar>
          Inter-net
        </q-toolbar-title>
        <span class="logout" @click="logout"
          ><i class="fas fa-sign-out-alt"></i> Log out</span
        >
      </q-toolbar>
    </q-footer>
  </q-layout>
</template>

<script>
import jwt_decode from "jwt-decode";

export default {
  data() {
    return {
      nombre:""
    };
  },
    mounted(){
          let decodedtoken = jwt_decode(sessionStorage.getItem("Session"));
          this.nombre = decodedtoken.user.name;
  },

  methods: {
    logout: async function() {
      console.log("logout");
      sessionStorage.removeItem("Session");

      this.$router.push("/");
    }
  }
};
</script>
