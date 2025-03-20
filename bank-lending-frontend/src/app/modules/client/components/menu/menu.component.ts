import {AfterViewInit, Component, ElementRef, OnInit, Renderer2} from '@angular/core';
import {NavigationEnd, Router, RouterModule} from "@angular/router";
import {filter} from "rxjs/operators";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  standalone: true,
  imports: [RouterModule],
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit, AfterViewInit{

  constructor(
    private router: Router,
    private elementRef: ElementRef,
    private renderer: Renderer2
  ) {
  }


  setActiveLinks(): void {
    const links = this.elementRef.nativeElement.querySelectorAll('.nav-link');
    const currentUrl = this.router.url;

    links.forEach((link: HTMLElement) => {
      const linkUrl = link.getAttribute('routerLink');
      if (linkUrl && currentUrl.includes(linkUrl)) {
        this.renderer.addClass(link, 'active');
      } else {
        this.renderer.removeClass(link, 'active');
      }
    });
  }

  ngAfterViewInit(): void {
    this.setActiveLinks();
  }

  ngOnInit(): void {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.setActiveLinks();
    });
  }

}
