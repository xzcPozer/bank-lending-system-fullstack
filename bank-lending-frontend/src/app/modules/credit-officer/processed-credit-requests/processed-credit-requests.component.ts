import {Component, OnInit} from '@angular/core';
import {PageResponseCreditRequestResponse} from "../../../services/models/page-response-credit-request-response";
import {CreditRequestService} from "../../../services/services/credit-request.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-processed-credit-requests',
  templateUrl: './processed-credit-requests.component.html',
  styleUrls: ['./processed-credit-requests.component.scss']
})
export class ProcessedCreditRequestsComponent implements OnInit{
  level: 'success' | 'error' = 'success';
  msg: string = '';
  response: PageResponseCreditRequestResponse = {};
  page: number = 0;
  size: number = 10;
  pages: any = [];

  constructor(
    private creditRequestService: CreditRequestService
  ) {
  }

  ngOnInit(): void {
    this.findAllRequests();
  }

  private findAllRequests() {
    this.creditRequestService.getAll1({
      isProcessed: true
    })
      .subscribe({
        next: (creditResponse) => {
          this.response = creditResponse;
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
    this.findAllRequests();
  }
}
