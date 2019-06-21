package com.chaojilaji.messageborad.service.Impl;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;


@Service
public class EmailServiceImpl implements InitializingBean {

    private static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";




    @Autowired
    private JavaMailSender javaMailSender;


    @Autowired
    private TemplateEngine templateEngine;



    @Value("${spring.mail.username}")
    String from;

    @Value("${spring.mail.port}")
    String port;

    @Value("${spring.mail.host}")
    String host;

    @Value("${spring.mail.password}")
    String pwd;

    /**
     * 发送安全性的邮件
     * @param email 收件人
     * @param subject 主题
     * @param name 项目名称
     * @throws IOException
     * @throws MessagingException
     */
    public Boolean sendEmail(String email, String subject, String name) throws MessagingException {
        try {
            synchronized (this){
                if (!templateEngine.isInitialized())templateEngine.addTemplateResolver(htmlTemplateResolver());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        final Context context = new Context();
        context.setVariable("name", name);
        final String contant = templateEngine.process("templates/mail/VerificationCode", context);
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(from);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(contant, true);
        try {
            javaMailSender.send(msg);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private synchronized ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(2));
        templateResolver.setResolvablePatterns(Collections.singleton("templates/mail/*"));
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}

