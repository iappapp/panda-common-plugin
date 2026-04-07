package com.github.iappapp.panda.generate.template.impl;

import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.template.AbstractGenerateTemplate;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Mapper XML配置文件代码生成模板
 * 对应模板: mybatis/mapper.xml.ftl
 * 生成文件: {ClassName}Mapper.xml
 * 
 * 特点：
 * - 生成为资源文件（isResourceFile=true）
 * - 包含基本的CRUD SQL语句
 * - 仅在不使用MyBatis-Plus时需要生成
 * 
 * @author system
 * @date 2026-02-12
 */
@NoArgsConstructor
public class MapperXmlTemplate extends AbstractGenerateTemplate {

    /**
     * 构造函数：注入 GenerateContext
     * @param context 代码生成上下文
     */
    public MapperXmlTemplate(GenerateContext context) {
        super(context);
    }

    @Override
    public String getTemplateName() {
        if (context.isUseMyBatisPlus()) {
            return "mybatis-plus/mapper.xml";
        }
        return "mybatis/mapper.xml";
    }

    @Override
    public String getFileSuffix() {
        return "Mapper.xml";
    }

    @Override
    public String getPackageName(GenerateContext context) {
        // XML文件通常放在 mapper 目录下
        return "mybatis/mapper";
    }

    @Override
    public String getModuleName(GenerateContext context) {
        return context.getProject().getDalModuleName();
    }

    @Override
    public boolean isResourceFile() {
        // XML文件是资源文件
        return true;
    }

    @Override
    protected String getPackageNameAsPath() {
        return "";
    }

    @Override
    public Map<String, Object> buildDataModel(GenerateContext context) {
        Map<String, Object> dataModel = buildBaseDataModel(context);
        
        // Mapper XML特定数据
        // 根据字段类型生成对应的映射关系
        
        return dataModel;
    }

    @Override
    public boolean shouldGenerate(GenerateContext context) {
        // 仅在不使用MyBatis-Plus时生成XML
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
