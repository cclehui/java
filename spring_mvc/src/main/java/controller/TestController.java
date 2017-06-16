package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "/hello")
    public String hello(ModelMap model) {
        model.addAttribute("message", "goooooood");
        
        return "hello";
        
    }
    
    
}
