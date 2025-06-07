<template>
    <div>
      <h1>Create an Account</h1>
      <p><input type="text" placeholder="Email" v-model="email" /></p>
      <p><input type="password" placeholder="Password" v-model="password" /></p>
      <p><button @click="register">Submit</button></p>
      <p><button @click="signInWithGoogle">Sign In With Google</button></p>
      <p><button @click="signInWithGitHub1">Sign In With GitHub</button></p>

    </div>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import { getAuth, signInWithEmailAndPassword, signInWithPopup, GithubAuthProvider } from "firebase/auth";
  import { useRouter } from "vue-router";
  
  const email = ref("");
  const password = ref("");
  const router = useRouter();

  const auth = getAuth();
const provider = new GithubAuthProvider();

const signInWithGitHub1 = () => {
  const auth = getAuth(); // możesz zostawić tu albo przenieść na górę
  const provider = new GithubAuthProvider();

  signInWithPopup(auth, provider)
    .then((result) => {
      const user = result.user;
      console.log("Zalogowano jako:", user.displayName);
      router.push("/feed");
    })
    .catch((error) => {
      console.error("Błąd logowania:", error);
    });
};


  const register = () => {
    // potrzebne .value, bo ref()
signInWithEmailAndPassword(auth, email.value, password.value)
  .then((data) => {
    console.log("Successfully signed in!");
    console.log(auth.currentUser);

    router.push("/feed"); // redirect to the feed
  })
  .catch((error) => {
    console.log(error.code);
    switch (error.code) {
  case "auth/invalid-email":
    errMsg.value = "Invalid email";
    break;
  case "auth/user-not-found":
    errMsg.value = "No account with that email was found";
    break;
  case "auth/wrong-password":
    errMsg.value = "Incorrect password";
    break;
  default:
    errMsg.value = "Email or password was incorrect";
    break;
}

  });
  };
  
  const signInWithGoogle = () => {
    // Tu dodaj logikę logowania z Firebase Google providerem
  };
  </script>
  