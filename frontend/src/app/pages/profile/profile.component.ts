import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../services/auth.service';

interface UserProfile {
    username: string;
    email: string;
    createdAt: string;
}

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
    user: UserProfile | null = null;
    loading: boolean = true;
    error: string | null = null;

    private http = inject(HttpClient);
    private authService = inject(AuthService);
    private cdr = inject(ChangeDetectorRef);

    ngOnInit(): void {
        console.log('🟢 1. Enseñando la placa (Token) y pidiendo el perfil a Java...');

        // Obtenemos tu Token de seguridad
        const token = this.authService.getToken();

        // Lo metemos en la cabecera de la petición
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

        // Hacemos la llamada con la cabecera incluida
        this.http.get<UserProfile>(`${environment.apiUrl}/user/me`, { headers }).subscribe({
            next: (data) => {
                console.log('🟢 2. ¡Datos recibidos de Java!:', data);
                this.user = data;
                this.loading = false;
                this.cdr.detectChanges(); // <--- ¡Fuerza la actualización del dibujo!
            },
            error: (err) => {
                console.error('🔴 3. Error. Java ha bloqueado la puerta o ha fallado:', err);
                this.error = 'Error al cargar los datos del perfil.';
                this.loading = false;
                this.cdr.detectChanges(); // <--- ¡Fuerza la actualización del dibujo!
            }
        });
    }
}