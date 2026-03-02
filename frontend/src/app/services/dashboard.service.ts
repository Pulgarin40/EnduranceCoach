import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RecentActivity {
    id: string;
    title: string;
    type: string;
    date: string;
}

export interface DashboardStats {
    totalPlans: number;
    currentStreak: number;
    recentActivities: RecentActivity[];
}

@Injectable({
    providedIn: 'root'
})
export class DashboardService {
    private apiUrl = 'http://localhost:8080/api/dashboard/stats';
    private http = inject(HttpClient);

    getStats(): Observable<DashboardStats> {
        return this.http.get<DashboardStats>(this.apiUrl);
    }
}
