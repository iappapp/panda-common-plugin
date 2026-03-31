package ${doPackage};

<#-- 自动分析需要导入的类 -->
<#list fields as field>
    <#if field.type == "Date"><#assign hasDate = true /></#if>
    <#if field.type == "BigDecimal"><#assign hasBigDecimal = true /></#if>
</#list>
<#if hasDate??>
import java.util.Date;
</#if>
<#if hasBigDecimal??>
import java.math.BigDecimal;
</#if>

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("${tableName}")
public class ${className}DO {

<#list fields as field>
    <#if field.primaryKey>
    @TableId(type = IdType.${idType!"AUTO"})
    </#if>
    /** ${field.comment} */
    private ${field.type} ${field.name};

</#list>
<#if !useLombok>
<#-- 手写 Getter/Setter 逻辑 -->
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