import { Component, OnInit } from '@angular/core';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { first } from 'rxjs/operators';
import { UtilsService } from 'src/app/services/utils.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { AlertService } from 'src/app/services/alert.service';

@Component({
  selector: 'app-code',
  templateUrl: './code.component.html',
  styleUrls: ['./code.component.css']
})
export class CodeComponent implements OnInit {

  codeForm: FormGroup;
  isLoading = false;
  submitted = false;
  isTakingAWhile = false;

  // tslint:disable: align
  constructor(private formBuilder: FormBuilder, private alertService: AlertService,
    private utilsService: UtilsService, private authenticationService: AuthenticationService) { }

  ngOnInit() {
    this.codeForm = this.formBuilder.group({
      code: ['', [Validators.required, Validators.minLength(8)]],
      username: ['', Validators.required]
    });
  }

  get f() { return this.codeForm.controls; }

  onSubmit() {
    this.submitted = true;

    // reset alerts on submit
    this.alertService.clear();
    console.log(this.codeForm);

    // stop here if form is invalid
    if (this.codeForm.invalid) {
      return;
    }

    let password = this.utilsService.generatePassword(8);

    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);

    let body = JSON.parse(JSON.stringify({
      "newPassword": password,
      "code": this.f.code.value,
      "username": this.f.username.value
    }));
    this.authenticationService.resetPassword(body)
      .pipe(first())
      .subscribe(
        data => {
          this.isLoading = false;
          this.isTakingAWhile = false;
          this.alertService.success('Your password has been reset successfully. Please check your email.', true);
        },
        error => {
          this.alertService.error(error.message);
          this.isTakingAWhile = false;
          this.isLoading = false;
        });
  }

}
