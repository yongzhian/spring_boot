package cn.zain.service.impl;

import cn.zain.dao.SysRoleMapper;
import cn.zain.dao.SysUserMapper;
import cn.zain.model.po.SysUser;
import cn.zain.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Zain
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    private static Logger logger = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public SysUser getSysUser(Long userId) {
        return sysUserMapper.getById(userId);
    }

    @Override
    public SysUser getByUsername(String username) {
        final SysUser sysUser = sysUserMapper.getByUsername(username);
        if(null == sysUser){
            return null;
        }
        sysUser.setSysRoles(sysRoleMapper.getSysRole(sysUser.getId()));
        return sysUser;
    }
}
