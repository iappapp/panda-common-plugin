package com.github.iappapp.panda.generate;

import com.github.iappapp.panda.generate.template.IGenerateTemplate;
import com.github.iappapp.panda.generate.template.TemplateRegistry;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.*;
import java.util.*;

/**
 * 代码生成引擎 - 使用模板设计模式重构
 * 
 * 核心功能：
 * 1. 基于FreeMarker加载和处理模板
 * 2. 使用IGenerateTemplate接口管理各个代码生成模板
 * 3. 支持按需生成特定模板或批量生成所有模板
 * 4. 自动处理文件路径、目录创建和错误处理
 * 
 * @author system
 * @date 2026-02-12
 */
public class CodeGeneratorEngine {

    private final Configuration cfg;

    private final TemplateRegistry templateRegistry;

    /**
     * 初始化代码生成引擎
     * 配置FreeMarker模板加载器和字符编码
     */
    public CodeGeneratorEngine() {
        this.cfg = new Configuration(Configuration.VERSION_2_3_31);
        this.cfg.setClassForTemplateLoading(this.getClass(), "/template");
        this.cfg.setDefaultEncoding("UTF-8");
        
        // 使用全局模板注册表
        this.templateRegistry = TemplateRegistry.getInstance();
    }

    /**
     * 初始化代码生成引擎（支持自定义模板注册表）
     * 用于测试或特殊场景
     * 
     * @param templateRegistry 自定义模板注册表
     */
    public CodeGeneratorEngine(TemplateRegistry templateRegistry) {
        this();
        // 如果需要使用自定义的注册表，可以在此处处理
    }

    /**
     * 批量生成所有已注册的模板对应的代码文件
     * 
     * 执行流程：
     * 1. 遍历所有注册的模板
     * 2. 检查模板是否应该生成（根据上下文配置）
     * 3. 为每个模板调用 generateByTemplate
     * 4. 统计生成结果
     * 
     * @param ctx 代码生成上下文，包含表名、字段、包名等元信息
     * @return 生成统计结果
     */
    public GenerateResult batchGenerateAll(GenerateContext ctx) {
        GenerateResult result = new GenerateResult();
        
        for (IGenerateTemplate template : templateRegistry.getTemplatesList()) {
            template.injectContext(ctx);
            // 检查该模板是否应该生成
            if (template.shouldGenerate(ctx)) {
                generateByTemplate(ctx, template, result);
            } else {
                result.addSkipped(template.getTemplateName());
            }
        }
        
        result.printSummary();
        return result;
    }

    /**
     * 生成指定模板的代码
     * 
     * @param ctx 代码生成上下文
     * @param templateName 模板名称
     * @return 生成结果
     */
    public GenerateResult generateByTemplateName(GenerateContext ctx, String templateName) {
        IGenerateTemplate template = templateRegistry.getTemplate(templateName);
        if (template == null) {
            throw new IllegalArgumentException("模板 [" + templateName + "] 不存在");
        }
        
        GenerateResult result = new GenerateResult();
        generateByTemplate(ctx, template, result);
        return result;
    }

    /**
     * 生成指定模板的代码（内部方法）
     * 
     * 执行流程：
     * 1. 为模板注入 GenerateContext
     * 2. 获取模板和数据模型
     * 3. 计算输出文件路径
     * 4. 创建必要的目录
     * 5. 使用FreeMarker渲染模板
     * 6. 写入文件
     * 7. 调用回调方法
     * 
     * @param ctx 代码生成上下文
     * @param template 代码生成模板
     * @param result 收集生成结果
     */
    private void generateByTemplate(GenerateContext ctx, IGenerateTemplate template, GenerateResult result) {
        String templateName = template.getTemplateName();
        
        try {
            // 1. 为模板注入 GenerateContext (JDK 1.8兼容方式)
            template.injectContext(ctx);
            
            // 2. 获取输出文件路径
            String outputPath = template.getOutputPath(ctx, template);
            File outFile = new File(outputPath);

            // 3. 创建必要的目录
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            
            // 创建输出文件
            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            // 4. 获取模板并构建数据模型
            Template ftlTemplate = cfg.getTemplate(templateName + ".ftl");
            Map<String, Object> dataModel = template.buildDataModel(ctx);

            // 5. 使用FreeMarker渲染模板
            try (Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"))) {
                ftlTemplate.process(dataModel, writer);
                
                // 生成成功
                result.addSuccess(templateName, outputPath);
                template.onGenerateSuccess(outputPath);
            }

        } catch (Exception e) {
            // 生成失败
            result.addFailure(templateName, e);
            template.onGenerateFailure(template.getOutputPath(ctx, template), e);
            System.err.println("✗ 模板 [" + templateName + "] 生成失败: " + e.getMessage());
        }
    }

    /**
     * 获取模板注册表
     * @return 模板注册表实例
     */
    public TemplateRegistry getTemplateRegistry() {
        return templateRegistry;
    }

    /**
     * 打印所有已注册的模板
     */
    public void printAvailableTemplates() {
        templateRegistry.printTemplateList();
    }

    /**
     * 代码生成结果统计类
     * 用于收集和展示生成过程中的统计信息
     */
    public static class GenerateResult {
        private final List<String> successTemplates = new ArrayList<>();
        private final Map<String, Exception> failedTemplates = new HashMap<>();
        private final List<String> skippedTemplates = new ArrayList<>();

        public void addSuccess(String templateName, String filePath) {
            successTemplates.add(templateName + " -> " + extractFileName(filePath));
        }

        public void addFailure(String templateName, Exception exception) {
            failedTemplates.put(templateName, exception);
        }

        public void addSkipped(String templateName) {
            skippedTemplates.add(templateName);
        }

        public int getSuccessCount() {
            return successTemplates.size();
        }

        public int getFailureCount() {
            return failedTemplates.size();
        }

        public int getSkippedCount() {
            return skippedTemplates.size();
        }

        public void printSummary() {
            System.out.println("\n========== 代码生成完成 ==========");
            System.out.println("✓ 成功: " + getSuccessCount() + " 个");
            for (String success : successTemplates) {
                System.out.println("  · " + success);
            }
            
            if (!failedTemplates.isEmpty()) {
                System.out.println("✗ 失败: " + getFailureCount() + " 个");
                for (Map.Entry<String, Exception> entry : failedTemplates.entrySet()) {
                    System.out.println("  · " + entry.getKey() + ": " + entry.getValue().getMessage());
                }
            }
            
            if (!skippedTemplates.isEmpty()) {
                System.out.println("⊘ 跳过: " + getSkippedCount() + " 个");
                for (String skipped : skippedTemplates) {
                    System.out.println("  · " + skipped);
                }
            }
            System.out.println("==================================\n");
        }

        private static String extractFileName(String filePath) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
    }
}