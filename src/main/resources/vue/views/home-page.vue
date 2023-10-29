<!-- the "home-page" element is passed as a parameter to VueComponent in the JavalinConfig file -->
<template id="home-page">
  <app-layout>
    <div class="row">
      <div class="col">
        <div class="card">
          <h5 class="card-header">Registered Users</h5>
          <div class="card-body">
            <h5 class="card-title">{{users.length}} users</h5>
            <a href="/users" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
      <div class="col">
        <div class="card">
          <h5 class="card-header">Total Activities</h5>
          <div class="card-body">
            <h5 class="card-title">{{activities.length}} activities</h5>
            <a href="/activities" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col">
        <div class="card">
          <h5 class="card-header">Registered Meals</h5>
          <div class="card-body">
            <h5 class="card-title">{{meals.length}} meals</h5>
            <a href="/meals" class="btn btn-primary">More Details...</a>
          </div>
        </div>
      </div>
        <div class="col">
          <div class="card">
            <h5 class="card-header">Registered Ingredients</h5>
            <div class="card-body">
              <h5 class="card-title">{{ingredients.length}} ingredients</h5>
              <a href="/ingredients" class="btn btn-primary">More Details...</a>
            </div>
          </div>
        </div>
    </div>
  </app-layout>
</template>

<script>
app.component('home-page',
    {
      template: "#home-page",
      data: () => ({
        activities: [],
        meals: [],
        users: [],
      }),
      created() {
        axios.get("/api/users")
            .then(res => this.users = res.data)
            .catch(() => alert("Error while fetching users"));
        axios.get("/api/activities")
            .then(res => this.activities = res.data)
            .catch(() => alert("Error while fetching activities"));
        axios.get("/api/meals")
            .then(res => this.meals = res.data)
            .catch(() => alert("Error while fetching meals"));
      }
    });
</script>