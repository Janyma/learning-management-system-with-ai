import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule,  Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
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
      next:()=> {
        this.success.set("Register successfull");
        this.form.reset();
        this.loading.set(false);
      },
      error: ()=>{
        console.log(this.error);
        this.error.set('Registration failed');
        this.loading.set(false);
      }
    })
  }
}
