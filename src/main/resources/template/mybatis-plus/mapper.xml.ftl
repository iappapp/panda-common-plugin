<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperPackage}.${className}Mapper">

    <resultMap id="BaseResultMap" type="${doPackage}.${className}DO">
        <#list fields as field>
            <#if field.primaryKey>
        <id column="${field.columnName}" property="${field.name}"/>
            <#else>
        <result column="${field.columnName}" property="${field.name}"/>
            </#if>
        </#list>
    </resultMap>

    <sql id="Base_Column_List">
        <#list fields as field>
        ${field.columnName}<#if field_has_next>, </#if>
        </#list>
    </sql>
</mapper>