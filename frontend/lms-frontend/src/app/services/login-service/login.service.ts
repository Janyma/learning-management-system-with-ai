import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { tap } from 'rxjs';

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
    return this.http.post<{token: string; tokenType: string; message: string}>(this.apiUrl, payload).pipe(
      tap(res => {
        if(res.token){
          localStorage.setItem('token', res.token);
        }
      })
    );
  }
  getToken(): string | null{
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout(): void{
    localStorage.removeItem('token');
  }
}
