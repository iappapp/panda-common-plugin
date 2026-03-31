package com.github.iappapp.panda.generate.utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.definition.FieldDefinition;
import com.github.iappapp.panda.generate.exception.SqlParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqlParserUtils {
    private final static String TABLE_NAME = "业务表";

    public static GenerateContext parseSqlToCtx(String sql, DbType dbType) {
        // 1. 使用 Druid 解析 SQL
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        if (stmtList.isEmpty() || !(stmtList.get(0) instanceof SQLCreateTableStatement)) {
            throw new SqlParseException("仅支持 CREATE TABLE 语句解析");
        }

        SQLCreateTableStatement createTable = (SQLCreateTableStatement) stmtList.get(0);

        // 2. 解析表名和类名
        String rawTableName = createTable.getTableName()
                .replace("`", "").replace("\"", "");
        String className = underlineToCamel(rawTableName, true);
        String tableComment = createTable.getComment() != null ?
                createTable.getComment().toString().replace("'", "") : TABLE_NAME;

        List<FieldDefinition> fields = new ArrayList<>();

        FieldDefinition primaryField = null;

        // 3. 遍历列定义
        for (SQLTableElement element : createTable.getTableElementList()) {
            if (element instanceof SQLColumnDefinition) {
                SQLColumnDefinition column = (SQLColumnDefinition) element;
                String columnName = column.getNameAsString()
                        .replace("`", "").replace("\"", "");
                String dataType = column.getDataType().getName().toLowerCase();

                FieldDefinition definition = FieldDefinition.builder()
                        .columnName(columnName)
                        .name(underlineToCamel(columnName, false))
                        .type(convertDbTypeToJavaType(dataType))
                        .comment(Objects.nonNull(column.getComment())
                                ? column.getComment().toString().replace("'", "") : "")
                        .primaryKey(column.isPrimaryKey() || columnName.equalsIgnoreCase("id"))
                        .build();
                if (definition.isPrimaryKey()) {
                    primaryField = definition;
                }
                fields.add(definition);
            }
        }

        return GenerateContext.builder()
                .tableName(rawTableName)
                .className(className)
                .tableDesc(tableComment)
                .fields(fields)
               // .primaryKey(primaryField)
                .primaryKeyName(primaryField.getName())
                .build();
    }

    // 数据库类型转 Java 类型逻辑
    private static String convertDbTypeToJavaType(String dbType) {
        switch (dbType) {
            case "smallint":
                return "Short";
            case "bigint":
            case "int8":
                return "Long";
            case "int":
            case "integer":
            case "int4":
            case "mediumint":
            case "tinyint":
                return "Integer";
            case "double":
                return "Double";
            case "float":
                return "Float";
            case "varchar":
            case "text":
            case "tinytext":
            case "mediumtext":
            case "longtext":
            case "char":
            case "character varying":
            case "json":
                return "String";
            case "datetime":
            case "timestamp":
            case "time":
            case "date":
                return "Date";
            case "decimal":
            case "numeric":
                return "BigDecimal";
            case "boolean":
            case "bool":
            case "bit":
                return "Boolean";
            case "binary":
            case "varbinary":
            case "blob":
            case "tinyblob":
            case "mediumblob":
            case "smallblob":
            case "longblob":
                return "byte[]";
            default:
                return "Object";
        }
    }

    // 下划线转驼峰
    private static String underlineToCamel(String str, boolean firstUpper) {
        StringBuilder sb = new StringBuilder();
        String[] split = str.split("_");
        for (String s : split) {
            if (sb.length() == 0 && !firstUpper) {
                sb.append(s.toLowerCase());
            } else {
                sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String ddl = "CREATE TABLE `chain`  (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '链路id',\n" +
                "  `application_name` varchar(32)  NULL  COMMENT '应用名',\n" +
                "  `chain_id` varchar(64)    NULL COMMENT '链路id',\n" +
                "  `chain_desc` varchar(64)  NULL  COMMENT '链路描述',\n" +
                "  `el_data` text   NULL COMMENT '链路el data',\n" +
                "  `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "  `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "  PRIMARY KEY (`id`) USING BTREE,\n" +
                "  UNIQUE INDEX `application_chain`(`application_name`,`chain_id`) USING BTREE\n" +
                ") COMMENT='链路表' ENGINE = InnoDB AUTO_INCREMENT = 1;";

        System.out.println(parseSqlToCtx(ddl, DbType.mysql));
    }
}