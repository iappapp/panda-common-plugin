package ${mapperPackage};

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import ${doPackage}.${className}DO;

/**
* ${tableDesc}
* @author ${author}
* Create by on ${.now?string("yyyy-MM-dd")}
*/
@Mapper
@Repository
public interface ${className}Mapper {

    /**
    * 新增
    * @param ${className?uncap_first}DO
    **/
    int insert(${className}DO ${className?uncap_first}DO);

    /**
    * 删除
    * @param id
    **/
    int delete(long id);

    /**
    * 更新
    * @param ${className?uncap_first}DO
    **/
    int update(${className}DO ${className?uncap_first}DO);
}