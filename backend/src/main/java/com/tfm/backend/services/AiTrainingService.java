package com.tfm.backend.services;

import com.tfm.backend.dto.TrainingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiTrainingService {

  private final ChatModel chatModel;

  public String generateTrainingPlan(TrainingRequest request) {
    String promptTemplate = """
        Actúa como un entrenador de resistencia de élite (Ironman, Trail, Maratón).
        Genera un plan de entrenamiento estructurado para el siguiente objetivo: %s.
        La distancia específica o meta es: %s.
        La duración del plan debe ser de %d semanas.

        MUY IMPORTANTE: Tienes que generar EXACTAMENTE el número de semanas solicitadas (%d). PROHIBIDO resumir o generar solo la semana 1. Debes devolver el array completo con todas las semanas, desde la 1 hasta la %d.

        Cada 'descripcion' de entrenamiento DEBE estar estructurada profesionalmente con: Calentamiento (ej. Z1/Z2), Bloque Principal (series, intervalos con Zonas Z3/Z4/Z5, FTP o RPE), y Vuelta a la Calma (Z1).

        Sé muy específico con tiempos (minutos), distancias (km) y zonas de intensidad. Nada de descripciones genéricas como 'Carrera suave'.

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
        request.getGoal(), distance, weeks, weeks, weeks);

    return chatModel.call(prompt);
  }
}
