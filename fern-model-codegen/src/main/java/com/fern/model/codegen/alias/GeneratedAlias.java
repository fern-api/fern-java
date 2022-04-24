package com.fern.model.codegen.alias;

import com.fern.codegen.GeneratedFileWithDefinition;
import com.fern.immutables.StagedBuilderStyle;
import com.types.AliasTypeDefinition;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedAlias extends GeneratedFileWithDefinition<AliasTypeDefinition> {

    static ImmutableGeneratedAlias.FileBuildStage builder() {
        return ImmutableGeneratedAlias.builder();
    }
}
