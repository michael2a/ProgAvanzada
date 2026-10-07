package org.example.demo1.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/home")

    public String home(@RequestParam(value = "name", defaultValue = "worl") String name,
                       @RequestParam(value= "languaje", defaultValue = "ES") String languaje) {
        String frase;

        if(languaje.equalsIgnoreCase("ES")){
            frase = "Hola";
        }else if(languaje.equalsIgnoreCase("EN")){
            frase = "Hi";
        }else if(languaje.equalsIgnoreCase("PO")){
            frase = "Olá";
        }else{
            frase = "Hola";
        }
        return String.format("<h1>%s %s!<h1>", frase, name);
    }
}
