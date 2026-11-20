import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ArchivoService } from '../../services/archivo/archivo';

@Component({
  selector: 'app-file-upload',
  standalone: false,
  templateUrl: './file-upload.html',
  styleUrls: ['./file-upload.scss']
})
export class FileUpload {
  
  @Input() datasetId!: number; 
  @Output() cerrarModal: EventEmitter<void> = new EventEmitter<void>();
  constructor(private archivoService: ArchivoService) {}
  
  ngOnInit() {
  }

  archivos: File[] = []; 
  isHovering: boolean = false; 
  errorMessage = '';
  private errorTimeout?: ReturnType<typeof setTimeout>;
  onDragOver(event: DragEvent) {
    event.preventDefault(); 
    event.stopPropagation();
    this.isHovering = true;
  }
  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isHovering = false;
  }
  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isHovering = false;

    if (event.dataTransfer?.files) {
      this.procesarArchivos(event.dataTransfer.files);
    }
  }
  onFileSelected(event: any) {
    if (event.target.files) {
      this.procesarArchivos(event.target.files);
    }
  }
  procesarArchivos(files: FileList) {
    this.clearError();
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      
      const isTxtType = file.type === 'text/plain';
      const isTxtExtension = file.name.toLowerCase().endsWith('.txt');
      const isWithinLimit = file.size <= 10 * 1024 * 1024;

      if ((isTxtType || isTxtExtension) && isWithinLimit) {
        this.archivos.push(file);
      } else if (!isWithinLimit) {
        this.showError(`El archivo "${file.name}" supera el límite máximo de 10 MB.`);
      } else {
        this.showError(`El archivo "${file.name}" no es válido. Para esta fase del sistema RAG solo se permiten archivos .txt.`);
      }
    }
  }
  borrarArchivo(index: number) {
    this.archivos.splice(index, 1);
  }

  crearArchivos() {
  if (this.archivos.length === 0) {
    this.showError('No has seleccionado ningún archivo para subir.');
    return;
  }

  this.clearError();
  const archivosPendientes = [...this.archivos];
  let completados = 0;
  let fallidos = 0;
  const archivosExitosos: File[] = [];
  const archivosFallidos: File[] = [];

  for (const archivo of archivosPendientes) {
    const formData = new FormData();
    formData.append('file', archivo); 
    
    const nombreOriginal = archivo.name;
    const extension = 'txt';
    const idDataset = this.datasetId;

    this.archivoService.crearArchivo(formData, idDataset, nombreOriginal, extension).subscribe({
      next: () => {
        completados++;
        archivosExitosos.push(archivo);
        if (completados + fallidos === archivosPendientes.length) {
          this.archivos = this.archivos.filter(item => archivosFallidos.includes(item));
          if (fallidos === 0) {
            this.cerrarModal.emit();
          }
        }
      },
      error: (err) => {
        fallidos++;
        archivosFallidos.push(archivo);
        const detalle = err?.error?.descripcion?.join(', ') || 'No se pudo subir el archivo.';
        this.showError(`${archivo.name}: ${detalle}`);
        if (completados + fallidos === archivosPendientes.length) {
          this.archivos = this.archivos.filter(item => archivosFallidos.includes(item));
        }
      }
    });
  }
  }

  private showError(message: string): void {
    this.errorMessage = message;
    if (this.errorTimeout) {
      clearTimeout(this.errorTimeout);
    }
    this.errorTimeout = setTimeout(() => {
      this.errorMessage = '';
      this.errorTimeout = undefined;
    }, 5000);
  }

  private clearError(): void {
    this.errorMessage = '';
    if (this.errorTimeout) {
      clearTimeout(this.errorTimeout);
      this.errorTimeout = undefined;
    }
  }
}