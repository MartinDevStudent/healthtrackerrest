<template id="activity-profile">
  <app-layout>
    <div v-if="noActivityFound">
      <p> We're sorry, we were not able to retrieve this activity.</p>
      <p> View <a :href="'/activities'">all activities</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noActivityFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Activity Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Update"
                    class="btn btn-info btn-simple btn-link"
                    @click="updateActivity()">
              <i class="far fa-save" aria-hidden="true"></i>
            </button>
            <button rel="tool tip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteActivity()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-id">Activity ID</span>
            </div>
            <input type="number" class="form-control" v-model="activity.id" name="id" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-description">Description</span>
            </div>
            <input type="text" class="form-control" v-model="activity.description" name="description" placeholder="Description"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-duration">Duration</span>
            </div>
            <input type="number" class="form-control" v-model="activity.duration" name="duration" step=".01" placeholder="Duration"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">

              <span class="input-group-text" id="input-activity-calories">Calories</span>
            </div>
            <input type="number" class="form-control" v-model="activity.calories" name="calories" placeholder="Calories"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-started">Started</span>
            </div>
            <input type="datetime-local" class="form-control" v-model="activity.started" name="started" readonly placeholder="Started"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-userId">User Id</span>
            </div>
            <input type="number" class="form-control" v-model="activity.userId" name="userId" readonly placeholder="User Id"/>
          </div>
        </form>
      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("activity-profile", {
  template: "#activity-profile",
  data: () => ({
    activity: null,
    noActivityFound: false,
    token: null,
  }),
  created() {
    this.getToken();
    this.getActivity();
  },
  methods: {
    async getActivity() {
      const activityId = this.$javalin.pathParams["activity-id"];

      try {
        const response = await axios.get(`/api/activities/${activityId}`, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.activity = response.data
      } catch(error) {
        if (error.response.status === 401) {
          location.href = '/login';
        } else {
          alert("Error while fetching user" + activityId)
          this.noUserFound = true
        }
      }
    },
    async updateActivity() {
      const activityId = this.$javalin.pathParams["activity-id"];

      try {
        const response = await axios.patch(`/api/activities/${activityId}`, {
          description: this.activity.description,
          duration: this.activity.duration,
          calories: this.activity.calories,
          started: this.activity.started,
          userId: this.activity.userId,
        }, {
          headers: { "Authorization": `Bearer ${this.token}` }
        });

        this.activity.push(response.data)
        alert("Activity updated!")
      } catch(error) {
        console.error(error)
      }
    },
    async deleteActivity() {
      if (confirm("Do you really want to delete?")) {
        const activityId = this.$javalin.pathParams["activity-id"];

        try {
          await axios.delete(`/api/activities/${activityId}`,{
            headers: { "Authorization": `Bearer ${this.token}`}
          })

          alert("Activity deleted");
          window.location.href = '/activities';
        } catch(error) {
            console.error(error)
        }
      }
    },
    getToken() {
      this.token = JSON.parse(localStorage.getItem("token"))
    }
  }
});
</script>