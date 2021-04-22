<template>
  <q-page class="row items-center justify-evenly">
   holi
  </q-page>
</template>

<script lang="ts">
import ExampleComponent from 'components/CompositionComponent.vue';
import { defineComponent, ref } from '@vue/composition-api';

export default defineComponent({
  name: 'PageIndex',
  components: { ExampleComponent },
  setup() {
  },
    async created() {
      console.log(this.$token);
      
    }
  });
</script>
