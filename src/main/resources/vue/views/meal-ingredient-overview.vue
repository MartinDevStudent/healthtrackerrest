<template id="meal-ingredient-overview">
  <app-layout>
    <div>
      <h3>Ingredients list </h3>
      <ul>
        <li v-for="ingredient in ingredients">
          {{ingredient.id}}: {{ingredient.name}}
        </li>
      </ul>
    </div>
  </app-layout>

</template>

<script>
app.component("meal-ingredient-overview",{
  template: "#meal-ingredient-overview",
  data: () => ({
    ingredients: [],
  }),
  created() {
    const mealId = this.$javalin.pathParams["meal-id"];
    axios.get(`/api/meals/${mealId}/ingredients`)
        .then(res => this.ingredients = res.data)
        .catch(() => alert("Error while fetching ingredients"));
  }
});
</script>