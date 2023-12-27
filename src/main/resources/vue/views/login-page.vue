<template id="login-page" v-if="store.token">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="card bg-light mb-3">
        <div class="card-header">
          <div class="row">
            <div class="col-6">
              Login
            </div>
          </div>
          <div class="card-body">
            <form @submit.prevent="login">
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-login-email">Email</span>
                </div>
                <input type="email" class="form-control" v-model="formData.email" name="email" placeholder="Email"/>
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-login-password">Password</span>
                </div>
                <input type="password" class="form-control" v-model="formData.password" name="password" placeholder="Password"/>
              </div>
            </form>
            <button rel="tooltip" title="Login" class="btn btn-info btn-simple btn-link mr-2" @click="login()">Login</button>
          </div>
        </div>
      </div>
    </div>
    <br />
  </app-layout>
</template>

<script>
  app.component('login-page',
  {
    template: "#login-page",
    data: () => {
      return {
        formData: [],
      };
    },
    methods: {
      async login() {
        try {
          const res = await axios.post("/api/login", {
            email: this.formData.email,
            password: this.formData.password,
          }, {
            headers: { "Content-Type": "application/json"}
          })

          if (res.status < 400) {
            store.setToken(res.data.jwt)
            localStorage.setItem("token", JSON.stringify(res.data.jwt));
            alert("You have logged in!")
            location.href = '/'
          } else {
            alert("Invalid login details")
          }
        } catch {
          alert("Error while logging in")
        }
      },
    },
    computed: {
      token: function () {
        return store.token
      }
    }
  });
</script>
