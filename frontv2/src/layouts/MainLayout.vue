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

      <q-tabs align="left">
        <q-route-tab to="/admin" label="Inicio" />
        <q-route-tab to="/admin/user" label="Usuarios" class="nav-font" />
        <q-route-tab to="/admin/document" label="Nominas" class="nav-font" />
        <q-route-tab to="/admin/vacation" label="Vacaciones" class="nav-font" />

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
    logout: function() {
      sessionStorage.removeItem("Session");
      sessionStorage.removeItem("gtoken");

      this.$router.push("/");
    }
  }
};
</script>
