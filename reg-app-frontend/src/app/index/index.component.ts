import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { IndexService } from './index.service';
import { Registry } from '../data/registry';
import { Service } from '../data/service';
import { Observable } from 'rxjs/Observable';

@Component({
    selector: 'app-index-index',
    templateUrl: './index.component.html',
    styleUrls: ['./index.component.css']
  })
  export class IndexComponent implements OnInit {

    constructor(private indexService: IndexService) { }

    registries: Registry[];
    availableServices: Service[];

    getRegistries(): void {
      this.indexService.getRegsitryList().subscribe(registries => {
        this.registries = registries;
        for (const registry of this.registries) {
          console.log('Got registry: ', registry.id);
        }
      });
    }

    getAvailableServices(): void {
      this.indexService.getAvailableServiceList().subscribe(availableServices => {
        this.availableServices = availableServices;
        for (const service of this.availableServices) {
          console.log('Got available: ', service.name);
        }
      });
    }

    ngOnInit(): void {
      this.getRegistries();
      this.getAvailableServices();
    }
  }
