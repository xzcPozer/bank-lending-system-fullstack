import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {CreditRequestResponse} from "../../../services/models/credit-request-response";
import {CreditQueryService} from "../../../services/services/credit-query.service";
import {CreditRequestService} from "../../../services/services/credit-request.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LocalStorageService} from "../../../services/local-storage/local-storage.service";

@Component({
  selector: 'app-employment-confirmation',
  templateUrl: './employment-confirmation.component.html',
  styleUrls: ['./employment-confirmation.component.scss']
})
export class EmploymentConfirmationComponent implements OnInit{

  errorMsg: Array<string> = [];
  uploadPdfUrl: SafeResourceUrl;
  employmentPdfUrl: SafeResourceUrl;
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
    this.uploadPdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl('about:blank');
    this.employmentPdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl('about:blank');
  }

  async ngOnInit() {
    this.isPossibleToContinue = false;
    this.errorMsg = [];
    this.getCreditRequest()
      .then(() => {
        if(this.creditRequest.type === 'HIRED_WORKERS')
          this.loadUploadEmploymentPdf();
        return Promise.resolve();
      })
      .then(() => {
        if (this.localStorageService.getLocalValue('confirmation-employment') === null || this.localStorageService.getLocalValue('confirmation-employment') === 'false') {
          this.employmentConfirmation()
            .then(()=>{
              this.loadEmploymentPdf()
              return Promise.resolve();
            })
          this.localStorageService.localValue('confirmation-employment', 'true')
        } else {
          this.isPossibleToContinue = true;
          this.loadEmploymentPdf()
        }
      })
      .catch((error) => {
        console.error('Error in ngOnInit:', error);
      });
  }

  reject() {
    this.localStorageService.deleteValueByKey('confirmation-employment');
    this.router.navigate(['credit-officer', 'credit-request', 'refuse', this.creditRequest.userId]);
  }

  confirm() {
    this.creditQueryService.employmentVerifyPdf({
      userId: this.creditRequest.userId as number
    }).subscribe({
      next: () => {
        this.sendMessage('Отчет о доходах подтвержден')
        window.location.reload();
      }
    });
  }

  nextConfirmation() {
    this.localStorageService.deleteValueByKey('confirmation-employment');
    this.router.navigate(['credit-officer', 'credit-request', 'credit-query-confirmation', this.creditRequestId]);
  }

  completeConfirmation() {
    this.localStorageService.deleteValueByKey('confirmation-employment');
    this.router.navigate(['credit-officer','all-credit-requests']);
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

  private loadUploadEmploymentPdf() {
    this.creditQueryService.getPaymentUploadInformation({
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

  private employmentConfirmation(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.creditQueryService.confirmationOfEmploymentPdf({
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
          this.localStorageService.localValue('confirmation-employment', 'false');
          reject(err);
        }
      });
    });
  }

  private loadEmploymentPdf() {
    this.creditQueryService.getPaymentInformation({
      userId: this.creditRequest.userId as number
    }).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        this['employmentPdfUrl'] = this.sanitizer.bypassSecurityTrustResourceUrl(url);
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
}
