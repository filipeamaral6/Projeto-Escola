import { Component, OnInit } from '@angular/core';
import { first } from 'rxjs/operators';
import { Router, ActivatedRoute } from '@angular/router';
import { EmployeeService } from '../services/employee.service';
import { AuthenticationService } from '../services/authentication.service';
import { Employee } from '../models/employee';
import { AlertService } from '../services/alert.service';
import { ExportToCsv } from 'export-to-csv';
import { EmployeeToCsv } from '../models/EmployeeToCsv';
import { User } from '../models/user';

const options = {
  fieldSeparator: ',',
  quoteStrings: '"',
  decimalSeparator: '.',
  showLabels: true,
  showTitle: true,
  title: 'Users CSV',
  useBom: true,
  useKeysAsHeaders: true,
};

@Component({
  selector: 'app-manage-employees',
  templateUrl: './manage-employees.component.html',
  styleUrls: ['./manage-employees.component.css']
})
export class ManageEmployeesComponent implements OnInit {

  employees: Employee[] = [];
  inactiveEmployees: Employee[] = [];
  employeesToCsv: EmployeeToCsv[] = [];
  employeeToCsv: EmployeeToCsv;
  isLoading = true;
  searchString: string;
  currentUser: User;
  isTakingAWhile = false;

  // tslint:disable-next-line: max-line-length
  constructor(private employeeService: EmployeeService, private router: Router, private route: ActivatedRoute, private authenticationService: AuthenticationService, private alertService: AlertService) {
    this.currentUser = this.authenticationService.currentUserValue;
  }

  ngOnInit() {
    this.fetchEmployees();
  }

  fetchActiveEmployees(event) {
    if (event.target.checked === true) {
      this.employees = [];
      if (this.currentUser.authorities[0].authority === 'ROLE_ADMIN') {
        this.employeeService.getAll().pipe(first()).subscribe(employees => {
          employees.forEach(employee => {
            if (employee.status === 'ACTIVE') {
              this.employees.push(employee);
              this.isLoading = false;
            }
          });
        },
          error => {
            this.employees = [];
            this.alertService.error(error.message);
            this.isLoading = false;
          });
      }
    } else {
      this.fetchEmployees();
    }
  }

  fetchInactiveEmployees(event) {
    if (event.target.checked === true) {
      this.employees = [];
      if (this.currentUser.authorities[0].authority === 'ROLE_ADMIN') {
        this.employeeService.getAll().pipe(first()).subscribe(employees => {
          employees.forEach(employee => {
            if (employee.status === 'INACTIVE') {
              this.employees.push(employee);
              this.isLoading = false;
            }
          });
        },
          error => {
            this.employees = [];
            this.alertService.error(error.message);
            this.isLoading = false;
          });
      }
    } else {
      this.fetchEmployees();
    }
  }

  fetchEmployees() {
    this.employees = [];
    if (this.currentUser.authorities[0].authority === 'ROLE_ADMIN') {
      this.employeeService.getAll().pipe(first()).subscribe(employees => {
        employees.forEach(employee => {
          if (employee.status === 'ACTIVE') {
            this.employees.push(employee);
            this.isLoading = false;
          }
        });
      },
        error => {
          this.employees = [];
          this.alertService.error(error.message);
          this.isLoading = false;
        });
    }
    if (this.currentUser.authorities[0].authority === 'ROLE_USER') {
      this.employeeService.getActive().pipe(first()).subscribe(employees => {
        employees.forEach(employee => {
          if (employee.status === 'ACTIVE') {
            this.employees.push(employee);
            this.isLoading = false;
          }
        });
      },
        error => {
          this.employees = [];
          this.alertService.error(error.message);
          this.isLoading = false;
        });
    }
  }

  fetchEmployeesEvent(event) {
    this.employees = [];
    if (event.target.checked) {
      if (this.currentUser.authorities[0].authority === 'ROLE_ADMIN') {
        this.employeeService.getAll().pipe(first()).subscribe(employees => {
          this.employees = employees;
          this.isLoading = false;
        },
          error => {
            this.employees = [];
            this.alertService.error(error.message);
            this.isLoading = false;
          });
      }
      if (this.currentUser.authorities[0].authority === 'ROLE_USER') {
        this.employeeService.getActive().pipe(first()).subscribe(employees => {
          employees.forEach(employee => {
            if (employee.status === 'ACTIVE') {
              this.employees.push(employee);
              this.isLoading = false;
            }
          });
        },
          error => {
            this.employees = [];
            this.alertService.error(error.message);
            this.isLoading = false;
          });
      }
    }
  }

  onNewEmployee() {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  onDeleteEmployee(id: number, name: string) {
    this.alertService.clear();
    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    let areYouSure = confirm('Are you sure you want to delete the employee ' + name);
    if (areYouSure) {
      this.employeeService.deleteEmployee(id).subscribe(data => {
        this.alertService.success('Employee deleted successfully', true);
        setTimeout(() => { this.alertService.clear(); }, 1500);
        this.isTakingAWhile = false;
        this.isLoading = false;
        this.fetchEmployees();
      },
        error => {
          this.alertService.error(error);
          this.fetchEmployees();
          this.isLoading = false;
          this.isTakingAWhile = false;
        });
    } else {
      this.isLoading = false;
      this.isTakingAWhile = false;
      this.fetchEmployees();
    }

  }

  onExportToCsv() {
    this.employees.forEach(employee => {
      this.employeeToCsv = new EmployeeToCsv();

      this.employeeToCsv.email = employee.email;
      this.employeeToCsv.firstName = employee.firstName;
      this.employeeToCsv.lastName = employee.lastName;
      this.employeeToCsv.fullName = employee.fullName;
      this.employeeToCsv.personalEmail = employee.personalEmail;
      this.employeeToCsv.username = employee.username;
      this.employeeToCsv.startedAt = employee.startedAt.toString().slice(0, 10);
      this.employeeToCsv.createdAt = employee.createdAt.slice(0, 10);
      this.employeeToCsv.intranet = employee.intranet;
      this.employeeToCsv.office365 = employee.office365;
      if (employee.slack === 0) {
        this.employeeToCsv.slack = 'FALSE';
      }
      if (employee.slack === 1) {
        this.employeeToCsv.slack = 'TRUE';
      }
      if (employee.slack === 2) {
        this.employeeToCsv.slack = 'FAILED';
      }
      if (employee.slack === 3) {
        this.employeeToCsv.slack = 'PENDING';
      }
      this.employeeToCsv.status = employee.status;

      this.employeesToCsv.push(this.employeeToCsv);
    });
    const csvExporter = new ExportToCsv(options);
    csvExporter.generateCsv(this.employeesToCsv);
    this.employeesToCsv = [];
  }


}
