import { Component, OnInit, inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { MetricsService, AthleteMetrics } from '../../services/metrics.service';

@Component({
    selector: 'app-metrics',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    templateUrl: './metrics.component.html',
    styleUrls: ['./metrics.component.css']
})
export class MetricsComponent implements OnInit {
    private fb = inject(FormBuilder);
    private metricsService = inject(MetricsService);
    private platformId = inject(PLATFORM_ID);
    private cdr = inject(ChangeDetectorRef);

    metricsForm: FormGroup = this.fb.group({
        weight: ['', [Validators.required, Validators.min(20), Validators.max(200)]],
        height: ['', [Validators.required, Validators.min(100), Validators.max(250)]],
        restingHeartRate: ['', [Validators.required, Validators.min(30), Validators.max(120)]],
        vo2Max: ['', [Validators.min(20), Validators.max(90)]],
        ftp: ['', [Validators.min(50), Validators.max(500)]]
    });

    isLoading = true;
    isSaving = false;
    successMessage = '';
    errorMessage = '';

    ngOnInit() {
        if (isPlatformBrowser(this.platformId)) {
            console.log('📡 [Angular] Pidiendo métricas al Backend...');
            this.metricsService.getMetrics()
                .pipe(
                    finalize(() => {
                        console.log('✅ [RxJS] Flujo terminado. Apagando spinner obligatoriamente.');
                        this.isLoading = false;
                        this.cdr.detectChanges();
                    })
                )
                .subscribe({
                    next: (data) => {
                        console.log('📥 Datos recibidos de Spring Boot:', data);
                        if (data) {
                            this.metricsForm.patchValue(data);
                            this.cdr.detectChanges();
                        }
                    },
                    error: (err) => {
                        console.error('❌ Error HTTP capturado:', err);
                        if (err.status !== 404) {
                            this.errorMessage = 'No se pudieron cargar las métricas. Inténtalo más tarde.';
                        }
                    },
                    complete: () => console.log('🏁 Observable completado')
                });
        } else {
            // Renderizado en Servidor (SSR)
            this.isLoading = false;
        }
    }

    onSubmit() {
        if (this.metricsForm.valid) {
            this.isSaving = true;
            this.successMessage = '';
            this.errorMessage = '';
            this.cdr.detectChanges(); // <--- Avisamos para que pinte "Guardando..."

            this.metricsService.saveMetrics(this.metricsForm.value)
                .pipe(finalize(() => {
                    this.isSaving = false;
                    this.cdr.detectChanges(); // <--- Avisamos para que quite "Guardando..."
                }))
                .subscribe({
                    next: (data) => {
                        this.successMessage = 'Métricas guardadas correctamente. ¡A tope!';
                        this.metricsForm.patchValue(data);
                        this.cdr.detectChanges(); // <--- Avisamos para que pinte el mensaje VERDE
                    },
                    error: (err) => {
                        console.error('Error al guardar:', err);
                        this.errorMessage = 'Hubo un error al guardar las métricas.';
                        this.cdr.detectChanges(); // <--- Avisamos para que pinte el mensaje ROJO
                    }
                });
        }
    }
}