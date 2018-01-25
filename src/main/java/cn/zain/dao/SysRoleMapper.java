package cn.zain.dao;

import cn.zain.model.po.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Zain
 */
@Mapper
public interface SysRoleMapper {
    /**
     * 功能说明：某个用户所有的角色
     * @param sysUserId Long
     * @return
     */
    @Select("SELECT * " +
            "FROM sys_role sr,sys_user_role sur " +
            "WHERE sr.id = sur.sys_role_id AND sur.id = #{sysUserId}")
    List<SysRole> getSysRole(Long sysUserId);
}
