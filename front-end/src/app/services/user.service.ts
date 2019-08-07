import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../models/user';
import { Observable } from 'rxjs';
import { UtilsService } from './utils.service';
import { Globals } from '../shared/globals';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
  })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient, private globals: Globals) { }

  API = this.globals.API;

  getAll() {
    return this.http.get<User[]>(this.API + '/users');
  }

  getById(id: number) {
    return this.http.get<User>(this.API + '/users/' + id);
  }

  getByUsername(username: string) {
    return this.http.get<User>(this.API + '/users/username/' + username);
  }

  addUser(user: string) {
    return this.http.post(this.API + '/auth/signup', user, httpOptions);
  }

  updateUser(id: number, user: string) {
    return this.http.put(this.API + '/users/' + id, user);
  }

  updatePassword(updatePassword: string) {
    return this.http.put(this.API + '/auth/update-password/', updatePassword);
  }

  deleteUser(id: number, username: string) {
    return this.http.post(this.API + '/users/' + id, username);
  }
}
