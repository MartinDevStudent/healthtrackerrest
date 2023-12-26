<template id="meal-profile">
  <app-layout>
    <div v-if="noMealFound">
      <p> We're sorry, we were not able to retrieve this meal.</p>
      <p> View <a :href="'/meals'">all meals</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noMealFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Meal Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteMeal()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-id">Meal ID</span>
            </div>
            <input type="number" class="form-control" v-model="meal.id" name="id" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-name">Name</span>
            </div>
            <input type="text" class="form-control" v-model="meal.name" name="name" readonly/>
          </div>
        </form>
      </div>
      <div class="card-footer text-left">
        <p  v-if="ingredients.length === 0"> No ingredients...</p>
        <p  v-if="ingredients.length > 0"> Ingredients...</p>
        <ul>
          <li v-for="ingredient in ingredients">
            <a :href="'/ingredients/' + ingredient.id">{{ ingredient.name }}</a>
          </li>
        </ul>
      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("meal-profile", {
  template: "#meal-profile",
  data: () => ({
    meal: null,
    noMealFound: false,
    ingredients: [],
  }),
  created() {
    const mealId = this.$javalin.pathParams["meal-id"];
    const url = `/api/meals/${mealId}`

    this.getToken()
    this.getMeal(url)
    this.getIngredientsByMealId(url)
    },
    methods: {
      async getMeal(url) {
        try {
          const res = await axios.get(url,{
            headers: { "Authorization": `Bearer ${this.token}`}
          })
          this.meal = res.data
        } catch(error) {
          console.error("No meal found for id passed in the path parameter: " + error)
          this.noMealFound = true
        }
      },
      async getIngredientsByMealId(baseUrl) {
        try {
          const res = await axios.get(baseUrl + `/ingredients`,{
            headers: { "Authorization": `Bearer ${this.token}`}
          })
          this.ingredients = res.data
        } catch(error) {
          console.error("No ingredients in this meal: " + error)
        }
      },
      async deleteMeal() {
        if (confirm('Are you sure you want to delete this meal? This action cannot be undone.', 'Warning')) {
          const mealId = this.$javalin.pathParams["meal-id"];
          const url = `/api/meals/${mealId}`;

          try {
            await axios.delete(url, {
              headers: { "Authorization": `Bearer ${this.token}`}
            })
            alert("Meal deleted")
            //display the /meals endpoint
            window.location.href = '/meals';
          } catch(error) {
            console.error(error)
          }
        }
      },
      getToken() {
        this.token = JSON.parse(localStorage.getItem("token"))
      },
    }

});
</script>