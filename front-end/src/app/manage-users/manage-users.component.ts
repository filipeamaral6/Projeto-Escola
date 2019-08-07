import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { first } from 'rxjs/operators';
import { User } from '../models/user';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { AlertService } from '../services/alert.service';

@Component({
  selector: 'app-manage-users',
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.css'],

})
export class ManageUsersComponent implements OnInit {

  users: User[] = [];
  isLoading = true;
  currentUser: User;
  searchString: string;
  isTakingAWhile = false;

  // tslint:disable-next-line: max-line-length
  constructor(private userService: UserService, private router: Router, private route: ActivatedRoute, private authenticationService: AuthenticationService, private alertService: AlertService) {
    this.currentUser = this.authenticationService.currentUserValue;
  }

  ngOnInit() {
    this.fetchUsers();
  }

  fetchUsers() {
    this.userService.getAll().pipe(first()).subscribe(users => {
      this.users = users;
      this.isLoading = false;
    },
      error => {
        this.users = [];
        this.alertService.error(error.message);
        this.isLoading = false;
      });

  }

  onNewUser() {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  onDeleteUser(id: number, name: string, username: string) {
    this.alertService.clear();
    this.isLoading = true;
    setTimeout(() => { this.isTakingAWhile = true; }, 2000);
    let usernameToJSON = JSON.parse(JSON.stringify({
      "username": username
    }));
    let areYouSure = confirm('Are you sure you want to delete the user ' + name);
    if (areYouSure) {
      this.userService.deleteUser(id, usernameToJSON).subscribe(data => {
        this.alertService.success('User deleted successfully', true);
        setTimeout(() => { this.alertService.clear(); }, 1500);
        this.isLoading = false;
        this.isTakingAWhile = false;
        this.fetchUsers();
      },
        error => {
          this.alertService.error(error.message);
          this.isLoading = false;
          this.isTakingAWhile = false;
          this.fetchUsers();
        });
    } else {
      this.isLoading = false;
      this.isTakingAWhile = false;
      this.fetchUsers();
    }
  }
}
