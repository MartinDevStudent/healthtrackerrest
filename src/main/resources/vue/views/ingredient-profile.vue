<template id="ingredient-profile">
  <app-layout>
    <div v-if="noIngredientFound">
      <p> We're sorry, we were not able to retrieve this ingredient.</p>
      <p> View <a :href="'/users'">all ingredients</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noIngredientFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            {{ this.ingredient.name[0].toUpperCase() + this.ingredient.name.slice(1).toLowerCase() }} Nutritional Profile ({{ this.ingredient.servingSize == null ? 100 : this.ingredient.servingSize }}g serving size)
          </div>
        </div>
      </div>
      <div class="card-body">
        <div class="container text-center">
          <div class="row">
            <div class="col">
              <img v-bind:src="caloriesUrl" />
              <p><strong>Calories (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="fatTotalGUrl" />
              <p><strong>Total fat (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="fatSaturatedGUrl" />
              <p><strong>Saturated fat (grams)</strong></p>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <img v-bind:src="proteinGUrl" />
              <p><strong>Protein (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="sodiumMgUrl" />
              <p><strong>Sodium (milligrams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="potassiumMgUrl" />
              <p><strong>Potassium (milligrams)</strong></p>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <img v-bind:src="carbohydratesTotalGUrl" />
              <p><strong>Carbohydrates (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="fiberGUrl" />
              <p><strong>Fiber (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="sugarGUrl" />
              <p><strong>Sugar (grams)</strong></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("ingredient-profile", {
  template: "#ingredient-profile",
  data: () => ({
    ingredient: null,
    rda: null,
    noIngredientFound: false,
    caloriesUrl: null,
    fatTotalGUrl: null,
    fatSaturatedGUrl: null,
    proteinGUrl: null,
    sodiumMgUrl: null,
    potassiumMgUrl: null,
    cholesterolMgUrl: null,
    carbohydratesTotalGUrl: null,
    fiberGUrl: null,
    sugarGUrl: null,
  }),
  created: async function () {
    await this.fetchIngredients()
    await this.fetchRdas()
    await this.fetchCharts()
  },
  methods: {
    fetchIngredients: async function () {
      const ingredientId = this.$javalin.pathParams["ingredient-id"];

      try {
        const res = await axios.get(`/api/ingredients/${ingredientId}`)
        this.ingredient = res.data
      } catch(error) {
        console.error("No ingredient found for id passed in the path parameter: " + error)
        this.noIngredientFound = true
      }
    },
    fetchRdas: async function () {
      try {
        const res = await axios.get(`/api/ingredients/rda`)
        this.rda = res.data
      } catch(error) {
        console.error("Issue retrieving RDA information: " + error)
        this.noIngredientFound = true
      }
    },
    fetchCharts: async function () {
      const grams = 'g';
      const milliGrams = 'mg';
      const {
        calories,
        fatTotalG,
        fatSaturatedG,
        proteinG,
        sodiumMg,
        potassiumMg,
        cholesterolMg,
        carbohydratesTotalG,
        fiberG,
        sugarG, } = this.ingredient;

      this.caloriesUrl = await this.fetchChart(calories, grams, this.rda.calories)
      this.fatTotalGUrl = await this.fetchChart(fatTotalG, grams, this.rda.fatTotalGUrl)
      this.fatSaturatedGUrl = await this.fetchChart(fatSaturatedG, grams, this.rda.fatSaturatedG)
      this.proteinGUrl = await this.fetchChart(proteinG, grams, this.rda.proteinG)
      this.sodiumMgUrl = await this.fetchChart(sodiumMg, milliGrams, this.rda.sodiumMg)
      this.potassiumMgUrl = await this.fetchChart(potassiumMg, milliGrams, this.rda.potassiumMg)
      this.cholesterolMgUrl = await this.fetchChart(cholesterolMg, milliGrams, this.rda.cholesterolMg)
      this.carbohydratesTotalGUrl = await this.fetchChart(carbohydratesTotalG, grams, this.rda.carbohydratesTotalG)
      this.fiberGUrl = await this.fetchChart(fiberG, grams, this.rda.fiberG)
      this.sugarGUrl = await this.fetchChart(sugarG, grams, this.rda.sugarG)
    },

    fetchChart: async function (value, unit, recommendedDailyAllowance) {
      try {
        const res = await axios.post('https://quickchart.io/chart', {
          backgroundColor: "transparent",
          width: 133,
          height: 80,
          format: "png",
          chart: this.getChartString(value, unit, recommendedDailyAllowance),
        },
        {
          responseType: "blob"
        })

        return this.parseBlobToImageUrl(res.data)
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
            fontSize: 10,
            backgroundColor: 'transparent',
            color: '#000',
            formatter: () => ${value} + ' ${unit}',
            bottomMarginPercentage: 10,
          },
        },
      }`
    },
    parseBlobToImageUrl: (imageData) => URL.createObjectURL(new Blob([imageData], {type: "image/png"})),
  },
});
</script>