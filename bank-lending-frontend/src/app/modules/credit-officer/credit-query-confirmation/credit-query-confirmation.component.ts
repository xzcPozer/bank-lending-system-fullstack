import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {CreditConditionRequest} from "../../../services/models/credit-condition-request";
import {CreditQueryService} from "../../../services/services/credit-query.service";
import {CreditRequestService} from "../../../services/services/credit-request.service";
import {ActivatedRoute, Router} from "@angular/router";
import {CreditConditionService} from "../../../services/services/credit-condition.service";
import {CreditRequestResponse} from "../../../services/models/credit-request-response";
import {LocalStorageService} from "../../../services/local-storage/local-storage.service";
import {PaymentCalculationRequest} from "../../../services/models/payment-calculation-request";
import {DataService} from "../../../services/data-shared/data.service";

@Component({
  selector: 'app-credit-query-confirmation',
  templateUrl: './credit-query-confirmation.component.html',
  styleUrls: ['./credit-query-confirmation.component.scss']
})
export class CreditQueryConfirmationComponent implements OnInit {

  isPossibleToIssue: boolean = false;
  errorMsg: Array<string> = [];
  creditQueryPdfUrl: SafeResourceUrl;
  creditRequestId: any;
  successMessage: string | null = null;
  creditRequest: CreditRequestResponse = {userId: 0};
  creditConditionReq: CreditConditionRequest = {};
  paymentCalculationReq: PaymentCalculationRequest = {};
  creditConditionName: string = '';
  conditionNames: string[] = [];

  constructor(
    private sanitizer: DomSanitizer,
    private creditConditionService: CreditConditionService,
    private creditQueryService: CreditQueryService,
    private localStorageService: LocalStorageService,
    private creditRequestService: CreditRequestService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private dataService: DataService
  ) {
    this.creditQueryPdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl('about:blank');
  }

  async ngOnInit() {
    this.errorMsg = [];
    this.isPossibleToIssue = false;
    this.getCreditRequest()
      .then(() => {
        if (this.localStorageService.getLocalValue('confirmation-credit-query') === null || this.localStorageService.getLocalValue('confirmation-credit-query') === 'false') {
          this.creditQueryConfirmation()
            .then(() => {
              this.loadCreditQueryPdf()
              return Promise.resolve();
            })
          this.localStorageService.localValue('confirmation-credit-query', 'true')
        } else {
          this.isPossibleToIssue = true;
          this.loadCreditQueryPdf()
        }
      })
      .then(() => {
        this.creditConditionReq.amount = this.creditRequest.amount;
        this.loadCreditCondition();
      })
      .catch((error) => {
        console.error('Error in ngOnInit:', error);
      });
    this.loadConditionNames();
  }

  changeCreditCondition() {
    this.creditConditionService.getConditionByName({
      name: this.creditConditionName
    }).subscribe({
      next: (req) => {
        this.paymentCalculationReq.amount = this.creditRequest.amount;
        this.paymentCalculationReq.term = req.term;
        this.paymentCalculationReq.interestRate = req.interestRate;
        this.creditConditionService.recalculationOfMonthlyPayment({
          body: this.paymentCalculationReq
        }).subscribe({
          next: (d) => {
            this.creditConditionReq.term = req.term;
            this.creditConditionReq.interestRate = req.interestRate;
            this.creditConditionReq.monthlyPayment = d;
          }
        });
      },
      error: (err) => {
        if (err.error.validationErrors)
          this.errorMsg = err.error.validationErrors;
        else
          this.errorMsg.push(err.error.error);
      }
    });
  }

  reject() {
    this.localStorageService.deleteValueByKey('confirmation-credit-query');
    this.router.navigate(['credit-officer', 'credit-request', 'refuse', this.creditRequest.userId]);
  }

  completeConfirmation() {
    this.creditConditionService.sendBestConditionForClient({
      userId: this.creditRequest.userId as number,
      body: this.creditConditionReq
    }).subscribe({
      next: () => {
        this.sendMainMessage('Запрос клиента успешно обработан');
        this.localStorageService.deleteValueByKey('confirmation-credit-query');
        this.router.navigate(['credit-officer', 'all-credit-requests']);
      },
      error: (err) => {
        if (err.error.validationErrors)
          this.errorMsg = err.error.validationErrors;
        else
          this.errorMsg.push(err.error.error);
      }
    })
  }

  confirm() {
    this.closeMessage();
    this.creditQueryService.creditQueryVerifyPdf({
      userId: this.creditRequest.userId as number
    }).subscribe({
      next: () => {
        this.sendMessage('Отчет о кредитном запросе подтвержден');
        window.location.reload();
      }
    });
  }

  private getCreditRequest(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.creditRequestId = this.activatedRoute.snapshot.params['creditRequestId'];
      this.creditRequestService.getCreditRequestById({
        creditRequestId: this.creditRequestId
      }).subscribe({
        next: (req) => {
          this.creditRequest = req;
          resolve();
        },
        error: (err) => {
          if (err.error.validationErrors)
            this.errorMsg = err.error.validationErrors;
          else
            this.errorMsg.push(err.error.error);
          reject(err);
        }
      });
    });
  }

  private loadCreditCondition() {
    this.creditConditionService.getBestConditionForUser({
      userId: this.creditRequest.userId as number
    }).subscribe({
      next: (req) => {
        this.creditConditionName = req.productName as string;
        this.creditConditionReq.id = req.id;
        this.creditConditionReq.monthlyPayment = req.monthlyPayment;
        this.creditConditionReq.term = req.term;
        this.creditConditionReq.interestRate = req.interestRate;
        this.isPossibleToIssue = true;
      },
      error: (err) => {
        if (err.error.validationErrors)
          this.errorMsg = err.error.validationErrors;
        else
          this.errorMsg.push(err.error.error);
      }
    });
  }

  private loadCreditQueryPdf() {
    this.creditQueryService.getCreditRequestInformation({
      userId: this.creditRequest.userId as number
    }).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        this['creditQueryPdfUrl'] = this.sanitizer.bypassSecurityTrustResourceUrl(url);
      },
      error: (err) => {
        if (err.error.validationErrors)
          this.errorMsg = err.error.validationErrors;
        else
          this.errorMsg.push(err.error.error);
      }
    });
  }

  private creditQueryConfirmation(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.creditQueryService.createCreditQueryPdf({
        userId: this.creditRequest.userId as number
      }).subscribe({
        next: () => {
          resolve();
          this.isPossibleToIssue = true;
        },
        error: (err) => {
          if (err.error.validationErrors)
            this.errorMsg = err.error.validationErrors;
          else
            this.errorMsg.push(err.error.error);
          this.localStorageService.localValue('confirmation-credit-query', 'false')
          reject(err);
        }
      });
    });
  }

  private sendMainMessage(message: string) {
    this.dataService.changeMessage(message);
  }

  private sendMessage(message: string) {
    this.successMessage = message;
  }

  private closeMessage(): void {
    this.successMessage = null;
  }

  private loadConditionNames() {
    this.creditConditionService.getAllConditionNames().subscribe({
      next: (data) => {
        this.conditionNames = data;
      },
      error: (err) => {
        this.errorMsg.push(err.error.error);
      }
    });
  }
}
