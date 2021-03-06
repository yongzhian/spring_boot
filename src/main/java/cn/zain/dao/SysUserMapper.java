package cn.zain.dao;

import cn.zain.model.po.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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
            @Result(property = "password", column = "password"),
            @Result(property = "isValid", column = "is_valid")
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
            @Result(property = "password", column = "password"),
            @Result(property = "isValid", column = "is_valid")
    })
    SysUser getById(Long id);

    /**
     * 功能说明：根据ID查询用户到MAP中
     *
     * @param id
     * @return
     */
    @Select("select id,username,create_time from sys_user where id=#{id}")
    Map<String,Object> getInMapById(Long id);

    /**
     * 功能说明：根据username查询用户
     *
     * @param username
     * @return
     */
    @Select("select * from sys_user where username=#{username}")
    @Results({
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "isValid", column = "is_valid")
    })
    SysUser getByUsername(String username);

    /**
     * 功能说明：添加用户
     *
     * @param sysUser
     * @return
     */
    @Insert("INSERT INTO SYS_USER(username,password) values(#{username},#{password})")
    @Options(useGeneratedKeys = true)
    int insert(SysUser sysUser);

    /**
     * 功能说明：批量添加用户
     * 通过SQL工厂类及对应的方法生产SQL语句,根据不同的需求生产出不同的SQL，适用性更好。
     * insertAll方法参数必须Map<String,Object> para
     *
     * @param sysUserList
     * @return
     */
    @InsertProvider(type = SysUserMapperProvider.class, method = "insertAll")
    @Options(useGeneratedKeys = true)
    int insertAll(List<SysUser> sysUserList);
}
