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
      </q-tabs>
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
export default {
  data() {
    return {};
  },
  methods: {
    logout: async function() {
      sessionStorage.removeItem("Session");
      if(gapi){
      let auth2 = gapi.auth2.getAuthInstance();
      await auth2.signOut().then(function() {
        console.log("User signed out.");
      });

      }

      this.$router.push("/");
    }
  }
};
</script>
