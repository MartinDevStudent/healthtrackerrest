<template id="activity-profile">
  <app-layout>
    <div v-if="noActivityFound">
      <p> We're sorry, we were not able to retrieve this activity.</p>
      <p> View <a :href="'/activities'">all activities</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noActivityFound">
      <form @submit.prevent="updateActivity">
        <div class="card-header">
          <div class="row">
            <div class="col-6"> Activity Profile </div>
            <div class="col" align="right">
              <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link" type="submit">
                <i class="far fa-save" aria-hidden="true"></i>
              </button>
              <button
                rel="tool tip"
                title="Delete"
                class="btn btn-info btn-simple btn-link"
                @click="confirmDeleteActivity()"
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
                <span class="input-group-text" id="input-activity-id">Activity ID</span>
              </div>
              <input type="number" class="form-control" v-model="activity.id" name="id" readonly/>
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="input-activity-description">Description</span>
              </div>
              <input type="text" class="form-control" v-model="activity.description" name="description" placeholder="Description" required />
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="input-activity-duration">Duration</span>
              </div>
              <input type="number" class="form-control" v-model="activity.duration" name="duration" step=".01" placeholder="Duration" required />
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">

                <span class="input-group-text" id="input-activity-calories">Calories</span>
              </div>
              <input type="number" class="form-control" v-model="activity.calories" name="calories" placeholder="Calories" required />
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="input-activity-started">Started</span>
              </div>
              <input type="datetime-local" class="form-control" v-model="activity.started" name="started" readonly placeholder="Started" required />
            </div>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="input-activity-userId">User Id</span>
              </div>
              <input type="number" class="form-control" v-model="activity.userId" name="userId" readonly placeholder="User Id"/>
            </div>
          </div>
        </form>
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
            <button type="button" id="delete" class="btn btn-danger" >Delete</button>
          </div>
        </div>
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
    modalTitle: null,
    modalBody: null
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
          this.showModal(`Error while fetching activity ${activityId}`)
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

        this.activity = response.data
        this.showModal("Activity updated!")
      } catch(error) {
        const problemDetails = this.getProblemDetailsString(error.response.data.details)
        this.showModal("Validation Errors", problemDetails)
      }
    },
    confirmDeleteActivity() {
      this.showModal("Are you sure you want to delete?", "This action cannot be undone...", true)
          .on("click", "#delete", () => this.deleteActivity())
    },
    async deleteActivity() {
      const activityId = this.$javalin.pathParams["activity-id"];

      try {
        await axios.delete(`/api/activities/${activityId}`,{
          headers: { "Authorization": `Bearer ${this.token}` }
        })

        this.showModal("Activity deleted")
        window.location.href = '/activities'
      } catch(error) {
        this.showModal("Error deleting activity")
      }
    },
    getProblemDetailsString(details) {
      return Object.entries(details).map(x => {
        const [property, issue] = x

        return `${property}:  ${issue}`
      }).join("<br />")
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