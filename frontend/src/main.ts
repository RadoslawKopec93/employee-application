import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

const pinia = createPinia();

createApp(App).use(router).use(ElementPlus).use(pinia).mount("#app");
