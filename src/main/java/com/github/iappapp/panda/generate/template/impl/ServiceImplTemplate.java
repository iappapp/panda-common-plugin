package com.github.iappapp.panda.generate.template.impl;

import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.template.AbstractGenerateTemplate;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Service业务实现类代码生成模板
 * 对应模板: serviceImpl.ftl
 * 生成文件: {ClassName}ServiceImpl.java
 * 
 * 特点：
 * - 业务实现类
 * - 依赖注入 Mapper/Repository
 * - 实现 IService 接口定义的方法
 * 
 * @author system
 * @date 2026-02-12
 */
@NoArgsConstructor
public class ServiceImplTemplate extends AbstractGenerateTemplate {

    /**
     * 构造函数：注入 GenerateContext
     * @param context 代码生成上下文
     */
    public ServiceImplTemplate(GenerateContext context) {
        super(context);
    }

    @Override
    public String getTemplateName() {
        if (context.isUseMyBatisPlus()) {
            return "mybatis-plus/serviceImpl";
        }
        return "mybatis/serviceImpl";
    }

    @Override
    public String getFileSuffix() {
        return "ServiceImpl.java";
    }

    @Override
    public String getPackageName(GenerateContext context) {
        return context.getBasePackage() + "core.service" + ".impl";
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
        
        // ServiceImpl 特定数据
        dataModel.put("serviceImplPackage", context.getBasePackage() + "core.service.impl");
        
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
