package sample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.marklogic.spring.proxy.MarkLogicProxy;

@Controller
@EnableAutoConfiguration
public class SampleController {

    // These would normally be in another Spring bean
    private String host = "localhost";
    private int port = 8320;
    private String username = "admin";
    private String password = "admin";

    private MarkLogicProxy mlProxy;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }

    /**
     * mlProxy would normally be autowired in as a dependency.
     */
    public SampleController() {
        RestTemplate t = newRestTemplate(host, port, username, password);
        mlProxy = new MarkLogicProxy(t, host, port);
    }

    @RequestMapping("/v1/*")
    @ResponseBody
    public void home(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        mlProxy.proxy(httpRequest, httpResponse);
    }

    /**
     * This would normally be handled by Spring.
     */
    public RestTemplate newRestTemplate(String host, int port, String username, String password) {
        BasicCredentialsProvider prov = new BasicCredentialsProvider();
        prov.setCredentials(new AuthScope(host, port, AuthScope.ANY_REALM), new UsernamePasswordCredentials(username,
                password));
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(prov).build();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
    }
}