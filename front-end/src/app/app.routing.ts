import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ManageEmployeesComponent } from './manage-employees/manage-employees.component';
import { NewEmployeeComponent } from './manage-employees/new-employee/new-employee.component';
import { AuthGuard } from './auth/auth.guard';
import { HomeComponent } from './home/home.component';
import { ManageUsersComponent } from './manage-users/manage-users.component';
import { NewUserComponent } from './manage-users/new-user/new-user.component';
import { EditUserComponent } from './manage-users/edit-user/edit-user.component';
import { EditEmployeeComponent } from './manage-employees/edit-employee/edit-employee.component';
import { EmployeeDetailComponent } from './manage-employees/employee-detail/employee-detail.component';
import { UserDetailComponent } from './manage-users/user-detail/user-detail.component';
import { ChangePasswordComponent } from './manage-users/change-password/change-password.component';
import { LoginGuard } from './auth/login.guard';
import { PasswordRecoveryComponent } from './password-recovery/password-recovery.component';
import { CodeComponent } from './password-recovery/code/code.component';

const appRoutes: Routes = [
  {
    path: '', component: HomeComponent, canActivate: [AuthGuard]
  },
  {
    path: 'employees',
    component: ManageEmployeesComponent, canActivate: [AuthGuard]
  },
  {
    path: 'employees/new',
    component: NewEmployeeComponent, canActivate: [AuthGuard]
  },
  {
    path: 'employees/edit/:id',
    component: EditEmployeeComponent, canActivate: [AuthGuard]
  },
  {
    path: 'employees/detail/:id',
    component: EmployeeDetailComponent, canActivate: [AuthGuard]
  },
  {
    path: 'users',
    component: ManageUsersComponent, canActivate: [AuthGuard]
  },
  {
    path: 'users/edit/:id',
    component: EditUserComponent, canActivate: [AuthGuard]
  },
  {
    path: 'users/detail/:id',
    component: UserDetailComponent, canActivate: [AuthGuard]
  },
  {
    path: 'users/new',
    component: NewUserComponent, canActivate: [AuthGuard]
  },
  {
    path: 'users/change-password',
    component: ChangePasswordComponent, canActivate: [AuthGuard]
  },
  {
    path: 'login',
    component: LoginComponent, canActivate: [LoginGuard]
  },
  {
    path: 'password-recovery',
    component: PasswordRecoveryComponent, canActivate: [LoginGuard]
  },
  {
    path: 'password-recovery/code',
    component: CodeComponent, canActivate: [LoginGuard]
  },
  {
    path: '**', redirectTo: ''
  }
];
export const AppRoutingModule = RouterModule.forRoot(appRoutes);
