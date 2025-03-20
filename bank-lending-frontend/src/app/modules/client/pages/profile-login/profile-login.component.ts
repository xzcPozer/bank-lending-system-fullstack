import {Component} from '@angular/core';
import {MenuComponent} from "../../components/menu/menu.component";
import {LoginRequest} from "../../../../services/models/login-request";
import {AuthenticationService} from "../../../../services/services/authentication.service";
import {TokenService} from "../../../../services/token/token.service";
import {Router} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-profile-login',
  templateUrl: './profile-login.component.html',
  standalone: true,
  imports: [MenuComponent, FormsModule, NgForOf, NgIf],
  styleUrls: ['./profile-login.component.scss']
})
export class ProfileLoginComponent {
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
    this.authService.login({
      body: this.authRequest
    }).subscribe({
      next: (res) => {
        this.tokenService.token = res.token as string;
        this.router.navigate(['client', 'profile']);
      },
      error: (err) => {
        console.log(err);
        if (err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else {
          this.errorMsg.push(err.error.errorMsg);
        }
      }
    });
  }
}
