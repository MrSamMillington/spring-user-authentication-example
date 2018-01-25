package com.teamtreehouse.todotoday.config;

import com.teamtreehouse.todotoday.service.UserService;
import com.teamtreehouse.todotoday.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //extends spring security filter chain

    @Autowired
    private UserService userService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder()); //configuring the auth manager builder
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**"); //spring ignores requests to anythin in that directory so unauthorised users can load them
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().hasRole("USER") //Not ROLE_USER, .hasRole prepends ROLE_
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler())
                .and()
            .logout()
                .permitAll()
                .logoutSuccessUrl("/login")
                .and()
                .csrf(); //cserf -> creates guids on value attributes on forms, will then cross reference the guid to the sessionid
    }

    private AuthenticationSuccessHandler loginSuccessHandler() { //AuthenSuccessHandler is an interface

        return (req, res, authentication) -> res.sendRedirect("/"); //redirect to application root
    }

    private AuthenticationFailureHandler loginFailureHandler() {
        return (req, res, authentication) -> {
            req.getSession().setAttribute("flash", new FlashMessage("Incorrect username or password", FlashMessage.Status.FAILURE));
            res.sendRedirect("/login");
        };
    }

    @Bean
    public EvaluationContextExtension securityExtension(){
        return new EvaluationContextExtensionSupport() {
            @Override
            public String getExtensionId() {
                return "security";
            }

            @Override
            public Object getRootObject() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                return new SecurityExpressionRoot(authentication) {};
            }
        };
    }

    //some security concerns
    //password protection -> currently inserted as plain text, needs encryption -> Bcrypt hashing function
    //csrf protection -> cserf -> cross site request forgery -> i.e getting hold of the JESSIONID cookie
                                // dont use get requests for anything that can change server data

    //other to read about:
        //Brute force attack
        //Code injection
        //Cross-site scripting
        //Session fixation
        //SQL injection


}
