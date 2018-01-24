package cn.zain.ws;

import cn.zain.model.po.SysUser;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * ws接口
 * @author Zain
 */
@WebService(name = "SysUserWebService", // 暴露服务名称
        targetNamespace = "http://ws.zain.cn/"// 命名空间,一般是接口的包名倒序
)
public interface SysUserWebService {

    /**
     * 功能说明：根据ID查询用户
     * @param userId
     * @return
     */
    @WebMethod
    @WebResult(name = "Object", targetNamespace = "")
    SysUser getSysUser(Long userId);

}
