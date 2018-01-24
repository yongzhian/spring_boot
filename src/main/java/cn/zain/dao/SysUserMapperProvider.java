package cn.zain.dao;

import cn.zain.model.po.SysUser;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @author Zain
 */
public class SysUserMapperProvider {
    public String insertAll(Map<String, Object> map) {
        List<SysUser> users = (List<SysUser>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO sys_user(username,password)");
        sb.append(" VALUES");
        MessageFormat mf = new MessageFormat("(''{0}'', ''{1}'')");
        for (int i = 0; i < users.size(); i++) {
            sb.append(mf.format(new Object[]{users.get(i).getUsername(), users.get(i).getUsername()}));
            if (i < users.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
