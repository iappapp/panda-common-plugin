package ${facadePackage};

import org.springframework.web.bind.annotation.RequestMapping;

/**
* ${tableDesc}
* @author ${author}
* Create by on ${.now?string("yyyy-MM-dd")}
*/
<#-- 自动将类名转为小写作为路径，例如 Chain -> chain -->
<#assign pathName = className?replace("Facade", "")?lower_case />
@RequestMapping("api/${pathName}")
public interface ${className}Facade {

<#-- 这里可以预留标准的 CRUD 方法定义，例如：
@PostMapping("/add")
Result<Boolean> add(@RequestBody ${className}DTO dto);
-->
}