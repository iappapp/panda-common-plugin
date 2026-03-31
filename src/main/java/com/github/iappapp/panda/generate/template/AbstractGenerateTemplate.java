package com.github.iappapp.panda.generate.template;

import com.github.iappapp.panda.generate.constant.Constants;
import com.github.iappapp.panda.generate.GenerateContext;

import java.io.File;
import java.util.Date;
import java.util.Objects;

/**
 * 抽象代码生成模板基类
 * 
 * 封装了以下逻辑：
 * 1. 注入并保存 GenerateContext（JDK 1.8兼容）
 * 2. 文件路径的生成（实现在getOutputPath方法中）
 * 3. 通用的数据模型构建
 * 4. 生命周期回调方法
 * 
 * 子类通过构造函数注入 GenerateContext，避免在方法参数中频繁传递
 * 
 * @author system
 * @date 2026-02-12
 */
public abstract class AbstractGenerateTemplate implements IGenerateTemplate {
    
    /**
     * 代码生成上下文，在构造时注入
     */
    protected GenerateContext context;

    /**
     * 构造函数：注入 GenerateContext
     * JDK 1.8 兼容的实现方式
     * 
     * @param context 代码生成上下文
     */
    public AbstractGenerateTemplate(GenerateContext context) {
        this.context = context;
    }

    /**
     * 默认构造函数（保持向后兼容）
     * 子类如未使用context可以调用此构造函数
     */
    public AbstractGenerateTemplate() {
        this(null);
    }

    /**
     * 注入 GenerateContext（JDK 1.8兼容）
     * 由引擎在执行时调用
     * 
     * @param context 代码生成上下文
     */
    @Override
    public void injectContext(GenerateContext context) {
        this.context = context;
    }

    /**
     * 将包名转换为路径格式
     * 例如：com.example.model -> com/example/model
     * @return 包名路径
     */
    protected String getPackageNameAsPath() {
        return ""; // 由子类实现具体逻辑，此处为占位
    }

    /**
     * 通用的常见字段准备
     * 所有模板都需要的基础数据，会在buildDataModel中被调用
     * 
     * @param context 生成上下文
     * @return 基础数据模型
     */
    protected java.util.Map<String, Object> buildBaseDataModel(GenerateContext context) {
        java.util.Map<String, Object> dataModel = new java.util.HashMap<>();

        // 基础元数据
        dataModel.put("className", context.getClassName());
        dataModel.put("tableName", context.getTableName());
        dataModel.put("tableDesc", context.getTableDesc());
        dataModel.put("author", context.getAuthor());
        dataModel.put("createDate", new Date());

        if (Objects.nonNull(context.getBasePackage())
                && !context.getBasePackage().endsWith(Constants.DOT)) {
            context.setBasePackage(context.getBasePackage() + Constants.DOT);
        }

        // 包名配置
        dataModel.put("doPackage", context.getBasePackage() + "common.dal.model");
        dataModel.put("mapperPackage", context.getBasePackage() + "common.dal.mapper");
        dataModel.put("coreModelPackage", context.getBasePackage() + "core.model");
        dataModel.put("convertPackage", context.getBasePackage() + "core.convert");
        dataModel.put("servicePackage", context.getBasePackage() + "core.service");
        dataModel.put("facadeModelPackage", context.getBasePackage() + "common.facade.model");
        dataModel.put("facadePackage", context.getBasePackage() + "common.facade");

        // 字段列表
        dataModel.put("fields", context.getFields());
        dataModel.put("primaryKeyName", context.getPrimaryKeyName());

        // 技术开关
        dataModel.put("useLombok", context.isUseLombok());
        dataModel.put("useMyBatisPlus", context.isUseMyBatisPlus());

        return dataModel;
    }

    /**
     * 记录模板生成日志
     * @param templateName 模板名称
     * @param filePath 生成的文件路径
     */
    protected void logGenerateInfo(String templateName, String filePath) {
        System.out.println("✓ 已生成: [" + templateName + "] -> " + extractFileName(filePath));
    }

    /**
     * 记录模板生成错误
     * @param templateName 模板名称
     * @param exception 异常信息
     */
    protected void logGenerateError(String templateName, Exception exception) {
        System.err.println("✗ 模板 [" + templateName + "] 生成失败: " + exception.getMessage());
    }

    /**
     * 从完整路径中提取文件名
     * @param filePath 完整文件路径
     * @return 文件名
     */
    private String extractFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    /**
     * 计算输出文件的完整路径
     *
     * 路径结构：
     * Java源文件: {projectRoot}/{moduleName}/src/main/java/{packagePath}/{prefix}{className}{suffix}.java
     * 资源文件:   {projectRoot}/{moduleName}/src/main/resources/{packagePath}/{prefix}{className}{suffix}
     *
     * @param ctx 代码生成上下文
     * @param template 代码生成模板
     * @return 完整的文件路径
     */
    @Override
    public String getOutputPath(GenerateContext ctx, IGenerateTemplate template) {
        String projectRoot = ctx.getProject().getProjectRoot();
        String moduleName = template.getModuleName(ctx);
        String packagePath = template.getPackageName(ctx).replace(Constants.DOT, Constants.SEPARATOR);
        String prefix = template.getFilePrefix() != null ? template.getFilePrefix() : "";
        String suffix = template.getFileSuffix();
        String baseDir = template.isResourceFile() ? Constants.RESOURCE_PATH :Constants.JAVA_SOURCE_PATH;

        return projectRoot + File.separator + moduleName + baseDir + packagePath +
                File.separator + prefix + ctx.getClassName() + suffix;
    }
}
