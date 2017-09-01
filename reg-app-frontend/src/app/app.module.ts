import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { IndexComponent } from './index/index.component';
import { LoginComponent } from './login/login.component';
import { PageNotFoundComponent } from './page-not-found.component';

import { LoginService } from './login/login.service';
import { CanActivateAuthGuard } from './auth.service';

const appRoutes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'index', component: IndexComponent, canActivate: [CanActivateAuthGuard] },
  { path: '', redirectTo: '/index', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    LoginComponent,
    PageNotFoundComponent
  ],
  imports: [
    BrowserModule,
    HttpModule,
    RouterModule.forRoot(
      appRoutes,
      // { enableTracing: true } <-- debugging purposes only
    )
  ],
  providers: [LoginService, CanActivateAuthGuard],
  bootstrap: [AppComponent]
})
export class AppModule { }
