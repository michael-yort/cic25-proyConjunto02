package es.cic.curso25.cic25_proyConjunto02.uc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.cic25_proyConjunto02.controller.PersonaController;
import es.cic.curso25.cic25_proyConjunto02.model.Perro;
import es.cic.curso25.cic25_proyConjunto02.model.Persona;



@SpringBootTest
@AutoConfigureMockMvc
public class SeHacenAmigosPerroPersonaIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testEstablecerAmistad() throws Exception {
        /**
         * Creo primero una persona
         */
       
        Persona persona = new Persona();
        persona.setDni("12345678a");
        persona.setNombre("Javier");
        persona.setApellidos("Martínez Samperio");
        persona.setEdad(30);


        Perro perroTest = new Perro();
        perroTest.setNombre("Firulais");
        perroTest.setPeso(10);
        perroTest.setRaza("Galgo");

        perroTest.setPersona(persona);
        // persona.setPerro(perroTest);


        //convertimos el objeto de tipo amistad en json con ObjectMapper
        String perroACrearJson = objectMapper.writeValueAsString(perroTest);
        
        
        //con MockMvc simulamos la peticion HTTP para crear una persona
        MvcResult mvcResult = mockMvc.perform(post("/persona/amistad")
        .contentType("application/json")
        .content(perroACrearJson))
        .andExpect(status().isOk())
        .andExpect( personaResult ->{
            assertNotNull(
                objectMapper.readValue(
                    personaResult.getResponse().getContentAsString(), Perro.class), 
                "Le tiré el palo y el perro ha vuelto");
            })
        .andReturn();


        Perro perroCreado = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Perro.class);
        Long id = perroCreado.getId();

        mockMvc.perform(get("/perro/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertEquals(objectMapper.readValue(result.getResponse().getContentAsString(), Perro.class).getId(),
                            id);
                });   
         
                
        perroCreado.getPersona().setApellidos("Otros");


        String perroJson = objectMapper.writeValueAsString(perroCreado);

        mockMvc.perform(put("/perro")
                .contentType("application/json")
                .content(perroJson))
                .andDo(print())                
                .andExpect(status().isOk());





        mockMvc.perform(delete("/perro/" + id))
                .andDo(print())        
                .andExpect(status().isOk());                
    }
}
