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
    token: null
  }),
  created() {
    this.getToken()
    this.getMealIngredients()
  },
  methods: {
    async getMealIngredients() {
      const mealId = this.$javalin.pathParams["meal-id"];
      try {
        const response = await axios.get(`/api/meals/${mealId}/ingredients`, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.ingredients = response.data
      } catch(error) {
        if (error.response.status === 401) {
          location.href = '/login';
        } else {
          alert("Error while fetching ingredients")
        }
      }
    },
    getToken() {
      this.token = JSON.parse(localStorage.getItem("token"))
    },
  }
});
</script>