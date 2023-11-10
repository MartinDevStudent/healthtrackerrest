<template id="ingredient-overview">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="card bg-light mb-3">
        <div class="card-header">
          <div class="row">
            <div class="col-6">
              Ingredients
            </div>
          </div>
        </div>
      </div>
      <div class="list-group-item d-flex align-items-start"
           v-for="(ingredient,index) in ingredients" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/ingredients/${ingredient.id}`"> {{ ingredient.name }}</a></span>
        </div>
        <div class="p2">
          <a :href="`/ingredients/${ingredient.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa-solid fa-eye" aria-hidden="true"></i>
            </button>
          </a>
        </div>
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
        .catch(function (error) {
          if (error.response.status !== 404)
          {
            alert("Error while fetching ingredients")
          }
        });
    }
  }
});
</script>