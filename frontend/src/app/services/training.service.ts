import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class TrainingService {
    private http = inject(HttpClient);
    private apiUrl = 'http://localhost:8080/api/training/generate';

    generatePlan(goal: string, distance: string, weeks: number): Observable<any> {
        const payload = { goal, distance, weeks };
        return this.http.post<any>(this.apiUrl, payload);
    }

    savePlan(planPayload: any): Observable<any> {
        return this.http.post<any>('http://localhost:8080/api/training/save', planPayload);
    }

    getMyPlans(): Observable<any[]> {
        return this.http.get<any[]>('http://localhost:8080/api/training/my-plans');
    }
}
