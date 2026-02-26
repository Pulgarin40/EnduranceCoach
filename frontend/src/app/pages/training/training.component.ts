import { Component, inject, PLATFORM_ID, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { TrainingService } from '../../services/training.service';

export interface Workout {
    dia: number;
    descripcion: string;
}

export interface Week {
    semana: number;
    entrenamientos: Workout[];
}

export interface TrainingPlan {
    objetivo: string;
    duracion_semanas: number;
    semanas: Week[];
}

export interface TrainingResponse {
    plan_entrenamiento: TrainingPlan;
}

@Component({
    selector: 'app-training',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    templateUrl: './training.component.html',
    styleUrls: ['./training.component.css']
})
export class TrainingComponent implements OnInit {
    private fb = inject(FormBuilder);
    private trainingService = inject(TrainingService);
    private platformId = inject(PLATFORM_ID);
    private cdr = inject(ChangeDetectorRef);

    trainingForm: FormGroup = this.fb.group({
        goal: ['', Validators.required],
        distance: ['', Validators.required],
        weeks: [12, [Validators.required, Validators.min(4), Validators.max(24)]]
    });

    isLoading = false;
    generatedPlan: TrainingPlan | null = null;
    errorMessage = '';
    planSaved = false;
    isSavingPlan = false;

    savedPlans: any[] = [];
    isLoadingSavedPlans = false;
    savedPlansError = '';

    ngOnInit(): void {
        this.loadSavedPlans();
    }

    loadSavedPlans(): void {
        if (!isPlatformBrowser(this.platformId)) return;

        this.isLoadingSavedPlans = true;
        this.savedPlansError = '';
        this.cdr.detectChanges();

        this.trainingService.getMyPlans()
            .pipe(
                finalize(() => {
                    this.isLoadingSavedPlans = false;
                    this.cdr.detectChanges();
                })
            )
            .subscribe({
                next: (plans) => {
                    this.savedPlans = plans.map(plan => {
                        return {
                            ...plan,
                            parsedData: typeof plan.planData === 'string' ? JSON.parse(plan.planData) : plan.planData,
                            showDetails: false
                        };
                    });
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    console.error('Error cargando planes guardados:', err);
                    this.savedPlansError = 'No se pudieron cargar tus planes guardados.';
                    this.cdr.detectChanges();
                }
            });
    }

    togglePlanDetails(plan: any): void {
        plan.showDetails = !plan.showDetails;
        if (plan.showDetails) {
            console.log('Plan parseado:', plan.parsedData);
        }
    }

    onSubmit() {
        if (this.trainingForm.invalid || !isPlatformBrowser(this.platformId)) {
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';
        this.generatedPlan = null;
        this.planSaved = false;
        this.cdr.detectChanges(); // Forzamos el renderizado del spinner

        const { goal, distance, weeks } = this.trainingForm.value;

        this.trainingService.generatePlan(goal, distance, weeks)
            .pipe(
                finalize(() => {
                    this.isLoading = false;
                    this.cdr.detectChanges(); // Ocultamos spinner cuando el flujo termine (éxito o error)
                })
            )
            .subscribe({
                next: (data: TrainingResponse) => {
                    this.generatedPlan = data.plan_entrenamiento;
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    console.error('Error generando plan:', err);
                    this.errorMessage = 'Hubo un error al generar el plan de entrenamiento con la IA.';
                    this.cdr.detectChanges();
                }
            });
    }

    saveGeneratedPlan() {
        if (!this.generatedPlan) return;

        this.isSavingPlan = true;
        this.cdr.detectChanges();

        const { goal, distance, weeks } = this.trainingForm.value;
        const payload = {
            goal,
            distance,
            weeks,
            planData: JSON.stringify(this.generatedPlan)
        };

        this.trainingService.savePlan(payload)
            .pipe(
                finalize(() => {
                    this.isSavingPlan = false;
                    this.cdr.detectChanges();
                })
            )
            .subscribe({
                next: () => {
                    this.planSaved = true;
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    console.error('Error guardando el plan:', err);
                    this.errorMessage = 'Hubo un error al guardar el plan en tu perfil.';
                    this.cdr.detectChanges();
                }
            });
    }
}
