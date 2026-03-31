package ${mapperPackage};

import ${doPackage}.${className}DO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* ${tableDesc} Mapper
* @author ${author}
* Create by on ${.now?string("yyyy-MM-dd")}
*/
@Mapper
public interface ${className}Mapper extends BaseMapper<${className}DO> {

    <#-- 这里可以预留自定义 SQL 方法的存放位置 -->
}