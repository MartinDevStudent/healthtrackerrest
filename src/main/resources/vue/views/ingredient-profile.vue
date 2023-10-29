<template id="ingredient-profile">
  <app-layout>
    <div>
      <form v-if="ingredient">
        <label class="col-form-label">Ingredient ID: </label>
        <input class="form-control" v-model="ingredient.id" name="id" type="number" readonly/><br>
        <label class="col-form-label">Name: </label>
        <input class="form-control" v-model="ingredient.name" name="name" type="text" readonly/><br>
        <label class="col-form-label">Calories: </label>
        <input class="form-control" v-model="ingredient.calories" name="calories" type="number" step=".01" readonly/><br>
        <label class="col-form-label">Serving Size (g): </label>
        <input class="form-control" v-model="ingredient.servingSizeG" name="servingSizeG" type="number"  step=".01" readonly/><br>
        <label class="col-form-label">Total Fat (g): </label>
        <input class="form-control" v-model="ingredient.fatTotalG" name="fatTotalG" type="number" step=".01" readonly/><br>
        <label class="col-form-label">Saturated Fat (g): </label>
        <input class="form-control" v-model="ingredient.fatSaturatedG" name="fatSaturatedG"  type="number" step=".01" readonly/><br>
        <label class="col-form-label">Protein (g): </label>
        <input class="form-control" v-model="ingredient.proteinG" name="proteinG"  type="number"  step=".01" readonly/><br>
        <label class="col-form-label">Sodium (mg): </label>
        <input class="form-control" v-model="ingredient.sodiumMg" name="sodiumMg"  type="number" readonly/><br>
        <label class="col-form-label">Potassium (mg): </label>
        <input class="form-control" v-model="ingredient.potassiumMg" name="potassiumMg"  type="number" readonly/><br>
        <label class="col-form-label">Cholesterol (mg): </label>
        <input class="form-control" v-model="ingredient.cholesterolMg" name="cholesterolMg"  type="number" readonly/><br>
        <label class="col-form-label">Carbohydrates (g): </label>
        <input class="form-control" v-model="ingredient.carbohydratesTotalG" name="carbohydratesTotalG"  type="number" step=".01" readonly/><br>
        <label class="col-form-label">Fiber (g): </label>
        <input class="form-control" v-model="ingredient.fiberG" name="fiberG"  type="number" step=".01" readonly/><br>
        <label class="col-form-label">Sugar (g): </label>
        <input class="form-control" v-model="ingredient.sugarG" name="sugarG"  type="number" step=".01" readonly/><br>
        <label class="col-form-label">Meal Id: </label>
        <input class="form-control" v-model="ingredient.mealId" name="mealId"  type="number" readonly/><br>
      </form>
    </div>
  </app-layout>
</template>

<script>
app.component("ingredient-profile", {
  template: "#ingredient-profile",
  data: () => ({
    ingredient: null
  }),
  created: function () {
    const ingredientId = this.$javalin.pathParams["ingredient-id"];
    const url = `/api/ingredients/${ingredientId}`
    axios.get(url)
        .then(res => this.ingredient = res.data)
        .catch(() => alert("Error while fetching ingredient " + ingredientId));
  }
});
</script>