/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';


export interface ChangeCreditRequest1$Params {
  creditRequestId: number;
      body?: {
'solvency': Blob;
'employment': Blob;
}
}

export function changeCreditRequest1(http: HttpClient, rootUrl: string, params: ChangeCreditRequest1$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
  const rb = new RequestBuilder(rootUrl, changeCreditRequest1.PATH, 'put');
  if (params) {
    rb.path('creditRequestId', params.creditRequestId, {});
    const formData = new FormData();
    formData.append('solvency', params.body?.solvency || '');
    formData.append('employment', params.body?.employment || '');
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

changeCreditRequest1.PATH = '/credit-request/change/by/solvency/and/employment/{creditRequestId}';
