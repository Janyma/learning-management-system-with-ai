import { Component, inject, signal } from '@angular/core';
import {  FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginService } from '../../services/login-service/login.service';
import { HttpErrorResponse } from '@angular/common/http';


@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
    private fb=inject(FormBuilder);
    private loginService=inject(LoginService);

    loading =signal(false);
    success = signal('');
    error = signal('');


    form =this.fb.nonNullable.group({
    username:['', [Validators.required]],
    password:['', [Validators.required, Validators.minLength(6)]]
  })

  onSubmit(){
    if(this.form.invalid){
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.success.set('');
    this.error.set('');

    this.loginService.login(this.form.getRawValue())

    .subscribe({
      next:(res: any)=> {
        const msg= res?.message ?? 'Login successfull'
        this.success.set(msg);
        this.form.reset();
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse)=>{
        console.error(err);
        const bodyText = err?.error && ( (err.error as any).text ?? (typeof err.error === 'string' ? err.error : null) );
            if (err.status >= 200 && err.status < 300 && bodyText) {
              this.success.set(bodyText);
              this.form.reset();
              this.loading.set(false);
              return;
            }
        const msg= err?.error.error.error ?? err?.message ?? 'Login failed'
        
        
        this.error.set(msg);
        this.loading.set(false);
      }
    })
  }


  
  
}
