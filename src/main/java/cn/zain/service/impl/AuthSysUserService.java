package cn.zain.service.impl;

import cn.zain.dao.SysRoleMapper;
import cn.zain.dao.SysUserMapper;
import cn.zain.model.po.SysRole;
import cn.zain.model.po.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 集成spring的security控制，需要UserDetailsService
 *
 * @author Zain
 */
@Service("authSysUserService")
public class AuthSysUserService implements UserDetailsService {
    private static Logger logger = LoggerFactory.getLogger(AuthSysUserService.class);

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.getByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在" + username);
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        final List<SysRole> sysRole = sysRoleMapper.getSysRole(sysUser.getId());

        //无权限则返回
        if (null == sysRole || sysRole.isEmpty()) {
            return new org.springframework.security.core.userdetails.User(sysUser.getUsername(),
                    sysUser.getPassword(), authorities);
        }

        for (SysRole role : sysRole) {
            authorities.add(new SimpleGrantedAuthority(role.getRolename()));
        }
        return new org.springframework.security.core.userdetails.User(sysUser.getUsername(),
                sysUser.getPassword(), authorities);
    }
}
