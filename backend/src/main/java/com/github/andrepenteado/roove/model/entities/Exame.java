package com.github.andrepenteado.roove.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(of = { "paciente", "arquivo" })
@ToString(of = { "paciente", "arquivo" })
public class Exame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Id_Paciente")
    @NotNull(message = "Paciente é um campo obrigatório")
    private Paciente paciente;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Id_Arquivo")
    @NotNull(message = "Exame é um campo obrigatório")
    private Arquivo arquivo;

    private LocalDateTime dataUpload;

    @NotBlank(message = "Descrição é um campo obrigatório")
    private String descricao;

}
