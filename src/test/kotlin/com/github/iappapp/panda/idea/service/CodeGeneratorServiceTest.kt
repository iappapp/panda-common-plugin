package com.github.iappapp.panda.idea.service

import com.github.iappapp.panda.plugin.service.CodeGeneratorService
import com.intellij.openapi.project.Project
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.nio.file.Files

class CodeGeneratorServiceTest {

    @Test
    fun `default generate config should contain expected values`() {
        val config = CodeGeneratorService.GenerateCodeConfig()

        assertEquals("iappapp", config.author)
        assertTrue(config.isUseLombok)
        assertTrue(config.isUseMyBatisPlus)
        assertEquals("com.aistarfish.cdssai.", config.basicPackage)
        assertEquals("", config.projectRoot)
    }

    @Test
    fun `generateCode should fail for invalid ddl`() {
        val project = mockProject(basePath = "/Users/mac/Desktop/cdssai", name = "cdssai")
        val service = CodeGeneratorService(project)

        val result = service.generateCode("not a ddl", CodeGeneratorService.GenerateCodeConfig())

        assertFalse(result.isSuccess)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `getProjectRoot should return absolute base path`() {
        val project = mockProject(basePath = "/Users/mac/Desktop/cdssai", name = "cdssai")
        val service = CodeGeneratorService(project)

        val method = CodeGeneratorService::class.java.getDeclaredMethod("getProjectRoot")
        method.isAccessible = true
        val root = method.invoke(service) as String

        assertEquals("/Users/mac/Desktop/cdssai", root)
    }

    @Test
    fun `generateCode should succeed with ui-like config and valid ddl`() {
        val tempProjectRoot = Files.createTempDirectory("panda-codegen-test-").toFile()
        val project = mockProject(basePath = tempProjectRoot.absolutePath, name = "cdssai")
        val service = CodeGeneratorService(project)

        val config = CodeGeneratorService.GenerateCodeConfig().apply {
            author = "iappapp"
            basicPackage = "com.aistarfish.cdssai."
            projectRoot = "/Users/mac/Desktop/cdssai"
            isUseLombok = true
            isUseMyBatisPlus = true
            dalModuleName = "app/common-dal"
            coreModelModuleName = "app/core-model"
            facadeModuleName = "app/common-facade"
            coreServiceModuleName = "app/core-service"
        }

        val ddlSql = """
            CREATE TABLE user_account (
                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                username VARCHAR(64) NOT NULL COMMENT '用户名',
                created_at DATETIME COMMENT '创建时间'
            ) COMMENT='用户表';
        """.trimIndent()

        val result = service.generateCode(ddlSql, config)

        assertTrue(result.isSuccess, result.errorMessage ?: "代码生成失败")
        assertNotNull(result.details)
        assertTrue((result.details?.successCount ?: 0) > 0)
    }

    private fun mockProject(basePath: String, name: String): Project {
        val project = Mockito.mock(Project::class.java)
        Mockito.`when`(project.basePath).thenReturn(basePath)
        Mockito.`when`(project.name).thenReturn(name)
        return project
    }
}
