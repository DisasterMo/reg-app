import { Component, OnInit } from '@angular/core';
import { LoginService } from '../login/login.service';
import { AuthMech } from '../data/auth-mech';

@Component({
    selector: 'app-login-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  authMechs: AuthMech[];

  constructor(private loginService: LoginService) { }

  getAuthMechs(): void {
    this.loginService.getAuthMechList().then(authMechs => this.authMechs = authMechs);
  }

  login(authMech: AuthMech): void {
    console.log('Login called with ' + authMech.name);

    if (authMech.type === 'LocalUPAuthMech') {
      console.log('Starting Local Username Password authentication');
      this.loginService.postLocalLogin(authMech);
    } else if (authMech.type === 'SamlAuthMech') {
      console.log('Starting Saml authentication');
    } else {
      console.log('unimplemented authentication type');
    }
  }

  ngOnInit(): void {
    this.getAuthMechs();
  }
}
