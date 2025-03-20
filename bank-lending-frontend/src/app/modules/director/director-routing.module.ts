import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {authGuard} from "../../services/guard/auth.guard";
import {CreditHistoryComponent} from "./credit-history/credit-history.component";

const routes: Routes = [
  {
    path: '',
    redirectTo: 'all-credit-requests',
    pathMatch: 'full'
  },
  {
    path: 'all-credit-requests',
    component: CreditHistoryComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_DIRECTOR']}
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DirectorRoutingModule { }
