package ${servicePackage}.impl;

import java.util.Map;
import java.util.List;

import ${servicePackage}.I${className}Service;
import ${mapperPackage}.${className}Mapper;
import ${doPackage}.${className}DO;
import ${coreModelPackage}.${className}Info;
import ${convertPackage}.${className}Convert;

import cn.hutool.core.bean.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

<#-- 预定义类型名称 -->
<#assign infoType = className + "Info">
<#assign doType = className + "DO">
<#assign convertClass = className + "Convert">
<#assign uncapClassName = className?uncap_first>
<#assign mapperClassName = className + "Mapper">

/**
* ${tableDesc}服务层实现类
* @author ${author}
* Create by on ${.now?string("yyyy-MM-dd")}
*/
@Service
public class ${className}ServiceImpl implements I${className}Service {

    private static final Logger logger = LoggerFactory.getLogger(${className}ServiceImpl.class);

    @Autowired
    private ${mapperClassName} ${uncapClassName}Mapper;

    @Override
    public boolean insert${className}(${infoType} ${uncapClassName}Info) {
        ${doType} ${uncapClassName} = ${convertClass}.INSTANCE.infoToDo(${uncapClassName}Info);
        return ${uncapClassName}Mapper.insert(${uncapClassName}) > 0;
    }

    @Override
    public boolean update${className}(${infoType} ${uncapClassName}Info) {
        ${doType} ${uncapClassName} = ${convertClass}.INSTANCE.infoToDo(${uncapClassName}Info);
        return ${uncapClassName}Mapper.update(${uncapClassName}) > 0;
    }

    @Override
    public boolean delete${className}By${primaryKeyName?cap_first!"Id"}(${primaryKeyType!"Long"} ${primaryKeyName!"id"}) {
        return ${uncapClassName}Mapper.delete(${primaryKeyName!"id"}) > 0;
    }
}