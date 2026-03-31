package com.github.iappapp.panda.generate.template;

import com.github.iappapp.panda.generate.template.impl.ModelTemplate;

import java.util.*;

/**
 * 代码生成模板管理器
 * 
 * 负责：
 * 1. 注册和管理所有的代码生成模板
 * 2. 根据需要返回对应的模板实例
 * 3. 获取所有需要生成的模板列表
 * 4. 支持JDK 1.8运行环境
 * 
 * 使用单例模式，确保全局唯一
 * 
 * 关键设计：
 * - 模板在注册时使用无参或null context构造
 * - GenerateContext在CodeGeneratorEngine的generateByTemplate中通过injectContext()注入
 * - 这种方式充分利用了JDK 1.8的default方法特性
 * 
 * @author system
 * @date 2026-02-12
 */
public class TemplateRegistry {

    private static final TemplateRegistry INSTANCE = new TemplateRegistry();

    private final Map<String, IGenerateTemplate> templateMap = new LinkedHashMap<>();

    private TemplateRegistry() {
        registerDefaultTemplates();
    }

    public static TemplateRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * 注册默认的代码生成模板
     * 
     * 注册顺序很重要，因为有些模板依赖于其他模板的生成结果
     * 推荐顺序：
     * 1. 数据模型 (Domain, Info, Model)
     * 2. 数据访问层 (Mapper, MapperXml)
     * 3. 转换工具 (Convert)
     * 4. 业务层 (Service, ServiceImpl)
     * 5. 外观层 (Facade)
     * 
     * JDK 1.8兼容：使用无参构造函数创建实例，context在运行时注入
     */
    private void registerDefaultTemplates() {
        // 数据模型层 - 使用无参构造函数
        register(new com.github.iappapp.panda.generate.template.impl.DomainTemplate());
        register(new com.github.iappapp.panda.generate.template.impl.InfoTemplate());
        register(new ModelTemplate());

        // 数据访问层
        register(new com.github.iappapp.panda.generate.template.impl.MapperTemplate());
        register(new com.github.iappapp.panda.generate.template.impl.MapperXmlTemplate());

        // 转换工具
        register(new com.github.iappapp.panda.generate.template.impl.ConvertTemplate());

        // 业务服务层
        register(new com.github.iappapp.panda.generate.template.impl.ServiceTemplate());
        register(new com.github.iappapp.panda.generate.template.impl.ServiceImplTemplate());

        // 外观层
        register(new com.github.iappapp.panda.generate.template.impl.FacadeTemplate());
    }

    /**
     * 注册一个生成模板
     * @param template 模板实例
     */
    public void register(IGenerateTemplate template) {
        String key = template.getClass().getSimpleName();
        templateMap.put(key, template);
    }

    /**
     * 获取指定模板
     * @param templateName 模板名称
     * @return 模板实例，如果不存在则返回null
     */
    public IGenerateTemplate getTemplate(String templateName) {
        return templateMap.get(templateName);
    }

    /**
     * 获取所有已注册的模板
     * @return 模板集合，按注册顺序返回
     */
    public Collection<IGenerateTemplate> getAllTemplates() {
        return new ArrayList<>(templateMap.values());
    }

    /**
     * 获取所有已注册的模板（不可修改）
     * @return 不可修改的模板集合
     */
    public List<IGenerateTemplate> getTemplatesList() {
        return Collections.unmodifiableList(new ArrayList<>(templateMap.values()));
    }

    /**
     * 检查是否已注册指定的模板
     * @param templateName 模板名称
     * @return 如果已注册返回true，否则返回false
     */
    public boolean hasTemplate(String templateName) {
        return templateMap.containsKey(templateName);
    }

    /**
     * 获取已注册的模板总数
     * @return 模板数量
     */
    public int getTemplateCount() {
        return templateMap.size();
    }

    /**
     * 清空所有已注册的模板
     * 谨慎使用，通常只在测试场景中使用
     */
    public void clear() {
        templateMap.clear();
    }

    /**
     * 打印所有已注册的模板信息
     * 用于调试和验证
     */
    public void printTemplateList() {
        System.out.println("===== 已注册的代码生成模板 =====");
        int index = 1;
        for (IGenerateTemplate template : templateMap.values()) {
            System.out.println(String.format(
                    "%d. [%s] -> %s (资源文件: %s)",
                    index++,
                    template.getTemplateName(),
                    template.getFileSuffix(),
                    template.isResourceFile()));
        }
    }
}
