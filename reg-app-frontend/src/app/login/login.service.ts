import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { User } from '../data/user';
import { AuthInfo } from '../data/auth-info';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/catch';

@Injectable()
export class LoginService {

    private authUrl = '/rest/auth/info';
    private userUrl = '/rest/user/detail';
    
    constructor(private http: Http) { }

    getAuthInfo(): Promise<AuthInfo> {
        return this.http.get(this.authUrl)
            .map((res: Response) => res.json())
            .toPromise()
            .catch(error => this.handleError(error));
    }

    getUser(): Observable<User> {
        return this.http.get(this.userUrl)
            .map((res: Response) => res.json())
            .catch((error: any) => Observable.throw(error.json().error || 'Server error'));
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
}
