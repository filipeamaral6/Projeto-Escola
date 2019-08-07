import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { AuthenticationService } from '../services/authentication.service';
import { Router } from '@angular/router';
import { AlertService } from '../services/alert.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private authenticationService: AuthenticationService, private router: Router, private alertService: AlertService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError(err => {
      let error = err.error;

      if (err.status === 400) {
        error.message = err.error.message.slice(1, err.error.message.length - 1).split(', ');
      }
      else if (err.status === 401) {
        // auto logout if 401 response returned from api
        this.authenticationService.logout();
        this.router.navigate(['/login']);
      }
      // auto logout if 403 response returned from api
      else if (err.status === 403) {
        this.authenticationService.logout();
        this.router.navigate(['/login']);
      }
      else if (err.statusText === 'Unknown Error' && err.name === 'HttpErrorResponse') {
        error.message = "Can't reach server";
      }


      return throwError(error);
    }));
  }
}
