import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  standalone: true,
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent {
  @Output() private _page = new EventEmitter<number>();

  private _curPage = 0;
  private _pages = 0;

  @Input()
  set currentPage(value: number) {
    this._curPage = value;
  }

  @Input()
  set totalPages(value: number) {
    this._pages = value;
  }

  get currentPage(): number {
    return this._curPage;
  }

  get totalPages(): number {
    return this._pages;
  }

  prevPage() {
    if (this._curPage > 0) {
      this._curPage--;
      this._page.emit(this._curPage);
    }
  }

  nextPage() {
    if(this._pages){
      if (this._curPage < this._pages - 1 ) {
        this._curPage++;
        this._page.emit(this._curPage);
      }
    }
  }
}
