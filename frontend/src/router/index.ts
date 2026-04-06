import { createRouter, createWebHistory } from 'vue-router'
import Sistemas from '../views/Sistemas.vue'
import Dashboard from '../views/Dashboard.vue'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/sistemas',
    name: 'Sistemas',
    component: Sistemas
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
