package com.github.iappapp.panda.generate.template.impl;

import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.template.AbstractGenerateTemplate;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 转换工具类代码生成模板 (Convert/MapStruct)
 * 对应模板: convert.ftl
 * 生成文件: {ClassName}Convert.java
 * 
 * 特点：
 * - 使用 MapStruct 框架
 * - 生成 DO <-> Info <-> Model 的相互转换方法
 * 
 * @author system
 * @date 2026-02-12
 */
@NoArgsConstructor
public class ConvertTemplate extends AbstractGenerateTemplate {

    /**
     * 构造函数：注入 GenerateContext
     * @param context 代码生成上下文
     */
    public ConvertTemplate(GenerateContext context) {
        super(context);
    }

    @Override
    public String getTemplateName() {
        return "convert";
    }

    @Override
    public String getFileSuffix() {
        return "Convert.java";
    }

    @Override
    public String getPackageName(GenerateContext context) {
        return context.getBasePackage() + "core.convert";
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
        
        // Convert 特定数据可以在此添加
        // 例如：自定义的转换规则、排除字段等
        /*dataModel.put("className", context.getClassName() + "Convert");
        dataModel.put("packageName", context.getCoreModelPackage() + ".convert");*/
        
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
}
