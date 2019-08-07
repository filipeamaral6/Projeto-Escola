import { Component, OnInit, Input } from '@angular/core';
import { User } from 'src/app/models/user';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from 'src/app/services/user.service';
import { first } from 'rxjs/operators';
import { AlertService } from 'src/app/services/alert.service';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.css']
})
export class UserDetailComponent implements OnInit {

  @Input() id: number;
  user: User;
  isLoading = false;
  currentUser: User;
  isTakingAWhile = false;

  // tslint:disable-next-line: max-line-length
  // tslint:disable: align
  constructor(private route: ActivatedRoute, private router: Router, private userService: UserService,
    private alertService: AlertService, private authenticationService: AuthenticationService) {
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
    this.userService.getById(this.id)
      .pipe(first())
      .subscribe(user => {
        this.user = user;
      });
  }
  onDeleteUser(id: number, name: string, username: string) {
    this.alertService.clear();
    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    let usernameToJSON = JSON.parse(JSON.stringify(username));
    let areYouSure = confirm('Are you sure you want to delete the user ' + name);
    if (areYouSure) {
      this.userService.deleteUser(id, usernameToJSON).subscribe(data => {
        this.alertService.success('User deleted successfully', true);
        setTimeout(() => { this.router.navigate(['/users']); this.isLoading = false; this.isTakingAWhile = false; }, 1500);

      },
        error => {
          this.alertService.error(error);
          this.isTakingAWhile = false;
          this.isLoading = false;
        });
    } else {
      this.isTakingAWhile = false;
      this.isLoading = false;
    }
  }

}
