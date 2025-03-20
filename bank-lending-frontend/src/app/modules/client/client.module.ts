import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {ClientRoutingModule} from './client-routing.module';
import {SendRequestComponent} from './pages/send-request/send-request.component';
import {NgxMaskDirective} from "ngx-mask";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MenuComponent} from './components/menu/menu.component';
import {ProfileLoginComponent} from './pages/profile-login/profile-login.component';
import {ProfileComponent} from './pages/profile/profile.component';
import {PaginationComponent} from './components/pagination/pagination.component';
import { ChangePasswordComponent } from './pages/change-password/change-password.component';
import { EditCreditRequestComponent } from './pages/edit-credit-request/edit-credit-request.component';


@NgModule({
  declarations: [
    ChangePasswordComponent,
    EditCreditRequestComponent
  ],
  imports: [
    CommonModule,
    ClientRoutingModule,
    NgxMaskDirective,
    ReactiveFormsModule,
    FormsModule
  ],
  exports: [ClientRoutingModule]
})
export class ClientModule {
}
