package com.github.iappapp.panda.generate.template.impl;

import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.template.AbstractGenerateTemplate;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Facade接口类代码生成模板
 * 对应模板: facade.ftl
 * 生成文件: I{ClassName}Facade.java
 * 
 * 特点：
 * - 外观接口类，使用I前缀
 * - 定义对外提供的API和业务方法
 * - 在分层架构中作为服务对外暴露的契约接口
 * 
 * @author system
 * @date 2026-02-12
 */
@NoArgsConstructor
public class FacadeTemplate extends AbstractGenerateTemplate {

    /**
     * 构造函数：注入 GenerateContext
     * @param context 代码生成上下文
     */
    public FacadeTemplate(GenerateContext context) {
        super(context);
    }

    @Override
    public String getTemplateName() {
        return "facade";
    }

    @Override
    public String getFilePrefix() {
        return "";
    }

    @Override
    public String getFileSuffix() {
        return "Facade.java";
    }

    @Override
    public String getPackageName(GenerateContext context) {
        return context.getBasePackage() + "common.facade";
    }

    @Override
    public String getModuleName(GenerateContext context) {
        return context.getProject().getFacadeModuleName();
    }

    @Override
    protected String getPackageNameAsPath() {
        return "";
    }

    @Override
    public Map<String, Object> buildDataModel(GenerateContext context) {
        Map<String, Object> dataModel = buildBaseDataModel(context);
        
        // Facade 特定数据
        // 标记为外观类接口
        
        return dataModel;
    }

    @Override
    public void onGenerateSuccess(String filePath) {
        logGenerateInfo(getTemplateName(), filePath);
    }

    @Override
    public void onGenerateFailure(String filePath, Exception exception) {
        logGenerateError(getTemplateName(), exception);
    }

    @Override
    public boolean shouldGenerate(GenerateContext context) {
        return false;
    }
}
