package br.com.jaanalves.fintechcoreapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fintech Core API")
                        .version("1.0.0")
                        .description("API RESTful para gestão de contas bancárias, depósitos, transferências e extrato paginado")
                );
    }
}
