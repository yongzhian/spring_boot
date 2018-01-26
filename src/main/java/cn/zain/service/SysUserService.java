package cn.zain.service;

import cn.zain.model.po.SysUser;

/**
 * @author Zain
 */
public interface SysUserService {

    /**
     * @param userId
     * @return
     */
    SysUser getSysUser(Long userId);

    /**
     * 功能说明：根据username查询用户
     *
     * @param username
     * @return
     */
    SysUser getByUsername(String username);
}
