<template id="user-activity-overview">
  <app-layout>
    <div>
      <h3>Activities list </h3>
      <ul>
        <li v-for="activity in activities">
          {{activity.id}}: {{activity.description}} for {{activity.duration}} minutes
        </li>
      </ul>
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
  </app-layout>
</template>

<script>
app.component("user-activity-overview",{
  template: "#user-activity-overview",
  data: () => ({
    activities: [],
    token: null,
    modalTitle: null,
    modalBody: null
  }),
  created() {
    this.getToken()
    this.getUserActivities()
  },
  methods: {
    async getUserActivities() {
      const userId = this.$javalin.pathParams["user-id"]

      try {
        const response = await axios.get(`/api/users/${userId}/activities`, {
          headers: {"Authorization": `Bearer ${this.token}`}
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