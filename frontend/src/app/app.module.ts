import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { routes } from './app.routes';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';

import { App } from './app';
import { FormsModule } from '@angular/forms';
import { FileUpload } from './features/fuentes/components/file-upload/file-upload';
import { DatasetCard } from './features/datasets/components/dataset-card/dataset-card';
import { Button } from './shared/components/button/button';
import { ExitButtonComponent } from './shared/components/exit-button/exit-button.component';
import { DatasetList } from './features/datasets/components/dataset-list/dataset-list';
import { Header } from './layout/header/header';
import { SearchBar } from './shared/components/search-bar/search-bar'; 
import { DetalleFuenteComponent } from './features/fuentes/components/detalle-fuente/detalle-fuente';
import { FormConexionComponent } from './shared/components/form-conexion/form-conexion.component';
import { FormModeloLlmComponent } from './shared/components/form-modelo-llm/form-modelo-llm.component';
import { FormModeloEmbeddingComponent } from './shared/components/form-modelo-embedding/form-modelo-embedding.component';
import { FormApiComponent } from './shared/components/form-api/form-api.component';
import { InputLimitDirective } from './shared/directives/input-limit.directive';
import { UiModal } from './shared/components/ui-modal/ui-modal';
import { ModalCreacion } from './features/datasets/components/modal-creacion/modal-creacion';

import { ModalAjustes } from './features/ajustes/components/modal-ajustes/modal-ajustes';
import { AjustesFuentesTabComponent } from './features/ajustes/components/ajustes-fuentes-tab/ajustes-fuentes-tab.component';
import { AjustesGeneralesComponent } from './features/ajustes/components/ajustes-generales/ajustes-generales.component';
import { AjustesIndexacionComponent } from './features/ajustes/components/ajustes-indexacion/ajustes-indexacion.component';

import { ModalAjustesGlobales } from './features/ajustes/components/modal-ajustes-globales/modal-ajustes-globales';
import { AjustesGlobalesApisComponent } from './features/ajustes/components/ajustes-globales-apis/ajustes-globales-apis.component';
import { AjustesGlobalesConexionesComponent } from './features/ajustes/components/ajustes-globales-conexiones/ajustes-globales-conexiones.component';
import { AjustesGlobalesModelosComponent } from './features/ajustes/components/ajustes-globales-modelos/ajustes-globales-modelos.component';
import { AjustesGlobalesModelosEmbeddingComponent } from './features/ajustes/components/ajustes-globales-modelos-embedding/ajustes-globales-modelos-embedding.component';

import { ModalConfirmacionComponent } from './shared/components/modal-confirmacion/modal-confirmacion.component';
import { ModalAgregarFuenteComponent } from './features/fuentes/components/modal-agregar-fuente/modal-agregar-fuente';
import { Home } from './pages/home/home';
import { MatButtonModule } from '@angular/material/button';



@NgModule({
  declarations: [
    App,
    DatasetCard,  
    DatasetList,
    Header,      
    SearchBar,   
    Home,
    UiModal,
    ModalCreacion,
    ModalAjustes,
    ModalAjustesGlobales,
    ModalConfirmacionComponent,
    ModalAgregarFuenteComponent,
    Button,
    ExitButtonComponent,
    FileUpload,
    DetalleFuenteComponent,
    AjustesFuentesTabComponent,
    AjustesGeneralesComponent,
    AjustesIndexacionComponent,
    AjustesGlobalesApisComponent,
    AjustesGlobalesConexionesComponent,
    AjustesGlobalesModelosComponent,
    AjustesGlobalesModelosEmbeddingComponent,
    FormConexionComponent,
    FormModeloLlmComponent,
    FormModeloEmbeddingComponent,
    FormApiComponent,
    InputLimitDirective
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    CommonModule,
    RouterModule.forRoot(routes),
    FormsModule,
    MatTableModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    MatPaginatorModule
  ],
  providers: [
    provideHttpClient(withFetch()),
  ],
  bootstrap: [App]
})
export class AppModule { }