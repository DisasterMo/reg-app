import { Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';
import { User } from '../data/user';

import 'rxjs/add/operator/toPromise';

@Injectable()
export class LoginService {

    private userUrl = '/rest/user/detail';

    constructor(private http: Http) { }

    getUser(): Promise<User> {
        return this.http.get(this.userUrl)
            .toPromise()
            .then(response => response.json().data as User)
            .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
}
