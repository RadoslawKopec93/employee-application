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
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ code }),
    }) 
    console.log("DUPSKO")
    //console.log(res.text())
    const a = await res.text();
    console.log("odpowiedz ", a)
/*     fetch('http://localhost:8080/api/github-auth')  // adres endpointu
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok: ' + response.statusText);
    }
    return response.json();  // zakładamy, że odpowiedź to JSON
  })
  .then(data => {
    console.log('Odpowiedź z API:', data);
  })
  .catch(error => {
    console.error('Błąd podczas fetch:', error);
  }); */

   // const data = await res.json()
   // localStorage.setItem('token', data.token)

    // Czyść URL z ?code=...
    window.history.replaceState(null, '', '/feed')
    router.push('/feed')
  }
})
</script>
