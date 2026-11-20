export interface Fuente {
    id: number;
    nombre: string;
    tipo: 'ARCHIVO' | 'BASE_DE_DATOS' | 'SERVICIO_WEB';
    estadoIndexacion: 'PENDIENTE' | 'PROCESANDO' | 'COMPLETADO' | 'ERROR';
    logError?: string; 
    idExternoVectorial?: number; 
    idIndexacion?: number; 
    idDataset?: number; 
    crdate: string;
}
