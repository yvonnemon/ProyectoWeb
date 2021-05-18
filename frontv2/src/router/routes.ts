import { RouteConfig } from 'vue-router';

const routes: RouteConfig[] = [
  {
    path: '/admin',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Index.vue') },
      { path: 'user', component: () => import('src/pages/admin/User.vue') },
      { path: 'vacation', component: () => import('src/pages/admin/Calendar.vue') },
      { path: 'document', component: () => import('src/pages/admin/Document.vue') },
    ],
  },
  {
    path: '/main',
    component: () => import('layouts/EmployeeLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Index.vue') },
      { path: 'form', component: () => import('src/pages/employee/Employee.vue') },
      { path: 'vacation', component: () => import('src/pages/employee/Vacation.vue') },
      { path: 'document', component: () => import('src/pages/employee/Document.vue') },
    ],
  },
  {
    path: '/',
    component: () => import('layouts/LoginLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Login.vue') },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '*',
    component: () => import('pages/Error404.vue'),
  },
];

export default routes;
