import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface NutritionRequest {
    weight: number;
    goal: string;
}

export interface NutritionPlan {
    id: number;
    weight: number;
    goal: string;
    strategyData: string; // JSON string
    createdAt: string;
}

@Injectable({
    providedIn: 'root'
})
export class NutritionService {
    private http = inject(HttpClient);
    private apiUrl = environment.apiUrl + '/nutrition';

    constructor() { }

    generatePlan(request: NutritionRequest): Observable<NutritionPlan> {
        return this.http.post<NutritionPlan>(`${this.apiUrl}/generate`, request);
    }

    getPlans(): Observable<any[]> {
        return this.http.get<any[]>(this.apiUrl);
    }

    deletePlan(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
