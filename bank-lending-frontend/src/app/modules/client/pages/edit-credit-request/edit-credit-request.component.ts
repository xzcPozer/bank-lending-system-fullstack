import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CreditRequestService} from "../../../../services/services/credit-request.service";

@Component({
  selector: 'app-edit-credit-request',
  templateUrl: './edit-credit-request.component.html',
  styleUrls: ['./edit-credit-request.component.scss']
})
export class EditCreditRequestComponent implements OnInit {

  errorMsg: Array<string> = [];
  solvencyFile: Blob | null = null;
  employmentFile: Blob | null = null;

  is2ndfl: boolean = false;
  creditRequestId: any;

  constructor(
    private router: Router,
    private creditRequestService: CreditRequestService,
    private activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.creditRequestId = this.activatedRoute.snapshot.params['creditRequestId'];
    this.creditRequestService.getCreditRequestById({
      creditRequestId: this.creditRequestId
    }).subscribe({
      next: (req) => {
        if (req.type === 'HIRED_WORKERS')
          this.is2ndfl = true;
      },
      error: (err) => {
        if (err.error.validationErrors)
          this.errorMsg = err.error.validationErrors;
        else
          this.errorMsg.push(err.error.error);
      }
    })
  }

  onSolvencySelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0
    ) {
      this.solvencyFile = input.files[0];
    }
  }

  onEmploymentSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0
    ) {
      this.employmentFile = input.files[0];
    }
  }

  send() {
    if (this.is2ndfl) {
      this.creditRequestService.changeCreditRequest1({
        creditRequestId: this.creditRequestId,
        body: {
          solvency: this.solvencyFile as Blob,
          employment: this.employmentFile as Blob
        }
      }).subscribe({
        next: () => {
          this.router.navigate(['client', 'profile']);
        },
        error: (err) => {
          if (err.error.validationErrors)
            this.errorMsg = err.error.validationErrors;
          else
            this.errorMsg.push(err.error.error);
        }
      });
    } else {
      this.creditRequestService.changeCreditRequest({
        creditRequestId: this.creditRequestId,
        body: {
          solvency: this.solvencyFile as Blob
        }
      }).subscribe({
        next: () => {
          this.router.navigate(['client', 'profile']);
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

  cancel() {
    this.router.navigate(['client', 'profile']);
  }
}
