package org.example.demo1.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeConValoresPorDefecto() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(content().string("<h1>Hola worl!<h1>"));
    }

    @Test
    void homeConParametros() throws Exception {
        mockMvc.perform(get("/home")
                        .param("name", "Juan")
                        .param("languaje", "EN"))
                .andExpect(status().isOk())
                .andExpect(content().string("<h1>Hi Juan!<h1>"));
    }
}
