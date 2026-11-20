import { Fuente } from "./fuente";

export interface ServicioWeb extends Fuente {
    urlEndpoint: string;
    metodoHttp: string;
    headersJson: string; 
    bodyPayload:string; 
    rutaTexto: string; 
    rutasMetadatos: string;
    crdate: string;
}