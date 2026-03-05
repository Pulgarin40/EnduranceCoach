import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class TrainingService {
    private http = inject(HttpClient);
    private apiUrl = 'https://endurancecoach-api.onrender.com/api/training/generate';

    generatePlan(goal: string, distance: string, weeks: number): Observable<any> {
        const payload = { goal, distance, weeks };
        return this.http.post<any>(this.apiUrl, payload);
    }

    savePlan(planPayload: any): Observable<any> {
        return this.http.post<any>('https://endurancecoach-api.onrender.com/api/training/save', planPayload);
    }

    getMyPlans(): Observable<any[]> {
        return this.http.get<any[]>('https://endurancecoach-api.onrender.com/api/training/my-plans');
    }

    deletePlan(id: number): Observable<void> {
        return this.http.delete<void>(`https://endurancecoach-api.onrender.com/api/training/${id}`);
    }
}
