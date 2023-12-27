<template id="ingredient-profile">
  <app-layout>
    <div v-if="!ingredientFound">
      <p> We're sorry, we were not able to retrieve this ingredient.</p>
      <p> View <a :href="'/users'">all ingredients</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-else>
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
              <img v-bind:src="caloriesUrl" alt="Calories chart" />
              <p><strong>Calories (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="fatTotalGUrl" alt="Total fat chart" />
              <p><strong>Total fat (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="fatSaturatedGUrl" alt="Saturated fat chart" />
              <p><strong>Saturated fat (grams)</strong></p>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <img v-bind:src="proteinGUrl" alt="Protein chart" />
              <p><strong>Protein (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="sodiumMgUrl" alt="Sodium chart" />
              <p><strong>Sodium (milligrams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="potassiumMgUrl" alt="Potassium chart" />
              <p><strong>Potassium (milligrams)</strong></p>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <img v-bind:src="carbohydratesTotalGUrl" alt=" chart" />
              <p><strong>Carbohydrates (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="fiberGUrl" alt="Fiber chart" />
              <p><strong>Fiber (grams)</strong></p>
            </div>
            <div class="col">
              <img v-bind:src="sugarGUrl" alt="Sugar chart" />
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
    ingredientFound: false,
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
    token: null
  }),
  async created() {
    this.getToken()
    await this.getIngredient()
    await this.getRdas()
    await this.getCharts()
  },
  methods: {
    async getIngredient() {
      const ingredientId = this.$javalin.pathParams["ingredient-id"];

      try {
        const response = await axios.get(`/api/ingredients/${ingredientId}`, {
          headers: { "Authorization": `Bearer ${this.token}`}
        })
        this.ingredient = response.data
        this.ingredientFound = true
      } catch(error) {
        if (error.response.status === 401) {
          location.href = '/login';
        } else {
          console.error("No ingredient found for id passed in the path parameter: " + error)
        }
      }
    },
    async getRdas() {
      try {
        const response = await axios.get(`/api/ingredients/rda`, {
          headers: { "Authorization": `Bearer ${this.token}`}
        })
        this.rda = response.data
        this.ingredientFound = true
      } catch(error) {
        console.error("Issue retrieving RDA information: " + error)
      }
    },
    async getCharts() {
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

      this.caloriesUrl = await this.getChart(calories, grams, this.rda.calories)
      this.fatTotalGUrl = await this.getChart(fatTotalG, grams, this.rda.fatTotalGUrl)
      this.fatSaturatedGUrl = await this.getChart(fatSaturatedG, grams, this.rda.fatSaturatedG)
      this.proteinGUrl = await this.getChart(proteinG, grams, this.rda.proteinG)
      this.sodiumMgUrl = await this.getChart(sodiumMg, milliGrams, this.rda.sodiumMg)
      this.potassiumMgUrl = await this.getChart(potassiumMg, milliGrams, this.rda.potassiumMg)
      this.cholesterolMgUrl = await this.getChart(cholesterolMg, milliGrams, this.rda.cholesterolMg)
      this.carbohydratesTotalGUrl = await this.getChart(carbohydratesTotalG, grams, this.rda.carbohydratesTotalG)
      this.fiberGUrl = await this.getChart(fiberG, grams, this.rda.fiberG)
      this.sugarGUrl = await this.getChart(sugarG, grams, this.rda.sugarG)
    },
    async getChart(value, unit, recommendedDailyAllowance) {
      try {
        const response = await axios.post('https://quickchart.io/chart', {
          backgroundColor: "transparent",
          width: 133,
          height: 80,
          format: "png",
          chart: this.getChartString(value, unit, recommendedDailyAllowance),
        },
        {
          responseType: "blob"
        })

        return this.parseBlobToImageUrl(response.data)
      } catch {
        console.error("Issue retrieving chart");
      }
    },
    getChartString(value, unit, recommendedDailyAllowance) {
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
    getToken() {
      this.token = JSON.parse(localStorage.getItem("token"))
    },
  },
});
</script>