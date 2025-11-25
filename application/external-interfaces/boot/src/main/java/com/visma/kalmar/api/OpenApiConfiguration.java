package com.visma.kalmar.api;

import com.visma.kalmar.api.model.Error;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info =
        @Info(
                title = "Feature API",
                description =
                        "The Feature API allows the <b>provisioning</b> of <b>feature for customers"
                                + "</b> . This version of the API is still in "
                                + "development. It can suffer any changes, without any prior notice."
                                + "<p>All calls, with the exception of <b>/api/v1/openweb/register</b>, require a Visma Connect access token. </p> "
                                + " <p>At the moment the <b>/api/v1/openweb/register</b> call is open, but authentication maybe added for it without notice</p>"
                                + "<br> <p><b>Note</b>: Resources with alternative identifiers can be retrieved by referencing in the URL path that identifier. "
                                + "For example, Users may be referenced by email by putting in the path the name of the identifier (email), followed by the actual email address. "
                                + "See <a href=#/Users/fetchUserByEmail>`GET /v1/users/email/{userEmail}`</a></p>",
                contact = @Contact(name = "ODP Team", email = "rd-ccmp-app-support-odp@visma.com"),
                extensions = {
                        @Extension(
                                name = "info-extension",
                                properties = {
                                        @ExtensionProperty(
                                                name = "x-api-id",
                                                value = "d89ddc6d-b91f-49c2-89cb-cb5cbb5a86bf"),
                                        @ExtensionProperty(name = "x-audience", value = "company-internal")
                                })
                }),
        servers = {@Server(url = "/")})
@SecurityScheme(
        name = "jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
@Configuration
class OpenApiConfiguration {

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> addSchema(openApi, "Error", Error.class);
    }

    private void addSchema(
            final OpenAPI openApi, final String schemaName, final Class<?> schemaClass) {
        var schema =
                ModelConverters.getInstance()
                        .resolveAsResolvedSchema(new AnnotatedType(schemaClass))
                        .schema;
        schema.name(schemaName);

        var schemas = openApi.getComponents().getSchemas();
        schemas.put(schema.getName(), schema);
    }
}
