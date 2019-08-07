import { Component, OnInit } from '@angular/core';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { AlertService } from '../services/alert.service';
import { UtilsService } from '../services/utils.service';
import { AuthenticationService } from '../services/authentication.service';
import { first } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-password-recovery',
  templateUrl: './password-recovery.component.html',
  styleUrls: ['./password-recovery.component.css']
})
export class PasswordRecoveryComponent implements OnInit {

  recoverForm: FormGroup;
  isLoading = false;
  submitted = false;
  isTakingAWhile = false;
  // tslint:disable: align
  constructor(private formBuilder: FormBuilder, private alertService: AlertService,
    private utilsService: UtilsService, private authenticationService: AuthenticationService) { }

  ngOnInit() {
    this.recoverForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  get f() { return this.recoverForm.controls; }

  onSubmit() {
    this.submitted = true;

    // reset alerts on submit
    this.alertService.clear();

    // stop here if form is invalid
    if (this.recoverForm.invalid) {
      return;
    }

    let code = this.utilsService.generatePassword(8);

    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    let body = JSON.parse(JSON.stringify({
      "email": this.f.email.value,
      "code": code
    }));
    this.authenticationService.recoverPassword(body)
      .pipe(first())
      .subscribe(
        data => {
          this.isLoading = false;
          this.isTakingAWhile = false;
          this.alertService.success('A reset code has been sent successfully. Please check your email.', true);
        },
        error => {
          this.alertService.error(error.message);
          this.isLoading = false;
          this.isTakingAWhile = false;
        });
  }


}
