import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule,  Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service/auth.service';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  private fb=inject(FormBuilder);
  private authService=inject(AuthService);

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

    this.authService.register(this.form.getRawValue())

    .subscribe({
      next:(res: any)=> {
        const msg= res?.message ?? 'Register successfull'
        this.success.set(msg);
        this.form.reset();
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse)=>{
        console.error(err);
        const msg= err?.error.message ?? err?.message ?? 'Registration failed'
        
        
        this.error.set(msg);
        this.loading.set(false);
      }
    })
  }
}
