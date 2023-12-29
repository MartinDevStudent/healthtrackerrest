<template id="meal-profile">
  <app-layout>
    <div v-if="noMealFound">
      <p> We're sorry, we were not able to retrieve this meal.</p>
      <p> View <a :href="'/meals'">all meals</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noMealFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Meal Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="confirmDeleteMeal()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-id">Meal ID</span>
            </div>
            <input type="number" class="form-control" v-model="meal.id" name="id" readonly/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-name">Name</span>
            </div>
            <input type="text" class="form-control" v-model="meal.name" name="name" readonly/>
          </div>
        </form>
      </div>
      <div class="card-footer text-left">
        <p  v-if="ingredients.length === 0"> No ingredients...</p>
        <p  v-if="ingredients.length > 0"> Ingredients...</p>
        <ul>
          <li v-for="ingredient in ingredients">
            <a :href="'/ingredients/' + ingredient.id">{{ ingredient.name }}</a>
          </li>
        </ul>
      </div>
    </div>

    <!-- Regular Modal -->
    <div class="modal fade" id="modal" tabindex="-1" role="dialog" aria-labelledby="modalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="modalLabel">{{ this.modalTitle }}</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times</span>
            </button>
          </div>
          <div class="modal-body">
            <p><span v-html="modalBody"></span></p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>
    <!-- Delete Modal -->
    <div class="modal fade" id="delete-modal" tabindex="-1" role="dialog" aria-labelledby="modalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="modalLabel">{{ this.modalTitle }}</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times</span>
            </button>
          </div>
          <div class="modal-body">
            <p><span v-html="modalBody"></span></p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
            <button type="button" id="delete" class="btn btn-danger" >Delete</button>
          </div>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("meal-profile", {
  template: "#meal-profile",
  data: () => ({
    meal: null,
    noMealFound: false,
    ingredients: [],
    token: null,
    modalTitle: null,
    modalBody: null
  }),
  created() {
    const mealId = this.$javalin.pathParams["meal-id"]
    const url = `/api/meals/${mealId}`

    this.getToken()
    this.getMeal(url)
    this.getIngredientsByMealId(url)
    },
    methods: {
      async getMeal(url) {
        try {
          const response = await axios.get(url, {
            headers: { "Authorization": `Bearer ${this.token}` }
          })
          this.meal = response.data
        } catch(error) {
          if (error.response.status === 401) {
            location.href = '/login'
          } else {
            console.error("No meal found for id passed in the path parameter: " + error)
            this.noMealFound = true
          }
        }
      },
      async getIngredientsByMealId(baseUrl) {
        try {
          const response = await axios.get(baseUrl + `/ingredients`,{
            headers: { "Authorization": `Bearer ${this.token}` }
          })
          this.ingredients = response.data
        } catch(error) {
          console.error("No ingredients in this meal: " + error)
        }
      },
      confirmDeleteMeal() {
        this.showModal("Are you sure you want to delete?", "This action cannot be undone...", true)
            .on("click", "#delete", () => this.deleteMeal())
      },
      async deleteMeal() {
        const mealId = this.$javalin.pathParams["meal-id"]

        try {
          await axios.delete(`/api/meals/${mealId}`, {
            headers: { "Authorization": `Bearer ${this.token}` }
          })

          this.showModal("Meal deleted!")

          // navigate to /meals endpoint
          window.location.href = '/meals'
        } catch(error) {
          this.showModal("Error deleting meal")
        }
      },
      getToken() {
        this.token = JSON.parse(localStorage.getItem("token"))
      },
      showModal(title, body = "", showDeletionModal = false) {
        this.modalTitle = title
        this.modalBody = body

        return $(showDeletionModal ? '#delete-modal' : '#modal').modal('show')
      }
    }
});
</script>