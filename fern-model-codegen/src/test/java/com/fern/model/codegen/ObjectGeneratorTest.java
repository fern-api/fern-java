package com.fern.model.codegen;

import com.fern.codegen.GeneratedObject;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.fern.model.codegen.ObjectGenerator;
import com.fern.model.codegen.TestConstants;
import com.fern.codegen.GeneratedInterface;
import com.fern.model.codegen.InterfaceGenerator;
import com.types.ContainerType;
import com.types.FernFilepath;
import com.types.NamedType;
import com.types.ObjectProperty;
import com.types.ObjectTypeDefinition;
import com.types.PrimitiveType;
import com.types.Type;
import com.types.TypeDefinition;
import com.types.TypeReference;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ObjectGeneratorTest {

    @Test
    public void test_basic() {
        ObjectTypeDefinition objectTypeDefinition = ObjectTypeDefinition.builder()
                .addProperties(ObjectProperty.builder()
                        .key("docs")
                        .valueType(TypeReference.container(
                                ContainerType.optional(TypeReference.primitive(PrimitiveType.STRING))))
                        .build())
                .build();
        ObjectGenerator objectGenerator = new ObjectGenerator(
                NamedType.builder()
                        .fernFilepath(FernFilepath.valueOf("com/fern"))
                        .name("WithDocs")
                        .build(),
                PackageType.TYPES,
                objectTypeDefinition,
                Collections.emptyList(),
                Optional.empty(),
                TestConstants.GENERATOR_CONTEXT);
        GeneratedObject generatedObject = objectGenerator.generate();
        System.out.println(generatedObject.file().toString());
    }

    @Test
    public void test_skipOwnFields() {
        ObjectTypeDefinition withDocsObjectTypeDefinition = ObjectTypeDefinition.builder()
                .addProperties(ObjectProperty.builder()
                        .key("docs")
                        .valueType(TypeReference.container(
                                ContainerType.optional(TypeReference.primitive(PrimitiveType.STRING))))
                        .build())
                .build();
        TypeDefinition withDocsTypeDefinition = TypeDefinition.builder()
                .name(NamedType.builder()
                        .fernFilepath(FernFilepath.valueOf("com/fern"))
                        .name("WithDocs")
                        .build())
                .shape(Type._object(withDocsObjectTypeDefinition))
                .build();
        InterfaceGenerator interfaceGenerator = new InterfaceGenerator(
                withDocsObjectTypeDefinition, withDocsTypeDefinition.name(), TestConstants.GENERATOR_CONTEXT);
        GeneratedInterface withDocsInterface = interfaceGenerator.generate();
        ObjectGenerator objectGenerator = new ObjectGenerator(
                withDocsTypeDefinition.name(),
                PackageType.TYPES,
                withDocsObjectTypeDefinition,
                Collections.emptyList(),
                Optional.of(withDocsInterface),
                TestConstants.GENERATOR_CONTEXT);
        GeneratedObject withDocsObject = objectGenerator.generate();
        System.out.println(withDocsObject.file().toString());
    }

    @Test
    public void test_lowerCasedClassName() {
        ObjectTypeDefinition objectTypeDefinition = ObjectTypeDefinition.builder()
                .addProperties(ObjectProperty.builder()
                        .key("prop")
                        .valueType(TypeReference.container(
                                ContainerType.optional(TypeReference.primitive(PrimitiveType.STRING))))
                        .build())
                .build();
        TypeDefinition typeDefinition = TypeDefinition.builder()
                .name(NamedType.builder()
                        .fernFilepath(FernFilepath.valueOf("com/fern"))
                        .name("lowercase")
                        .build())
                .shape(Type._object(objectTypeDefinition))
                .build();
        ObjectGenerator objectGenerator = new ObjectGenerator(
                typeDefinition.name(),
                PackageType.TYPES,
                objectTypeDefinition,
                Collections.emptyList(),
                Optional.empty(),
                TestConstants.GENERATOR_CONTEXT);
        GeneratedObject object = objectGenerator.generate();
        System.out.println(object.file().toString());
    }

    @Test
    public void test_underscoredProperty() {
        ObjectTypeDefinition objectTypeDefinition = ObjectTypeDefinition.builder()
                .addProperties(ObjectProperty.builder()
                        .key("_class")
                        .valueType(TypeReference.container(
                                ContainerType.optional(TypeReference.primitive(PrimitiveType.STRING))))
                        .build())
                .addProperties(ObjectProperty.builder()
                        .key("_returns")
                        .valueType(TypeReference.primitive(PrimitiveType.STRING))
                        .build())
                .build();
        TypeDefinition typeDefinition = TypeDefinition.builder()
                .name(NamedType.builder()
                        .fernFilepath(FernFilepath.valueOf("com/fern"))
                        .name("Test")
                        .build())
                .shape(Type._object(objectTypeDefinition))
                .build();
        ObjectGenerator objectGenerator = new ObjectGenerator(
                typeDefinition.name(),
                PackageType.TYPES,
                objectTypeDefinition,
                Collections.emptyList(),
                Optional.empty(),
                TestConstants.GENERATOR_CONTEXT);
        GeneratedObject object = objectGenerator.generate();
        System.out.println(object.file().toString());
    }
}