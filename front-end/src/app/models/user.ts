import { Authority } from './authority';
import { Role } from './role';

export class User {
  id?: number;
  email: string;
  personalEmail: string;
  password: string;
  firstName: string;
  lastName: string;
  username?: string;
  fullName: string;
  createdAt: string;
  accessToken?: string;
  authorities: Authority[];
  roles: any;

}
