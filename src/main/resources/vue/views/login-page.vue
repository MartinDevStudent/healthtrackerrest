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
                <input type="email" class="form-control" v-model="formData.email" name="email" placeholder="Email" required />
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-login-password">Password</span>
                </div>
                <input type="password" class="form-control" v-model="formData.password" name="password" placeholder="Password" required/>
              </div>
              <button rel="tooltip" title="Login" class="btn btn-info btn-simple btn-link mr-2" type="submit">Login</button>
            </form>
          </div>
        </div>
      </div>
    </div>
    <br />

    <!-- Modal -->
    <div class="modal fade" id="modal" tabindex="-1" role="dialog" aria-labelledby="modalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="modalLabel">{{ this.modalTitle }}</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            {{ this.modalBody }}
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
  app.component('login-page',
  {
    template: "#login-page",
    data: () => {
      return {
        formData: [],
        modalTitle: null,
        modalBody: null
      };
    },
    methods: {
      async login() {
        try {
          const response = await axios.post("/api/login", {
            email: this.formData.email,
            password: this.formData.password,
          }, {
            headers: { "Content-Type": "application/json"}
          })

          store.setToken(response.data.jwt)
          localStorage.setItem("token", JSON.stringify(response.data.jwt));

          this.showModal("You have logged in!").on('hidden.bs.modal',  () => {
            location.href = '/'
          })
        } catch {
          this.showModal("Error while logging in")
        }
      },
      showModal(title, body = "") {
        this.modalTitle = title
        this.modalBody = body

        return $('#exampleModal').modal('show')
      }
    },
    computed: {
      token: function () {
        return store.token
      }
    }
  });
</script>
