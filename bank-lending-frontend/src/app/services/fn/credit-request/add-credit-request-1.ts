/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';


export interface AddCreditRequest1$Params {
      body?: {
'requestStr': string;
'solvency': Blob;
'employment': Blob;
}
}

export function addCreditRequest1(http: HttpClient, rootUrl: string, params?: AddCreditRequest1$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
  const rb = new RequestBuilder(rootUrl, addCreditRequest1.PATH, 'post');
  if (params && params.body) {
    const formData = new FormData();
    formData.append('requestStr', params.body.requestStr);
    formData.append('solvency', params.body.solvency);
    formData.append('employment', params.body.employment);
    rb.body(formData);
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

addCreditRequest1.PATH = '/credit-request/add/by/hired-worker';
