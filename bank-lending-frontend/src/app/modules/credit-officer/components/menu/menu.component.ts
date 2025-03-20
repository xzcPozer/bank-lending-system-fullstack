import {AfterViewInit, Component, ElementRef, OnInit, Renderer2} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {filter} from "rxjs/operators";
import {TokenService} from "../../../../services/token/token.service";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit, AfterViewInit {

  constructor(
    private router: Router,
    private elementRef: ElementRef,
    private renderer: Renderer2,
    private tokenService: TokenService
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

  logout() {
    this.tokenService.logout();
    this.router.navigate(['login'])
  }
}
