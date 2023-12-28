<template id="register-page">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="card bg-light mb-3">
        <div class="card-header">
          <div class="row">
            <div class="col-6">
              Register
            </div>
          </div>
          <div class="card-body">
            <form @submit.prevent="register">
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-login-email">Name</span>
                </div>
                <input type="email" class="form-control" v-model="formData.name" name="name" placeholder="Name"/>
              </div>
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
            <button rel="tooltip" title="Register" class="btn btn-info btn-simple btn-link mr-2" @click="registerUser()">Register</button>
          </div>
        </div>
      </div>
    </div>
    <br />
  </app-layout>
</template>

<script>
  app.component('register-page',
  {
    template: "#register-page",
    data: () => {
      return {
        formData: [],
      };
    },
    methods: {
      async registerUser() {
        try {
          const response = await axios.post("/api/login/register", {
            name: this.formData.name,
            email: this.formData.email,
            password: this.formData.password,
          })

            alert("Account created! You can now login")
            location.href = '/login';
        } catch {
          alert("Invalid registration details")
        }
      },
    },
  });
</script>
