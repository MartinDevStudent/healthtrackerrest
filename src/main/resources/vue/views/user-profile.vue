<template id="user-profile">
  <app-layout>
    <div v-if="noUserFound">
      <p> We're sorry, we were not able to retrieve this user.</p>
      <p> View <a :href="'/users'">all users</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noUserFound">
      <form @submit.prevent="updateUser">
        <div class="card-header">
          <div class="row">
            <div class="col-6"> User Profile </div>
            <div class="col" align="right">
              <button rel="tooltisp" title="Update" class="btn btn-info btn-simple btn-link" type="submit">
                <i class="far fa-save" aria-hidden="true"></i>
              </button>
              <button
                rel="tooltip"
                title="Delete"
                class="btn btn-info btn-simple btn-link"
                @click="confirmDeleteUser()"
                type="button"
              >
                <i class="fas fa-trash" aria-hidden="true"></i>
              </button>
            </div>
          </div>
        </div>
        <div class="card-body">
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="input-user-id">User ID</span>
              </div>
              <input type="number" class="form-control" v-model="user.id" name="id" readonly placeholder="Id" required />
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="input-user-name">Name</span>
              </div>
              <input type="text" class="form-control" v-model="user.name" name="name" placeholder="Name" required/>
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="input-user-email">Email</span>
              </div>
              <input type="email" class="form-control" v-model="user.email" name="email" placeholder="Email" required />
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="input-user-password">Password</span>
              </div>
              <input type="password" class="form-control" v-model="password" name="password" placeholder="*******"/>
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="input-user-level">Level</span>
              </div>
              <input type="text" class="form-control" v-model="user.level" name="level" readonly placeholder="Level"/>
            </div>
        </div>
      </form>
      <div class="card-footer text-left">
        <p  v-if="activities.length === 0"> No activities yet...</p>
        <p  v-if="activities.length > 0"> Activities so far...</p>
        <ul>
          <li v-for="activity in activities">
            {{ activity.description }} for {{ activity.duration }} minutes
          </li>
        </ul>
      </div>
    </div>
    <!-- Regular Modal -->
    <div class="modal fade" id="modal" tabindex="-1" role="dialog" aria-labelledby="modalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="modalLabel">{{ this.modalTitle }}</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times</span>
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
    <!-- Delete Modal -->
    <div class="modal fade" id="delete-modal" tabindex="-1" role="dialog" aria-labelledby="modalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="modalLabel">{{ this.modalTitle }}</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times</span>
            </button>
          </div>
          <div class="modal-body">
            <p><span v-html="modalBody"></span></p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
            <button type="button" id="delete" class="btn btn-danger">Delete</button>
          </div>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
  app.component("user-profile", {
  template: "#user-profile",
  data: () => ({
    user: null,
    password: null,
    noUserFound: false,
    activities: [],
    token: null,
    modalTitle: null,
    modalBody: null
  }),
  created() {
    const userId = this.$javalin.pathParams["user-id"];
    const url = `/api/users/${userId}`

    this.getToken()
    this.getUser(url)
    this.getUserActivities(url)
  },
  methods: {
    async getUser(url) {
      try {
        const response = await axios.get(url, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.user = response.data
      } catch(error) {
        if (error.response.status === 401) {
          location.href = '/login'
        } else {
          console.error("No user found for id passed in the path parameter: " + error)
          this.noUserFound = true
        }
      }
    },
    async getUserActivities(baseUrl) {
      try {
        const response = await axios.get(baseUrl + `/activities`, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.activities = response.data
      } catch(error) {
        console.error("No activities added yet (this is ok): " + error)
      }
    },
    async updateUser() {
      const userId = this.$javalin.pathParams["user-id"];

      try {
        const response = await axios.patch(`/api/users/${userId}`, {
          name: this.user.name,
          email: this.user.email,
          password: this.password
        }, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })

        this.showModal("User updated!")
        this.user.push(response.data)
      } catch(error) {
        const problemDetails = this.getProblemDetailsString(error.response.data.details)
        this.showModal(`Validation Errors`, problemDetails)
      }
    },
    confirmDeleteUser() {
      this.showModal("Are you sure you want to delete?", "This action cannot be undone...", true)
          .on("click", "#delete", () => this.deleteUser())
    },
    async deleteUser() {
      const userId = this.$javalin.pathParams["user-id"];

      try {
        await axios.delete(`/api/users/${userId}`, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })

        this.showModal("User deleted")

        // navigate to /users endpoint
        window.location.href = '/users'
      } catch(error) {
        this.showModal("Error deleting user")
      }
    },
    getProblemDetailsString(details) {
      return Object.entries(details).map(x => {
        const [property, issue] = x

        return `${property}:  ${issue}`
      }).join("\n")
    },
    getToken() {
      this.token = JSON.parse(localStorage.getItem("token"))
    },
    showModal(title, body = "", showDeletionModal = false) {
      this.modalTitle = title
      this.modalBody = body

      return $(showDeletionModal ? '#delete-modal' : '#modal').modal('show')
    }
  }
});
</script>