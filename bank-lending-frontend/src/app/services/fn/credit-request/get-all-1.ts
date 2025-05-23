/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PageResponseCreditRequestResponse } from '../../models/page-response-credit-request-response';

export interface GetAll1$Params {
  page?: number;
  size?: number;
  isProcessed: boolean;
}

export function getAll1(http: HttpClient, rootUrl: string, params: GetAll1$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseCreditRequestResponse>> {
  const rb = new RequestBuilder(rootUrl, getAll1.PATH, 'get');
  if (params) {
    rb.query('page', params.page, {});
    rb.query('size', params.size, {});
    rb.query('isProcessed', params.isProcessed, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<PageResponseCreditRequestResponse>;
    })
  );
}

getAll1.PATH = '/credit-request/all-requests/by/processed';
