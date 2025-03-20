import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DirectorRoutingModule } from './director-routing.module';
import { CreditHistoryComponent } from './credit-history/credit-history.component';
import { PaginationComponent } from './pagination/pagination.component';


@NgModule({
  declarations: [
    CreditHistoryComponent,
    PaginationComponent
  ],
  imports: [
    CommonModule,
    DirectorRoutingModule
  ]
})
export class DirectorModule { }
