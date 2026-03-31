package ${convertPackage};

import java.util.List;

import ${doPackage}.${className}DO;
import ${facadeModelPackage}.${className}Model;
import ${coreModelPackage}.${className}Info;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

<#assign infoClassName = className + "Info">
<#assign modelClassName = className + "Model">

/**
 * ${tableDesc}转换类
 * @author ${author}
 * Create by on ${.now?string("yyyy-MM-dd")}
 */
@Mapper
public interface ${className}Convert {
    ${className}Convert INSTANCE = Mappers.getMapper(${className}Convert.class);

    /**
     * Info 转 DO
     */
    ${className}DO infoToDo(${className}Info ${className?uncap_first}Info);

    /**
     * InfoList 转 DOList
     */
    List<${className}DO> infoToDoList(List<${infoClassName}> ${className?uncap_first}InfoList);

    /**
     * DO 转 Info
     */
    ${className}Info doToInfo(${className}DO ${className?uncap_first});

    /**
     * DOList 转 InfoList
     */
    List<${infoClassName}> doToInfoList(List<${className}DO> ${className?uncap_first}List);

    /**
     * Info 转 Model
     */
    ${className}Model infoToModel(${className}Info ${className?uncap_first}Info);

    /**
     * InfoList 转 ModelList
     */
    List<${modelClassName}> infoToModelList(List<${infoClassName}> ${className?uncap_first}InfoList);
}