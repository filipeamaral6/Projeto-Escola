import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../services/authentication.service';
import { User } from '../models/user';
import { UserService } from '../services/user.service';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  currentUser: User;
  user: User;

  constructor(
    private authenticationService: AuthenticationService, private userService: UserService

  ) {
    this.currentUser = this.authenticationService.currentUserValue;
  }

  ngOnInit() {

  }
}
