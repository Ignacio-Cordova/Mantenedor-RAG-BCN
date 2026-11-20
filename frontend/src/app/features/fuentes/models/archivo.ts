import { Fuente } from "../models/fuente";

export interface Archivo extends Fuente {
    ruta: string;
    extension: string;
}
