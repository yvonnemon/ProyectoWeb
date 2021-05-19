import { RouteConfig } from 'vue-router';
import jwt_decode from "jwt-decode";

const routes: RouteConfig[] = [
  {
    path: '/admin',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('src/pages/admin/AdminHome.vue') },
      { path: 'user', component: () => import('src/pages/admin/User.vue') },
      { path: 'vacation', component: () => import('src/pages/admin/Calendar.vue') },
      { path: 'document', component: () => import('src/pages/admin/Document.vue') },
    ],
    beforeEnter: (from, to, next) => {
      if(!sessionStorage.getItem("Session")){
        next("/");
      } else {
        const token: any = sessionStorage.getItem("Session");
        let decoded: any = jwt_decode(token);
        if(decoded.role != "ADMIN"){
          next("/");
        } else {
          next();
        }
      }
    }

  },
  {
    path: '/main',
    component: () => import('layouts/EmployeeLayout.vue'),
    children: [
      { path: '', component: () => import('src/pages/employee/Index.vue') },
      { path: 'form', component: () => import('src/pages/employee/Employee.vue') },
      { path: 'vacation', component: () => import('src/pages/employee/Vacation.vue') },
      { path: 'document', component: () => import('src/pages/employee/Document.vue') },
    ],   
     beforeEnter: (from, to, next) => {
      if(!sessionStorage.getItem("Session")){
        next("/");
      } else {
        const token: any = sessionStorage.getItem("Session");
        let decoded: any = jwt_decode(token);
        if(decoded.role != "EMPLOYEE"){
          next("/");
        } else {
          next();
        }
      }
    }

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
