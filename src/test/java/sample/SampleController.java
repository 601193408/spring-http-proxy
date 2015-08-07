package sample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marklogic.spring.proxy.MarkLogicProxy;

@Controller
@EnableAutoConfiguration
public class SampleController {

    private MarkLogicProxy mlProxy;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }

    /**
     * mlProxy would normally be autowired in as a dependency.
     */
    public SampleController() {
        mlProxy = new MarkLogicProxy();
    }

    @RequestMapping("/v1/*")
    @ResponseBody
    public void home(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        mlProxy.proxy(httpRequest, httpResponse);
    }

}