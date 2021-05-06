<template>
  <q-layout view="hHh lpR fFf">

    <q-header elevated class="bg-primary text-white" height-hint="98">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar>
            <img src="https://cdn.quasar.dev/logo/svg/quasar-logo.svg">
          </q-avatar>
          Inter-net
        </q-toolbar-title>
      </q-toolbar>

      <q-tabs align="left">
        <q-route-tab to="/admin/user" label="Usuarios" class="nav-font"/>
        <q-route-tab to="/page2" label="Nominas" class="nav-font"/>
        <q-route-tab to="/page3" label="Vacaciones" class="nav-font"/>
      </q-tabs>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>

    <q-footer reveal elevated class="bg-grey-8 text-white">
      <q-toolbar>
        <q-toolbar-title>
          <q-avatar>
            <img src="https://cdn.quasar.dev/logo/svg/quasar-logo.svg">
          </q-avatar>
          Inter-net
        </q-toolbar-title>
      </q-toolbar>
    </q-footer>

  </q-layout>
</template>

<script>
import jwt_decode from "jwt-decode";
export default {
  data() {
    return {
    };
  },
    beforeCreate: function() {
      let token;
      if(!sessionStorage.getItem("Session")){
          this.$router.push("/");
      } else {
        token = sessionStorage.getItem("Session");
        let decoded = jwt_decode(token);
        if(decoded.role != "ADMIN"){
          this.$router.push("/");
        }
      }  }

}
</script>
