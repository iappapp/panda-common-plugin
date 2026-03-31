package com.github.iappapp.panda.generate.definition;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldDefinition {
    // Java 属性名：applicationName
    private String name;
    // DB 字段名：application_name
    private String columnName;
    // Java 类型：String / Date
    private String type;
    // 字段备注：应用名
    private String comment;
    // 是否为主键
    private boolean primaryKey = false;
}