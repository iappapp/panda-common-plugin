package com.github.iappapp.panda.generate;

import com.github.iappapp.panda.generate.definition.FieldDefinition;
import com.github.iappapp.panda.generate.definition.ProjectDefinition;
import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 代码生成上下文元数据
 */
@Data
@Builder
public class GenerateContext {
    // --- 基础配置 ---
    private String author;
    private String createDate;
    private String className;
    private String tableName;
    private String tableDesc;
    
    // --- 技术开关 ---
    // 是否支持 Lombok
    private boolean useLombok;
    // 是否支持 MyBatis-Plus
    private boolean useMyBatisPlus;
    //
    private boolean useSwagger;

    // --- 字段列表 ---
    private List<FieldDefinition> fields;

    // 主键键名
    private String primaryKeyName;

    // 包基础地址
    private String basePackage;

    // --- 包名配置 (精细化控制) ---
    /*
    // DO 所在包
    private String doPackage;
    // Mapper 所在包
    private String mapperPackage;
    // Info 所在包
    private String coreModelPackage;
    // Convert Utils 所在包
    private String convertPackage;
    // IService 所在包
    private String servicePackage;
    // Model (DTO) 所在包
    private String facadeModelPackage;
    // Facade 接口所在包
    private String facadePackage;
    */
    // 工程信息
    private ProjectDefinition project;
}