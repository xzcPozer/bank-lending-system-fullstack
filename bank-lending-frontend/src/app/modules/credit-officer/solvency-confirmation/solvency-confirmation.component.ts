import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {
  CreditQueryTaxAgentHiredWorkerRequest
} from "../../../services/models/credit-query-tax-agent-hired-worker-request";
import {CreditQueryTaxAgentIpRequest} from "../../../services/models/credit-query-tax-agent-ip-request";
import {
  CreditQueryFinancialSituationHiredWorkerRequest
} from "../../../services/models/credit-query-financial-situation-hired-worker-request";
import {
  CreditQueryFinancialSituationIpRequest
} from "../../../services/models/credit-query-financial-situation-ip-request";
import {CreditQueryService} from "../../../services/services/credit-query.service";
import {ActivatedRoute, Router} from "@angular/router";
import {CreditRequestResponse} from "../../../services/models/credit-request-response";
import {CreditRequestService} from "../../../services/services/credit-request.service";
import {LocalStorageService} from "../../../services/local-storage/local-storage.service";

@Component({
  selector: 'app-solvency-confirmation',
  templateUrl: './solvency-confirmation.component.html',
  styleUrls: ['./solvency-confirmation.component.scss']
})
export class SolvencyConfirmationComponent implements OnInit {

  errorMsg: Array<string> = [];
  uploadPdfUrl: SafeResourceUrl;
  solvencyPdfUrl: SafeResourceUrl;
  reqTaxAgent: CreditQueryTaxAgentHiredWorkerRequest | CreditQueryTaxAgentIpRequest;
  reqFinancialSituation: CreditQueryFinancialSituationHiredWorkerRequest | CreditQueryFinancialSituationIpRequest;
  creditRequestId: any;
  creditRequest: CreditRequestResponse = {userId: 0};
  successMessage: string | null = null;
  isPossibleToContinue: boolean = false;

  constructor(
    private sanitizer: DomSanitizer,
    private creditQueryService: CreditQueryService,
    private creditRequestService: CreditRequestService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private localStorageService: LocalStorageService
  ) {
    this.reqTaxAgent = {inn: '', kpp: '', name: ''};
    this.reqFinancialSituation = {monthlyPayment: '', taxCalculationAmount: '', totalIncome: ''};


    this.uploadPdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl('about:blank');
    this.solvencyPdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl('about:blank');
  }

  async ngOnInit() {
    this.isPossibleToContinue = false;
    this.errorMsg = [];
    this.getCreditRequest()
      .then(() => {
        this.loadUploadSolvencyPdf();
        return Promise.resolve();
      })
      .then(() => {
        if (this.localStorageService.getLocalValue('confirmation-solvency') === null || this.localStorageService.getLocalValue('confirmation-solvency') === 'false') {
          this.solvencyConfirmation()
            .then(() => {
              this.loadSolvencyPdf()
              return Promise.resolve();
            })
          this.localStorageService.localValue('confirmation-solvency', 'true')
        } else {
          this.isPossibleToContinue = true;
          this.loadSolvencyPdf()
        }
      })
      .then(() => {
        this.checkForFormsData();
      })
      .catch((error) => {
        console.error('Error in ngOnInit:', error);
      });
  }

  updateFinancialInfo() {
    this.closeMessage();
    if (this.creditRequest.type === 'HIRED_WORKERS') {
      this.creditQueryService.changeSolvencyHiredWorkerFinancialSituation({
        userId: this.creditRequest.userId as number,
        body: this.reqFinancialSituation as CreditQueryFinancialSituationHiredWorkerRequest
      }).subscribe({
        next: () => {
          window.location.reload();
          this.sendMessage('Финансовая ситуация была обновлена')
        },
        error: (err) => {
          if (err.error.validationErrors)
            this.errorMsg = err.error.validationErrors;
          else
            this.errorMsg.push(err.error.error);
        }
      });
    } else if (this.creditRequest.type === 'INDIVIDUAL_ENTREPRENEUR') {
      this.creditQueryService.changeSolvencyIpFinancialSituation({
        userId: this.creditRequest.userId as number,
        body: this.reqFinancialSituation as CreditQueryFinancialSituationIpRequest
      }).subscribe({
        next: () => {
          window.location.reload();
          this.sendMessage('Финансовая ситуация была обновлена')
        },
        error: (err) => {
          if (err.error.validationErrors)
            this.errorMsg = err.error.validationErrors;
          else
            this.errorMsg.push(err.error.error);
        }
      });
    }
  }

  updateAgentInfo() {
    this.closeMessage();
    if (this.creditRequest.type === 'HIRED_WORKERS') {
      this.creditQueryService.changeSolvencyHiredWorkerTaxAgent({
        userId: this.creditRequest.userId as number,
        body: this.reqTaxAgent as CreditQueryTaxAgentHiredWorkerRequest
      }).subscribe({
        next: () => {
          window.location.reload();
          this.sendMessage('Информация о налоговом агенте была обновлена');
        },
        error: (err) => {
          if (err.error.validationErrors)
            this.errorMsg = err.error.validationErrors;
          else
            this.errorMsg.push(err.error.error);
        }
      });
    } else if (this.creditRequest.type === 'INDIVIDUAL_ENTREPRENEUR') {
      this.creditQueryService.changeSolvencyIpTaxAgent({
        userId: this.creditRequest.userId as number,
        body: this.reqTaxAgent as CreditQueryTaxAgentIpRequest
      }).subscribe({
        next: () => {
          window.location.reload();
          this.sendMessage('Информация о налоговом агенте была обновлена');
        },
        error: (err) => {
          if (err.error.validationErrors)
            this.errorMsg = err.error.validationErrors;
          else
            this.errorMsg.push(err.error.error);
        }
      });
    }
  }

  reject() {
    this.router.navigate(['credit-officer', 'credit-request', 'refuse', this.creditRequest.userId]);
  }

  confirm() {
    this.creditQueryService.solvencyVerifyPdf({
      userId: this.creditRequest.userId as number
    }).subscribe({
      next: () => {
        window.location.reload();
      }
    });
  }

  nextConfirmation() {
    this.localStorageService.deleteValueByKey('confirmation-solvency');
    this.router.navigate(['credit-officer', 'credit-request', 'employment-confirmation', this.creditRequestId]);
  }

  completeConfirmation() {
    this.localStorageService.deleteValueByKey('confirmation-solvency');
    this.router.navigate(['credit-officer', 'all-credit-requests']);
  }

  sendForRevision() {
    this.localStorageService.deleteValueByKey('confirmation-employment');
    this.router.navigate(['credit-officer', 'credit-request', 'send-for-revision', this.creditRequest.userId]);
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

  private checkForFormsData() {
    if (this.creditRequest.type === 'HIRED_WORKERS') {
      this.reqTaxAgent = {inn: '', kpp: '', name: ''};
      this.reqFinancialSituation = {monthlyPayment: '', taxCalculationAmount: '', totalIncome: ''};
    } else {
      this.reqTaxAgent = {inn: '', ogrnip: '', name: ''};
      this.reqFinancialSituation = {monthlyPayment: '', totalIncome: ''};
    }
  }

  private loadUploadSolvencyPdf() {
    this.creditQueryService.getSolvencyUploadInformation({
      userId: this.creditRequest.userId as number
    }).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        this['uploadPdfUrl'] = this.sanitizer.bypassSecurityTrustResourceUrl(url);
      },
      error: (err) => {
        if (err.error.validationErrors)
          this.errorMsg = err.error.validationErrors;
        else
          this.errorMsg.push(err.error.error);
      }
    });
  }

  private solvencyConfirmation(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.creditQueryService.confirmationOfSolvencyPdf({
        userId: this.creditRequest.userId as number
      }).subscribe({
        next: () => {
          this.isPossibleToContinue = true;
          resolve();
        },
        error: (err) => {
          if (err.error.validationErrors)
            this.errorMsg = err.error.validationErrors;
          else
            this.errorMsg.push(err.error.error);
          this.isPossibleToContinue = false;
          this.localStorageService.localValue('confirmation-solvency', 'false')
          reject(err);
        }
      })
    });
  }

  private loadSolvencyPdf() {
    this.creditQueryService.getSolvencyInformation({
      userId: this.creditRequest.userId as number
    }).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        this['solvencyPdfUrl'] = this.sanitizer.bypassSecurityTrustResourceUrl(url);
      },
      error: (err) => {
        if (err.error.validationErrors)
          this.errorMsg = err.error.validationErrors;
        else
          this.errorMsg.push(err.error.error);
      }
    });
  }

  private sendMessage(message: string) {
    this.successMessage = message;
  }

  private closeMessage(): void {
    this.successMessage = null;
  }

  isHiredWorkerTaxAgent(
    obj: CreditQueryTaxAgentHiredWorkerRequest | CreditQueryTaxAgentIpRequest
  ): obj is CreditQueryTaxAgentHiredWorkerRequest {
    return (obj as CreditQueryTaxAgentHiredWorkerRequest).kpp !== undefined;
  }

  isHiredWorkerFinancialSituation(
    obj: CreditQueryFinancialSituationHiredWorkerRequest | CreditQueryFinancialSituationIpRequest
  ): obj is CreditQueryFinancialSituationHiredWorkerRequest {
    return (obj as CreditQueryFinancialSituationHiredWorkerRequest).taxCalculationAmount !== undefined;
  }
}
