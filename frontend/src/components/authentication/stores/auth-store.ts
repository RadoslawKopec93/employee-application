import { defineStore } from "pinia";
import { ref } from "vue";

export const useAuthStore = defineStore("authStore", () => {
  const employees = ref([]);

  const login = (userName?: string, password?: string) => {
    if (!userName && !password) {
      console.log("dupa")
      const clientId = "Ov23li74ydf1wrP9LsLX"; // z GitHuba
const redirectUri = "http://localhost:3001/oauth-callback";

const githubAuthUrl = `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=read:user`;
//alert(githubAuthUrl)
console.log(githubAuthUrl);
window.location.href = githubAuthUrl;


      /* fetch("http://localhost:8080/oauth2/authorization/github")
        .then((response) => {
          if (!response.ok) throw new Error("Błąd sieci");
          return response.json();
        })
        .then((data) => {
          employees.value = data;
        })
        .catch((error) => {
          console.error("Błąd:", error);
        }); */
    }
  };

  return {
    login,
  };
});
