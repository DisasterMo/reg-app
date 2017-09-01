import { Component, OnInit } from '@angular/core';
import { User } from './data/user';
import { AuthInfo } from './data/auth-info';
import { LoginService } from './login/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'Reg-App';
  user: User;
  authInfo: AuthInfo;

  constructor(private loginService: LoginService) { }

  getLoginStatus(): void {
    this.loginService.getAuthInfo().then(authInfo => this.authInfo = authInfo);
  }

  ngOnInit(): void {
    this.getLoginStatus();
  }
}
