import {Component, OnInit} from '@angular/core';
import {ClientInformationService} from "../../../services/services/client-information.service";
import {
  PageResponseClientInformationResponse
} from "../../../services/models/page-response-client-information-response";
import {ClientInformationResponse} from "../../../services/models/client-information-response";

@Component({
  selector: 'app-client-info',
  templateUrl: './client-info.component.html',
  styleUrls: ['./client-info.component.scss']
})
export class ClientInfoComponent implements OnInit {

  r: ClientInformationResponse[] = [];
  sortDirection: string = '';
  level: 'success' | 'error' = 'success';
  msg: string = '';
  searchedSerialNumber: string = '';
  response: PageResponseClientInformationResponse = {};
  page: number = 0;
  size: number = 10;
  pages: any = [];
  isSearch = false;

  constructor(
    private clientInfoService: ClientInformationService,
  ) {
  }

  ngOnInit(): void {
    this.findAllClientInfo();
  }

  private findAllClientInfo() {
    this.clientInfoService.getAllClientInfo({
      page: this.page,
      size: this.size
    })
      .subscribe({
        next: (clientInfoResponse) => {
          this.response = clientInfoResponse;
          this.pages = this.response.totalPages;
          if (this.pages == 0)
            this.pages = 1;
        },
        error: (err) => {
          this.level = 'error';
          this.msg = err.error.error;
        }
      });
  }

  private findAllClientInfoBySerialNumber() {
    this.clientInfoService.getClientInfoBySerialNumber({
      serialNum: this.searchedSerialNumber
    })
      .subscribe({
        next: (res) => {
          this.r.push(res);
          this.response.content = this.r;
          this.pages = 1;
          this.r = [];
        },
        error: (err) => {
          this.level = 'error';
          this.msg = err.error.error;
        }
      });
  }

  displayPerformedWorks(_curPage: number) {
    this.page = _curPage;
    if (this.isSearch) {
      this.findAllClientInfoBySerialNumber();
      this.sortDirection = '';
    } else {
      this.findAllClientInfo();
      this.sortDirection = '';
    }
  }

  resetSearch() {
    this.page = 0;
    this.findAllClientInfo();
  }

  search() {
    this.page = 0;
    this.findAllClientInfoBySerialNumber();
  }
}
