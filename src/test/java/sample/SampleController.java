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

/**
 * Spring Boot class that can be used to quickly fire up a Tomcat instance for testing out our proxy in a real-life
 * scenario - i.e. where we're receiving HTTP requests via Spring MVC and then proxying them to MarkLogic.
 * 
 * This class is currently hardcoding a number of things that would normally be autowired in via Spring, but it's just
 * for demonstration purposes.
 */
@Controller
@EnableAutoConfiguration
public class SampleController {

    // These would normally be in another Spring bean
    private String host = "localhost";
    private int port = 8320;
    private String username = "admin";
    private String password = "admin";

    private MarkLogicProxy mlProxy;

    /**
     * Fire up Tomcat.
     */
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

    /**
     * A Spring-MVC controller can have as many methods like this as it wants. This one is copying the Content-type
     * header to the MarkLogic HTTP request; your method can choose whichever headers it wants.
     */
    @RequestMapping("/v1/*")
    @ResponseBody
    public void home(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        mlProxy.proxy(httpRequest, httpResponse, "Content-type");
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