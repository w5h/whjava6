
package com.chaojilaji.messageborad.service.Impl;


import com.chaojilaji.messageborad.mapper.SqlMapper;
import com.chaojilaji.messageborad.service.UserSerivice;
import com.chaojilaji.messageborad.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserSerivice {

    @Autowired
    private SqlMapper sqlMapper;

    @Autowired
    private EmailServiceImpl emailService;

    private ConcurrentHashMap<String,String> registerCodes = new ConcurrentHashMap<>();

    @Override
    public Boolean checkUser(String userName, String password,String code) {
        String newPassword = Token.md5Encode(password);
        String sql1 = "select count(*) from user where user_name=#{userName}";
        Map<String,Object>params = new HashMap<>();
        params.put("sql",sql1);
        params.put("userName",userName);
        List<Map<String,Object>> res = sqlMapper.sql(params);
        if(Objects.nonNull(res) && res.size()>0){
            Map<String,Object> tmp = res.get(0);
            long cnt = (long) tmp.get("count(*)");
            if (cnt == 1){
                return false;
            }
        }
        if (Objects.isNull(code)){
            try {
                code = getCode(6);
                emailService.sendEmail(userName,"留言板注册",code);
                registerCodes.put(userName,code);
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }else {
            if (code.equals(registerCodes.get(userName))){
                String sql = "insert into user(user_name,user_password) values(#{userName},#{password})";
                Map<String,Object> params1 = new HashMap<>();
                params1.put("sql",sql);
                params1.put("userName",userName);
                params1.put("password",newPassword);
                try {
                    sqlMapper.sql(params1);
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
                return true;
            }else {
                return false;
            }

        }

    }

    @Override
    public Boolean checkLogin(String userName, String password) {
        password = Token.md5Encode(password);
        String sql = "select count(*) from user where user_name=#{username} and user_password=#{password}";
        Map<String,Object> params = new HashMap<>();
        params.put("sql",sql);
        params.put("username",userName);
        params.put("password",password);
        List<Map<String,Object>> res = sqlMapper.sql(params);
        if (Objects.nonNull(res) && res.size()>0){
            Map<String,Object> tmp = res.get(0);
            long cnt = (long) tmp.get("count(*)");
            if (cnt == 1){
                return true;
            }
        }
        return false;
    }

    private String getCode(int length){
        Random random = new Random();
        String val = "";
        for (int i = 0; i < length; i++){
            int type = random.nextInt(3);
            if (type == 0){
                val = val + String.valueOf(random.nextInt(9));
            }else if (type == 1){
                val = val + (char) (random.nextInt(26) + 65);
            }else if (type == 2){
                val = val + (char) (random.nextInt(26) + 97);
            }
        }
        return val;
    }
}