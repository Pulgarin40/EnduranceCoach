import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserMetrics {
    weight?: number;
    restingHr?: number;
    maxHr?: number;
}

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = 'http://localhost:8080/api/user/me/metrics';
    private http = inject(HttpClient);

    getMetrics(): Observable<UserMetrics> {
        return this.http.get<UserMetrics>(this.apiUrl);
    }

    updateMetrics(metrics: UserMetrics): Observable<UserMetrics> {
        return this.http.put<UserMetrics>(this.apiUrl, metrics);
    }
}
