import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Registry } from '../data/registry';
import { Service } from '../data/service';
import { Observable } from 'rxjs/Observable';
import { map } from 'rxjs/operators';

@Injectable()
export class IndexService {

    private registryListUrl = '/rest/user/registry/list';
    private availableServiceListUrl = '/rest/service/available/list';
    private serviceShortUrl = '/rest/service/detail';

    private headers = new Headers({'Content-Type': 'application/json'});

    constructor(private http: Http) { }

    getRegsitryList(): Observable<Registry[]> {
        return this.http.get(this.registryListUrl)
            .map((res: Response) => res.json())
            .catch(error => this.handleError(error));
    }

    getAvailableServiceList(): Observable<Service[]> {
        return this.http.get(this.availableServiceListUrl)
            .map((res: Response) => res.json())
            .catch(error => this.handleError(error));
    }

    getServiceShort(id: number): Observable<Service> {
        return this.http.get(`${this.serviceShortUrl}/${id}`)
            .map((res: Response) => res.json())
            .catch(error => this.handleError(error));
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }
}
