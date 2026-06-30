import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { ChatComponent } from './pages/chat/chat.component';
import { ChatHistoryComponent } from './pages/chat-history/chat-history.component';
import { CourseListComponent } from './pages/course-list/course-list.component';
import { CourseDetailComponent } from './pages/course-detail/course-detail.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    {path: '', component: LandingComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'login', component: LoginComponent},
    {path: 'chat', component: ChatComponent, canActivate: [authGuard]},
    {path: 'chat/history', component: ChatHistoryComponent, canActivate: [authGuard]},
    {path: 'courses', component: CourseListComponent, canActivate: [authGuard]},
    {path: 'courses/:id', component: CourseDetailComponent, canActivate: [authGuard]}
];
