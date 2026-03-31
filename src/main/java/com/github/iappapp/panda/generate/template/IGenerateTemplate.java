package com.github.iappapp.panda.generate.template;

import com.github.iappapp.panda.generate.GenerateContext;
import java.util.Map;

/**
 * 代码生成模板接口
 * 每个实现类对应一个FTL模板文件，负责生成特定的代码文件
 * 
 * JDK 1.8兼容：使用default方法和方法重载实现
 * 
 * @author system
 * @date 2026-02-12
 */
public interface IGenerateTemplate {

    /**
     * 注入 GenerateContext（JDK 1.8兼容的方式）
     * 由引擎在执行时调用，为模板提供上下文信息
     * 
     * @param context 代码生成上下文
     */
    void injectContext(GenerateContext context);

    /**
     * 获取模板文件名（相对于template目录的路径，不含.ftl后缀）
     * 例如：model, domain, service_impl 等
     * @return 模板文件名
     */
    String getTemplateName();

    /**
     * 获取生成文件的后缀
     * 例如：ServiceImpl.java, Mapper.xml 等
     * @return 文件后缀
     */
    String getFileSuffix();

    /**
     * 获取生成文件的前缀（可选）
     * 例如：I（用于接口类）
     * @return 文件前缀，若无则返回null
     */
    default String getFilePrefix() {
        return null;
    }

    /**
     * 获取目标包名
     * @param context 生成上下文
     * @return 完整包名路径，用.分隔
     */
    String getPackageName(GenerateContext context);

    /**
     * 获取模块名称
     * @param context 生成上下文
     * @return 模块名称
     */
    String getModuleName(GenerateContext context);

    /**
     * 判断是否为资源文件（XML文件为resource）
     * @return true表示为资源文件，false表示为Java源文件
     */
    default boolean isResourceFile() {
        return false;
    }

    /**
     * 构建数据模型，用于FTL模板渲染
     * 模板可以通过${dataModel.xxx}或${xxx}访问这些数据
     * 
     * @param context 生成上下文，包含表名、字段、包名等元信息
     * @return 数据模型Map，将被传入FTL模板
     */
    Map<String, Object> buildDataModel(GenerateContext context);

    /**
     * 当前模板生成的代码是否应该生成
     * 可以根据上下文中的开关配置（如useLombok、useMyBatisPlus等）返回true/false
     * 
     * @param context 生成上下文
     * @return true表示应该生成，false表示跳过
     */
    default boolean shouldGenerate(GenerateContext context) {
        return true;
    }

    /**
     * 回调方法：文件生成成功后调用
     * 可用于自定义处理，如记录日志、更新统计等
     * 
     * @param filePath 生成的文件完整路径
     */
    default void onGenerateSuccess(String filePath) {
        // 默认实现空
    }

    /**
     * 回调方法：文件生成失败后调用
     * 可用于错误处理和日志记录
     * 
     * @param filePath 生成的文件完整路径
     * @param exception 异常信息
     */
    default void onGenerateFailure(String filePath, Exception exception) {
        // 默认实现空
    }

    /**
     *
     * @param ctx
     * @param template
     * @return
     */
    String getOutputPath(GenerateContext ctx, IGenerateTemplate template);
}
