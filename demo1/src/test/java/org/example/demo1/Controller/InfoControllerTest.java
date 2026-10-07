package org.example.demo1.Controller;

import org.example.demo1.config.AppInfoProperties;
import org.example.demo1.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InfoController.class)
@Import({SecurityConfig.class, InfoControllerTest.TestConfig.class})
public class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    @EnableConfigurationProperties(AppInfoProperties.class)
    static class TestConfig {
        @Bean
        public AppInfoProperties appInfoProperties() {
            AppInfoProperties props = new AppInfoProperties();
            props.setName("Gestor de Inventario");
            props.setVersion("0.0.1-SNAPSHOT");
            props.setEnvironment("dev");
            props.setDeveloper("Michael");
            props.setEmail("michael@example.com");
            return props;
        }
    }

    @Test
    void getInfoReturnsOkAndValidJson() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Gestor de Inventario"))
                .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))
                .andExpect(jsonPath("$.environment").value("dev"))
                .andExpect(jsonPath("$.developer").value("Michael"))
                .andExpect(jsonPath("$.email").value("michael@example.com"))
                .andExpect(jsonPath("$.email").value(matchesPattern("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")));
    }
}
