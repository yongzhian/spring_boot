package cn.zain.dao;

import cn.zain.model.po.SysUser;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @author Zain
 */
public class SysUserMapperProvider {
    public String insertAll(Map map) {
        List<SysUser> users = (List<SysUser>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO User ");
        sb.append("(id, name) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat("(null, #'{'list[{0}].name})");
        for (int i = 0; i < users.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < users.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
