import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { LoginService } from './login/login.service';
import { AuthInfo } from './data/auth-info';

@Injectable()
export class CanActivateAuthGuard implements CanActivate {

    constructor(private loginService: LoginService) { }

    canActivate() {
        return this.loginService.getAuthInfo().then((authInfo: AuthInfo) => {
            if (authInfo.loggedIn) {
                return true;
            } else {
                return false;
            }
        });
    }
}
