package cn.zain.ws;

import cn.zain.model.po.SysUser;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.List;

public class SysUserWebServiceTest {

    //不带密码
    @Test
    public void getSysUserTest() throws Exception {
        String address = "http://localhost:8081/services/SysUserWebService?wsdl";
        // 代理工厂
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress(address);
        jaxWsProxyFactoryBean.setServiceClass(SysUserWebService.class);
        SysUserWebService cs = (SysUserWebService) jaxWsProxyFactoryBean.create();

        final SysUser sysUser = cs.getSysUser(5L);
        System.out.println("success");
    }

    @Test
    public void getSysUserWithAuthTest() throws Exception {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        String address = "http://localhost:8081/services/SysUserWebService?wsdl";
        Client client = dcf.createClient(address);
        client.getOutInterceptors().add(new ClientLoginInterceptor("admin", "test"));

        Object[] objects = new Object[0];
        try {
            // invoke("方法名",参数1,参数2,参数3....);
            objects = client.invoke("getSysUser", 5L);
            System.out.println("返回数据:" + objects);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    private class ClientLoginInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
        public ClientLoginInterceptor(String username, String password) {
            super(Phase.PREPARE_SEND);
            this.username = username;
            this.password = password;
        }


        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public void handleMessage(SoapMessage soapMessage) throws Fault {
            List<Header> headers = soapMessage.getHeaders();
            Document doc = DOMUtils.createDocument();
            Element auth = doc.createElement("authrity");
            Element username = doc.createElement("username");
            Element password = doc.createElement("password");

            username.setTextContent(this.username);
            password.setTextContent(this.password);

            auth.appendChild(username);
            auth.appendChild(password);

            headers.add(0, new Header(new QName("tiamaes"), auth));
        }
    }
}
