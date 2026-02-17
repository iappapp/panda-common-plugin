package com.github.iappapp.panda.idea.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Panda代码生成工具窗口工厂
 *
 * @author system
 * @date 2026-02-12
 */
class PandaCodeGenToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = PandaCodeGenPanel(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel.content, "", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project): Boolean {
        return true
    }
}
