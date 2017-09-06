import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { LoginService } from '../login/login.service';
import { AuthMech, SamlAuthFederation, SamlAuthIdp } from '../data/auth-mech';

@Component({
  selector: 'app-login-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  authMechs: AuthMech[];

  constructor(private loginService: LoginService) { }

  getAuthMechs(): void {
    this.loginService.getAuthMechList().subscribe(authMechs => {
      this.authMechs = authMechs;
      for (const authMech of this.authMechs) {
        if (authMech.type === 'SamlAuthMech') {
          console.log('Getting federation {}', authMech.federationId);
          this.getFederation(authMech);
        }
      }
    });
  }

  getFederation(authMech: AuthMech): void {
    this.loginService.getFederation(authMech.federationId).subscribe(federation => {
      console.log('Promised federation returned: {}', federation.id);
      authMech.federation = federation;
      console.log('Federations array at pos {} returnes: {}', federation.id, authMech.federation.entityId);
    });
  }

  login(authMech: AuthMech): void {
    console.log('Login called with ' + authMech.name);

    if (authMech.type === 'LocalUPAuthMech') {
      console.log('Starting Local Username Password authentication');
      this.loginService.postLocalLogin(authMech)
        .then(() => authMech.error = null )
        .catch(error => {
          authMech.error = 'loginFailed';
          authMech.errorDetail = 'Login failed';
        });
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
