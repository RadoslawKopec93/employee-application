<template>
  <div>
    <div>test</div>
    <ElInput v-model="test" />
    <ElButton @click="login()">Git hub</ElButton>
  </div>
</template>
<script lang="ts" setup>
import { ElInput } from "element-plus";
import { ref } from "vue";
import { useAuthStore } from "./stores/auth-store";

const store = useAuthStore();

const login = () => {
  store.login();
};
const test = ref("");
</script>
