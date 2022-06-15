<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white" height-hint="98">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar>
            <img src="https://cdn.quasar.dev/logo/svg/quasar-logo.svg" />
          </q-avatar>
          Inter-net
        </q-toolbar-title>
      </q-toolbar>

      <q-tabs align="left" shrink stretch>
        <q-route-tab to="/admin/index" label="Inicio" />
        <q-route-tab to="/admin/user" label="Usuarios" class="nav-font" />
        <q-route-tab to="/admin/document" label="Nominas" class="nav-font" />
        <q-route-tab to="/admin/vacation" label="Vacaciones" class="nav-font" />
        <q-route-tab to="/admin/clock" label="Fichar" />
        <q-route-tab to="/admin/adminedition" label="Admin" class="nav-font adminedit"/>
      </q-tabs>
      </q-toolbar>
            <div>
                <q-chip align="right" class="fixed-top-right avatar" color="white">
            <q-avatar>
              <img icon="fas fa-eye"
>
                </q-avatar>
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
      nombre: "",
    };
  },
    mounted(){
          let decodedtoken = jwt_decode(sessionStorage.getItem("Session"));
          this.nombre = decodedtoken.user.name;
  },
  methods: {

    logout: function() {
      sessionStorage.removeItem("Session");
      window.location.href = window.location.href
      this.$router.push("/");
    }
  }
};
</script>
