
package com.chaojilaji.messageborad.controller;


import com.chaojilaji.messageborad.Constant;
import com.chaojilaji.messageborad.model.User;
import com.chaojilaji.messageborad.service.UserSerivice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@SessionAttributes("user")
@Api
public class UserController{

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserSerivice userSerivice;

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "注册用户", notes="加入一个新的用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",value = "用户名",paramType = "path",dataType = "string"),
            @ApiImplicitParam(name = "password",value = "密码",paramType = "path",dataType = "string")
    })
    public Map<String,Object> register(@RequestParam("username")final String username,
                                       @RequestParam("password")final String password,
                                       String code){
        Map<String,Object> ans = new HashMap<>();
        logger.info("提交的参数{}{}{}",username,password,code);
        if (userSerivice.checkUser(username,password,code)){
            ans.put("info","成功");
            ans.put("code",200);
        }


        return ans;
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "用户登录", notes="根据用户名与密码进行登陆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",value = "用户名",paramType = "path",dataType = "string"),
            @ApiImplicitParam(name = "password",value = "密码",paramType = "path",dataType = "string")
    })
    public Map<String,Object> login(@RequestParam("username")final String userName,
                                    @RequestParam("password")final String password,
                                    Model model,
                                    HttpServletRequest httpServletRequest){
        Map<String,Object> ans = new HashMap<>();
        ans.put("code",400);
        ans.put("info","失败");
        if (userSerivice.checkLogin(userName,password)){
            ans.put("code",200);
            ans.put("info","成功");
            User user = new User();
            user.setUserName(userName);
            model.addAttribute("user",user);
            httpServletRequest.getSession();
        }
        return ans;
    }

    @RequestMapping(value = "/logincheck",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "检查用户", notes="判断用户是否登录成功")
    public Map<String,Object> loginCheck(@ModelAttribute("user")final User user){
        String userName = user.getUserName();
        Map<String,Object> ans = new HashMap<>();
        ans.put("code",200);
        ans.put("info",userName);
        return ans;
    }



}