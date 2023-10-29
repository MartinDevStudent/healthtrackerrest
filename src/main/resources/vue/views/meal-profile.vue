<template id="meal-profile">
  <app-layout>
    <div>
      <form v-if="meal">
        <label class="col-form-label">Meal ID: </label>
        <input class="form-control" v-model="meal.id" name="id" type="number" readonly/><br>
        <label class="col-form-label">Name: </label>
        <input class="form-control" v-model="meal.name" name="name" type="text" readonly/><br>
      </form>
      <dt v-if="meal">
        <br>
        <a :href="`/meals/${meal.id}/ingredients`">View Meal Ingredients</a>
      </dt>
    </div>
  </app-layout>
</template>

<script>
app.component("meal-profile", {
  template: "#meal-profile",
  data: () => ({
    meal: null
  }),
  created: function () {
    const mealId = this.$javalin.pathParams["meal-id"];
    const url = `/api/meals/${mealId}`
    axios.get(url)
        .then(res => this.meal = res.data)
        .catch(() => alert("Error while fetching meal" + mealId));
  }
});
</script>