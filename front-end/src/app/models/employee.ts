export class Employee {
  id: number;
  email: string;
  personalEmail: string;
  password: string;
  firstName: string;
  lastName: string;
  username: string;
  fullName: string;
  createdAt: string;
  startedAt: Date;
  status: string;

  // platforms
  intranet: boolean;
  slack: number;
  office365: boolean;
}
