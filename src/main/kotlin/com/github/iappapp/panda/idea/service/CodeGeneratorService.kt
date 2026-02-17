package com.github.iappapp.panda.idea.service

import com.github.iappapp.panda.common.generate.CodeGeneratorEngine
import com.github.iappapp.panda.common.generate.GenerateContext
import com.github.iappapp.panda.common.generate.definition.ProjectDefinition
import com.github.iappapp.panda.idea.parser.DDLSqlParser
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import java.io.File
import java.time.LocalDate
import java.util.*

/**
 * 代码生成服务
 * 负责整合SQL解析和代码生成功能
 *
 * @author system
 * @date 2026-02-12
 */
class CodeGeneratorService(private val project: Project) {

    private val engine = CodeGeneratorEngine()
    private val parser = DDLSqlParser()

    fun generateCode(ddlSql: String, config: GenerateCodeConfig): GenerateCodeResult {
        return try {
            val parseResult = parser.parse(ddlSql)
            val context = buildGenerateContext(parseResult, config)
            val result = engine.batchGenerateAll(context)
            GenerateCodeResult(true, null, result)
        } catch (e: DDLSqlParser.ParseException) {
            GenerateCodeResult(false, "SQL解析失败: ${e.message}", null)
        } catch (e: Exception) {
            GenerateCodeResult(false, "代码生成失败: ${e.message}", null)
        }
    }

    private fun buildGenerateContext(
        parseResult: DDLSqlParser.ParseResult,
        config: GenerateCodeConfig
    ): GenerateContext {
        return GenerateContext.builder()
            .author(config.author)
            .className(parseResult.className)
            .tableName(parseResult.tableName)
            .useLombok(config.isUseLombok)
            .basePackage(config.basicPackage)
            .useMyBatisPlus(config.isUseMyBatisPlus)
            .fields(parseResult.fields)
            .primaryKeyName(parseResult.primaryKey?.name ?: "id")
            .project(
                ProjectDefinition.builder()
                    .projectRoot(getProjectRoot())
                    .projectName(project.name)
                    .dalModuleName(config.dalModuleName)
                    .coreModelModuleName(config.coreModelModuleName)
                    .facadeModuleName(config.facadeModuleName)
                    .coreServiceModuleName(config.coreServiceModuleName)
                    .build()
            )
            .build()
    }

    private fun getProjectRoot(): String {
        val basePath = project.basePath ?: ""
        val projectFile = File(basePath)
        return projectFile.absolutePath
    }

    class GenerateCodeConfig {
        var author: String = "panda common generate"
        var isUseLombok: Boolean = true
        var isUseMyBatisPlus: Boolean = true
        var basicPackage: String = "com.aistarfish.cdssai."

        var dalModuleName: String = "/app/common-dal"
        var coreModelModuleName: String = "/app/core-model"
        var facadeModuleName: String = "/app/common-facade"
        var coreServiceModuleName: String = "/app/core-service"
    }

    class GenerateCodeResult(
        val isSuccess: Boolean,
        val errorMessage: String?,
        val details: CodeGeneratorEngine.GenerateResult?
    )

    companion object {
        fun getInstance(project: Project): CodeGeneratorService {
            return ServiceManager.getService(project, CodeGeneratorService::class.java)
        }
    }
}
