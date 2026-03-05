import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private http = inject(HttpClient);
    private platformId = inject(PLATFORM_ID);
    private apiUrl = 'https://endurancecoach-api.onrender.com/api/auth'

    login(credentials: any): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
            tap(response => {
                if (response.token && isPlatformBrowser(this.platformId)) {
                    localStorage.setItem('token', response.token);
                }
            })
        );
    }

    register(userData: any): Observable<any> {
        const payload = { ...userData, role: 'ATHLETE' };
        return this.http.post<any>(`${this.apiUrl}/register`, payload).pipe(
            tap(response => {
                if (response.token && isPlatformBrowser(this.platformId)) {
                    localStorage.setItem('token', response.token);
                }
            })
        );
    }

    logout(): void {
        if (isPlatformBrowser(this.platformId)) {
            localStorage.removeItem('token');
        }
    }

    getToken(): string | null {
        if (isPlatformBrowser(this.platformId)) {
            return localStorage.getItem('token');
        }
        return null;
    }

    isLoggedIn(): boolean {
        return !!this.getToken();
    }
}
