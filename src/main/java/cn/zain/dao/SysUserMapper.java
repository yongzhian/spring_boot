package cn.zain.dao;

import cn.zain.model.po.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Zain
 */
@Mapper
public interface SysUserMapper {
    /**
     * 功能说明：查询所有用户
     *
     * @return
     */
    @Select("select * from sys_user")
    @Results({
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password")
    })
    List<SysUser> getAll();

    /**
     * 功能说明：根据ID查询用户
     *
     * @param id
     * @return
     */
    @Select("select * from sys_user where id=#{id}")
    @Results({
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password")
    })
    SysUser getById(Long id);

    /**
     * 功能说明：添加用户
     *
     * @param sysUser
     * @return
     */
    @Insert("insert into sys_user() values(O)")
    @Options(useGeneratedKeys = true)
    int insert(SysUser sysUser);

    /**
     * 功能说明：批量添加用户
     * @param sysUserList
     * @return
     */
    @InsertProvider(type = SysUserMapperProvider.class, method = "insertAll")
    int insert(List<SysUser> sysUserList);
}
