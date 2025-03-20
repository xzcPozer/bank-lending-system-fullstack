import {Component} from '@angular/core';
import {CreditQueryService} from "../../../services/services/credit-query.service";
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {LocalStorageService} from "../../../services/local-storage/local-storage.service";

@Component({
  selector: 'app-refuse',
  templateUrl: './refuse.component.html',
  styleUrls: ['./refuse.component.scss']
})
export class RefuseComponent {

  errorMsg: Array<string> = [];
  description: string = '';
  userId: any;

  constructor(
    private location: Location,
    private router: Router,
    private creditQueryService: CreditQueryService,
    private activatedRoute: ActivatedRoute,
    private localStorageService: LocalStorageService
  ) {
  }

  send() {
    this.userId = this.activatedRoute.snapshot.params['userId'];
    this.creditQueryService.sendRefuseRequest({
      userId: this.userId,
      body: {
        description: this.description
      }
    }).subscribe({
      next: () => {
        this.localStorageService.deleteValueByKey('confirmation-solvency');
        this.router.navigate(['credit-officer', 'all-credit-requests']);
      }
    })
  }

  cancel() {
    this.location.back();
  }
}
