package com.xinliangjishipin.pushwms.controller;

import com.xinliangjishipin.pushwms.config.WebSecurityConfig;
import com.xinliangjishipin.pushwms.entity.User;
import com.xinliangjishipin.pushwms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {


    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(@SessionAttribute(WebSecurityConfig.SESSION_KEY) String account, Model model) {
        model.addAttribute("name", account);
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, ModelMap map) {

        //读取cookies
        String name = "";
        String password = "";
        try{
            Cookie[] cookies = request.getCookies();
            if(cookies!=null){
                for(int i = 0;i<cookies.length;i++){
                    if(cookies[i].getName().equals("cookie_user")){
                        String values = cookies[i].getValue();
                        // 如果value字段不为空
                        if(!StringUtils.isEmpty(values)){
                            String[] elements = values.split("-");
                            // 获取账户名或者密码
                            if(!StringUtils.isEmpty(elements[0])){
                                name = elements[0];
                            }
                            if(!StringUtils.isEmpty(elements[1])){
                                password =  elements[1];
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
        }
        map.addAttribute("userName",name);
        map.addAttribute("password", password);

        return "login";
    }

    @PostMapping("/loginPost")
    public String loginPost(String account, String password, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        User user = userService.userLogin(account, password);

        if (user == null) {
            map.put("success", false);
            map.put("message", "用户名或密码错误");
            return "login";
        }
        // 设置session
        session.setAttribute(WebSecurityConfig.SESSION_KEY, account);
        session.setAttribute(WebSecurityConfig.REALNAME_KEY, user.getRealName());

        // 放到cookie中
        String flag = request.getParameter("flag");
        // 如果需要记住账户就存储账号和密码
        if(flag!=null&&flag.equals("1")){
            Cookie cookie = new Cookie("cookie_user",user.getUserName()+"-"+user.getPassword());
            cookie.setMaxAge(60*60*24*3);// 保存
            response.addCookie(cookie);
        }

        return "redirect:/";
    }

    @GetMapping("/loginOut")
    public String logout(HttpSession session) {
        // 移除session
        session.removeAttribute(WebSecurityConfig.SESSION_KEY);
        session.removeAttribute(WebSecurityConfig.REALNAME_KEY);
        return "redirect:/login";
    }


}
