import { Component } from '@angular/core';
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {CreditQueryService} from "../../../services/services/credit-query.service";

@Component({
  selector: 'app-send-for-revision',
  templateUrl: './send-for-revision.component.html',
  styleUrls: ['./send-for-revision.component.scss']
})
export class SendForRevisionComponent {

  errorMsg: Array<string> = [];
  description: string = '';
  userId: any;

  constructor(
    private location: Location,
    private router: Router,
    private creditQueryService: CreditQueryService,
    private activatedRoute: ActivatedRoute
  ) {
  }

  send() {
    this.userId = this.activatedRoute.snapshot.params['userId'];
    this.creditQueryService.sendForRevisionRequest({
      userId: this.userId,
      body: {
        description: this.description
      }
    }).subscribe({
      next: () => {
        this.router.navigate(['credit-officer', 'all-credit-requests']);
      }
    })
  }

  cancel() {
    this.location.back();
  }
}
