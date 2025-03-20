import {Component, OnInit} from '@angular/core';
import {CreditQueryService} from "../../../services/services/credit-query.service";
import {
  PageResponseCreditQueryClientResponse
} from "../../../services/models/page-response-credit-query-client-response";

@Component({
  selector: 'app-client-credit-query',
  templateUrl: './client-credit-query.component.html',
  styleUrls: ['./client-credit-query.component.scss']
})
export class ClientCreditQueryComponent  implements OnInit{

  level: 'success' | 'error' = 'success';
  msg: string = '';
  searchedLastName: string = '';
  response: PageResponseCreditQueryClientResponse = {};
  page: number = 0;
  size: number = 10;
  pages: any = [];
  isSearch = false;

  constructor(
    private creditQueryService: CreditQueryService,
  ) {
  }

  ngOnInit(): void {
    this.findAllCreditQueryInfo();
  }

  private findAllCreditQueryInfo() {
    this.creditQueryService.getPaymentInformation1({
      page: this.page,
      size: this.size
    })
      .subscribe({
        next: (creditQueryResponse) => {
          this.response = creditQueryResponse;
          this.pages = this.response.totalPages;
          if (this.pages == 0)
            this.pages = 1;
        },
        error: (err) => {
          this.level = 'error';
          this.msg = err.error.error;
        }
      });
  }

  private findAllClientInfoByLastname() {
    this.creditQueryService.getAllClientPaymentInformationByLastname({
      lastname: this.searchedLastName
    })
      .subscribe({
        next: (res)=>{
          this.response = res;
          this.pages = this.response.totalPages;
          if (this.pages == 0)
            this.pages = 1;
        },
        error: (err) => {
          this.level = 'error';
          this.msg = err.error.error;
        }
      });
  }

  displayPerformedWorks(_curPage: number) {
    this.page = _curPage;
    if (this.isSearch) {
      this.findAllClientInfoByLastname();
    } else {
      this.findAllCreditQueryInfo();
    }
  }

  resetSearch() {
    this.page = 0;
    this.findAllCreditQueryInfo();
  }

  search() {
    this.page = 0;
    this.findAllClientInfoByLastname();
  }

}
