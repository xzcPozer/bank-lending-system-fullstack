import {Component} from '@angular/core';
import {LoginChangeRequest} from "../../../../services/models/login-change-request";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../../../services/services/authentication.service";
import {DataService} from "../../../../services/data-shared/data.service";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent {

  errorMsg: Array<string> = [];
  loginChangeReq: LoginChangeRequest = {
    confirmNewPassword: "",
    email: "",
    newPassword: "",
    oldPassword: ""
  };

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private dataService: DataService
  ) {
  }


  change() {
    this.errorMsg = [];
    this.authService.login1({
      body: this.loginChangeReq
    }).subscribe({
      next: () => {
        this.sendMessage('пароль успешно изменен')
        this.router.navigate(['client', 'profile']);
      },
      error: (err) => {
        console.log(err);
        if (err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else {
          this.errorMsg.push(err.error.error);
        }
      }
    });
  }

  cancel() {
    this.sendMessage('');
    this.router.navigate(['client', 'profile']);
  }

  private sendMessage(message: string) {
    this.dataService.changeMessage(message);
  }
}
