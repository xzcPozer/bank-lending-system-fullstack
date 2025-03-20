import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {TokenService} from "../token/token.service";

export const authGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);
  const requiredRole = route.data['role'] as string[];

  if (tokenService.isTokenNotValid()) {
    if (requiredRole.includes('ROLE_CLIENT'))
      router.navigate(['client', 'login-in-profile']);
    else if(requiredRole.includes('ROLE_CREDIT_OFFICER') || requiredRole.includes('ROLE_DIRECTOR') )
      router.navigate(['login'])
    return false;
  } else
    return tokenService.hasAnyRole(requiredRole);
};
