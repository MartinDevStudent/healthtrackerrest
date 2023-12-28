<template id="user-overview">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            Users
          </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Add"
                    class="btn btn-info btn-simple btn-link"
                    @click="hideForm =!hideForm">
              <i class="fa fa-plus" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body" :class="{ 'd-none': hideForm}">
        <form @submit.prevent="addUser">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-name">Name</span>
            </div>
            <input type="text" class="form-control" v-model="formData.name" name="name" placeholder="Name" required />
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-email">Email</span>
            </div>
            <input type="email" class="form-control" v-model="formData.email" name="email" placeholder="Email" required />
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-password">Password</span>
            </div>
            <input type="password" class="form-control" v-model="formData.password" name="password" placeholder="Password" required />
          </div>
          <button rel="tooltip" title="AddUser" class="btn btn-info btn-simple btn-link" type="submit">
            Add User
          </button>
        </form>
      </div>
    </div>
    <div class="list-group list-group-flush" name="list-group">
      <div class="list-group-item d-flex align-items-start"  name="list-group-item"
           v-for="(user,index) in users" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/users/${user.id}`">{{ user.name }} ({{ user.email }})</a></span>
        </div>
        <div class="p2">
          <a :href="`/users/${user.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
          </a>
          <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                  @click="deleteUser(user, index)">
            <i class="fas fa-trash" aria-hidden="true"></i>
          </button>
        </div>
      </div>
    </div>
  </app-layout>
</template>
<script>

app.component("user-overview", {
  template: "#user-overview",
  data: () => ({
    users: [],
    formData: [],
    hideForm :true,
    token: null
  }),
  created() {
    this.getToken()
    this.getUsers()
  },
  methods: {
    async getUsers() {
      try {
        const response = await axios.get("/api/users", {
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.users = response.data
      } catch(error) {
        if (error.response.status === 401) {
          location.href = '/login'
        } else {
          alert("Error while fetching users")
        }
      }
    },
    async deleteUser(user, index) {
      if (confirm('Are you sure you want to delete this user? This action cannot be undone.', 'Warning')) {
        //user confirmed delete
        const userId = user.id;

        try {
          const response = await axios.delete(`/api/users/${userId}`, {
            headers: { "Authorization": `Bearer ${this.token}` }
          })
          //delete from the local state so Vue will reload list automatically
          this.users.splice(index, 1).push(response.data)
        } catch(error) {
          console.error(error)
        }
      }
    },
    async addUser() {
      try {
        const response = await axios.post(`/api/users`, {
          name: this.formData.name,
          email: this.formData.email,
          password: this.formData.password,
        }, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.users.push(response.data)
        this.hideForm= true;
      } catch(error) {
        const problemDetails = this.getProblemDetailsString(error.response.data.details)
        alert(`Validation Errors\n\n` + problemDetails)
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
  }
});
</script>