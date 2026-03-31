package ${coreModelPackage};

<#-- 自动分析需要导入的类型 -->
<#assign hasDate = false />
<#assign hasBigDecimal = false />
<#list fields as field>
    <#if field.type == "Date"><#assign hasDate = true /></#if>
    <#if field.type == "BigDecimal"><#assign hasBigDecimal = true /></#if>
</#list>
<#if hasDate>
import java.util.Date;
</#if>
<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>

<#if useLombok>
import lombok.Data;
</#if>

/**
* ${tableDesc}
* @author ${author}
* Create by on ${.now?string("yyyy-MM-dd")}
*/
<#if useLombok>
@Data
</#if>
public class ${className}Info {

<#list fields as field>
    /** ${field.comment} */
    private ${field.type} ${field.name};

</#list>
<#if !useLombok>
<#-- 手写 Getter/Setter -->
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