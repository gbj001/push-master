package com.xinliangjishipin.pushwms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by longshen on 2017/5/26.
 */

@Controller
public class HelloWorldApplication {
    //private final Logger log = Logger.getLogger(this.getClass());
    @RequestMapping(value = "/tetetewte")
    public String index(){
        //log.info("这是hello world！");
        return "index";
    }

    @RequestMapping(value = "/info/more")
    public String more(Model model){
        //log.info("这是详情页");
        model.addAttribute("name","张三");
        return "login2222";
    }
}


