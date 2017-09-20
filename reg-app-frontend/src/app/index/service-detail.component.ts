import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Location } from '@angular/common';

import { IndexService } from './index.service';
import { Service } from '../data/service';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/switchMap';

@Component({
  selector: 'app-index-service-detail',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class ServiceDetailComponent implements OnInit {

  constructor(
    private indexService: IndexService,
    private route: ActivatedRoute,
    private location: Location
  ) { }

  service: Service;

  ngOnInit(): void {
    this.route.paramMap.switchMap(
      (params: ParamMap) => this.indexService.getServiceShort(+params.get('id')))
      .subscribe(service => this.service = service);
  }
}
