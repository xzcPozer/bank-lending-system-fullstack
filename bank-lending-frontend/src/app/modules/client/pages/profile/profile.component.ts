import {Component, OnInit} from '@angular/core';
import {MenuComponent} from "../../components/menu/menu.component";
import {FormsModule} from "@angular/forms";
import {PageResponseCreditRequestResponse} from "../../../../services/models/page-response-credit-request-response";
import {CreditRequestService} from "../../../../services/services/credit-request.service";
import {Router} from "@angular/router";
import {DataService} from "../../../../services/data-shared/data.service";
import {PaginationComponent} from "../../components/pagination/pagination.component";
import {NgForOf, NgIf} from "@angular/common";
import {
  PageResponseCreditRequestClientResponse
} from "../../../../services/models/page-response-credit-request-client-response";
import {TokenService} from "../../../../services/token/token.service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  standalone: true,
  imports: [MenuComponent, FormsModule, PaginationComponent, NgForOf, NgIf],
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  level: 'success' | 'error' = 'success';
  msg: string = '';
  response: PageResponseCreditRequestClientResponse = {totalElements: 0};
  page: number = 0;
  size: number = 10;
  pages: any = [];


  constructor(
    private creditRequestService: CreditRequestService,
    private router: Router,
    private dataService: DataService,
    private tokenService: TokenService
  ) {
  }

  ngOnInit(): void {
    this.dataService.currentMessage.subscribe(message => this.msg = message);
    this.findAllClientRequests();
  }

  private findAllClientRequests() {
    this.creditRequestService.getAll({
      page: this.page,
      size: this.size
    })
      .subscribe({
        next: (clientRequests) => {
          this.response = clientRequests;
          this.pages = this.response.totalPages;
        },
        error: (err) => {
          this.level = 'error';
          this.msg = err.error.error;
        }
      });
  }

  onEdit(id: number | undefined) {
    this.router.navigate(['client', 'profile', 'credit-request', 'edit', id])
  }

  displayPerformedWorks(_curPage: number) {
    this.page = _curPage;
    this.findAllClientRequests();
  }

  onPasswordEdit() {
    this.router.navigate(['client', 'profile','change-password'])
  }

  logout() {
    this.tokenService.logout();
    this.router.navigate(['client', 'login-in-profile'])
  }
}
