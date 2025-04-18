/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { CreditQueryTaxAgentIpRequest } from '../../models/credit-query-tax-agent-ip-request';

export interface ChangeSolvencyIpTaxAgent$Params {
  userId: number;
      body: CreditQueryTaxAgentIpRequest
}

export function changeSolvencyIpTaxAgent(http: HttpClient, rootUrl: string, params: ChangeSolvencyIpTaxAgent$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
  const rb = new RequestBuilder(rootUrl, changeSolvencyIpTaxAgent.PATH, 'put');
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

changeSolvencyIpTaxAgent.PATH = '/credit-query/change/solvency/ip/tax-agent-info/{userId}';
