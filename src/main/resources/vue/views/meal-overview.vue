<template id="meal-overview">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="card bg-light mb-3">
        <div class="card-header">
          <div class="row">
            <div class="col-6">
              Meals
            </div>
            <div class="col" align="right">
              <button rel="tooltip" title="Add"
                      class="btn btn-info btn-simple btn-link"
                      @click="hideForm =!hideForm">
                <i class="fa fa-plus" aria-hidden="true"></i>
              </button>
            </div>
          </div>
          <div class="card-body" :class="{ 'd-none': hideForm}">
            <form id="addMeal">
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-meal-name">Name</span>
                </div>
                <input type="text" class="form-control" v-model="formData.name" name="name" placeholder="Name"/>
              </div>
            </form>
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link" @click="addMeal()">Add Meal</button>
          </div>
        </div>
      </div>
      <div class="list-group-item d-flex align-items-start"
           v-for="(meal,index) in meals" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/meals/${meal.id}`"> {{ meal.name }}</a></span>
        </div>
        <div class="p2">
          <a :href="`/meals/${meal.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
          </a>
          <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                  @click="deleteMeal(meal, index)">
            <i class="fas fa-trash" aria-hidden="true"></i>
          </button>
        </div>
      </div>
    </div>
  </app-layout>
</template>
<script>
app.component("meal-overview", {
  template: "#meal-overview",
  data: () => ({
    meals: [],
    formData: [],
    hideForm :true,
  }),
  created() {
    this.fetchMeals();
  },
  methods: {
    fetchMeals: function () {
      axios.get("/api/meals")
        .then(res => this.meals = res.data)
        .catch(() => alert("Error while fetching meals"));
    },
    deleteMeal: function (meal, index) {
      if (confirm('Are you sure you want to delete this meal? This action cannot be undone.', 'Warning')) {
        //user confirmed delete
        const mealId = meal.id;
        const url = `/api/meals/${mealId}`;
        axios.delete(url)
          .then(response =>
              //delete from the local state so Vue will reload list automatically
              this.meals.splice(index, 1).push(response.data))
          .catch(function (error) {
            console.error(error)
          });
      }
    },
    addMeal: function () {
      const url = `/api/meals`;
      axios.post(url,
        {
          name: this.formData.name,
        })
        .then(response => {
          this.meals.push(response.data)
          this.hideForm= true;
        })
        .catch(error => {
          alert("Invalid name for meal")
        })
    }
  }
});
</script>