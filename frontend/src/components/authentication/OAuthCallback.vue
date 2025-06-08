<template>
    <div>sadas</div>
</template>
<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(async () => {
  const code = new URLSearchParams(window.location.search).get('code')

  if (code) {
    const res = await fetch('http://localhost:8080/api/github-auth', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ code }),
    }) 
    console.log("DUPSKO")
    //console.log(res.text())
    const token = await res.text();
    console.log(token)
    const a = JSON.parse(token);
    console.log("odpowiedz ")
    localStorage.setItem("access_token", a.token)
    if (a.token) {
      window.history.replaceState(null, '', '/feed')
      router.push('/feed')
    }
  }
})
</script>
