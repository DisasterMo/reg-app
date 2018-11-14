import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Location } from '@angular/common';

import { IndexService } from './index.service';
import { Service } from '../data/service';
import { switchMap, map } from 'rxjs/operators';

@Component({
  selector: 'app-index-service-detail',
  templateUrl: './service-detail.component.html',
  styleUrls: ['./service-detail.component.css']
})
export class ServiceDetailComponent implements OnInit {

  constructor(
    private indexService: IndexService,
    private route: ActivatedRoute,
    private location: Location,
    private router: Router
  ) { }

  service: Service;

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap((params: ParamMap) => this.indexService.getServiceShort(+params.get('id')))
    ).subscribe(
      (service) => this.service = service
    );
  }

  toIndex(): void {
    this.router.navigate(['/index']);
  }
}
