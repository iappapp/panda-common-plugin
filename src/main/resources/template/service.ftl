package ${servicePackage};

import java.util.List;

import ${doPackage}.${className}DO;
import ${coreModelPackage}.${className}Info;

import com.baomidou.mybatisplus.extension.service.IService;

<#-- 预定义类型名称，防止泛型解析歧义 -->
<#assign infoType = className + "Info">
<#assign doType = className + "DO">

/**
* ${tableDesc}服务层
* @author ${author}
* Create by on ${.now?string("yyyy-MM-dd")}
*/
public interface I${className}Service extends IService<${doType}> {

    /**
    * 查询${tableDesc}
    *
    * @param ${primaryKeyName!"id"}
    * @return ${infoType}
    */
    ${infoType} select${className}By${primaryKeyName?cap_first!"Id"}(${primaryKeyType!"Long"} ${primaryKeyName!"id"});

    /**
    * 查询${tableDesc}列表
    *
    * @param ${className?uncap_first}Info
    * @return ${infoType}集合
    */
    List<${infoType}> select${className}List(${infoType} ${className?uncap_first}Info);

    /**
    * 新增${tableDesc}
    *
    * @param ${className?uncap_first}Info
    * @return 结果
    */
    boolean insert${className}(${infoType} ${className?uncap_first}Info);

    /**
    * 新增或修改${tableDesc},如果有主键id则修改
    *
    * @param ${className?uncap_first}Info
    * @return 结果
    */
    boolean insertOrUpdate${className}(${infoType} ${className?uncap_first}Info);

    /**
    * 删除${tableDesc}信息
    *
    * @param ${primaryKeyName!"id"}
    * @return 结果
    */
    boolean delete${className}By${primaryKeyName?cap_first!"Id"}(${primaryKeyType!"Long"} ${primaryKeyName!"id"});

}