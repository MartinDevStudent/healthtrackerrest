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
    token: null,
  }),
  created() {
    this.getToken()
    this.getIngredients()
  },
  methods: {
    async getIngredients() {
      try {
        const response = await axios.get("/api/ingredients", {
          headers: { "Authorization": `Bearer ${this.token}`}
        })
        this.ingredients = response.data
      } catch (error) {
        if (error.response.status === 401) {
          location.href = '/login';
        } else if (error.response.status !== 404) {
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