import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SendRequestComponent} from "./pages/send-request/send-request.component";
import {ProfileLoginComponent} from "./pages/profile-login/profile-login.component";
import {ProfileComponent} from "./pages/profile/profile.component";
import {authGuard} from "../../services/guard/auth.guard";
import {EditCreditRequestComponent} from "./pages/edit-credit-request/edit-credit-request.component";
import {ChangePasswordComponent} from "./pages/change-password/change-password.component";

const routes: Routes = [
  {
    path: '',
    redirectTo: 'send-credit-request',
    pathMatch: 'full'
  },
  {
    path: 'send-credit-request',
    component: SendRequestComponent
  },
  {
    path: 'login-in-profile',
    component: ProfileLoginComponent
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CLIENT']}
  },
  {
    path: 'profile/credit-request/edit/:creditRequestId',
    component: EditCreditRequestComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CLIENT']}
  },
  {
    path: 'profile/change-password',
    component: ChangePasswordComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CLIENT']}
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientRoutingModule {
}
