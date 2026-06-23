import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { AuthService } from '../../service/authService';
import { UserService } from '../../service/userService';

function passwordsMatch(control: AbstractControl): ValidationErrors | null {
  const pw  = control.get('password')?.value;
  const cpw = control.get('confirmPassword')?.value;
  if (pw && cpw && pw !== cpw) return { passwordMismatch: true };
  return null;
}

@Component({
  selector: 'app-edit-profile',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './edit-profile.html',
  styleUrl: './edit-profile.css'
})
export class EditProfile {
  private auth        = inject(AuthService);
  private userService = inject(UserService);
  private router      = inject(Router);
  private fb          = inject(FormBuilder);

  loading = signal(false);
  success = signal('');
  error   = signal('');

  form = this.fb.group({
    name:            [this.auth.userName(),    [Validators.required]],
    surname:         [this.auth.userSurname(), [Validators.required]],
    email:           [this.auth.userEmail(),   [Validators.required, Validators.email]],
    password:        ['', [Validators.minLength(6)]],
    confirmPassword: [''],
  }, { validators: passwordsMatch });

  get name()            { return this.form.get('name')!; }
  get surname()         { return this.form.get('surname')!; }
  get email()           { return this.form.get('email')!; }
  get password()        { return this.form.get('password')!; }
  get confirmPassword() { return this.form.get('confirmPassword')!; }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const userId = this.auth.getUserId();
    if (!userId) return;

    const { name, surname, email, password } = this.form.value;
    const payload: any = { name, surname, email };
    if (password) payload.password = password;

    this.loading.set(true);
    this.error.set('');
    this.success.set('');

    this.userService.updateUser(userId, payload).subscribe({
      next: updated => {
        this.auth.updateProfile(updated.name, updated.surname, updated.email);
        this.success.set('Profile updated successfully.');
        this.loading.set(false);
        this.form.patchValue({ password: '', confirmPassword: '' });
      },
      error: () => {
        this.error.set('Failed to update profile. Please try again.');
        this.loading.set(false);
      }
    });
  }
}
