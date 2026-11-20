export interface Dataset {
    id: number;
    nombre: string;
    tamanoChunk: number;
    collectionName: string;
    overlap: number,
    topK: number;
    systemPrompt: string;
    idApi: number;
    idModeloLlm: number;
    idModeloEmbedding: number;
    idRetrievalMethod: number; 
    idIndexMethod: number;
    crdate: string;
}
