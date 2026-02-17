package com.github.iappapp.panda.idea.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager

/**
 * 打开工具窗口的Action
 *
 * @author system
 * @date 2026-02-12
 */
class ShowToolWindowAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        val manager = ToolWindowManager.getInstance(project)
        val toolWindow: ToolWindow? = manager.getToolWindow("Panda Code Generator")
        toolWindow?.activate(null)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }
}
