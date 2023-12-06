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
    recommendedDailyAllowances: null,
    noIngredientFound: false,
    caloriesUrl: null,
  }),
  created: async function () {
    await this.fetchIngredients()
    await this.fetchRdas()
    await this.fetchCharts()
  },
  methods: {
    fetchIngredients: async function () {
      ingredientId = this.$javalin.pathParams["ingredient-id"];
      url = `/api/ingredients/${ingredientId}`

      try {
        const res = await axios.get(url)
        this.ingredient = res.data
      } catch(error) {
        console.error("No ingredient found for id passed in the path parameter: " + error)
        this.noIngredientFound = true
      }
    },
    fetchRdas: async function () {
      try {
        const res = await axios.get(`/api/ingredients/rda`)
        this.recommendedDailyAllowances = res.data
      } catch(error) {
        console.error("Issue retrieving RDA information: " + error)
        this.noIngredientFound = true
      }
    },
    fetchCharts: async function () {
      this.caloriesUrl = await this.fetchChart(this.ingredient.calories, 'g', this.recommendedDailyAllowances.calories)
    },
    fetchChart: async function (value, unit, recommendedDailyAllowance) {
      try {
        const res = await axios.post('https://quickchart.io/chart', {
          backgroundColor: "transparent",
          width: 500,
          height: 300,
          format: "png",
          chart: this.getChartString(value, unit, recommendedDailyAllowance),
        },
        {
          responseType: "blob"
        })

        return URL.createObjectURL(new Blob([res.data], { type: "image/png" }))
      } catch {
        console.error("Issue retrieving chart");
      }
    },
    getChartString: (value, unit, recommendedDailyAllowance) => {
      const percentageOfRda = (value / recommendedDailyAllowance) * 100;

      return `{
        type: 'gauge',
        data: {
          datasets: [
            {
              value: ${percentageOfRda},
              data: [20, 40, 60],
              backgroundColor: ['green', 'orange', 'red'],
              borderWidth: 2,
            },
          ],
        },
        options: {
          valueLabel: {
            fontSize: 22,
            backgroundColor: 'transparent',
            color: '#000',
            formatter: () => ${value} + ' ${unit}',
            bottomMarginPercentage: 10,
          },
        },
      }`
    }
  },
});
</script>