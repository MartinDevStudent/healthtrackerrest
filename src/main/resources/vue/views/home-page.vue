<!-- the "home-page" element is passed as a parameter to VueComponent in the JavalinConfig file -->
<template id="home-page">
  <app-layout>
    <div class="row mb-3">
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
        <p>{{ $javalin.state.user }}</p>
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
        ingredients: [],
        token: null,
      }),
      created() {
        this.getToken()
        this.getUsers()
        this.getActivities()
        this.getMeals()
        this.getIngredients()
      },
      methods: {
        async getUsers() {
          try {
            const response = await axios.get("/api/users", {
              headers: { "Authorization": `Bearer ${this.token}`}
            })
            this.users = response.data
          } catch(error) {
            if (error.response.status === 401) {
              location.href = '/login';
            } else if (error.response.status !== 404) {
              alert("Error while fetching users")
            }
          }
        },
        async getActivities() {
          try {
            const response = await axios.get("/api/activities", {
              headers: { "Authorization": `Bearer ${this.token}`}
            })
            this.activities = response.data
          } catch(error) {
            if (error.response.status !== 404) {
              alert("Error while fetching activities")
            }
          }
        },
        async getMeals() {
          try {
            const response = await axios.get("/api/meals", {
              headers: { "Authorization": `Bearer ${this.token}`}
            })
            this.meals = response.data
          } catch(error) {
            if (error.response.status !== 404) {
              alert("Error while fetching meals")
            }
          }
        },
        async getIngredients() {
          try {
            const response = await axios.get("/api/ingredients", {
              headers: { "Authorization": `Bearer ${this.token}`}
            })
            this.ingredients = response.data
          } catch (error) {
            if (error.response.status !== 404) {
              alert("Error while fetching ingredients")
            }
          }
        },
        getToken() {
          this.token = JSON.parse(localStorage.getItem("token"))
        }
      }
    });
</script>