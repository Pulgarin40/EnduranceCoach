import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AthleteMetrics {
    id?: number;
    weight?: number;
    height?: number;
    restingHeartRate?: number;
    vo2Max?: number;
    ftp?: number;
    updatedAt?: string;
}

@Injectable({
    providedIn: 'root'
})
export class MetricsService {
    private http = inject(HttpClient);
    private apiUrl = 'http://localhost:8080/api/metrics';

    getMetrics(): Observable<AthleteMetrics> {
        return this.http.get<AthleteMetrics>(this.apiUrl);
    }

    saveMetrics(metrics: AthleteMetrics): Observable<AthleteMetrics> {
        return this.http.post<AthleteMetrics>(this.apiUrl, metrics);
    }
}
