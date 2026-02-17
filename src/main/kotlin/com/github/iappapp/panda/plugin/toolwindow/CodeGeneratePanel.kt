package com.github.iappapp.panda.plugin.toolwindow

import com.github.iappapp.panda.common.generate.CodeGeneratorEngine
import com.github.iappapp.panda.plugin.service.CodeGeneratorService
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
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JTextField

import com.intellij.ui.components.JBScrollPane

/**
 * Panda代码生成UI面板
 *
 * @author system
 * @date 2026-02-12
 */
class CodeGeneratePanel(private val project: com.intellij.openapi.project.Project) {

    private val properties: PropertiesComponent = PropertiesComponent.getInstance(project)

    private val mainPanel = JPanel()
    private lateinit var sqlTextArea: JTextArea
    private lateinit var generateButton: JButton
    private lateinit var resultTextArea: JTextArea
    private lateinit var lombokCheckBox: JCheckBox
    private lateinit var myBatisPlusCheckBox: JCheckBox
    private lateinit var authorTextField: JTextField
    private lateinit var basicPackageTextField: JTextField
    private lateinit var projectRootTextField: JTextField

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

        val centerSection = JPanel(BorderLayout(5, 5))
        centerSection.add(configPanel, BorderLayout.NORTH)
        centerSection.add(resultPanel, BorderLayout.CENTER)

        mainPanel.add(sqlPanel, BorderLayout.NORTH)
        mainPanel.add(centerSection, BorderLayout.CENTER)
    }

    private fun createSqlPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder("DDL SQL")

        sqlTextArea = JTextArea(10, 50)
        sqlTextArea.font = Font("Monospaced", Font.PLAIN, 12)
        sqlTextArea.lineWrap = true
        sqlTextArea.wrapStyleWord = true

        val scrollPane = JBScrollPane(sqlTextArea)

        panel.add(scrollPane, BorderLayout.CENTER)

        return panel
    }

    private fun createConfigPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createTitledBorder("配置")
        panel.alignmentX = Component.LEFT_ALIGNMENT

        authorTextField = JTextField(30)
        authorTextField.text = "iappapp"

        basicPackageTextField = JTextField(30)
        basicPackageTextField.text = "com.aistarfish.cdssai."

        projectRootTextField = JTextField(30)
        projectRootTextField.text = project.basePath ?: ""

        myBatisPlusCheckBox = JCheckBox("MyBatis-Plus", true)
        myBatisPlusCheckBox.toolTipText = "MyBatis-Plus开关"

        lombokCheckBox = JCheckBox("Lombok", true)
        lombokCheckBox.toolTipText = "Lombok开关"

        panel.add(createTextConfigRow("作者", authorTextField))
        panel.add(Box.createVerticalStrut(8))
        panel.add(createTextConfigRow("基础包", basicPackageTextField))
        panel.add(Box.createVerticalStrut(8))
        panel.add(createTextConfigRow("项目总目录", projectRootTextField))
        panel.add(Box.createVerticalStrut(8))
        panel.add(createTextConfigRow("MyBatis", myBatisPlusCheckBox))
        panel.add(Box.createVerticalStrut(6))
        panel.add(createTextConfigRow("Lombok", lombokCheckBox))
        panel.add(Box.createVerticalStrut(4))
        panel.add(Box.createVerticalGlue())

        return panel
    }

    private fun createTextConfigRow(labelText: String, component: JComponent): JPanel {
        val row = JPanel(BorderLayout(8, 0))
        row.alignmentX = Component.LEFT_ALIGNMENT
        row.isOpaque = false

        val label = JLabel("$labelText:")
        label.preferredSize = Dimension(72, label.preferredSize.height)

        row.add(label, BorderLayout.WEST)
        row.add(component, BorderLayout.CENTER)

        return row
    }

    private fun createResultPanel(): JPanel {
        val panel = JPanel(BorderLayout(5, 5))

        generateButton = JButton("Generate")
        generateButton.font = Font("Dialog", Font.BOLD, 14)
        generateButton.preferredSize = Dimension(150, 30)
        generateButton.addActionListener(GenerateActionListener())

        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        buttonPanel.add(generateButton)

        val resultLabel = JLabel("Result")
        resultTextArea = JTextArea(8, 50)
        resultTextArea.font = Font("Monospaced", Font.PLAIN, 11)
        resultTextArea.isEditable = false
        resultTextArea.lineWrap = true
        resultTextArea.wrapStyleWord = true

        val scrollPane = com.intellij.ui.components.JBScrollPane(resultTextArea)

        panel.add(buttonPanel, BorderLayout.NORTH)
        panel.add(resultLabel, BorderLayout.CENTER)
        panel.add(scrollPane, BorderLayout.SOUTH)

        return panel
    }

    private inner class GenerateActionListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            val ddlSql = sqlTextArea.text.trim()

            if (ddlSql.isEmpty()) {
                showError("请输入DDL SQL")
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
                        println(ex)
                    }
                }
            }
        }
    }

    private fun createConfig(): CodeGeneratorService.GenerateCodeConfig {
        return CodeGeneratorService.GenerateCodeConfig().apply {
            author = authorTextField.text
            basicPackage = basicPackageTextField.text.trim()
            projectRoot = projectRootTextField.text.trim()
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
        val basicPackage = properties.getValue("panda.gen.basicPackage", "com.aistarfish.cdssai.")
        val projectRoot = properties.getValue("panda.gen.projectRoot", project.basePath ?: "")
        val useLombok = properties.getBoolean("panda.gen.lombok", true)
        val useMyBatisPlus = properties.getBoolean("panda.gen.mybatis", true)

        authorTextField.text = author
        basicPackageTextField.text = basicPackage
        projectRootTextField.text = projectRoot
        lombokCheckBox.isSelected = useLombok
        myBatisPlusCheckBox.isSelected = useMyBatisPlus
    }

    private fun saveSettings() {
        properties.setValue("panda.gen.author", authorTextField.text)
        properties.setValue("panda.gen.basicPackage", basicPackageTextField.text.trim())
        properties.setValue("panda.gen.projectRoot", projectRootTextField.text.trim())
        properties.setValue("panda.gen.lombok", lombokCheckBox.isSelected)
        properties.setValue("panda.gen.mybatis", myBatisPlusCheckBox.isSelected)
    }

    val content: JPanel
        get() = mainPanel
}
