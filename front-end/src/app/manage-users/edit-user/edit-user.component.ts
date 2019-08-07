import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { UserService } from 'src/app/services/user.service';
import { AlertService } from 'src/app/services/alert.service';
import { first } from 'rxjs/operators';
import { User } from 'src/app/models/user';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {

  @Input() id: number;
  isLoading = false;
  editUserForm: FormGroup;
  submitted = false;
  error = '';
  user: User;
  userToJSON: string;
  isTakingAWhile = false;

  constructor(private route: ActivatedRoute,
    // tslint:disable: align
    private router: Router,
    private formBuilder: FormBuilder,
    private userService: UserService,
    private alertService: AlertService,
    private utilsService: UtilsService) {

  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.id = +params.get('id');
      this.fetchUserById();
    }
    );
  }

  get f() {
    return this.editUserForm.controls;
  }

  fetchUserById() {
    this.userService.getById(this.id)
      .pipe(first())
      .subscribe(user => {
        this.user = { ...user };
        this.createForm();

      });
  }

  createForm() {
    this.editUserForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      personalEmail: ['', [Validators.required, Validators.email]],
      role: ['', Validators.required]
    });
    this.updateForm();
  }

  updateForm() {
    this.editUserForm.setValue({
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      password: 'resetFalse',
      username: this.user.username,
      email: this.user.email,
      personalEmail: this.user.personalEmail,
      role: this.user.roles[0].name
    });
  }

  onReset() {
    this.updateForm();
  }

  onClear() {
    this.editUserForm.reset();
  }

  onSubmit() {
    if (this.editUserForm.value.password === 'resetTrue') {
      let password = this.utilsService.generatePassword(8);
      this.editUserForm.value.password = password;
    }

    this.submitted = true;

    // reset alerts on submit
    this.alertService.clear();

    // stop here if form is invalid
    if (this.editUserForm.invalid) {
      return;
    }

    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);

    // role array to string
    this.editUserForm.value.role = [this.editUserForm.value.role.toString()];

    // user to JSON
    this.userToJSON = JSON.parse(JSON.stringify(this.editUserForm.value));

    this.userService.updateUser(this.id, this.userToJSON)
      .pipe(first())
      .subscribe(
        data => {
          this.alertService.success('User was edited successfully', true);
          setTimeout(() => { this.router.navigate(['/users']); }, 1500);
        },
        error => {
          this.alertService.error(error.message);
          this.isLoading = false;
          this.isTakingAWhile = false;
        });
  }
}
