import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppComponent } from './app.component';

import { LoginComponent } from './login/login.component';
import { LoadingSpinnerComponent } from './shared/loading-spinner/loading-spinner.component';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from './app.routing';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { AlertComponent } from './shared/alert/alert.component';
import { HomeComponent } from './home/home.component';
import { ManageUsersComponent } from './manage-users/manage-users.component';
import { NewUserComponent } from './manage-users/new-user/new-user.component';
import { ManageEmployeesComponent } from './manage-employees/manage-employees.component';
import { NewEmployeeComponent } from './manage-employees/new-employee/new-employee.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EditUserComponent } from './manage-users/edit-user/edit-user.component';
import { EditEmployeeComponent } from './manage-employees/edit-employee/edit-employee.component';
import { FilterPipe } from './shared/filter.pipe';
import { EmployeeDetailComponent } from './manage-employees/employee-detail/employee-detail.component';
import { UserDetailComponent } from './manage-users/user-detail/user-detail.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { MyDatePickerModule } from 'mydatepicker';
import { ChangePasswordComponent } from './manage-users/change-password/change-password.component';
import { Globals } from './shared/globals';
import { PasswordRecoveryComponent } from './password-recovery/password-recovery.component';
import { CodeComponent } from './password-recovery/code/code.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ManageUsersComponent,
    NewUserComponent,
    LoadingSpinnerComponent,
    AlertComponent,
    HomeComponent,
    ManageEmployeesComponent,
    NewEmployeeComponent,
    EditUserComponent,
    EditEmployeeComponent,
    FilterPipe,
    EmployeeDetailComponent,
    UserDetailComponent,
    ChangePasswordComponent,
    PasswordRecoveryComponent,
    CodeComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    NgxPaginationModule,
    MyDatePickerModule

  ],
  exports: [
    FilterPipe
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    /* { provide: HTTP_INTERCEPTORS, useClass: BasicAuthInterceptor, multi: true }, */
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    Globals
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
