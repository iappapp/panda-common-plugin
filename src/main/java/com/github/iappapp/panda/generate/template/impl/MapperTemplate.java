package com.github.iappapp.panda.generate.template.impl;

import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.template.AbstractGenerateTemplate;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Mapper接口代码生成模板
 * 对应模板: mybatis/mapper.ftl 或 mybatis-plus/mapper.ftl
 * 生成文件: {ClassName}Mapper.java
 * 
 * 特点：
 * - 继承自 BaseMapper (MyBatis-Plus) 或自定义基类
 * - 仅定义接口，具体实现在XML中或使用MyBatis-Plus默认实现
 * 
 * @author system
 * @date 2026-02-12
 */
@NoArgsConstructor
public class MapperTemplate extends AbstractGenerateTemplate {

    /**
     * 构造函数：注入 GenerateContext
     * @param context 代码生成上下文
     */
    public MapperTemplate(GenerateContext context) {
        super(context);
    }

    @Override
    public String getTemplateName() {
        if (context.isUseMyBatisPlus()) {
            return "mybatis-plus/mapper";
        }
        return "mybatis/mapper";
    }

    @Override
    public String getFileSuffix() {
        return "Mapper.java";
    }

    @Override
    public String getPackageName(GenerateContext context) {
        return context.getBasePackage() + "common.dal.mapper";
    }

    @Override
    public String getModuleName(GenerateContext context) {
        return context.getProject().getDalModuleName();
    }

    @Override
    protected String getPackageNameAsPath() {
        return "";
    }

    @Override
    public Map<String, Object> buildDataModel(GenerateContext context) {
        Map<String, Object> dataModel = buildBaseDataModel(context);
        
        // Mapper 特定数据
        // 如果使用MyBatis-Plus，标记需要继承BaseMapper
        dataModel.put("mapperBaseName", context.isUseMyBatisPlus() ? "BaseMapper" : "Mapper");
        
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
