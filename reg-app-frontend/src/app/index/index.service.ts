import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Registry } from '../data/registry';
import { Service } from '../data/service';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class IndexService {

    private registryListUrl = '/rest/user/registry/list';
    private availableServiceListUrl = '/rest/service/available/list';
    private serviceShortUrl = '/rest/service/detail';

    private headers = new HttpHeaders({'Content-Type': 'application/json'});

    constructor(private http: HttpClient) { }

    getRegsitryList(): Observable<Registry[]> {
        return this.http.get<Registry[]>(this.registryListUrl).pipe(
            catchError(error => this.handleError(error))
        );
    }

    getAvailableServiceList(): Observable<Service[]> {
        return this.http.get<Service[]>(this.availableServiceListUrl).pipe(
            catchError(error => this.handleError(error))
        );
    }

    getServiceShort(id: number): Observable<Service> {
        return this.http.get<Service>(`${this.serviceShortUrl}/${id}`).pipe(
            catchError(error => this.handleError(error))
        );
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
}
