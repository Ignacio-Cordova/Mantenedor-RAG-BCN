from fastapi import FastAPI
from pydantic import BaseModel
from fastembed import SparseTextEmbedding

app = FastAPI()
# Carga el modelo SPLADE de manera local y ultra rápida
sparse_model = SparseTextEmbedding(model_name="prithivida/Splade_PP_en_v1")

class TextRequest(BaseModel):
    text: str

@app.post("/splade")
def get_sparse_vector(req: TextRequest):
    # Genera el vector disperso
    sparse_result = list(sparse_model.embed([req.text]))[0]
    return {
        "indices": sparse_result.indices.tolist(),
        "values": sparse_result.values.tolist()
    }