<template>
  <Log v-if="!state.globalVar"></Log>
  <Game v-if="state.globalVar"></Game>
</template>
<script>
import Log from './components/Log.vue';
import Game from './components/Game.vue';
import { reactive, provide } from 'vue';
export default {
  components: {
    Log, Game,
  },
  setup() {
    const state = reactive({
      globalVar: false
    });
    provide('globalVar', state);
    return { state };
  }
};
</script>
<style scoped></style>