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
            <form @submit.prevent="addMeal">
              <div class="input-group mb-3">
                <div class="input-group-prepend">
                  <span class="input-group-text" id="input-meal-name">Name</span>
                </div>
                <input type="text" class="form-control" v-model="formData.name" name="name" placeholder="Name" required />
              </div>
              <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link" type="submit">
                Add Meal
              </button>
            </form>
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
                  @click="confirmDeleteMeal(meal, index)">
            <i class="fas fa-trash" aria-hidden="true"></i>
          </button>
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
app.component("meal-overview", {
  template: "#meal-overview",
  data: () => ({
    meals: [],
    formData: [],
    hideForm: true,
    token: null,
    modalTitle: null,
    modalBody: null
  }),
  created() {
    this.getToken();
    this.getMeals();
  },
  methods: {
    async getMeals() {
      try {
        const response = await axios.get("/api/meals", {
          headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.meals = response.data
      } catch(error) {
        if (error.response.status === 401) {
          location.href = '/login';
        } else {
          this.showModal("Error while fetching meals")
        }
      }
    },
    confirmDeleteMeal(meal, index) {
      this.showModal("Are you sure you want to delete?", "This action cannot be undone...", true)
          .on("click", "#delete", () => this.deleteMeal(index, index))
    },
    async deleteMeal(meal, index) {
      try {
        const response = await axios.delete(`/api/meals/${meal.id}`, {
          headers: { "Authorization": `Bearer ${this.token}` }
        })

        // close modal
        $('#delete-modal').modal('hide')

        // delete from the local state so Vue will reload list automatically
        this.meals.splice(index, 1).push(response.data)
      } catch(error) {
        this.showModal("Error deleting meal")
      }
    },
    async addMeal() {
      try {
        const response = await axios.post(`/api/meals`,
          { name: this.formData.name },
          { headers: { "Authorization": `Bearer ${this.token}` }
        })
        this.meals.push(response.data)
        this.hideForm= true;
      } catch(error) {
        if (error.response.status === 400) {
          const problemDetails = this.getProblemDetailsString(error.response.data.details)
          this.showModal(`Validation Errors`, problemDetails)
        } else {
          this.showModal(`Issue adding meal to database`)
        }
      }
    },
    getProblemDetailsString(details) {
      return Object.entries(details).map(x => {
        const [property, issue] = x

        return `${property}:  ${issue}`
      }).join("\n")
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