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

  ngOnInit(): void {
    this.getAuthMechs();
  }
}
