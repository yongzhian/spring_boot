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
}
