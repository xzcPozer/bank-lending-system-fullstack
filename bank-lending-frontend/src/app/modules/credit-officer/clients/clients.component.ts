import {Component, OnInit} from '@angular/core';
import {PageResponseUserResponse} from "../../../services/models/page-response-user-response";
import {UserService} from "../../../services/services/user.service";
import {UserResponse} from "../../../services/models/user-response";

@Component({
  selector: 'app-clients',
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.scss']
})
export class ClientsComponent implements OnInit {

  r: UserResponse[] = [];
  sortDirection: string = '';
  level: 'success' | 'error' = 'success';
  msg: string = '';
  searchedSerialNumber: string = '';
  response: PageResponseUserResponse = {};
  page: number = 0;
  size: number = 10;
  pages: any = [];
  isSearch = false;

  constructor(
    private userService: UserService,
  ) {
  }

  ngOnInit(): void {
    this.findAllClient();
  }

  private findAllClient() {
    this.userService.getAllClient({
      page: this.page,
      size: this.size
    })
      .subscribe({
        next: (clientResponse) => {
          this.response = clientResponse;
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

  private findAllClientBySerialNumber() {
    this.userService.getUserBySerialNumber({
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

  private findAllBySort(isAsc: boolean) {
    this.userService.getAllClientBySort({
      page: this.page,
      size: this.size,
      isAsc: isAsc
    })
      .subscribe({
        next: (clientResponse) => {
          this.response = clientResponse;
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

  private selectDataType() {
    if(this.sortDirection === 'asc')
      this.findAllBySort(true);
    else if (this.sortDirection === 'desc')
      this.findAllBySort(false);
    else
      this.findAllClient();
  }

  displayPerformedWorks(_curPage: number) {
    this.page = _curPage;
    if (this.isSearch) {
      this.findAllClientBySerialNumber();
      this.sortDirection = '';
    } else {
      this.findAllClient();
      this.sortDirection = '';
    }
  }

  resetSearch() {
    this.page = 0;
    this.findAllClient();
  }

  search() {
    this.page = 0;
    this.findAllClientBySerialNumber();
  }

  handleSort() {
    if (this.sortDirection === '') {
      this.sortDirection = 'asc';
    } else if (this.sortDirection === 'asc') {
      this.sortDirection = 'desc';
    } else {
      this.sortDirection = '';
    }

    this.selectDataType();
  }

  getSortIcon(): string {
    if (this.sortDirection === 'asc') return '↑';
    if (this.sortDirection === 'desc') return '↓';
    return '';
  }
}
