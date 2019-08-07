import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { EmployeeService } from 'src/app/services/employee.service';
import { AlertService } from 'src/app/services/alert.service';
import { first } from 'rxjs/operators';
import { Employee } from 'src/app/models/employee';
import { IMyDpOptions } from 'mydatepicker';

@Component({
  selector: 'app-edit-employee',
  templateUrl: './edit-employee.component.html',
  styleUrls: ['./edit-employee.component.css']
})
export class EditEmployeeComponent implements OnInit {

  @Input() id: number;
  isLoading = false;
  editEmployeeForm: FormGroup;
  submitted = false;
  error = '';
  employee: Employee;
  employeeToJSON: string;
  dayToString: string;
  yearToString: string;
  monthToString: string;
  isTakingAWhile = false;

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
    private alertService: AlertService) {

  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.id = +params.get('id');
      this.fetchEmployeeById();

    }
    );
  }

  get f() {
    return this.editEmployeeForm.controls;
  }

  fetchEmployeeById() {
    this.employeeService.getById(this.id)
      .pipe(first())
      .subscribe(employee => {
        this.employee = { ...employee };
        this.createForm();
      });
  }

  createForm() {
    this.editEmployeeForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      personalEmail: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      startedAt: [null, Validators.required]
    });
    this.updateForm();
  }

  updateForm() {

    let date = new Date();
    date.setFullYear(+this.employee.startedAt.toString().slice(0, 4));
    date.setMonth(+this.employee.startedAt.toString().slice(5, 7));
    date.setDate(+this.employee.startedAt.toString().slice(8, 10));
    this.editEmployeeForm.setValue({
      firstName: this.employee.firstName,
      lastName: this.employee.lastName,
      email: this.employee.email,
      personalEmail: this.employee.personalEmail,
      username: this.employee.username,
      startedAt: ''
    });
    this.editEmployeeForm.patchValue({
      startedAt: {
        date: {
          year: date.getFullYear(),
          month: date.getMonth(),
          day: date.getDate()
        }
      }
    });
  }

  setDate() {
    // Set today date using the patchValue function
    let date = new Date();
    this.editEmployeeForm.patchValue({
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
    this.editEmployeeForm.patchValue({ startedAt: null });
  }

  onReset() {
    this.updateForm();
  }

  onClear() {
    this.editEmployeeForm.reset();
  }

  pad(num: number, size: number): string {
    let s = num + '';
    while (s.length < size) {
      s = '0' + s;
    }
    return s;
  }

  onSubmit() {

    this.submitted = true;

    // reset alerts on submit
    this.alertService.clear();

    // stop here if form is invalid
    if (this.editEmployeeForm.invalid) {
      return;
    }
    let formDate = this.editEmployeeForm.value.startedAt.date;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    this.isLoading = true;

    this.dayToString = this.pad(formDate.day, 2);
    this.monthToString = this.pad(formDate.month, 2);
    this.yearToString = formDate.year.toString();

    this.editEmployeeForm.value.startedAt = this.yearToString + '-' + this.monthToString + '-' + this.dayToString;

    // employee to JSON
    this.employeeToJSON = JSON.parse(JSON.stringify(this.editEmployeeForm.value));

    this.employeeService.updateEmployee(this.id, this.employeeToJSON)
      .pipe(first())
      .subscribe(
        data => {
          this.alertService.success('Employee was edited successfully', true);
          setTimeout(() => { this.router.navigate(['/employees']); }, 1500);
        },
        error => {
          this.alertService.error(error.message);
          this.isLoading = false;
          this.isTakingAWhile = false;
        });
  }
}
