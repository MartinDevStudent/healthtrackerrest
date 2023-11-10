<template id="ingredient-profile">
  <app-layout>
    <div v-if="noIngredientFound">
      <p> We're sorry, we were not able to retrieve this ingredient.</p>
      <p> View <a :href="'/users'">all ingredients</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noIngredientFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Ingredient Profile </div>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-id">Ingredient ID</span>
            </div>
            <input type="number" class="form-control" v-model="ingredient.id" name="id" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-name">Name</span>
            </div>
            <input type="text" class="form-control" v-model="ingredient.name" name="name" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-calories">Calories</span>
            </div>
            <input type="number" step=".01" class="form-control" v-model="ingredient.calories" name="calories" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-servingSizeG">Serving size (g)</span>
            </div>
            <input type="number" step=".01" class="form-control" v-model="ingredient.servingSizeG" name="servingSizeG" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-fatTotalG">Fat total (g)</span>
            </div>
            <input type="number" step=".01" class="form-control" v-model="ingredient.fatTotalG" name="fatTotalG" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-fatSaturatedG">Fat saturated (g)</span>
            </div>
            <input type="number" step=".01" class="form-control" v-model="ingredient.fatSaturatedG" name="fatSaturatedG" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-proteinG">Protein (g)</span>
            </div>
            <input type="number" step=".01" class="form-control" v-model="ingredient.proteinG" name="proteinG" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-sodiumMg">Sodium (Mg)</span>
            </div>
            <input type="number" class="form-control" v-model="ingredient.sodiumMg" name="sodiumMg" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-potassiumMg">Potassium (Mg)</span>
            </div>
            <input type="number" class="form-control" v-model="ingredient.potassiumMg" name="potassiumMg" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-cholesterolMg">Cholesterol (Mg)</span>
            </div>
            <input type="number" class="form-control" v-model="ingredient.cholesterolMg" name="cholesterolMg" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-carbohydratesTotalG">Carbohydrates Total (g)</span>
            </div>
            <input type="number" step=".01" class="form-control" v-model="ingredient.carbohydratesTotalG" name="carbohydratesTotalG" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-fiberG">Fiber (g)</span>
            </div>
            <input type="number" step=".01" class="form-control" v-model="ingredient.fiberG" name="fiberG" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-ingredient-sugarG">Sugar (g)</span>
            </div>
            <input type="number" step=".01" class="form-control" v-model="ingredient.sugarG" name="sugarG" readonly/>
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
  }),
  created: function () {
    const ingredientId = this.$javalin.pathParams["ingredient-id"];
    const url = `/api/ingredients/${ingredientId}`
    axios.get(url)
      .then(res => this.ingredient = res.data)
      .catch(() => {
        console.error("No activity found for id passed in the path parameter: " + error)
        this.noIngredientFound = true
      });
  }
});
</script>