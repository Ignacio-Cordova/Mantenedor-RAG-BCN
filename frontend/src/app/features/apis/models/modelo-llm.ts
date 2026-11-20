export interface ModeloLlm {
    id: number;
    nombre: string;
    nombreTecnico: string;
    limiteTokens: number;
    idApi: number;
    crdate: string;
    endpoint: string;
    plantilla_payload: string;
    ruta_respuesta: string;
}
