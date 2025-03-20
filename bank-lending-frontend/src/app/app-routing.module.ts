import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainLoginComponent} from "./pages/main-login/main-login.component";
import {authGuard} from "./services/guard/auth.guard";

const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path:'login',
    component: MainLoginComponent
  },
  {
    path: 'client',
    loadChildren: () => import('./modules/client/client.module').then(m=>m.ClientModule)
  },
  {
    path: 'credit-officer',
    loadChildren: () => import('./modules/credit-officer/credit-officer.module').then(m=>m.CreditOfficerModule),
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'director',
    loadChildren: () => import('./modules/director/director.module').then(m=>m.DirectorModule),
    canActivate: [authGuard],
    data: {role: ['ROLE_DIRECTOR']}
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
