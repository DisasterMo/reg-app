import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { IndexService } from './index.service';
import { Registry } from '../data/registry';
import { Observable } from 'rxjs/Observable';

@Component({
    selector: 'app-index-index',
    templateUrl: './index.component.html',
    styleUrls: ['./index.component.css']
  })
  export class IndexComponent implements OnInit {

    constructor(private indexService: IndexService) { }

    registries: Registry[];

    getRegistries(): void {
      this.indexService.getRegsitryList().subscribe(registries => {
        this.registries = registries;
        for (const registry of this.registries) {
          console.log('Got registry: ', registry.id);
        }
      });
    }

    ngOnInit(): void {
      this.getRegistries();
    }
  }
