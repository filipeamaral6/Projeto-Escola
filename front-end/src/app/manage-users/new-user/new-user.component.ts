import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { UserService } from 'src/app/services/user.service';
import { AlertService } from 'src/app/services/alert.service';
import { first } from 'rxjs/operators';
import { UtilsService } from 'src/app/services/utils.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-new-user',
  templateUrl: './new-user.component.html',
  styleUrls: ['./new-user.component.css']
})
export class NewUserComponent implements OnInit, OnDestroy {

  isLoading = false;
  newUserForm: FormGroup;
  submitted = false;
  error = '';
  userToJSON: string;
  firstNameSub: Subscription;
  lastNameSub: Subscription;
  isTakingAWhile = false;


  constructor(private route: ActivatedRoute,
    // tslint:disable: align
    private router: Router,
    private formBuilder: FormBuilder,
    private userService: UserService,
    private alertService: AlertService,
    private utilsService: UtilsService) { }

  ngOnInit() {
    this.newUserForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      username: ['', [Validators.required, Validators.pattern(/^\S*$/)]],
      personalEmail: ['', [Validators.required, Validators.email]],
      role: ['ROLE_USER', Validators.required]
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

  get f() {
    return this.newUserForm.controls;
  }

  get firstName() {
    return this.newUserForm.get('firstName');
  }
  get lastName() {
    return this.newUserForm.get('lastName');
  }

  get username() {
    return this.newUserForm.get('username');
  }

  onClear() {
    this.newUserForm.reset();
  }

  onSubmit() {

    const password = this.utilsService.generatePassword(8);
    this.newUserForm.value.password = password;
    this.submitted = true;

    // reset alerts on submit
    this.alertService.clear();

    // stop here if form is invalid
    if (this.newUserForm.invalid) {
      return;
    }

    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    // role array to string
    this.newUserForm.value.role = [this.newUserForm.value.role.toString()];
    // user to JSON
    this.userToJSON = JSON.parse(JSON.stringify(this.newUserForm.value));
    this.userService.addUser(this.userToJSON)
      .pipe(first())
      .subscribe(
        data => {
          this.alertService.success('User added successfully', true);
          setTimeout(() => { this.router.navigate(['/users']); }, 1500);
        },
        error => {
          this.alertService.error(JSON.parse(JSON.stringify(error)).message);
          this.isTakingAWhile = false;
          this.isLoading = false;
        });
  }
}
