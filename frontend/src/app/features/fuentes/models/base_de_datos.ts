import { Fuente } from "./fuente";

export interface BaseDeDatos extends Fuente{
    tablaObjetivo: string;
    querySql: string;
    modoExtraccion: string;
    columnaContenido: string;
    idConexion: number;
}
