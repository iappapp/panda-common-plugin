package com.github.iappapp.panda.idea.parser

import com.github.iappapp.panda.common.generate.definition.FieldDefinition
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * DDL SQL 解析器
 * 支持MySQL、PostgreSQL等SQL方言
 *
 * @author system
 * @date 2026-02-12
 */
class DDLSqlParser {

    fun parse(ddlSql: String?): ParseResult {
        if (ddlSql == null || ddlSql.trim().isEmpty()) {
            throw ParseException("SQL 语句不能为空")
        }

        val normalizedSql = normalizeSql(ddlSql)

        val tableName = extractTableName(normalizedSql)
        if (tableName == null) {
            throw ParseException("无法从SQL中解析出表名")
        }

        val fields = extractFields(normalizedSql)
        if (fields.isEmpty()) {
            throw ParseException("表中至少需要一个字段")
        }

        val primaryKey = identifyPrimaryKey(fields)

        return ParseResult(tableName, fields, primaryKey)
    }

    private fun normalizeSql(sql: String): String {
        var text = sql
        text = text.replace("--[^\n]*".toRegex(), "")
        text = text.replace("/\\*.*?\\*/".toRegex(), "")
        text = text.replace("\\s+".toRegex(), " ")
        return text.trim()
    }

    private fun extractTableName(sql: String): String? {
        val matcher: Matcher = TABLE_PATTERN.matcher(sql)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun extractFields(sql: String): List<FieldDefinition> {
        val fields = mutableListOf<FieldDefinition>()

        val contentPattern = Pattern.compile("\\((.*)\\)", Pattern.DOTALL)
        val contentMatcher = contentPattern.matcher(sql)

        if (!contentMatcher.find()) {
            throw ParseException("无效的CREATE TABLE语法")
        }

        val tableContent = contentMatcher.group(1)
        val definitions = tableContent.split(",(?![^()]*\\))".toRegex())

        for (definition in definitions) {
            val trimmed = definition.trim()

            if (
                trimmed.uppercase().startsWith("PRIMARY KEY") ||
                trimmed.uppercase().startsWith("UNIQUE") ||
                trimmed.uppercase().startsWith("FOREIGN KEY") ||
                trimmed.uppercase().startsWith("CONSTRAINT") ||
                trimmed.uppercase().startsWith("KEY") ||
                trimmed.uppercase().startsWith("INDEX")
            ) {
                continue
            }

            val field = parseFieldDefinition(trimmed)
            if (field != null) {
                fields.add(field)
            }
        }

        return fields
    }

    private fun parseFieldDefinition(definition: String): FieldDefinition? {
        if (definition.isEmpty()) {
            return null
        }

        val namePattern = Pattern.compile("^`?([\\w_]+)`?\\s+")
        val nameMatcher = namePattern.matcher(definition)
        if (!nameMatcher.find()) {
            return null
        }

        val fieldName = nameMatcher.group(1)
        val remainder = definition.substring(nameMatcher.end())

        val typePattern = Pattern.compile("^([\\w()]+)")
        val typeMatcher = typePattern.matcher(remainder)
        if (!typeMatcher.find()) {
            return null
        }

        val fieldType = typeMatcher.group(1)
        val javaType = sqlTypeToJavaType(fieldType)

        val comment = extractComment(definition)
        val isPrimaryKey = definition.uppercase().contains("PRIMARY KEY")
        val javaFieldName = underscoreToCamelCase(fieldName)

        return FieldDefinition.builder()
            .name(javaFieldName)
            .columnName(fieldName)
            .type(javaType)
            .comment(comment)
            .primaryKey(isPrimaryKey)
            .build()
    }

    private fun sqlTypeToJavaType(sqlType: String): String {
        val type = sqlType.lowercase()

        return when {
            type.contains("int") -> "Integer"
            type.contains("bigint") || type.contains("long") -> "Long"
            type.contains("float") || type.contains("double") -> "Double"
            type.contains("decimal") || type.contains("number") -> "java.math.BigDecimal"
            type.contains("date") || type.contains("timestamp") -> "java.util.Date"
            type.contains("time") -> "java.util.Date"
            type.contains("boolean") || type.contains("bit") -> "Boolean"
            type.contains("blob") || type.contains("binary") -> "byte[]"
            else -> "String"
        }
    }

    private fun extractComment(definition: String): String {
        val matcher = COMMENT_PATTERN.matcher(definition)
        return if (matcher.find()) matcher.group(1) else ""
    }

    private fun underscoreToCamelCase(name: String): String {
        val result = StringBuilder()
        var toUpperCase = false

        for (c in name) {
            if (c == '_') {
                toUpperCase = true
            } else {
                if (toUpperCase && c.isLetter()) {
                    result.append(c.uppercaseChar())
                    toUpperCase = false
                } else {
                    result.append(c)
                    toUpperCase = false
                }
            }
        }

        return result.toString()
    }

    private fun identifyPrimaryKey(fields: List<FieldDefinition>): FieldDefinition? {
        for (field in fields) {
            if (field.isPrimaryKey) {
                return field
            }
        }

        return fields.firstOrNull()
    }

    class ParseResult(
        val tableName: String,
        val fields: List<FieldDefinition>,
        val primaryKey: FieldDefinition?
    ) {
        val className: String
            get() = tableNameToClassName(tableName)

        companion object {
            private fun tableNameToClassName(tableNameInput: String): String {
                var tableName = tableNameInput
                if (tableName.startsWith("t_")) {
                    tableName = tableName.substring(2)
                }

                val result = StringBuilder()
                var toUpperCase = true

                for (c in tableName) {
                    if (c == '_') {
                        toUpperCase = true
                    } else {
                        if (toUpperCase) {
                            result.append(c.uppercaseChar())
                            toUpperCase = false
                        } else {
                            result.append(c)
                        }
                    }
                }

                return result.toString()
            }
        }
    }

    class ParseException(message: String, cause: Throwable? = null) : Exception(message, cause)

    companion object {
        private val TABLE_PATTERN =
            Pattern.compile("CREATE\\s+TABLE\\s+`?([\\w_]+)`?\\s*\\(", Pattern.CASE_INSENSITIVE)

        private val COLUMN_PATTERN =
            Pattern.compile(
                "`?([\\w_]+)`?\\s+([\\w()]+)(?:\\s+(NOT\\s+NULL|NULL|AUTO_INCREMENT|PRIMARY\\s+KEY|UNIQUE|DEFAULT\\s+[^,;\\)]+|CHECK\\s*\\([^)]+\\)|COMMENT\\s+'[^']*'|ON\\s+UPDATE[^,;\\)]*|CONSTRAINT[^,;\\)]*|FOREIGN\\s+KEY[^,;\\)]*|REFERENCES[^,;\\)]*)[^,]*)*",
                Pattern.CASE_INSENSITIVE
            )

        private val COMMENT_PATTERN =
            Pattern.compile("COMMENT\\s+'([^']*)'", Pattern.CASE_INSENSITIVE)
    }
}
