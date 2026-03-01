import { Component, inject, ChangeDetectorRef, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NutritionService, NutritionRequest } from '../../services/nutrition.service';
import { RouterModule } from '@angular/router';
import { finalize } from 'rxjs/operators';

@Component({
    selector: 'app-nutrition',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './nutrition.component.html',
    styleUrls: ['./nutrition.component.css']
})
export class NutritionComponent implements OnInit {
    private nutritionService = inject(NutritionService);
    private fb = inject(FormBuilder);
    private cdr = inject(ChangeDetectorRef);

    nutritionForm: FormGroup;
    isLoading = false;
    planData: any = null;
    errorMessage = '';

    savedPlans: any[] = [];
    isLoadingHistory = false;

    goals = ['Maratón', 'Media Maratón', '10K', 'Trail'];

    ngOnInit(): void {
        this.loadHistory();
    }

    loadHistory() {
        this.isLoadingHistory = true;
        this.nutritionService.getPlans().subscribe({
            next: (plans) => {
                // Parseamos el JSON de cada plan guardado de forma segura
                this.savedPlans = plans.map(plan => {
                    try {
                        let rawData = plan.strategyData || plan.strategy_data;
                        if (!rawData) return { ...plan, parsedData: { raw: 'Sin datos' }, showDetails: false };

                        let cleanData = rawData.replace(/```json|```/g, '');
                        const match = cleanData.match(/\{[\s\S]*\}/);
                        return { ...plan, parsedData: JSON.parse(match ? match[0] : cleanData), showDetails: false };
                    } catch (e) {
                        return { ...plan, parsedData: { raw: plan.strategyData }, showDetails: false };
                    }
                });
                this.isLoadingHistory = false;
                this.cdr.detectChanges(); // Forzar refresco
            },
            error: (err) => {
                console.error("Error cargando historial", err);
                this.isLoadingHistory = false;
            }
        });
    }

    deletePlan(planId: number) {
        if (!confirm('¿Estás seguro de que deseas eliminar este plan?')) return;

        this.nutritionService.deletePlan(planId).subscribe({
            next: () => {
                // Sincronización perfecta: lo borramos de la vista sin recargar la página
                this.savedPlans = this.savedPlans.filter(p => p.id !== planId);
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error("Error al eliminar", err);
                alert("Hubo un error al eliminar el plan.");
            }
        });
    }

    // Método para alternar el detalle de un plan en su propia tarjeta
    togglePlanDetails(plan: any) {
        plan.showDetails = !plan.showDetails;
        if (plan.showDetails) {
            console.log('Plan de nutrición expandido:', plan.parsedData);
        }
    }

    constructor() {
        this.nutritionForm = this.fb.group({
            weight: ['', [Validators.required, Validators.min(30), Validators.max(200)]],
            goal: ['', Validators.required]
        });
    }

    onSubmit() {
        if (this.nutritionForm.invalid) {
            this.nutritionForm.markAllAsTouched();
            return;
        }

        this.isLoading = true;
        this.errorMessage = '';
        this.planData = null;

        const request: NutritionRequest = this.nutritionForm.value;

        this.nutritionService.generatePlan(request)
            .pipe(finalize(() => {
                this.isLoading = false;
                this.cdr.detectChanges(); // <-- FALTABA ESTO: Despierta a Angular al terminar
            }))
            .subscribe({
                next: (response: any) => {
                    console.log('Respuesta recibida:', response);

                    // 1. Buscamos los datos (cubrimos camelCase y snake_case)
                    let rawData = response.strategyData || response.strategy_data;

                    // 2. Protección vital: Si no hay datos, salimos limpios
                    if (!rawData) {
                        console.warn('El objeto llegó, pero strategyData está vacío.');
                        this.planData = { raw: 'El servidor no devolvió el texto de la estrategia.' };
                        this.cdr.detectChanges(); // <-- FALTABA ESTO
                        return;
                    }

                    // 3. Parseo si sabemos que rawData existe
                    try {
                        let cleanData = rawData.replace(/```json|```/g, '');
                        const match = cleanData.match(/\{[\s\S]*\}/);
                        this.planData = JSON.parse(match ? match[0] : cleanData);
                    } catch (e) {
                        console.warn("Parsing fallido, usando fallback");
                        this.planData = { raw: rawData };
                    }

                    this.cdr.detectChanges(); // <-- FALTABA ESTO: Pinta las tarjetas
                },
                error: (error) => {
                    this.errorMessage = 'Hubo un error al generar el plan. Inténtalo de nuevo.';
                    console.error(error);
                    this.cdr.detectChanges(); // <-- FALTABA ESTO: Pinta el mensaje de error
                }
            });
    }
}
