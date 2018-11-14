import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormControl } from '@angular/forms';
import { LoginService } from '../login/login.service';
import { AuthMech, SamlAuthFederation, SamlAuthIdp } from '../data/auth-mech';

import { startWith, map } from 'rxjs/operators';

@Component({
  selector: 'app-login-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  private samlAuthUrl = '/Shibboleth.sso/Login';

  authMechs: AuthMech[];

  myControl: FormControl = new FormControl();

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

      authMech.federation.filteredIdpList = this.myControl.valueChanges.pipe(
        map(val => this.filter(authMech.federation, val))
      );
    });
  }

  login(authMech: AuthMech): void {
    console.log('Login called with ' + authMech.name);

    if (authMech.type === 'LocalUPAuthMech') {
      console.log('Starting Local Username Password authentication');
      this.loginService.postLocalLogin(authMech)
        .then(() => authMech.error = null)
        .catch(error => {
          authMech.error = 'loginFailed';
          authMech.errorDetail = 'Login failed';
        });
    } else if (authMech.type === 'SamlAuthMech') {
      console.log('Starting Saml authentication');
      if (authMech.selectedIdp) {
        console.log('Loggin in via ' + authMech.selectedIdp.displayName);
        window.location.href = `${this.samlAuthUrl}?mech=${authMech.id}&idp=${authMech.selectedIdp.id}`;
      } else {
        console.log('No IDP selected');
      }
    } else {
      console.log('unimplemented authentication type');
    }
  }

  filter(federation: SamlAuthFederation, val: string): SamlAuthIdp[] {
    if (val) {
      return federation.idpList.filter(idp =>
        idp.displayName.toLowerCase().indexOf(val.toLowerCase()) !== -1);
    } else {
      return federation.idpList;
    }
  }

  select(idp: SamlAuthIdp, authMech: AuthMech) {
    console.log('Idp from federation selected: ', idp.id, authMech.federation.id);
    authMech.selectedIdp = idp;
  }

  ngOnInit(): void {
    this.getAuthMechs();
  }
}
