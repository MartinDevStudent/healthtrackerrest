<template id="activity-profile">
  <app-layout>
    <div>
      <form v-if="activity">
        <label class="col-form-label">Activity ID: </label>
        <input class="form-control" v-model="activity.id" name="id" type="number" readonly/><br>
        <label class="col-form-label">Description: </label>
        <input class="form-control" v-model="activity.description" name="description" type="text"/><br>
        <label class="col-form-label">Duration: </label>
        <input class="form-control" v-model="activity.duration" name="duration" type="number" step=".01"/><br>
        <label class="col-form-label">Calories: </label>
        <input class="form-control" v-model="activity.calories" name="calories" type="number"/><br>
        <label class="col-form-label">Started: </label>
        <input class="form-control" v-model="activity.started" name="started" type="date"/><br>
        <label class="col-form-label">User ID: </label>
        <input class="form-control" v-model="activity.userId" name="userId" type="number" readonly/><br>
      </form>
    </div>
  </app-layout>
</template>

<script>
app.component("activity-profile", {
  template: "#activity-profile",
  data: () => ({
    activity: null
  }),
  created: function () {
    const activityId = this.$javalin.pathParams["activity-id"];
    const url = `/api/activities/${activityId}`
    axios.get(url)
        .then(res => this.activity = res.data)
        .catch(() => alert("Error while fetching user" + activityId));
  }
});
</script>