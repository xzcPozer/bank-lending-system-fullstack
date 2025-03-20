import {Component, OnInit} from '@angular/core';
import {CreditRequestService} from "../../../services/services/credit-request.service";
import {
  PageResponseCreditRequestResponseForDirector
} from "../../../services/models/page-response-credit-request-response-for-director";
import {TokenService} from "../../../services/token/token.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-credit-history',
  templateUrl: './credit-history.component.html',
  styleUrls: ['./credit-history.component.scss']
})
export class CreditHistoryComponent implements OnInit {

  page: number = 0;
  size: number = 15;
  pages: any = [];
  response: PageResponseCreditRequestResponseForDirector = {};


  constructor(
    private creditRequestService: CreditRequestService,
    private tokenService: TokenService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.findAllCreditRequests();
  }

  logout() {
    this.tokenService.logout();
    this.router.navigate(['login']);
  }

  displayPerformedWorks(_curPage: number) {
    this.page = _curPage;
    this.findAllCreditRequests();
  }


  private findAllCreditRequests() {
    this.creditRequestService.getAllWithLendingOfficer({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (r) => {
        this.response = r;
        this.pages = this.response.totalPages;
        if (this.pages == 0)
          this.pages = 1;
      }
    })
  }
}
