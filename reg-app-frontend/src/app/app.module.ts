import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule, MatCheckboxModule, MatCardModule, MatFormFieldModule,
  MatAutocompleteModule, MatInputModule } from '@angular/material';

import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { AppComponent } from './app.component';
import { IndexComponent } from './index/index.component';
import { ServiceDetailComponent } from './index/service-detail.component';
import { LoginComponent } from './login/login.component';
import { PageNotFoundComponent } from './page-not-found.component';

import { LoginService } from './login/login.service';
import { IndexService } from './index/index.service';
import { CanActivateAuthGuard } from './auth.service';

const appRoutes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'index', component: IndexComponent, canActivate: [CanActivateAuthGuard] },
  { path: 'service/:id', component: ServiceDetailComponent, canActivate: [CanActivateAuthGuard] },
  { path: '', redirectTo: '/index', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent },
];

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    LoginComponent,
    ServiceDetailComponent,
    PageNotFoundComponent
  ],
  imports: [
    BrowserModule,
    FormsModule, ReactiveFormsModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    RouterModule.forRoot(
      appRoutes,
      // { enableTracing: true } <-- debugging purposes only
    ),
    NoopAnimationsModule, MatButtonModule, MatCheckboxModule, MatCardModule, MatInputModule, MatFormFieldModule, MatAutocompleteModule
  ],
  providers: [LoginService, IndexService, CanActivateAuthGuard],
  bootstrap: [AppComponent]
})
export class AppModule { }
