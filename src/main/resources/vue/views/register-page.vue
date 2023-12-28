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
            <form @submit.prevent="registerUser">
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-login-email">Name</span>
                </div>
                <input type="text" class="form-control" v-model="formData.name" name="name" placeholder="Name" required/>
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-login-email">Email</span>
                </div>
                <input type="email" class="form-control" v-model="formData.email" name="email" placeholder="Email" required/>
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-login-password">Password</span>
                </div>
                <input type="password" class="form-control" v-model="formData.password" name="password" placeholder="Password" required />
              </div>
              <button rel="tooltip" title="Register" class="btn btn-info btn-simple btn-link mr-2" type="submit">Register</button>
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
            <p><span v-html="modalBody"></span></p>
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
  app.component('register-page',
  {
    template: "#register-page",
    data: () => {
      return {
        formData: [],
        modalTitle: null,
        modalBody: null
      };
    },
    methods: {
      async registerUser() {
        try {
          await axios.post("/api/login/register", {
            name: this.formData.name,
            email: this.formData.email,
            password: this.formData.password,
          })

            alert("Account created! You can now login")
            location.href = '/login';
        } catch(error) {
          const problemDetails = this.getProblemDetailsString(error.response.data.details)
          //alert(`Validation Errors\n\n` + problemDetails)
          this.showModal("Validation Errors", problemDetails)
        }
      },
      getProblemDetailsString(details) {
        return Object.entries(details).map(x => {
          const [property, issue] = x

          return `${property}:  ${issue}`
        }).join("<br />")
      },
      showModal(title, body = "") {
        this.modalTitle = title
        this.modalBody = body

        return $('#modal').modal('show')
      }
    },
  });
</script>
