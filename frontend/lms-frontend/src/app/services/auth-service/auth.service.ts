import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

export interface RegisterRequest{
  username: string,
  password: string 
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http=inject(HttpClient);
  private apiUrl= 'http://localhost:8080/api/auth/register';

  register(payload: RegisterRequest){
    return this.http.post(this.apiUrl, payload);
  }
}
