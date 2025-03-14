import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { API_PACIENTES } from "../config/api";
import { Paciente } from "../domain/entities/paciente";

@Injectable({
  providedIn: 'root'
})
export class PacienteService {

  constructor(
    private http: HttpClient
  ) { }

  public listar(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(`${environment.urlBackend}${API_PACIENTES}`);
  }

  public buscar(id: number): Observable<Paciente> {
    return this.http.get<Paciente>(`${environment.urlBackend}${API_PACIENTES}/${id}`);
  }

  public gravar(paciente: any): Observable<Paciente> {
    if (paciente.id > 0) {
      return this.http.put<Paciente>(`${environment.urlBackend}${API_PACIENTES}/${paciente.id}`, paciente);
    }
    else {
      return this.http.post<Paciente>(`${environment.urlBackend}${API_PACIENTES}`, paciente);
    }
  }

  public excluir(id: number): Observable<any> {
    return this.http.delete(`${environment.urlBackend}${API_PACIENTES}/${id}`);
  }

  public total(): Observable<number> {
    return this.http.get<number>(`${environment.urlBackend}${API_PACIENTES}/total`);
  }

}
