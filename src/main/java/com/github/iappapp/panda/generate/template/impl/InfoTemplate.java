package com.github.iappapp.panda.generate.template.impl;

import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.template.AbstractGenerateTemplate;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Info核心模型类代码生成模板
 * 对应模板: info.ftl
 * 生成文件: {ClassName}Info.java
 * 
 * 特点：
 * - 核心数据模型，用于业务层传递
 * - 支持 Lombok @Data 注解
 * - 包含所有业务属性
 * 
 * @author system
 * @date 2026-02-12
 */
@NoArgsConstructor
public class InfoTemplate extends AbstractGenerateTemplate {

    /**
     * 构造函数：注入 GenerateContext
     * @param context 代码生成上下文
     */
    public InfoTemplate(GenerateContext context) {
        super(context);
    }

    @Override
    public String getTemplateName() {
        return "info";
    }

    @Override
    public String getFileSuffix() {
        return "Info.java";
    }

    @Override
    public String getPackageName(GenerateContext context) {
        return context.getBasePackage() + "core.model";
    }

    @Override
    public String getModuleName(GenerateContext context) {
        return context.getProject().getCoreModelModuleName();
    }

    @Override
    protected String getPackageNameAsPath() {
        return "";
    }

    @Override
    public Map<String, Object> buildDataModel(GenerateContext context) {
        Map<String, Object> dataModel = buildBaseDataModel(context);
        
        // Info 特定数据
        // 模板中将通过 ${useLombok} 判断是否使用Lombok
        
        return dataModel;
    }

    @Override
    public boolean shouldGenerate(GenerateContext context) {
        // Info 类总是需要生成
        return true;
    }

    @Override
    public void onGenerateSuccess(String filePath) {
        logGenerateInfo(getTemplateName(), filePath);
    }

    @Override
    public void onGenerateFailure(String filePath, Exception exception) {
        logGenerateError(getTemplateName(), exception);
    }
}
