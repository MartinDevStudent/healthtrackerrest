<template id="ingredient-overview">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="card bg-light mb-3">
        <div class="card-header">
          <div class="row">
            <div class="col-6">
              Ingredients
            </div>
          </div>
        </div>
      </div>
      <div class="list-group-item d-flex align-items-start"
           v-for="(ingredient,index) in ingredients" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/ingredients/${ingredient.id}`"> {{ ingredient.name }}</a></span>
        </div>
        <div class="p2">
          <a :href="`/ingredients/${ingredient.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa-solid fa-eye" aria-hidden="true"></i>
            </button>
          </a>
        </div>
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
  </app-layout>
</template>
<script>
app.component("ingredient-overview", {
  template: "#ingredient-overview",
  data: () => ({
    ingredients: [],
    token: null,
    modalTitle: null,
    modalBody: null
  }),
  created() {
    this.getToken()
    this.getIngredients()
  },
  methods: {
    async getIngredients() {
      try {
        const response = await axios.get("/api/ingredients", {
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.ingredients = response.data
      } catch (error) {
        if (error.response.status === 401) {
          location.href = '/login';
        } else if (error.response.status !== 404) {
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