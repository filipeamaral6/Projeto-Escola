import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Employee } from '../models/employee';
import { Globals } from '../shared/globals';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
  })
};

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  constructor(private http: HttpClient, private globals: Globals) { }

  API = this.globals.API;

  getAll() {
    return this.http.get<Employee[]>(this.API + '/admin/employees');
  }

  getActive() {
    return this.http.get<Employee[]>(this.API + '/employees/active');
  }

  getById(id: number) {
    return this.http.get<Employee>(this.API + '/employees/' + id);
  }

  addEmployee(employee: string) {
    return this.http.post(this.API + '/employees/add', employee, httpOptions);
  }

  updateEmployee(id: number, employee: string) {
    return this.http.put(this.API + '/employees/' + id, employee);
  }

  setActive(id: number) {
    return this.http.put(this.API + '/admin/employees/activate/' + id, '');
  }

  deleteEmployee(id: number) {
    return this.http.delete(this.API + '/admin/employees/delete/' + id);
  }

  inviteSlack(id: string) {
    return this.http.post(this.API + '/platforms/slack/invite', id, httpOptions);
  }

  inviteIntranet(employee: string) {
    return this.http.post(this.API + '/platforms/intranet/invite', employee, httpOptions);
  }

  inviteOffice365(employee: string) {
    return this.http.post(this.API + '/platforms/office365/invite', employee, httpOptions);
  }

}
