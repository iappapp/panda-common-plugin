<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperPackage}.${className}Mapper">

    <resultMap id="BaseResultMap" type="${doPackage}.${className}DO" >
        <#list fields as field>
            <#if field.primaryKey>
        <id column="${field.columnName}" property="${field.name}" />
            <#else>
        <result column="${field.columnName}" property="${field.name}" />
            </#if>
        </#list>
    </resultMap>

    <sql id="Base_Column_List">
        <#list fields as field>${field.columnName}<#if field_has_next>, </#if></#list>
    </sql>

    <insert id="insert" useGeneratedKeys="true" keyColumn="${primaryKeyColumn!"id"}" keyProperty="${primaryKeyName!"id"}" parameterType="${doPackage}.${className}DO">
        INSERT INTO ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list fields as field>
                <#if !field.primaryKey>
            <if test="null != ${field.name}<#if field.type == 'String'> and '' != ${field.name}</#if>">
                ${field.columnName},
            </if>
                </#if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list fields as field>
                <#if !field.primaryKey>
            <if test="null != ${field.name}<#if field.type == 'String'> and '' != ${field.name}</#if>">
                <#noparse>#</#noparse>{${field.name}},
            </if>
                </#if>
            </#list>
        </trim>
    </insert>

    <delete id="delete">
        DELETE FROM ${tableName}
        WHERE ${primaryKeyColumn!"id"} = <#noparse>#</#noparse>{${primaryKeyName!"id"}}
    </delete>

    <update id="update" parameterType="${doPackage}.${className}DO">
        UPDATE ${tableName}
        <set>
            <#list fields as field>
                <#if !field.primaryKey>
        <if test="null != ${field.name}<#if field.type == 'String'> and '' != ${field.name}</#if>">${field.columnName} = <#noparse>#</#noparse>{${field.name}}<#if field_has_next>,</#if></if>
                </#if>
            </#list>
        </set>
        WHERE ${primaryKeyColumn!"id"} = <#noparse>#</#noparse>{${primaryKeyName!"id"}}
    </update>
</mapper>