<template id="activity-overview">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="card bg-light mb-3">
        <div class="card-header">
          <div class="row">
            <div class="col-6">
              Activities
            </div>
            <div class="col" align="right">
              <button rel="tooltip" title="Add"
                      class="btn btn-info btn-simple btn-link"
                      @click="hideForm =!hideForm">
                <i class="fa fa-plus" aria-hidden="true"></i>
              </button>
            </div>
          </div>
          <div class="card-body" :class="{ 'd-none': hideForm}">
          <form id="addActivity">
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-activity-description">Description</span>
                </div>
                <input type="text" class="form-control" v-model="formData.description" name="description" placeholder="Description"/>
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-activity-duration">Duration</span>
                </div>
                <input class="form-control" v-model="formData.duration" name="duration" placeholder="Duration" type="number" step=".01"/>
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-activity-calories">Calories</span>
                </div>
                <input type="number" class="form-control" v-model="formData.calories" name="calories" placeholder="Calories"/>
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-activity-started">Started</span>
                </div>
                <input type="datetime-local" class="form-control" v-model="formData.started" name="started" placeholder="Started"/>
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-activity-userId">User</span>
                </div>
                <select v-model="formData.userId" class="form-selects form-control">
                  <option v-for="user in users" :value="user.id">
                    {{ user.name }}
                  </option>
                </select>
              </div>
            </form>
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link" @click="addActivity()">Add Activity</button>
          </div>
        </div>
      </div>
      <div class="list-group-item d-flex align-items-start"
           v-for="(activity,index) in activities" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/activities/${activity.id}`"> {{ activity.description }}</a></span>
        </div>
        <div class="p2">
          <a :href="`/activities/${activity.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
          </a>
          <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                  @click="deleteActivity(activity, index)">
            <i class="fas fa-trash" aria-hidden="true"></i>
          </button>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("activity-overview", {
  template: "#activity-overview",
  data: () => ({
    activities: [],
    users: [],
    formData: [],
    hideForm: true,
    token: null,
  }),
  created() {
    this.getToken();
    this.getActivities();
    this.getUsers();
  },
  methods: {
    async getActivities() {
      try {
        const response = await axios.get("/api/activities", {
          headers: { "Authorization": `Bearer ${this.token}`}
        })
        this.activities = response.data
      } catch(error) {
        if (error.response.status === 404) {
          alert("Error while fetching activities")
        } else if (error.response.status !== 401) {
          location.href = '/login';
        }
      }
    },
    async deleteActivity(activity, index) {
      if (confirm('Are you sure you want to delete this activity? This action cannot be undone.', 'Warning')) {
        //user confirmed delete
        const activityId = activity.id
        const url = `/api/activities/${activityId}`

        try {
          const response = await axios.delete(url, {
            headers: { "Authorization": `Bearer ${this.token}`}
          })

          //delete from the local state so Vue will reload list automatically
          this.activities.splice(index, 1).push(response.data)
        } catch(error)  {
          console.error(error)
        }
      }
    },
    async addActivity() {
      const url = `/api/activities`

      try {
        const response = await axios.post(url, {
            description: this.formData.description,
            duration: this.formData.duration,
            calories: this.formData.calories,
            started: this.formData.started,
            userId: this.formData.userId
        }, {
          headers: { "Authorization": `Bearer ${this.token}`}
        })

        this.activities.push(response.data)
        this.hideForm= true
      } catch(error)  {
        console.error(error)
      }
    },
    async getUsers() {
      try {
        const response = await axios.get("/api/users")
        this.users = response.data
      } catch {
        alert("Error while fetching users")
      }
    },
    getToken() {
      this.token = JSON.parse(localStorage.getItem("token"))
    }
  },
});
</script>