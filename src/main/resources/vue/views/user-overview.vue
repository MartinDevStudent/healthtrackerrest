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
        <form id="addUser">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-name">Name</span>
            </div>
            <input type="text" class="form-control" v-model="formData.name" name="name" placeholder="Name"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-email">Email</span>
            </div>
            <input type="email" class="form-control" v-model="formData.email" name="email" placeholder="Email"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-password">Password</span>
            </div>
            <input type="password" class="form-control" v-model="formData.password" name="password" placeholder="******"/>
          </div>
        </form>
        <button rel="tooltip" title="AddUser" class="btn btn-info btn-simple btn-link" @click="addUser()">Add User
        </button>
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
        const response = await axios.get("/api/users",
          { headers: { "Authorization": `Bearer ${this.token}`}
        })
        this.users = response.data
      } catch {
        alert("Error while fetching users")
      }
    },
    async deleteUser(user, index) {
      if (confirm('Are you sure you want to delete this user? This action cannot be undone.', 'Warning')) {
        //user confirmed delete
        const userId = user.id;
        const url = `/api/users/${userId}`;

        try {
          const response = await axios.delete(url,
            { headers: { "Authorization": `Bearer ${this.token}`}
          })
          //delete from the local state so Vue will reload list automatically
          this.users.splice(index, 1).push(response.data)
        } catch(error) {
          console.error(error)
        }
      }
    },
    addUser: function (){
      const url = `/api/users`;
      axios.post(url,
        {
          name: this.formData.name,
          email: this.formData.email,
          password: this.formData.password,
        })
        .then(response => {
          this.users.push(response.data)
          this.hideForm= true;
        })
        .catch(error => {
          console.error(error)
        })
    },
    getToken() {
      this.token = JSON.parse(localStorage.getItem("token"))
    },
  }
});
</script>