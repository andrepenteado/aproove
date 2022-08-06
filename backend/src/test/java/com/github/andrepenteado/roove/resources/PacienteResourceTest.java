package com.github.andrepenteado.roove.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.andrepenteado.roove.models.Paciente;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

;

/**
 * Testes do resource {@link com.github.andrepenteado.roove.resources.PacienteResource}
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class
})
@DatabaseSetup("/datasets/paciente.xml")
@Transactional
public class PacienteResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String NOME_PACIENTE = "Nome de Paciente de Testes";

    private final Long CPF_PACIENTE = 12345678901L;

    private Paciente getPaciente(Long id) {
        Paciente paciente = new Paciente();
        if (id != null)
            paciente.setId(id);
        paciente.setDataCadastro(LocalDateTime.now());
        paciente.setNome(NOME_PACIENTE);
        paciente.setCpf(CPF_PACIENTE);
        paciente.setQueixaPrincipal("Queixa principal NOT NULL");
        paciente.setHistoriaMolestiaPregressa("Histório pregressa NOT NULL");
        return paciente;
    }

    private String getJsonPaciente(Long id) throws Exception {
        return objectMapper.writeValueAsString(getPaciente(id));
    }

    @Test
    @DisplayName("Listar todos pacientes")
    void testListar() throws Exception {
        String json = mockMvc.perform(get("/pacientes")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        List<Paciente> lista = objectMapper.readValue(json, new TypeReference<List<Paciente>>() {});
        assertEquals(lista.size(), 2);
    }

    @Test
    @DisplayName("Buscar pacientes por ID")
    void testBuscar() throws Exception {
        mockMvc.perform(get("/pacientes/100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        mockMvc.perform(get("/pacientes/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Incluir novo paciente")
    void testIncluir() throws Exception {
        String json = mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(getJsonPaciente(-1L)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        Paciente pacienteNovo = objectMapper.readValue(json, Paciente.class);
        assertEquals(pacienteNovo.getNome(), NOME_PACIENTE);
        assertNotNull(pacienteNovo.getId());

        // Sem dados obrigatórios
        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Paciente())))
            .andExpect(status().isUnprocessableEntity());

        // CPF duplicado
        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(getJsonPaciente(-1L)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Alterar paciente existente")
    void testAlterar() throws Exception {
        String json = mockMvc.perform(put("/pacientes/100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(getJsonPaciente(100L)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        Paciente pacienteAlterado = objectMapper.readValue(json, Paciente.class);
        assertEquals(pacienteAlterado.getNome(), NOME_PACIENTE);
        assertEquals(pacienteAlterado.getId(), 100);

        mockMvc.perform(put("/pacientes/300")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(getJsonPaciente(100L)))
            .andExpect(status().isNotFound());

        mockMvc.perform(put("/pacientes/100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(getJsonPaciente(300L)))
            .andExpect(status().isConflict());

        mockMvc.perform(put("/pacientes/100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Paciente())))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Excluir paciente existente")
    void testExcluir() throws Exception {
        mockMvc.perform(delete("/pacientes/200")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/pacientes/999")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    }

}
