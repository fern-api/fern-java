package com.fern.services.jersey.codegen;

import com.errors.ErrorDefinition;
import com.errors.ErrorProperty;
import com.errors.HttpErrorConfiguration;
import com.fern.codegen.GeneratedException;
import com.fern.codegen.GeneratedHttpService;
import com.fern.codegen.GeneratorContext;
import com.fern.model.codegen.ExceptionGenerator;
import com.services.commons.ResponseError;
import com.services.commons.ResponseErrors;
import com.services.commons.WireMessage;
import com.services.http.HttpEndpoint;
import com.services.http.HttpMethod;
import com.services.http.HttpService;
import com.services.http.PathParameter;
import com.types.AliasTypeDefinition;
import com.types.FernFilepath;
import com.types.NamedType;
import com.types.PrimitiveType;
import com.types.Type;
import com.types.TypeReference;
import java.util.Collections;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ServiceGeneratorTest {

    private static final String PACKAGE_PREFIX = "com";
    private static final GeneratorContext GENERATOR_CONTEXT = new GeneratorContext(
            Optional.of(PACKAGE_PREFIX),
            Collections.emptyMap());

    @Test
    public void test_basic() {
        HttpService testHttpService = HttpService.builder()
                .basePath("/person")
                .name(NamedType.builder()
                        .fernFilepath(FernFilepath.valueOf("fern"))
                        .name("PersonCrudService")
                        .build())
                .addEndpoints(HttpEndpoint.builder()
                        .endpointId("getPerson")
                        .path("/{personId}")
                        .method(HttpMethod.GET)
                        .errors(ResponseErrors.builder().discriminant("").build())
                        .addParameters(PathParameter.builder()
                                .key("personId")
                                .valueType(TypeReference.primitive(PrimitiveType.STRING))
                                .build())
                        .build())
                .addEndpoints(HttpEndpoint.builder()
                        .endpointId("createPerson")
                        .path("/create")
                        .method(HttpMethod.POST)
                        .errors(ResponseErrors.builder().discriminant("").build())
                        .request(WireMessage.builder()
                                .type(Type.alias(AliasTypeDefinition.builder()
                                        .aliasOf(TypeReference.named(NamedType.builder()
                                                .fernFilepath(FernFilepath.valueOf("fern"))
                                                .name("CreatePersonRequest")
                                                .build()))
                                        .build()))
                                .build())
                        .response(WireMessage.builder()
                                .type(Type.alias(AliasTypeDefinition.builder()
                                        .aliasOf(TypeReference.primitive(PrimitiveType.STRING))
                                        .build()))
                                .build())
                        .build())
                .build();
        HttpServiceGenerator httpServiceGenerator = new HttpServiceGenerator(
                GENERATOR_CONTEXT, Collections.emptyMap(), Collections.emptyList(), testHttpService);
        GeneratedHttpService generatedHttpService = httpServiceGenerator.generate();
        System.out.println(generatedHttpService.file().toString());
        Assertions.assertThat(generatedHttpService.generatedErrorDecoder()).isEmpty();
    }

    @Test
    public void test_withErrors() {
        ErrorDefinition personIdNotFound = ErrorDefinition.builder()
            .name(NamedType.builder()
                .fernFilepath(FernFilepath.valueOf("fern"))
                .name("PersonIdNotFound")
                .build())
            .addProperties(ErrorProperty.builder()
                .name("personId")
                .type(TypeReference.primitive(PrimitiveType.STRING))
                .build())
            .http(HttpErrorConfiguration.builder()
                .statusCode(400)
                .build())
            .build();
        HttpService testHttpService = HttpService.builder()
                .basePath("/person")
                .name(NamedType.builder()
                        .fernFilepath(FernFilepath.valueOf("fern"))
                        .name("PersonCrudService")
                        .build())
                .addEndpoints(HttpEndpoint.builder()
                        .endpointId("getPerson")
                        .path("/{personId}")
                        .method(HttpMethod.GET)
                        .errors(ResponseErrors.builder()
                                .discriminant("_type")
                                .addPossibleErrors(ResponseError.builder()
                                    .discriminantValue("notFound")
                                    .error(personIdNotFound.name())
                                    .build())
                                .build())
                        .addParameters(PathParameter.builder()
                                .key("personId")
                                .valueType(TypeReference.primitive(PrimitiveType.STRING))
                                .build())
                        .build())
                .addEndpoints(HttpEndpoint.builder()
                        .endpointId("createPerson")
                        .path("/create")
                        .method(HttpMethod.POST)
                        .errors(ResponseErrors.builder()
                                .discriminant("_type")
                                .build())
                        .request(WireMessage.builder()
                                .type(Type.alias(AliasTypeDefinition.builder()
                                        .aliasOf(TypeReference.named(NamedType.builder()
                                                .fernFilepath(FernFilepath.valueOf("fern"))
                                                .name("CreatePersonRequest")
                                                .build()))
                                        .build()))
                                .build())
                        .response(WireMessage.builder()
                                .type(Type.alias(AliasTypeDefinition.builder()
                                        .aliasOf(TypeReference.primitive(PrimitiveType.STRING))
                                        .build()))
                                .build())
                        .build())
                .build();
        ExceptionGenerator personIdNotFoundExceptionGenerator =
                new ExceptionGenerator(GENERATOR_CONTEXT, personIdNotFound);
        GeneratedException personIdNotFoundException = personIdNotFoundExceptionGenerator.generate();
        HttpServiceGenerator httpServiceGenerator = new HttpServiceGenerator(
                GENERATOR_CONTEXT,
                Collections.emptyMap(),
                Collections.singletonList(personIdNotFoundException),
                testHttpService);
        GeneratedHttpService generatedHttpService = httpServiceGenerator.generate();
        System.out.println(generatedHttpService.file().toString());
        Assertions.assertThat(generatedHttpService.generatedErrorDecoder()).isPresent();
        System.out.println(generatedHttpService.generatedErrorDecoder().get().file().toString());
    }
}
