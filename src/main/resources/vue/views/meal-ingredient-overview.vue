<template id="meal-ingredient-overview">
  <app-layout>
    <div>
      <h3>Ingredients list </h3>
      <ul>
        <li v-for="ingredient in ingredients">
          {{ingredient.id}}: {{ingredient.name}}
        </li>
      </ul>
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
  </app-layout>
</template>

<script>
app.component("meal-ingredient-overview",{
  template: "#meal-ingredient-overview",
  data: () => ({
    ingredients: [],
    token: null,
    modalTitle: null,
    modalBody: null
  }),
  created() {
    this.getToken()
    this.getMealIngredients()
  },
  methods: {
    async getMealIngredients() {
      const mealId = this.$javalin.pathParams["meal-id"];
      try {
        const response = await axios.get(`/api/meals/${mealId}/ingredients`, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.ingredients = response.data
      } catch(error) {
        if (error.response.status === 401) {
          location.href = '/login';
        } else {
          this.showModal("Error while fetching ingredients")
        }
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