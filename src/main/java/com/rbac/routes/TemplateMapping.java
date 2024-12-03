package com.rbac.routes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * @author pratiksolanki
 */
@Controller
@Slf4j
public class TemplateMapping {

    @RequestMapping("/login")
    public String login() {
        return "index";
    }

    @RequestMapping("/signup")
    public String signup() {
        return "index";
    }
    
    @RequestMapping("/app/**")
    public String appPath() {
        return "index";
    }

}
