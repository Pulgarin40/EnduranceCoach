import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { DashboardService } from '../../services/dashboard.service';
import { UserService } from '../../services/user.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule],
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
    private authService = inject(AuthService);
    private dashboardService = inject(DashboardService);
    private userService = inject(UserService);
    private router = inject(Router);

    athleteName = 'Deportista';
    currentDate = new Date();

    // Stats overview
    planesCreados = 0;
    rachaActual = 0;

    // Metrics form data
    metrics = {
        weight: null as number | null,
        restingHr: null as number | null,
        maxHr: null as number | null
    };

    updateSuccess = false;
    isUpdating = false;

    ngOnInit(): void {
        this.loadStats();
        this.loadMetrics();
    }

    loadStats() {
        this.dashboardService.getStats().subscribe({
            next: (data) => {
                this.planesCreados = data.planesCreados;
                this.rachaActual = data.rachaActual;
            },
            error: (err) => console.error('Error fetching dashboard stats', err)
        });
    }

    loadMetrics() {
        this.userService.getMetrics().subscribe({
            next: (data) => {
                this.metrics.weight = data.weight ?? null;
                this.metrics.restingHr = data.restingHr ?? null;
                this.metrics.maxHr = data.maxHr ?? null;
            },
            error: (err) => console.error('Error fetching user metrics', err)
        });
    }

    updateMetrics() {
        this.isUpdating = true;
        this.updateSuccess = false;

        const payload = {
            weight: this.metrics.weight ?? undefined,
            restingHr: this.metrics.restingHr ?? undefined,
            maxHr: this.metrics.maxHr ?? undefined
        };

        this.userService.updateMetrics(payload).subscribe({
            next: (data) => {
                this.metrics.weight = data.weight ?? null;
                this.metrics.restingHr = data.restingHr ?? null;
                this.metrics.maxHr = data.maxHr ?? null;

                this.updateSuccess = true;
                this.isUpdating = false;

                setTimeout(() => {
                    this.updateSuccess = false;
                }, 3000);
            },
            error: (err) => {
                console.error('Error updating metrics', err);
                this.isUpdating = false;
            }
        });
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
