import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Location } from '@angular/common';

import { IndexService } from './index.service';
import { Service } from '../data/service';
import { switchMap } from 'rxjs/operators';

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
    this.service = this.route.paramMap.pipe<Service>(
      switchMap((params: ParamMap) => this.indexService.getServiceShort(params.get('id')))
    );
  }

  toIndex(): void {
    this.router.navigate(['/index']);
  }
}
