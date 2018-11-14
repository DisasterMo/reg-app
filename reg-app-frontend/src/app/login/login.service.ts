import { User } from '../data/user';
import { AuthInfo } from '../data/auth-info';
import { AuthMech, SamlAuthFederation, SamlAuthIdp } from '../data/auth-mech';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { Injectable } from '@angular/core';

@Injectable()
export class LoginService {

    private authUrl = '/rest/auth/info';
    private authMechUrl = '/rest/auth/mech/list';
    private userUrl = '/rest/user/detail';
    private federationUrl = '/rest/auth/saml/federation/list';
    private localLoginUrl = '/rest/auth/locallogin';

    private headers = new HttpHeaders({'Content-Type': 'application/json'});

    constructor(private http: HttpClient) { }

    getAuthInfo(): Promise<AuthInfo> {
        return this.http.get(this.authUrl).pipe(
                map((res: Response) => res.json()),
                catchError(error => this.handleError(error))
            ).toPromise();
    }

    getAuthMechList(): Observable<AuthMech[]> {
        return this.http.get<AuthMech[]>(this.authMechUrl).pipe(
                catchError(error => this.handleError(error))
            );
    }

    getFederation(id: number): Observable<SamlAuthFederation> {
        const url = `${this.federationUrl}/${id}`;
        return this.http.get<SamlAuthFederation>(url).pipe(
                catchError(error => this.handleError(error))
            );
    }

    getUser(): Observable<User> {
        return this.http.get<User>(this.userUrl).pipe(
                catchError(error => this.handleError(error))
            );
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
