package util_demo.contorller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanzhangkai@foxmail.com
 * @date 2018/7/12 11:30
 */
@RestController
public class HelloController {

    @RequestMapping(value="/hello",method=RequestMethod.GET)
    public String test() {
        return "Hello";
    }

}