import {Component, OnInit} from '@angular/core';
import {PageResponseCreditRequestResponse} from "../../../services/models/page-response-credit-request-response";
import {CreditRequestService} from "../../../services/services/credit-request.service";
import {Router} from "@angular/router";
import {DataService} from "../../../services/data-shared/data.service";

@Component({
  selector: 'app-credit-request',
  templateUrl: './credit-request.component.html',
  styleUrls: ['./credit-request.component.scss']
})
export class CreditRequestComponent implements OnInit {

  level: 'success' | 'error' = 'success';
  msg: string = '';
  response: PageResponseCreditRequestResponse = {};
  page: number = 0;
  size: number = 10;
  pages: any = [];

  constructor(
    private creditRequestService: CreditRequestService,
    private router: Router,
    private dataService: DataService
  ) {
  }

  ngOnInit(): void {
    this.dataService.currentMessage.subscribe(message => this.msg = message);
    this.findAllRequests();
  }

  private findAllRequests() {
    this.creditRequestService.getAll1({
      isProcessed: false
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

  onEdit(id: number | undefined) {
    this.router.navigate(['credit-officer', 'credit-request', 'solvency-confirmation', id])
  }

  displayPerformedWorks(_curPage: number) {
    this.page = _curPage;
    this.findAllRequests();
  }
}
