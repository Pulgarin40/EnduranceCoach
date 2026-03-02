package com.tfm.backend.services;

import com.tfm.backend.dto.TrainingRequest;
import com.tfm.backend.models.AthleteMetrics;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiTrainingService {

  private static final Logger logger = LoggerFactory.getLogger(AiTrainingService.class);

  private final ChatModel chatModel;
  private final MetricsService metricsService;

  public String generateTrainingPlan(TrainingRequest request, String userEmail) {
    AthleteMetrics metrics = null;
    try {
      metrics = metricsService.getMetrics(userEmail);
    } catch (Exception e) {
      logger.warn("No se pudieron obtener métricas para el usuario {}. Se usarán valores por defecto.", userEmail);
    }

    Integer hr = metrics != null && metrics.getRestingHeartRate() != null ? metrics.getRestingHeartRate() : 60;
    Double vo2 = metrics != null && metrics.getVo2Max() != null ? metrics.getVo2Max() : 50.0;
    Integer ftp = metrics != null && metrics.getFtp() != null ? metrics.getFtp() : 200;

    String promptTemplate = """
        Actúa como un entrenador de resistencia de élite (Ironman, Trail, Maratón).
        Genera un plan de entrenamiento estructurado para el siguiente objetivo: %s.
        La distancia específica o meta es: %s.
        La duración del plan debe ser de %d semanas.

        El atleta tiene una FC en reposo de %d ppm, un VO2Max de %.1f y un FTP de %dW. Ajusta las zonas de intensidad basándote en estas métricas.

        MUY IMPORTANTE: Tienes que generar EXACTAMENTE el número de semanas solicitadas (%d). PROHIBIDO resumir o generar solo la semana 1. Debes devolver el array completo con todas las semanas, desde la 1 hasta la %d.

        Cada 'descripcion' de entrenamiento DEBE estar estructurada profesionalmente con: Calentamiento (ej. Z1/Z2), Bloque Principal (series, intervalos con Zonas Z3/Z4/Z5, FTP o RPE), y Vuelta a la Calma (Z1).

        Sé muy específico con tiempos (minutos), distancias (km) y zonas de intensidad. Nada de descripciones genéricas como 'Carrera suave'.

        ERES UN ENTRENADOR DE ÉLITE DE DEPORTES DE RESISTENCIA. Tienes prohibido generar semanas de solo 3 días. DEBES generar exactamente 7 días (Día 1 al Día 7) por cada semana en el JSON. Planifica de 5 a 6 días de entrenamiento efectivo y marca explícitamente los días restantes como "Descanso Total" o "Descanso Activo". El array de entrenamientos de cada semana debe tener SIEMPRE 7 objetos.

        PROHIBIDA LA MONOTONÍA: No repitas la misma estructura de días cada semana. Varía los estímulos. Un martes no puede ser siempre el mismo tipo de entrenamiento.

        VARIEDAD DE SESIONES: Utiliza obligatoriamente un amplio arsenal de entrenamientos: Fartlek, Cuestas (Desnivel), Tiradas Largas (LSD), Rodajes Regenerativos, Series Cortas/Largas, Tempo/Umbral, y Transiciones (Bricks) si el objetivo es Triatlón.

        PERIODIZACIÓN Y DESCARGA (DELOAD): Si el plan dura 4 semanas o más, es OBLIGATORIO que cada 3 o 4 semanas incluyas una 'Semana de Descarga' donde el volumen y la intensidad bajen drásticamente para permitir la supercompensación.

        ESPECIFICIDAD DE DISCIPLINA: Adapta el vocabulario al objetivo. Si es Trail Running, habla de desnivel positivo y terrenos técnicos. Si es Ciclismo, habla de cadencia y vatios.

        5. MULTIDISCIPLINA OBLIGATORIA: Si el objetivo seleccionado por el usuario es un Triatlón (Ironman, Half, Sprint), ES OBLIGATORIO que cada día de entrenamiento inicie indicando el deporte en mayúsculas (ej: [NATACIÓN], [CICLISMO], [CARRERA] o [TRANSICIÓN/BRICK]). Es inaceptable generar un día de triatlón sin especificar la disciplina exacta.

        MUY IMPORTANTE: Debes mantener exactamente la misma estructura JSON:
        {
          "plan_entrenamiento": {
            "objetivo": "...",
            "duracion_semanas": 12,
            "semanas": [
              {
                "semana": 1,
                "entrenamientos": [
                  {
                    "dia": 1,
                    "descripcion": "TEXTO SÚPER DETALLADO AQUÍ"
                  }
                ]
              }
            ]
          }
        }
        No añadas campos nuevos al JSON, simplemente mete todo el nivel de detalle dentro del campo 'descripcion'.

        Es OBLIGATORIO que respondas ÚNICAMENTE con un JSON válido, sin formato de Markdown, sin ```json al principio ni al final. Solo debe contener el JSON puro con el plan semanal y diario.""";

    int weeks = request.getWeeks() != null ? request.getWeeks() : 12;
    String distance = request.getDistance() != null ? request.getDistance() : "No especificada";
    String prompt = String.format(promptTemplate,
        request.getGoal(), distance, weeks, hr, vo2, ftp, weeks, weeks);

    return chatModel.call(prompt);
  }
}
