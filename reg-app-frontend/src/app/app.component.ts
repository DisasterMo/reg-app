import { Component, OnInit } from '@angular/core';
import { User } from './data/user';
import { LoginService } from './login/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'Reg-App';
  user: User;

  constructor(private loginService: LoginService) { }

  getLogin(): void {
    this.loginService.getUser().then(user => this.user = user);
  }
  ngOnInit(): void {
    this.getLogin();
  }
}
