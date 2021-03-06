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
package com.fern.spring;

import com.fern.codegen.utils.server.HttpPathUtils;
import com.fern.types.services.HttpEndpoint;
import com.fern.types.services.HttpMethod;
import com.squareup.javapoet.AnnotationSpec;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public final class SpringHttpMethodAnnotationVisitor implements HttpMethod.Visitor<AnnotationSpec> {

    private final String path;

    public SpringHttpMethodAnnotationVisitor(HttpEndpoint httpEndpoint) {
        this.path = HttpPathUtils.getPathWithCurlyBracedPathParams(httpEndpoint.path());
    }

    @Override
    public AnnotationSpec visitGET() {
        return AnnotationSpec.builder(GetMapping.class)
                .addMember("value", "$S", path)
                .build();
    }

    @Override
    public AnnotationSpec visitPOST() {
        return AnnotationSpec.builder(PostMapping.class)
                .addMember("value", "$S", path)
                .build();
    }

    @Override
    public AnnotationSpec visitPUT() {
        return AnnotationSpec.builder(PutMapping.class)
                .addMember("value", "$S", path)
                .build();
    }

    @Override
    public AnnotationSpec visitDELETE() {
        return AnnotationSpec.builder(DeleteMapping.class)
                .addMember("value", "$S", path)
                .build();
    }

    @Override
    public AnnotationSpec visitPATCH() {
        return AnnotationSpec.builder(PatchMapping.class)
                .addMember("value", "$S", path)
                .build();
    }

    @Override
    public AnnotationSpec visitUnknown(String unknownType) {
        throw new RuntimeException("Encountered unknown HttpMethod: " + unknownType);
    }
}
