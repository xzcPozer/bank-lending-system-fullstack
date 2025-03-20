import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CreditOfficerRoutingModule } from './credit-officer-routing.module';
import { CreditRequestComponent } from './credit-request/credit-request.component';
import { MenuComponent } from './components/menu/menu.component';
import { PaginationComponent } from './components/pagination/pagination.component';
import { CheckCreditRequestComponent } from './check-credit-request/check-credit-request.component';
import { ProcessedCreditRequestsComponent } from './processed-credit-requests/processed-credit-requests.component';
import { ClientInfoComponent } from './client-info/client-info.component';
import {FormsModule} from "@angular/forms";
import { ClientCreditQueryComponent } from './client-credit-query/client-credit-query.component';
import { ClientsComponent } from './clients/clients.component';
import { SolvencyConfirmationComponent } from './solvency-confirmation/solvency-confirmation.component';
import { EmploymentConfirmationComponent } from './employment-confirmation/employment-confirmation.component';
import { CreditQueryConfirmationComponent } from './credit-query-confirmation/credit-query-confirmation.component';
import { SendForRevisionComponent } from './send-for-revision/send-for-revision.component';
import { RefuseComponent } from './refuse/refuse.component';


@NgModule({
  declarations: [
    CreditRequestComponent,
    MenuComponent,
    PaginationComponent,
    CheckCreditRequestComponent,
    ProcessedCreditRequestsComponent,
    ClientInfoComponent,
    ClientCreditQueryComponent,
    ClientsComponent,
    SolvencyConfirmationComponent,
    EmploymentConfirmationComponent,
    CreditQueryConfirmationComponent,
    SendForRevisionComponent,
    RefuseComponent
  ],
    imports: [
        CommonModule,
        CreditOfficerRoutingModule,
        FormsModule
    ]
})
export class CreditOfficerModule { }
