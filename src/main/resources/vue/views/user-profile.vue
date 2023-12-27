<template id="user-profile">
  <app-layout>
    <div v-if="noUserFound">
      <p> We're sorry, we were not able to retrieve this user.</p>
      <p> View <a :href="'/users'">all users</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noUserFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> User Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Update"
                    class="btn btn-info btn-simple btn-link"
                    @click="updateUser()">
              <i class="far fa-save" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteUser()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-id">User ID</span>
            </div>
            <input type="number" class="form-control" v-model="user.id" name="id" readonly placeholder="Id"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-name">Name</span>
            </div>
            <input type="text" class="form-control" v-model="user.name" name="name" placeholder="Name"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-email">Email</span>
            </div>
            <input type="email" class="form-control" v-model="user.email" name="email" placeholder="Email"/>
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
        </form>
      </div>
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
    token: null
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
        this.user.push(response.data)
        alert("User updated!")
      } catch(error) {
        console.error(error)
      }
    },
    async deleteUser() {
      if (confirm("Do you really want to delete?")) {
        const userId = this.$javalin.pathParams["user-id"];

        try {
          await axios.delete(`/api/users/${userId}`, {
            headers: { "Authorization": `Bearer ${this.token}` }
          })
          alert("User deleted")
          //display the /users endpoint
          window.location.href = '/users'
        } catch(error) {
          console.error(error)
        }
      }
    },
    getToken() {
      this.token = JSON.parse(localStorage.getItem("token"))
    },
  }
});
</script>