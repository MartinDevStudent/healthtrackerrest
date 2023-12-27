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
  </app-layout>
</template>

<script>
app.component("user-activity-overview",{
  template: "#user-activity-overview",
  data: () => ({
    activities: [],
    token: null
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
          alert("Error while fetching activities")
        }
      }
    },
    getToken() {
      this.token = JSON.parse(localStorage.getItem("token"))
    },
  }
});
</script>