package ${doPackage};

<#-- 自动导入逻辑 -->
<#assign importList = [] />
<#list fields as field>
    <#if field.type == "BigDecimal" && !importList?seq_contains("java.math.BigDecimal")>
        <#assign importList = importList + ["java.math.BigDecimal"] />
    </#if>
    <#if field.type == "Date" && !importList?seq_contains("java.util.Date")>
        <#assign importList = importList + ["java.util.Date"] />
    </#if>
</#list>
<#list importList as importItem>
import ${importItem};
</#list>

<#t>

<#if useLombok>
import lombok.Data;
</#if>
<#t>
/**
* ${tableDesc}
* @author ${author}
* Create by on ${.now?string("yyyy-MM-dd")}
*/
<#if useLombok>
@Data
</#if>
public class ${className}DO {

<#list fields as field>
    /** ${field.comment} */
    private ${field.type} ${field.name};

</#list>
<#if !useLombok>
<#-- 非 Lombok 模式下的 Getter/Setter -->
    <#list fields as field>
    public ${field.type} get${field.name?cap_first}() {
        return this.${field.name};
    }

    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
    }
    </#list>
</#if>
}