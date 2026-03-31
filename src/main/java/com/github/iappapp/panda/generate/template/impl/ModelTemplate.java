package com.github.iappapp.panda.generate.template.impl;

import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.template.AbstractGenerateTemplate;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 模型类代码生成模板 (Model/DTO)
 * 对应模板: model.ftl
 * 生成文件: {ClassName}Model.java
 * 
 * 特点：
 * - 支持 Lombok @Data 注解
 * - 自动生成 Getter/Setter（当不使用Lombok时）
 * - 自动导入必需的类型（如Date、BigDecimal等）
 * 
 * @author system
 * @date 2026-02-12
 */
@NoArgsConstructor
public class ModelTemplate extends AbstractGenerateTemplate {

    /**
     * 构造函数：注入 GenerateContext
     * @param context 代码生成上下文
     */
    public ModelTemplate(GenerateContext context) {
        super(context);
    }

    @Override
    public String getTemplateName() {
        return "model";
    }

    @Override
    public String getFileSuffix() {
        return "Model.java";
    }

    @Override
    public String getPackageName(GenerateContext context) {
        return context.getBasePackage() + "common.facade.model";
    }

    @Override
    public String getModuleName(GenerateContext context) {
        return context.getProject().getFacadeModuleName();
    }

    @Override
    protected String getPackageNameAsPath() {
        // 由运行时动态获取，这里不使用
        return "";
    }

    @Override
    public Map<String, Object> buildDataModel(GenerateContext context) {
        Map<String, Object> dataModel = buildBaseDataModel(context);
        
        // 为model.ftl特定化的数据
        // 模板中将通过 ${useLombok} 判断是否使用Lombok
        
        return dataModel;
    }

    @Override
    public boolean shouldGenerate(GenerateContext context) {
        // Model 类总是需要生成
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

