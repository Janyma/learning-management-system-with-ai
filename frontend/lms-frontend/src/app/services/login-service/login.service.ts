import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

export interface LoginRequest{
  username: string,
  password: string 
}

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private http=inject(HttpClient);
  private apiUrl= 'http://localhost:8080/api/auth/login';

  login(payload: LoginRequest){
    return this.http.post(this.apiUrl, payload);
  }
}
