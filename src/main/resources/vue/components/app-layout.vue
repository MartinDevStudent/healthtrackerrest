<template id="app-layout">
  <div class="app-layout">
    <div class="container">
      <!-- Start of navbar -->
      <nav class="navbar navbar-expand-lg navbar-light">
        <a class="navbar-brand" href="/">Home</a>
        <p>{{ $javalin.state.user }}</p>
        <button class="navbar-toggler" type="button" data-toggle="collapse"
                data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav mr-auto">
            <li class="nav-item active">
              <a class="nav-link" href="/users">
                Users <span class="sr-only">(current)</span>
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/activities">Activities</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/meals">Meals</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/ingredients">Ingredient</a>
            </li>
          </ul>
          <button v-if="token === null" class="btn btn-outline-success me-2 mr-2" onclick="location.href = '/login';">
            Login
          </button>
          <button v-if="token === null" class="btn btn-outline-success me-2" onclick="location.href = '/register';">
            Register
          </button>
          <button v-else class="btn btn-outline-danger me-2" @click="logout()">
            Logout
          </button>
        </div>
      </nav>
      <!--End of nav bar-->
      <!--Start of main content area-->
      <div class="content mt-3">
        <div class="container-fluid">
          <slot></slot>
        </div>
      </div>
      <!--End of main content area-->
    </div>
  </div>
</template>

<script>
  app.component("app-layout",
  {
    template: "#app-layout",
    data: () => ({
      token: null,
    }),
    created() {
      this.getToken()
    },
    methods: {
      getToken() {
        this.token = JSON.parse(localStorage.getItem("token"))
      },
      logout() {
        localStorage.removeItem("token")
        location.href = '/login'
      }
    }
  });
</script>

<style>
.navbar{
  background-color: #e3f2fd;
}
</style>