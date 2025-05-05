import { defineStore } from "pinia";
import { ref } from "vue";

export const useAuthStore = defineStore("authStore", () => {
  const employees = ref([]);

  const login = (userName?: string, password?: string) => {
    if (!userName && !password) {
      console.log("test1");
      fetch("http://localhost:8080/oauth2/authorization/github")
        .then((response) => {
          if (!response.ok) throw new Error("Błąd sieci");
          return response.json();
        })
        .then((data) => {
          employees.value = data;
        })
        .catch((error) => {
          console.error("Błąd:", error);
        });
    }
  };

  return {
    login,
  };
});
