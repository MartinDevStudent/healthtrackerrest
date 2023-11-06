<template id="login-page" v-if="store.token">
  <div class="container">
    <h1>LOGIN</h1>
      <form @submit.prevent="login" class="mt-5">
        <div class="form-group">
          <label for="email">Email</label>
          <input v-model="email" placeholder="email" class="form-control form-control-lg"/>
          <br />
          <label for="password">Password</label>
          <input v-model="password" placeholder="password" type="password" class="form-control form-control-lg"/>
        </div>
        <button type="submit" class="btn btn-primary">Login</button>
      </form>
    <button v-on:click="validate" class="btn btn-primary">Validate</button>
    <br />
    <p v-if="token !== null">{{ `Bearer token: ${token}` }}</p>
    <p v-if="validationResponse !== null">{{ `${validationResponse}, you were able to login using the bearer token above` }}</p>
  </div>
</template>
<script>
  app.component('login-page',
  {
    template: "#login-page",
    data: () => {
      return {
        email: "",
        password: "",
        validationResponse: null,
      };
    },
    methods: {
      async login() {
        axios.post("/api/login", {
          email: this.email,
          password: this.password,
        }, {
          headers: { "Content-Type": "application/json"}
        })

          .then(res => store.setToken(res.data.jwt))
          .catch(() => alert("Error while logging in"));
      },
      async validate() {
        axios.get("/api/login/validate", {
          headers: { "Authorization": `Bearer ${store.token}`}
        })
            .then(res => this.validationResponse = res.data)
            .catch(() => alert("Error while validating token"));
      },
    },
    computed: {
      token: function () {
        return store.token
      }
    }
  });
</script>
