import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeeService } from 'src/app/services/employee.service';
import { AlertService } from 'src/app/services/alert.service';
import { first } from 'rxjs/operators';
import { IMyDpOptions } from 'mydatepicker';
import { Subscription } from 'rxjs';
import { Employee } from 'src/app/models/employee';
import { Data } from 'src/app/models/data';

@Component({
  selector: 'app-new-employee',
  templateUrl: './new-employee.component.html',
  styleUrls: ['./new-employee.component.css']
})
export class NewEmployeeComponent implements OnInit, OnDestroy {

  isLoading = false;
  newEmployeeForm: FormGroup;
  submitted = false;
  error = '';
  employeeToJSON: string;
  firstNameSub: Subscription;
  lastNameSub: Subscription;
  isTakingAWhile = false;
  employee: Employee;


  public myDatePickerOptions: IMyDpOptions = {
    // other options...
    dateFormat: 'yyyy-mm-dd',
    editableDateField: false
  };

  constructor(private route: ActivatedRoute,
    // tslint:disable: align
    private router: Router,
    private formBuilder: FormBuilder,
    private employeeService: EmployeeService,
    private alertService: AlertService) { }

  ngOnInit() {
    this.newEmployeeForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      username: ['', [Validators.required, Validators.pattern(/^\S*$/)]],
      personalEmail: ['', [Validators.required, Validators.email]],
      startedAt: [null, Validators.required]
    });

    this.firstNameSub = this.firstName.valueChanges.subscribe(
      value => {
        let valueReplace = value.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '') + '.'
          + this.lastName.value.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
        this.username.setValue(valueReplace.replace(/\s/g, '.'));
      }
    );

    this.lastNameSub = this.lastName.valueChanges.subscribe(
      value => {
        let valueReplace = this.firstName.value.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
          + '.' + value.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
        this.username.setValue(valueReplace.replace(/\s/g, '.'));
      }
    );
  }

  ngOnDestroy() {
    this.lastNameSub.unsubscribe();
    this.firstNameSub.unsubscribe();
  }

  setDate() {
    // Set today date using the patchValue function
    let date = new Date();
    this.newEmployeeForm.patchValue({
      startedAt: {
        date: {
          year: date.getFullYear(),
          month: date.getMonth() + 1,
          day: date.getDate()
        }
      }.date
    });
  }

  clearDate() {
    // Clear the date using the patchValue function
    this.newEmployeeForm.patchValue({ startedAt: null });
  }

  get f() {
    return this.newEmployeeForm.controls;
  }

  get firstName() {
    return this.newEmployeeForm.get('firstName');
  }
  get lastName() {
    return this.newEmployeeForm.get('lastName');
  }

  get username() {
    return this.newEmployeeForm.get('username');
  }

  onClear() {
    this.newEmployeeForm.reset();
  }

  onSubmit() {
    this.submitted = true;

    // reset alerts on submit
    this.alertService.clear();

    // stop here if form is invalid
    if (this.newEmployeeForm.invalid) {
      return;
    }

    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);

    // user to JSON
    this.newEmployeeForm.value.startedAt = this.newEmployeeForm.value.startedAt.formatted;
    this.employeeToJSON = JSON.parse(JSON.stringify(this.newEmployeeForm.value));
    this.employeeService.addEmployee(this.employeeToJSON)
      .pipe(first())
      .subscribe(
        data => {
          let dataMessage = data as Data;
          this.alertService.success('Employee added successfully', true);
          setTimeout(() => { this.router.navigate(['/employees/detail/', dataMessage.message]); }, 1500);
        },
        error => {
          this.clearDate();
          this.alertService.error(JSON.parse(JSON.stringify(error)).message);
          this.isTakingAWhile = false;
          this.isLoading = false;
        });
  }

}
