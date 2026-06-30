import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { LoginService } from '../login-service/login.service';

export interface Section {
  id: number;
  content: string;
}

export interface Course {
  id: number;
  title: string;
  description: string;
  sections: Section[];
}

@Injectable({
  providedIn: 'root',
})
export class CourseService {
  private http = inject(HttpClient);
  private loginService = inject(LoginService);
  private apiUrl = 'http://localhost:8080/api/courses';

  private authHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.loginService.getToken()}`
    });
  }

  getAllCourses() {
    return this.http.get<Course[]>(this.apiUrl, { headers: this.authHeaders() });
  }

  getCourse(courseId: number) {
    return this.http.get<Course>(`${this.apiUrl}/${courseId}`, { headers: this.authHeaders() });
  }
}
