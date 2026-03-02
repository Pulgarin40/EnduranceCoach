import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { DashboardService } from '../../services/dashboard.service';

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
    private router = inject(Router);
    private cdr = inject(ChangeDetectorRef);

    athleteName = 'Deportista';
    currentDate = new Date();

    // Stats overview
    planesCreados = 0;
    rachaActual = 0;
    actividadReciente: any[] = [];

    ngOnInit(): void {
        this.loadStats();
    }

    loadStats() {
        this.dashboardService.getStats().subscribe({
            next: (response: any) => {
                // 🔴 CHIVATO: Veremos exactamente qué manda Java en la consola del navegador
                console.log('🔴 DATOS CRUDOS DEL BACKEND:', response);

                // Asignación a prueba de balas (Atrapa diferentes formatos)
                this.planesCreados = response?.totalPlans || response?.total_plans || response?.planesCreados || 0;
                this.rachaActual = response?.currentStreak || response?.current_streak || response?.rachaActual || 0;
                this.actividadReciente = response?.recentActivities || response?.recent_activities || response?.actividadReciente || [];
                this.cdr.detectChanges();
            },
            error: (err) => console.error('🔴 Error fetching dashboard stats', err)
        });
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}