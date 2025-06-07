import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAbz3wF0jsY9nFfio_R172HxCPGWZ_napc",
  authDomain: "vue-learn-1e619.firebaseapp.com",
  projectId: "vue-learn-1e619",
  storageBucket: "vue-learn-1e619.firebasestorage.app",
  messagingSenderId: "992342853782",
  appId: "1:992342853782:web:9cac91ccebf6454576dab7",
  measurementId: "G-KPHPKJT9QK",
};

// Initialize Firebase
initializeApp(firebaseConfig);

const pinia = createPinia();

createApp(App).use(router).use(ElementPlus).use(pinia).mount("#app");
