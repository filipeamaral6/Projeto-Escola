import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/app/models/user';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { Router, ActivatedRoute } from '@angular/router';
import { AlertService } from 'src/app/services/alert.service';
import { UtilsService } from 'src/app/services/utils.service';
import { UpdatePassword } from 'src/app/models/UpdatePassword';
import { AppComponent } from 'src/app/app.component';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  isLoading = false;
  changePasswordForm: FormGroup;
  submitted = false;
  error = '';
  user: User;
  updatePassword: UpdatePassword;
  userToJSON: string;
  passwordToJSON: string;
  currentUser: User;
  isTakingAWhile = false;

  constructor(private userService: UserService, private router: Router, private formBuilder: FormBuilder, private route: ActivatedRoute,
    // tslint:disable: align
    private alertService: AlertService, private utilsService: UtilsService,
    private authenticationService: AuthenticationService) {

    this.currentUser = this.authenticationService.currentUserValue;
  }

  ngOnInit() {
    this.createForm();
  }

  createForm() {
    this.changePasswordForm = this.formBuilder.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
    }, {
        validator: this.utilsService.MustMatch('newPassword', 'confirmPassword'),
      });

  }

  onClear() {
    this.changePasswordForm.reset();
  }

  get f() {
    return this.changePasswordForm.controls;
  }

  onSubmit() {
    this.submitted = true;


    // reset alerts on submit
    this.alertService.clear();


    // stop here if form is invalid
    if (this.changePasswordForm.invalid) {
      return;
    }

    this.updatePassword = new UpdatePassword();
    this.updatePassword.currentPassword = this.changePasswordForm.value.currentPassword;
    this.updatePassword.newPassword = this.changePasswordForm.value.newPassword;
    this.updatePassword.username = this.currentUser.username;

    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);

    // role array to string

    // user to JSON
    this.passwordToJSON = JSON.parse(JSON.stringify(this.updatePassword));


    this.userService.updatePassword(this.passwordToJSON)
      .pipe(first())
      .subscribe(
        data => {
          this.alertService.success('Password updated successfully', true);
          setTimeout(() => { this.router.navigate(['/employees']); }, 1500);
        },
        error => {
          this.alertService.error(error.message);
          this.isTakingAWhile = false;
          this.isLoading = false;
        });
  }
}


