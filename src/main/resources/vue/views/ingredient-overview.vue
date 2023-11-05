<template id="ingredient-overview">
  <app-layout>
    <div>
      <div>
        <ul class="ingredient">
          <li v-for="ingredient in ingredients">
            <a :href="`/ingredients/${ingredient.id}`">{{ingredient.name}}</a>
          </li>
        </ul>
      </div>
    </div>
  </app-layout>

</template>
<script>
app.component("ingredient-overview", {
  template: "#ingredient-overview",
  data: () => ({
    ingredients: [],
  }),
  created() {
    this.fetchIngredients();
  },
  methods: {
    fetchIngredients: function () {
      axios.get("/api/ingredients")
          .then(res => this.ingredients = res.data)
          .catch(() => alert("Error while fetching ingredients"));
    }
  }
});
</script>