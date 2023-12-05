<template id="ingredient-profile">
  <app-layout>
    <div v-if="noIngredientFound">
      <p> We're sorry, we were not able to retrieve this ingredient.</p>
      <p> View <a :href="'/users'">all ingredients</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noIngredientFound">
      <img v-bind:src="caloriesUrl" />
      <div ref="table_div">
      </div>
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Ingredient Profile </div>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="mb-3 row">
            <label for="id" class="col-sm-3 col-form-label">Ingredient ID</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="id" v-model="ingredient.id">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="name" class="col-sm-3 col-form-label">Name</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="name" v-model="ingredient.name">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="calories" class="col-sm-3 col-form-label">Calories</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="calories" v-model="ingredient.calories">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="servingSizeG" class="col-sm-3 col-form-label">Serving size (g)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="calories" v-model="ingredient.servingSizeG">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="fatTotalG" class="col-sm-3 col-form-label">Fat total (g)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="fatTotalG" v-model="ingredient.fatTotalG">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="fatSaturatedG" class="col-sm-3 col-form-label">Fat saturated (g)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="fatSaturatedG" v-model="ingredient.fatSaturatedG">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="proteinG" class="col-sm-3 col-form-label">Protein (g)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="proteinG" v-model="ingredient.proteinG">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="sodiumMg" class="col-sm-3 col-form-label">Sodium (Mg)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="sodiumMg" v-model="ingredient.sodiumMg">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="potassiumMg" class="col-sm-3 col-form-label">Potassium (Mg)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="potassiumMg" v-model="ingredient.potassiumMg">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="cholesterolMg" class="col-sm-3 col-form-label">Cholesterol (Mg)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="cholesterolMg" v-model="ingredient.cholesterolMg">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="carbohydratesTotalG" class="col-sm-3 col-form-label">Carbohydrates Total (g)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="carbohydratesTotalG" v-model="ingredient.carbohydratesTotalG">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="fiberG" class="col-sm-3 col-form-label">Fiber (g)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="fiberG" v-model="ingredient.fiberG">
            </div>
          </div>
          <div class="mb-3 row">
            <label for="sugarG" class="col-sm-3 col-form-label">Sugar (g)</label>
            <div class="col-sm-15">
              <input type="text" readonly class="form-control-plaintext" id="sugarG" v-model="ingredient.sugarG">
            </div>
          </div>
        </form>
      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("ingredient-profile", {
  template: "#ingredient-profile",
  data: () => ({
    ingredient: null,
    noIngredientFound: false,
    caloriesUrl: null,
  }),
  created: function () {
    this.fetchIngredients()
    this.fetchCharts()
  },
  methods: {
    fetchIngredients: function () {
      const ingredientId = this.$javalin.pathParams["ingredient-id"];
      const url = `/api/ingredients/${ingredientId}`
      axios.get(url)
        .then(res => this.ingredient = res.data)
        .catch(() => {
          console.error("No activity found for id passed in the path parameter: " + error)
          this.noIngredientFound = true
        });
    },
    fetchCharts: function () {
      axios.post('https://quickchart.io/chart', {
        backgroundColor: "transparent",
        width: 500,
        height: 300,
        format: "png",
        chart: "{type:'bar',data:{labels:['January','February','March','April','May'],datasets:[{label:'Dogs',data:[50,60,70,180,190]}]},options:{scales:{yAxes:[{ticks:{callback:function(value){return'$'+value;}}}]}}}"
      },
      {
        responseType: "blob"
      })
      .then(res => this.caloriesUrl =
          URL.createObjectURL(new Blob([res.data], { type: "image/png" })))
      .catch(() => {
        console.error("Issue retrieving chart");
      });
    }
  }
});
</script>