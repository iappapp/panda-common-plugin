package com.github.iappapp.panda.generate.definition;

import lombok.Builder;
import lombok.Data;

/**
 * @author iappapp
 * @date 2026-02-08 21:03
 * @description
 */
@Data
@Builder
public class ProjectDefinition {
    private String projectRoot;

    private String projectName;

    private String dalModuleName;

    private String coreModelModuleName;

    private String facadeModuleName;

    private String coreServiceModuleName;
}
