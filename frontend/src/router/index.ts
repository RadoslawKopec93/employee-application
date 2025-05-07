import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import HomeView from "../views/HomeView.vue";
import UserLogin from "../components/authentication/UserLogin.vue";
import Register from "../components/authentication/Register.vue";
import FeedPage from "../components/authentication/FeedPage.vue";
import SignIn from "../components/authentication/SignIn.vue";
import OAuthCallback from "../components/authentication/OAuthCallback.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: UserLogin,
  },
  {
    path: "/register",
    name: "register",
    component: Register,
  },
  {
    path: "/feed",
    name: "FeedPage",
    component: FeedPage,
  },
  {
    path: "/login",
    name: "SignIn",
    component: SignIn,
  },
  {
    path: "/oauth-callback",
    name: "OAuthCallback",
    component: OAuthCallback,
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
