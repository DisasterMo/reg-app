import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { User } from '../data/user';
import { AuthInfo } from '../data/auth-info';
import { AuthMech, SamlAuthFederation, SamlAuthIdp } from '../data/auth-mech';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/catch';

@Injectable()
export class LoginService {

    private authUrl = '/rest/auth/info';
    private authMechUrl = '/rest/auth/mech/list';
    private userUrl = '/rest/user/detail';
    private federationUrl = '/rest/auth/saml/federation/list';
    private localLoginUrl = '/rest/auth/locallogin';

    private headers = new Headers({'Content-Type': 'application/json'});

    constructor(private http: Http) { }

    getAuthInfo(): Promise<AuthInfo> {
        return this.http.get(this.authUrl)
            .map((res: Response) => res.json())
            .toPromise()
            .catch(error => this.handleError(error));
    }

    getAuthMechList(): Observable<AuthMech[]> {
        return this.http.get(this.authMechUrl)
            .map((res: Response) => res.json())
            .catch(error => this.handleError(error));
    }

    getFederation(id: number): Observable<SamlAuthFederation> {
        const url = `${this.federationUrl}/${id}`;
        return this.http.get(url)
            .map((res: Response) => res.json())
            .catch(error => this.handleError(error));
    }

    getUser(): Observable<User> {
        return this.http.get(this.userUrl)
            .map((res: Response) => res.json())
            .catch(error => this.handleError(error));
    }

    postLocalLogin(authMech: AuthMech): Promise<AuthMech> {
        const url = `${this.localLoginUrl}/${authMech.id}`;
        console.log('Posting local login: ' + JSON.stringify(authMech));

        return this.http
            .post(url, JSON.stringify(authMech), { headers: this.headers })
            .toPromise()
            .then(() => authMech);
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
}
