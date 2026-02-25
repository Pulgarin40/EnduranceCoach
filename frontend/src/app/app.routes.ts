import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { MetricsComponent } from './pages/metrics/metrics.component';
import { TrainingComponent } from './pages/training/training.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'metrics', component: MetricsComponent, canActivate: [authGuard] },
    { path: 'training', component: TrainingComponent, canActivate: [authGuard] }
];
