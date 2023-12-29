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
          <form @submit.prevent="addActivity">
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-activity-description">Description</span>
                </div>
                <input type="text" class="form-control" v-model="formData.description" name="description" placeholder="Description" required />
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-activity-duration">Duration</span>
                </div>
                <input class="form-control" v-model="formData.duration" name="duration" placeholder="Duration" type="number" step=".01" required />
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-activity-calories">Calories</span>
                </div>
                <input type="number" class="form-control" v-model="formData.calories" name="calories" placeholder="Calories" required />
              </div>
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-activity-started">Started</span>
                </div>
                <input type="datetime-local" class="form-control" v-model="formData.started" name="started" placeholder="Started" required />
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
              <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link" type="submit">
                Add Activity
              </button>
            </form>
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
                  @click="confirmDeleteActivity(activity, index)">
            <i class="fas fa-trash" aria-hidden="true"></i>
          </button>
        </div>
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
            <button type="button" id="delete" class="btn btn-danger" >Delete</button>
          </div>
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
    modalTitle: null,
    modalBody: null
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
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.activities = response.data
      } catch(error) {
        if (error.response.status === 401) {
          location.href = '/login'
        } else {
          this.showModal("Error while fetching activities")
        }
      }
    },
    confirmDeleteActivity(activity, index) {
      this.showModal("Are you sure you want to delete?", "This action cannot be undone...", true)
          .on("click", "#delete", () => this.deleteActivity(activity, index))
    },
    async deleteActivity(activity, index) {
      try {
        const response = await axios.delete(`/api/activities/${activity.id}`, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })

        // close modal
        $('#delete-modal').modal('hide')

        // delete from the local state so Vue will reload list automatically
        this.activities.splice(index, 1).push(response.data)
      } catch (error) {
        this.showModal("Error deleting activity")
      }
    },
    async addActivity() {
      try {
        const response = await axios.post(`/api/activities`, {
          description: this.formData.description,
          duration: this.formData.duration,
          calories: this.formData.calories,
          started: this.formData.started,
          userId: this.formData.userId
        }, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })

        this.activities.push(response.data)
        this.hideForm = true
      } catch(error) {
        const problemDetails = this.getProblemDetailsString(error.response.data.details)
        this.showModal("Validation Errors", problemDetails)
      }
    },
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