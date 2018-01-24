package cn.zain.service.impl;

import cn.zain.dao.SysUserMapper;
import cn.zain.model.po.SysUser;
import cn.zain.service.SysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser getSysUser(Long userId) {
        return sysUserMapper.getById(userId);
    }
}
