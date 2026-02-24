package com.github.iappapp.panda.plugin.service

import com.alibaba.druid.DbType
import com.github.iappapp.panda.common.generate.CodeGeneratorEngine
import com.github.iappapp.panda.common.generate.GenerateContext
import com.github.iappapp.panda.common.generate.utils.SqlParserUtils
import com.github.iappapp.panda.common.generate.definition.ProjectDefinition
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.io.File
import java.util.UUID

/**
 * 代码生成服务
 * 负责整合SQL解析和代码生成功能
 *
 * @author system
 * @date 2026-02-12
 */
class CodeGeneratorService(private val project: Project) {
    private val engine = CodeGeneratorEngine()
    private val logger = Logger.getInstance(CodeGeneratorService::class.java)

    fun generateCode(ddlSql: String, config: GenerateCodeConfig): GenerateCodeResult {
        val traceId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()
        logger.info(
            "[CodeGen][$traceId] Start generate code, project=${project.name}, ddlLength=${ddlSql.length}, basePackage=${config.basicPackage}, projectRoot=${config.projectRoot.ifBlank { "<auto>" }}"
        )

        return try {
            val generateContext = SqlParserUtils.parseSqlToCtx(ddlSql, DbType.mysql)
            val context = buildGenerateContext(generateContext, config)
            val result = engine.batchGenerateAll(context)
            val elapsed = System.currentTimeMillis() - startTime
            logger.info(
                "[CodeGen][$traceId] Generate code success, table=${generateContext.tableName}, class=${generateContext.className}, elapsedMs=$elapsed"
            )
            GenerateCodeResult(true, null, result)
        } catch (e: Exception) {
            val elapsed = System.currentTimeMillis() - startTime
            logger.warn(
                "[CodeGen][$traceId] Generate code failed, elapsedMs=$elapsed, ddlPreview=${ddlSql.safePreview()}, error=${e.message}",
                e
            )
            GenerateCodeResult(false, "代码生成失败[$traceId]: ${e.message}", null)
        }
    }

    private fun String.safePreview(maxLength: Int = 200): String {
        val normalized = this.replace("\n", " ").replace("\r", " ").trim()
        return if (normalized.length <= maxLength) normalized else normalized.substring(0, maxLength) + "..."
    }

    private fun buildGenerateContext(
        generateContext: GenerateContext,
        config: GenerateCodeConfig
    ): GenerateContext {
            var projectDefinition = ProjectDefinition.builder()
            .projectRoot(config.projectRoot.ifBlank { getProjectRoot() })
            .dalModuleName(config.dalModuleName)
            .coreModelModuleName(config.coreModelModuleName)
            .facadeModuleName(config.facadeModuleName)
            .coreServiceModuleName(config.coreServiceModuleName)
            .build();

        return GenerateContext.builder()
            .author(config.author)
            .className(generateContext.className)
            .tableDesc(generateContext.tableDesc)
            .tableName(generateContext.tableName)
            .fields(generateContext.fields)
            .primaryKeyName(generateContext.primaryKeyName)
            .project(projectDefinition)
            .useLombok(config.isUseLombok)
            .basePackage(config.basicPackage)
            .useMyBatisPlus(config.isUseMyBatisPlus)
            .build()
    }

    private fun getProjectRoot(): String {
        val basePath = project.basePath ?: ""
        val projectFile = File(basePath)
        return projectFile.absolutePath
    }

    class GenerateCodeConfig {
        var author: String = "iappapp"
        var isUseLombok: Boolean = true
        var isUseMyBatisPlus: Boolean = true
        var basicPackage: String = "com.aistarfish.cdssai."
        var projectRoot: String = ""

        var dalModuleName: String = "app/common-dal"
        var coreModelModuleName: String = "app/core-model"
        var facadeModuleName: String = "app/common-facade"
        var coreServiceModuleName: String = "app/core-service"
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
