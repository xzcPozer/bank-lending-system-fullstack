import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CreditRequestComponent} from "./credit-request/credit-request.component";
import {authGuard} from "../../services/guard/auth.guard";
import {CheckCreditRequestComponent} from "./check-credit-request/check-credit-request.component";
import {ProcessedCreditRequestsComponent} from "./processed-credit-requests/processed-credit-requests.component";
import {ClientInfoComponent} from "./client-info/client-info.component";
import {ClientCreditQueryComponent} from "./client-credit-query/client-credit-query.component";
import {ClientsComponent} from "./clients/clients.component";
import {SolvencyConfirmationComponent} from "./solvency-confirmation/solvency-confirmation.component";
import {EmploymentConfirmationComponent} from "./employment-confirmation/employment-confirmation.component";
import {CreditQueryConfirmationComponent} from "./credit-query-confirmation/credit-query-confirmation.component";
import {SendForRevisionComponent} from "./send-for-revision/send-for-revision.component";
import {RefuseComponent} from "./refuse/refuse.component";

const routes: Routes = [
  {
    path: '',
    redirectTo: 'all-credit-requests',
    pathMatch: 'full'
  },
  {
    path: 'all-credit-requests',
    component: CreditRequestComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'credit-request/solvency-confirmation/:creditRequestId',
    component: SolvencyConfirmationComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'credit-request/employment-confirmation/:creditRequestId',
    component: EmploymentConfirmationComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'credit-request/credit-query-confirmation/:creditRequestId',
    component: CreditQueryConfirmationComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'credit-request/send-for-revision/:userId',
    component: SendForRevisionComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'credit-request/refuse/:userId',
    component: RefuseComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'processed-credit-requests',
    component: ProcessedCreditRequestsComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'client-info',
    component: ClientInfoComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'client-financial-situation',
    component: ClientCreditQueryComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  },
  {
    path: 'clients',
    component: ClientsComponent,
    canActivate: [authGuard],
    data: {role: ['ROLE_CREDIT_OFFICER']}
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CreditOfficerRoutingModule { }
