package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController {

    @RequestMapping(value = "/")
    public String index(HttpServletRequest request, HttpServletResponse response) {

        return "redirect:/user/index.do";
//        return "redirect:/test/hello.do";
    }
}
