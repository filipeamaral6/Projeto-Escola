import { Component, OnInit, Input } from '@angular/core';
import { Employee } from 'src/app/models/employee';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeeService } from 'src/app/services/employee.service';
import { first } from 'rxjs/operators';
import { AlertService } from 'src/app/services/alert.service';
import { UtilsService } from 'src/app/services/utils.service';
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/app/models/user';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-employee-detail',
  templateUrl: './employee-detail.component.html',
  styleUrls: ['./employee-detail.component.css']
})
export class EmployeeDetailComponent implements OnInit {

  @Input() id: number;
  employee: Employee;
  isLoading = false;
  dayToString: string;
  yearToString: string;
  monthToString: string;
  user: User;
  isUser = true;
  currentUser: User;
  isTakingAWhile = false;
  isActive = true;
  isDisabled = false;

  // tslint:disable: align
  constructor(private route: ActivatedRoute, private employeeService: EmployeeService,
    private alertService: AlertService,
    private utilsService: UtilsService, private userService: UserService,
    private authenticationService: AuthenticationService) {
    this.currentUser = this.authenticationService.currentUserValue;
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.id = +params.get('id');
      this.fetchEmployeeById();

    }
    );
  }

  fetchEmployeeById() {
    this.employeeService.getById(this.id)
      .pipe(first())
      .subscribe(employee => {
        this.employee = employee;
        console.log(this.employee);
        this.getUserByUsername();
        this.isEmployeeActive();
      });
  }

  assignUserRole(employee: Employee) {
    this.isTakingAWhile = false;
    this.alertService.clear();
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    this.isLoading = true;
    let randomPassword = this.utilsService.generatePassword(8);
    let userToJSON = JSON.parse(JSON.stringify({
      "firstName": employee.firstName,
      "lastName": employee.lastName,
      "username": employee.username,
      "password": randomPassword,
      "personalEmail": employee.personalEmail,
      "role": ['ROLE_USER']
    }));
    this.userService.addUser(userToJSON).subscribe(data => {
      this.alertService.success('User role assigned successfully', true);
      setTimeout(() => { this.alertService.clear(); }, 2000);
      this.isLoading = false;
      this.isTakingAWhile = false;
      this.fetchEmployeeById();
    },
      error => {
        this.alertService.error(error.message);
        this.isLoading = false;
        this.isTakingAWhile = false;
        this.fetchEmployeeById();
      });
  }

  getUserByUsername() {
    if (this.currentUser.authorities[0].authority === 'ROLE_ADMIN') {
      this.userService.getByUsername(this.employee.username)
        .pipe(first())
        .subscribe(user => {
          this.user = user;
          if (user) {
            this.isUser = true;
          }
          this.isUser = false;
        });
    }
  }

  isEmployeeActive() {
    if (this.employee.status === 'INACTIVE') {
      this.isActive = false;
    } else {
      this.isActive = true;
    }
  }


  onDeleteEmployee(id: number, name: string) {
    this.isTakingAWhile = false;
    this.alertService.clear();
    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    let areYouSure = confirm('Are you sure you want to set ' + name + ' to inactive?');
    if (areYouSure) {
      this.employeeService.deleteEmployee(id).subscribe(data => {
        this.alertService.success('Employee status was set to inactive successfully', true);
        setTimeout(() => { this.alertService.clear(); }, 2000);
        this.isTakingAWhile = false;
        this.isLoading = false;
        this.fetchEmployeeById();

      },
        error => {
          this.alertService.error(error.message);
          this.isTakingAWhile = false;
          this.isLoading = false;
          this.fetchEmployeeById();
        });
    } else {
      this.isLoading = false;
      this.isTakingAWhile = false;
    }
  }

  onSlackInvite(id: number) {
    this.alertService.clear();
    this.isTakingAWhile = false;
    this.isLoading = true;
    setTimeout(() => {
      this.isLoading = false;
      this.alertService.success('Slack invite was sent successfully', true);
      this.fetchEmployeeById();
    }, 2000);
    setTimeout(() => { this.alertService.clear(); }, 4000);
    let idToJSON = JSON.parse(JSON.stringify({
      "id": id
    }));
    this.employeeService.inviteSlack(idToJSON).subscribe(data => {
    },
      error => {
        this.alertService.error(error.message);
        this.fetchEmployeeById();
      });
  }

  onOffice365Invite(employee: Employee) {
    this.isTakingAWhile = false;
    this.alertService.clear();
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    this.isLoading = true;
    let randomPassword = this.utilsService.generatePassword(8);
    let employeeToJson = JSON.parse(JSON.stringify({
      "email": employee.email,
      "firstName": employee.firstName,
      "lastName": employee.lastName,
      "username": employee.username,
      "password": randomPassword,
      "personalEmail": employee.personalEmail
    }));
    this.employeeService.inviteOffice365(employeeToJson).subscribe(data => {
      this.alertService.success('Office 365 invite was sent successfully', true);
      setTimeout(() => { this.alertService.clear(); }, 2000);
      this.isLoading = false;
      this.isTakingAWhile = false;
      this.fetchEmployeeById();
    },
      error => {
        this.alertService.error(error.message);
        this.isLoading = false;
        this.isTakingAWhile = false;
        this.fetchEmployeeById();
      });
  }

  onIntranetInvite(employee: Employee) {
    this.isTakingAWhile = false;
    this.alertService.clear();
    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    let employeeToJson = JSON.parse(JSON.stringify(employee));
    this.employeeService.inviteIntranet(employeeToJson).subscribe(data => {
      this.alertService.success('Intranet invite was sent successfully', true);
      setTimeout(() => { this.alertService.clear(); }, 2000);
      this.isLoading = false;
      this.isTakingAWhile = false;
      this.fetchEmployeeById();
    },
      error => {
        this.alertService.error(error.message);
        this.isLoading = false;
        this.isTakingAWhile = false;
        this.fetchEmployeeById();
      });
  }

  setActive(id: number) {
    this.isTakingAWhile = false;
    this.alertService.clear();
    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    this.employeeService.setActive(id).subscribe(data => {
      this.alertService.success('Employee status was set to active successfully', true);
      setTimeout(() => { this.alertService.clear(); }, 2000);
      this.isTakingAWhile = false;
      this.isLoading = false;
      this.fetchEmployeeById();
    },
      error => {
        this.alertService.error(error.message);
        this.isTakingAWhile = false;
        this.isLoading = false;
        this.fetchEmployeeById();
      });
  }
}
