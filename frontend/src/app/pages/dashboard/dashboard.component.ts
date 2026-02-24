import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
    private authService = inject(AuthService);
    private router = inject(Router);

    // Funciones placeholder para las tarjetas, pueden ampliarse más adelante.
    athleteName = 'Atleta';

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
