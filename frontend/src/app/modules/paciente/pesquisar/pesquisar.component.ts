import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Datatables, ExibirMensagemService } from "@andre.penteado/ngx-apcore";
import { PacienteService } from "../../../services/paciente.service";
import { Paciente } from "../../../domain/entities/paciente";
import { NgxSpinnerService } from "ngx-spinner";

@Component({
    selector: 'app-pesquisar',
    templateUrl: './pesquisar.component.html',
    styles: ``,
    standalone: false
})
export class PesquisarComponent implements OnInit {

  lista: Paciente[] = [];

  constructor(
    private pacienteService: PacienteService,
    private exibirMensagem: ExibirMensagemService,
    private spinnerService: NgxSpinnerService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.spinnerService.show();
    this.pesquisar();
  }

  pesquisar(): void {
    this.pacienteService.listar().subscribe({
      next: listaPacientes => {
        this.lista = listaPacientes;
        this.spinnerService.hide();
        setTimeout(() => {
          $('#datatable-pesquisar-paciente').DataTable(Datatables.config);
        }, 5);
      }
    });
  }

  incluir(): void {
    this.router.navigate([`/paciente/cadastro`]);
  }

  editar(paciente: Paciente): void {
    this.router.navigate([`/paciente/cadastro/${paciente.id}`]);
  }

  excluir(paciente: Paciente): void {
    this.exibirMensagem
      .showConfirm(`Confirma a exclusão do paciente ${paciente.nome}`, "Excluir?")
      .then((resposta) => {
        if (resposta.value) {
        this.pacienteService.excluir(paciente.id).subscribe({
          next: () => {
            window.location.reload();
          }
        });
      }
    });
  }

}
