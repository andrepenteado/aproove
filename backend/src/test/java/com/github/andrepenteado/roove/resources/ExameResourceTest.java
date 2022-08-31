package com.github.andrepenteado.roove.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.andrepenteado.roove.models.Arquivo;
import com.github.andrepenteado.roove.models.Exame;
import com.github.andrepenteado.roove.models.Paciente;
import com.github.andrepenteado.roove.models.Prontuario;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testes do resource {@link ExameResource}
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
@DatabaseSetup("/datasets/exame.xml")
@Transactional
public class ExameResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String DESCRICAO_EXAME = "Descricao do exame de testes";

    private Paciente getPaciente() {
        Paciente paciente = new Paciente();
        paciente.setId(100L);
        paciente.setDataCadastro(LocalDateTime.now());
        paciente.setNome("Paciente com prontuário");
        paciente.setCpf(99999999999L);
        paciente.setQueixaPrincipal("Queixa principal NOT NULL");
        paciente.setHistoriaMolestiaPregressa("Histório pregressa NOT NULL");
        return paciente;
    }

    private Arquivo getNovoArquivo() {
        Arquivo arquivo = new Arquivo();
        arquivo.setNome("Arquivo novo para testes");
        arquivo.setTipoMime("application/xml");
        arquivo.setModificado(99999999L);
        arquivo.setTamanho(99999L);
        arquivo.setBase64("XXXXXXXX");
        return arquivo;
    }

    private String getJsonExame() throws Exception {
        Exame exame = new Exame();
        exame.setPaciente(getPaciente());
        exame.setArquivo(getNovoArquivo());
        exame.setDescricao(DESCRICAO_EXAME);
        exame.setDataUpload(LocalDateTime.now());
        return objectMapper.writeValueAsString(exame);
    }

    @Test
    @DisplayName("Listar exames do paciente")
    void testListarPorPaciente() throws Exception {
        String json = mockMvc.perform(get("/exames/100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        List<Exame> lista = objectMapper.readValue(json, new TypeReference<List<Exame>>() {});
        assertEquals(lista.size(), 2);
    }

    @Test
    @DisplayName("Incluir novo exame")
    void testIncluir() throws Exception {
        String json = mockMvc.perform(post("/exames")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(getJsonExame()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        Exame exameNovo = objectMapper.readValue(json, Exame.class);
        assertEquals(exameNovo.getDescricao(), DESCRICAO_EXAME);
        assertNotNull(exameNovo.getId());

        // Sem dados obrigatórios
        mockMvc.perform(post("/exames")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Exame())))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Excluir exame existente")
    void testExcluir() throws Exception {
        mockMvc.perform(delete("/exames/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/exames/9999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

}
