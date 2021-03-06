/*
 * (c) Copyright 2022 Birch Solutions Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fern.model.codegen;

import com.fern.codegen.GeneratedInterface;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.IGeneratedFile;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.fern.model.codegen.types.AliasGenerator;
import com.fern.model.codegen.types.EnumGenerator;
import com.fern.model.codegen.types.ObjectGenerator;
import com.fern.model.codegen.types.UnionGenerator;
import com.fern.types.AliasTypeDeclaration;
import com.fern.types.DeclaredTypeName;
import com.fern.types.EnumTypeDeclaration;
import com.fern.types.ObjectTypeDeclaration;
import com.fern.types.Type;
import com.fern.types.TypeDeclaration;
import com.fern.types.UnionTypeDeclaration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TypeDefinitionGenerator implements Type.Visitor<IGeneratedFile> {

    private final TypeDeclaration typeDeclaration;
    private final GeneratorContext generatorContext;
    private final Map<DeclaredTypeName, GeneratedInterface> generatedInterfaces;
    private final PackageType packageType;

    public TypeDefinitionGenerator(
            TypeDeclaration typeDeclaration,
            GeneratorContext generatorContext,
            Map<DeclaredTypeName, GeneratedInterface> generatedInterfaces,
            PackageType packageType) {
        this.typeDeclaration = typeDeclaration;
        this.generatorContext = generatorContext;
        this.generatedInterfaces = generatedInterfaces;
        this.packageType = packageType;
    }

    @Override
    public IGeneratedFile visitObject(ObjectTypeDeclaration objectTypeDefinition) {
        Optional<GeneratedInterface> selfInterface =
                Optional.ofNullable(generatedInterfaces.get(typeDeclaration.name()));
        List<GeneratedInterface> extendedInterfaces = objectTypeDefinition._extends().stream()
                .map(generatedInterfaces::get)
                .sorted(Comparator.comparing(
                        generatedInterface -> generatedInterface.className().simpleName()))
                .collect(Collectors.toList());
        ObjectGenerator objectGenerator = new ObjectGenerator(
                typeDeclaration.name(),
                packageType,
                objectTypeDefinition,
                extendedInterfaces,
                selfInterface,
                generatorContext);
        return objectGenerator.generate();
    }

    @Override
    public IGeneratedFile visitUnion(UnionTypeDeclaration unionTypeDeclaration) {
        UnionGenerator unionGenerator =
                new UnionGenerator(typeDeclaration.name(), packageType, unionTypeDeclaration, generatorContext);
        return unionGenerator.generate();
    }

    @Override
    public IGeneratedFile visitAlias(AliasTypeDeclaration aliasTypeDeclaration) {
        AliasGenerator aliasGenerator =
                new AliasGenerator(aliasTypeDeclaration, packageType, typeDeclaration.name(), generatorContext);
        return aliasGenerator.generate();
    }

    @Override
    public IGeneratedFile visitEnum(EnumTypeDeclaration enumTypeDeclaration) {
        EnumGenerator enumGenerator =
                new EnumGenerator(typeDeclaration.name(), packageType, enumTypeDeclaration, generatorContext);
        return enumGenerator.generate();
    }

    @Override
    public IGeneratedFile visitUnknown(String unknownType) {
        throw new RuntimeException("Encountered unknown Type: " + unknownType);
    }
}
