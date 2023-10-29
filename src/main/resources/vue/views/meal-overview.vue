<template id="meal-overview">
  <app-layout>
    <div>
      <div>
        <ul class="meal-overview-list">
          <li v-for="meal in meals">
            <a :href="`/meals/${meal.id}`">{{meal.name}}</a>
          </li>
        </ul>
      </div>
    </div>
  </app-layout>

</template>
<script>
app.component("meal-overview", {
  template: "#meal-overview",
  data: () => ({
    meals: [],
  }),
  created() {
    this.fetchMeals();
  },
  methods: {
    fetchMeals: function () {
      axios.get("/api/meals")
          .then(res => this.meals = res.data)
          .catch(() => alert("Error while fetching meals"));
    }
  }
});
</script>