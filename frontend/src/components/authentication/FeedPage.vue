<template>
  <div>dup</div>
  <button @click="callRefresh">call test</button>
</template>
<script setup>
const callRefresh = async () => {
  console.log("ACCESS - token" , localStorage.getItem("access_token"))
  const response = await fetch("http://localhost:8080/test", {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem("access_token"),
      credentials: 'include'
    },
  })

  console.log("CALL REFRESH ", await response)
}
</script>
