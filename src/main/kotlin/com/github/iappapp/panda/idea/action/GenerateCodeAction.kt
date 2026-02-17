package com.github.iappapp.panda.idea.action

import com.github.iappapp.panda.idea.service.CodeGeneratorService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages

/**
 * 生成代码的Action
 * 支持从编辑器中选中的DDL SQL生成代码
 *
 * @author system
 * @date 2026-02-12
 */
class GenerateCodeAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        val selectedText = editor?.selectionModel?.selectedText

        if (selectedText == null || selectedText.trim().isEmpty()) {
            Messages.showWarningDialog(
                project,
                "请在编辑器中选择 DDL SQL 语句",
                "Panda Code Generator"
            )
            return
        }

        val result = Messages.showInputDialog(
            project,
            "输入作者名称:",
            "Panda Code Generator",
            Messages.getQuestionIcon(),
            "Panda Plugin",
            object : InputValidator {
                override fun checkInput(inputString: String): Boolean {
                    return inputString.trim().isNotEmpty()
                }

                override fun canClose(inputString: String): Boolean {
                    return checkInput(inputString)
                }
            }
        )

        if (result == null) {
            return
        }

        val service = CodeGeneratorService.getInstance(project)
        val config = CodeGeneratorService.GenerateCodeConfig().apply {
            author = result
        }

        val generateResult = service.generateCode(selectedText, config)

        if (generateResult.isSuccess) {
            Messages.showInfoMessage(
                project,
                "代码生成成功！\n成功: ${generateResult.details?.successCount ?: 0} 个文件",
                "Panda Code Generator"
            )
        } else {
            Messages.showErrorDialog(
                project,
                generateResult.errorMessage,
                "Panda Code Generator"
            )
        }
    }

    override fun update(e: AnActionEvent) {
        val enabled = e.project != null && e.getData(CommonDataKeys.EDITOR) != null
        e.presentation.isEnabled = enabled
    }
}
