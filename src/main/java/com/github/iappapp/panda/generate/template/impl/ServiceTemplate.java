package com.github.iappapp.panda.generate.template.impl;

import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.template.AbstractGenerateTemplate;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Service业务接口代码生成模板
 * 对应模板: service.ftl
 * 生成文件: I{ClassName}Service.java
 * 
 * 特点：
 * - 接口类，使用I前缀
 * - 定义业务方法（增删改查等）
 * 
 * @author system
 * @date 2026-02-12
 */
@NoArgsConstructor
public class ServiceTemplate extends AbstractGenerateTemplate {

    /**
     * 构造函数：注入 GenerateContext
     * @param context 代码生成上下文
     */
    public ServiceTemplate(GenerateContext context) {
        super(context);
    }

    @Override
    public String getTemplateName() {
        if (context.isUseMyBatisPlus()) {
            return "mybatis-plus/service";
        }
        return "mybatis/service";
    }

    @Override
    public String getFilePrefix() {
        return "I";
    }

    @Override
    public String getFileSuffix() {
        return "Service.java";
    }

    @Override
    public String getPackageName(GenerateContext context) {
        return context.getBasePackage() + "core.service";
    }

    @Override
    public String getModuleName(GenerateContext context) {
        return context.getProject().getCoreServiceModuleName();
    }

    @Override
    protected String getPackageNameAsPath() {
        return "";
    }

    @Override
    public Map<String, Object> buildDataModel(GenerateContext context) {
        Map<String, Object> dataModel = buildBaseDataModel(context);
        
        // Service 特定数据
        // 可以添加常见的业务方法模板等

        
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
