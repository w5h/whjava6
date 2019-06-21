
package com.chaojilaji.messageborad.service;


public interface UserSerivice {
    Boolean checkUser(String userName, String password,String code);
    Boolean checkLogin(String userName,String password);
}