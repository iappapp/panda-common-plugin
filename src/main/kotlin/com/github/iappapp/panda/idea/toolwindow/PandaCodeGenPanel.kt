package com.github.iappapp.panda.idea.toolwindow

import com.github.iappapp.panda.common.generate.CodeGeneratorEngine
import com.github.iappapp.panda.idea.service.CodeGeneratorService
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.Point
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField

/**
 * Panda代码生成UI面板
 *
 * @author system
 * @date 2026-02-12
 */
class PandaCodeGenPanel(private val project: com.intellij.openapi.project.Project) {

    private val properties: PropertiesComponent = PropertiesComponent.getInstance(project)

    private val mainPanel = JPanel()
    private lateinit var sqlTextArea: JTextArea
    private lateinit var generateButton: JButton
    private lateinit var resultTextArea: JTextArea
    private lateinit var lombokCheckBox: JCheckBox
    private lateinit var myBatisPlusCheckBox: JCheckBox
    private lateinit var authorTextField: JTextField

    init {
        initializeUI()
        loadSettings()
    }

    private fun initializeUI() {
        mainPanel.layout = BorderLayout(10, 10)
        mainPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val sqlPanel = createSqlPanel()
        val configPanel = createConfigPanel()
        val resultPanel = createResultPanel()

        val topSection = JPanel(BorderLayout(5, 5))
        topSection.add(sqlPanel, BorderLayout.CENTER)
        topSection.add(configPanel, BorderLayout.EAST)

        mainPanel.add(topSection, BorderLayout.NORTH)
        mainPanel.add(resultPanel, BorderLayout.CENTER)
    }

    private fun createSqlPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder("DDL SQL 输入")

        val label = JLabel("粘贴你的 CREATE TABLE SQL 语句:")
        sqlTextArea = JTextArea(10, 50)
        sqlTextArea.font = Font("Monospaced", Font.PLAIN, 12)
        sqlTextArea.lineWrap = true
        sqlTextArea.wrapStyleWord = true

        val scrollPane = JScrollPane(sqlTextArea)

        panel.add(label, BorderLayout.NORTH)
        panel.add(scrollPane, BorderLayout.CENTER)

        return panel
    }

    private fun createConfigPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createTitledBorder("配置")
        panel.preferredSize = Dimension(200, 200)

        val authorLabel = JLabel("作者:")
        authorTextField = JTextField(15)
        authorTextField.text = "Panda Plugin"
        val authorPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        authorPanel.add(authorLabel)
        authorPanel.add(authorTextField)

        lombokCheckBox = JCheckBox("使用 Lombok", true)
        lombokCheckBox.toolTipText = "自动生成 getter/setter @Data 注解"

        myBatisPlusCheckBox = JCheckBox("使用 MyBatis-Plus", true)
        myBatisPlusCheckBox.toolTipText = "使用 MyBatis-Plus 框架"

        panel.add(authorPanel)
        panel.add(Box.createVerticalStrut(10))
        panel.add(lombokCheckBox)
        panel.add(Box.createVerticalStrut(5))
        panel.add(myBatisPlusCheckBox)
        panel.add(Box.createVerticalGlue())

        return panel
    }

    private fun createResultPanel(): JPanel {
        val panel = JPanel(BorderLayout(5, 5))

        generateButton = JButton("🚀 生成代码")
        generateButton.font = Font("Dialog", Font.BOLD, 14)
        generateButton.preferredSize = Dimension(150, 40)
        generateButton.addActionListener(GenerateActionListener())

        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        buttonPanel.add(generateButton)

        val resultLabel = JLabel("生成结果:")
        resultTextArea = JTextArea(8, 50)
        resultTextArea.font = Font("Monospaced", Font.PLAIN, 11)
        resultTextArea.isEditable = false
        resultTextArea.lineWrap = true
        resultTextArea.wrapStyleWord = true

        val scrollPane = JScrollPane(resultTextArea)

        panel.add(buttonPanel, BorderLayout.NORTH)
        panel.add(resultLabel, BorderLayout.CENTER)
        panel.add(scrollPane, BorderLayout.SOUTH)

        return panel
    }

    private inner class GenerateActionListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            val ddlSql = sqlTextArea.text.trim()

            if (ddlSql.isEmpty()) {
                showError("请输入 DDL SQL 语句")
                return
            }

            saveSettings()

            ApplicationManager.getApplication().executeOnPooledThread {
                try {
                    val service = CodeGeneratorService.getInstance(project)
                    val config = createConfig()
                    val result = service.generateCode(ddlSql, config)

                    ApplicationManager.getApplication().invokeLater {
                        if (result.isSuccess) {
                            showSuccess("代码生成成功！\n\n${formatResultDetails(result.details)}")
                        } else {
                            showError(result.errorMessage ?: "未知错误")
                        }
                    }
                } catch (ex: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        showError("异常: ${ex.message}")
                    }
                }
            }
        }
    }

    private fun createConfig(): CodeGeneratorService.GenerateCodeConfig {
        return CodeGeneratorService.GenerateCodeConfig().apply {
            author = authorTextField.text
            isUseLombok = lombokCheckBox.isSelected
            isUseMyBatisPlus = myBatisPlusCheckBox.isSelected
        }
    }

    private fun formatResultDetails(details: CodeGeneratorEngine.GenerateResult?): String {
        if (details == null) {
            return "生成成功"
        }

        val sb = StringBuilder()
        sb.append("✓ 成功: ").append(details.successCount).append(" 个\n")

        if (details.failureCount > 0) {
            sb.append("✗ 失败: ").append(details.failureCount).append(" 个\n")
        }

        if (details.skippedCount > 0) {
            sb.append("⊘ 跳过: ").append(details.skippedCount).append(" 个\n")
        }

        return sb.toString()
    }

    private fun showSuccess(message: String) {
        resultTextArea.text = message
        resultTextArea.foreground = Color(34, 139, 34)
        showBalloon(message, MessageType.INFO)
    }

    private fun showError(message: String) {
        resultTextArea.text = "❌ 错误: $message"
        resultTextArea.foreground = Color(220, 20, 60)
        showBalloon(message, MessageType.ERROR)
    }

    private fun showBalloon(message: String, type: MessageType) {
        val popupFactory = JBPopupFactory.getInstance()
        val balloon = popupFactory
            .createHtmlTextBalloonBuilder(
                "<html>${message.replace("\n", "<br/>")}</html>",
                type,
                null
            )
            .createBalloon()

        val component: Component? = content
        if (component != null) {
            balloon.show(RelativePoint(component, Point(0, 0)), Balloon.Position.above)
        }
    }

    private fun loadSettings() {
        val author = properties.getValue("panda.gen.author", "Panda Plugin")
        val useLombok = properties.getBoolean("panda.gen.lombok", true)
        val useMyBatisPlus = properties.getBoolean("panda.gen.mybatis", true)

        authorTextField.text = author
        lombokCheckBox.isSelected = useLombok
        myBatisPlusCheckBox.isSelected = useMyBatisPlus
    }

    private fun saveSettings() {
        properties.setValue("panda.gen.author", authorTextField.text)
        properties.setValue("panda.gen.lombok", lombokCheckBox.isSelected)
        properties.setValue("panda.gen.mybatis", myBatisPlusCheckBox.isSelected)
    }

    val content: JPanel
        get() = mainPanel
}
