package com.github.iappapp.panda.generate.template.impl;

import com.github.iappapp.panda.generate.GenerateContext;
import com.github.iappapp.panda.generate.constant.Constants;
import com.github.iappapp.panda.generate.template.AbstractGenerateTemplate;
import com.github.iappapp.panda.generate.template.IGenerateTemplate;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Map;

import static com.github.iappapp.panda.generate.constant.Constants.JAVA_EXTENSION;

/**
 * 数据对象类代码生成模板 (Domain/DO)
 * 对应模板: mybatis/domain.ftl 或 mybatis-plus/domain.ftl
 * 生成文件: {ClassName}.java
 * 
 * 特点：
 * - 支持 MyBatis-Plus 注解 @TableName, @TableId 等（可选）
 * - 支持 Lombok @Data 注解（可选）
 * - 根据useMyBatisPlus开关选择不同模板
 * 
 * @author system
 * @date 2026-02-12
 */
@NoArgsConstructor
public class DomainTemplate extends AbstractGenerateTemplate {

    /**
     * 构造函数：注入 GenerateContext
     * @param context 代码生成上下文
     */
    public DomainTemplate(GenerateContext context) {
        super(context);
    }

    @Override
    public String getTemplateName() {
        // 根据是否使用MyBatis-Plus返回不同的模板
        if (context.isUseMyBatisPlus()) {
            return "mybatis-plus/domain";
        }
        return "mybatis/domain";
    }

    @Override
    public String getFileSuffix() {
        return JAVA_EXTENSION;
    }

    @Override
    public String getPackageName(GenerateContext context) {
        return context.getBasePackage() + "common.dal.model";
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
        
        // 添加Domain特定数据
        // 例如：需要导入的ORM注解等
        return dataModel;
    }

    @Override
    public boolean shouldGenerate(GenerateContext context) {
        // Domain 类总是需要生成
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

    @Override
    public String getOutputPath(GenerateContext ctx, IGenerateTemplate template) {
        String projectRoot = ctx.getProject().getProjectRoot();
        String moduleName = template.getModuleName(ctx);
        String packagePath = template.getPackageName(ctx).replace(Constants.DOT, Constants.SEPARATOR);
        String prefix = template.getFilePrefix() != null ? template.getFilePrefix() : "";
        String suffix = template.getFileSuffix();
        String baseDir = template.isResourceFile() ? Constants.RESOURCE_PATH :Constants.JAVA_SOURCE_PATH;

        return projectRoot + File.separator + moduleName + baseDir + packagePath +
                File.separator + prefix + ctx.getClassName() + "DO" + suffix;
    }
}
