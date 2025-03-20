/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { CreditQueryFinancialSituationHiredWorkerRequest } from '../../models/credit-query-financial-situation-hired-worker-request';

export interface ChangeSolvencyHiredWorkerFinancialSituation$Params {
  userId: number;
      body: CreditQueryFinancialSituationHiredWorkerRequest
}

export function changeSolvencyHiredWorkerFinancialSituation(http: HttpClient, rootUrl: string, params: ChangeSolvencyHiredWorkerFinancialSituation$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
  const rb = new RequestBuilder(rootUrl, changeSolvencyHiredWorkerFinancialSituation.PATH, 'put');
  if (params) {
    rb.path('userId', params.userId, {});
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return (r as HttpResponse<any>).clone({ body: parseFloat(String((r as HttpResponse<any>).body)) }) as StrictHttpResponse<number>;
    })
  );
}

changeSolvencyHiredWorkerFinancialSituation.PATH = '/credit-query/change/solvency/hired-worker/financial-situation/{userId}';
