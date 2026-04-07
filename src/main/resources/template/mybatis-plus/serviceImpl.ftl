package ${servicePackage}.impl;

import java.util.Map;
import java.util.List;

import ${servicePackage}.I${className}Service;
import ${mapperPackage}.${className}Mapper;
import ${doPackage}.${className}DO;
import ${coreModelPackage}.${className}Info;
import ${convertPackage}.${className}Convert;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
public class ${className}ServiceImpl extends ServiceImpl<${mapperClassName}, ${doType}> implements I${className}Service {

    private static final Logger logger = LoggerFactory.getLogger(${className}ServiceImpl.class);

    @Override
    public ${infoType} select${className}By${primaryKeyName?cap_first!"Id"}(${primaryKeyType!"Long"} ${primaryKeyName!"id"}) {
        return ${convertClass}.INSTANCE.doToInfo(getById(${primaryKeyName!"id"}));
    }

    @Override
    public List<${infoType}> select${className}List(${infoType} ${uncapClassName}Info) {
        ${doType} ${uncapClassName} = ${convertClass}.INSTANCE.infoToDo(${uncapClassName}Info);
        <#-- Hutool BeanUtil 将对象转为 Map 用于 QueryWrapper -->
        <#noparse>Map<String, Object></#noparse> map = BeanUtil.beanToMap(${uncapClassName}, true, true);

        // queryWrapper组装,具体查阅mybatis-plus官网
        QueryWrapper<${doType}> queryWrapper = new QueryWrapper<>();

        // false 不会把null值 转换为 isNull
        queryWrapper.allEq(map, false);
        return ${convertClass}.INSTANCE.doToInfoList(list(queryWrapper));
    }

    @Override
    public boolean insert${className}(${infoType} ${uncapClassName}Info) {
        ${doType} ${uncapClassName} = ${convertClass}.INSTANCE.infoToDo(${uncapClassName}Info);
        return save(${uncapClassName});
    }

    @Override
    public boolean insertOrUpdate${className}(${infoType} ${uncapClassName}Info) {
        ${doType} ${uncapClassName} = ${convertClass}.INSTANCE.infoToDo(${uncapClassName}Info);
        return saveOrUpdate(${uncapClassName});
    }

    @Override
    public boolean delete${className}By${primaryKeyName?cap_first!"Id"}(${primaryKeyType!"Long"} ${primaryKeyName!"id"}) {
        return removeById(${primaryKeyName!"id"});
    }
}