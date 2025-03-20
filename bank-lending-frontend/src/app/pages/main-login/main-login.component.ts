import {Component} from '@angular/core';
import {LoginRequest} from "../../services/models/login-request";
import {AuthenticationService} from "../../services/services/authentication.service";
import {TokenService} from "../../services/token/token.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-main-login',
  templateUrl: './main-login.component.html',
  styleUrls: ['./main-login.component.scss']
})
export class MainLoginComponent {

  authRequest: LoginRequest = {email: "", password: ""};
  errorMsg: Array<string> = [];

  constructor(
    private authService: AuthenticationService,
    private tokenService: TokenService,
    private router: Router
  ) {
  }

  login() {
    this.errorMsg = [];
    this.tokenService.logout();
    this.authService.login({
      body: this.authRequest
    }).subscribe({
      next: (res) => {
        this.tokenService.token = res.token as string;
        if (this.tokenService.hasRole('ROLE_CREDIT_OFFICER'))
          this.router.navigate(['credit-officer']);
        else if (this.tokenService.hasRole('ROLE_CLIENT'))
          this.router.navigate(['client']);
        else if(this.tokenService.hasRole('ROLE_DIRECTOR'))
          this.router.navigate(['director']);
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

  loginAsGuest() {
    this.router.navigate(['client']);
  }
}
