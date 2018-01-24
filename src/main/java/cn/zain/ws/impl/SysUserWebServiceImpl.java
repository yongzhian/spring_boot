package cn.zain.ws.impl;

import cn.zain.model.po.SysUser;
import cn.zain.service.SysUserService;
import cn.zain.ws.SysUserWebService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jws.WebService;

/**
 * @author Zain
 */
@WebService(serviceName = "SysUserWebService", // 与接口中指定的name一致
        targetNamespace = "http://ws.zain.cn/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "cn.zain.ws.SysUserWebService"// 接口地址
)
@Component
public class SysUserWebServiceImpl implements SysUserWebService {
    @Resource
    private SysUserService sysUserService;

    @Override
    public SysUser getSysUser(Long userId) {
        return sysUserService.getSysUser(userId);
    }
}
