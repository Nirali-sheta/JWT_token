package com.example.Spring_sercurity_JWT.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class securityConfig {


    @Autowired
    public UserDetailsService userDetailsService;


    @Autowired
    private JwtFilter jwtFilter;

//    it disables the inbuilt securityfileterchain and we can customize the secuiryfilterchain using this way
//    by this we can disable the csrf

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(customizer->customizer.disable());
        http.authorizeHttpRequests(request->request
//                this request doesn't need authentication register and login
                .requestMatchers("auth/register","auth/refresh-token","auth/login")
                .permitAll()
                .anyRequest()
                .authenticated());
//        this is for browser
//        http.formLogin(Customizer.withDefaults());
//        this is only for postman otherwise it sends the html as response in postman
        http.httpBasic(Customizer.withDefaults());
//        for every request it generates new session id
        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return  http.build();
    }

//    cutomizing authentication
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }


//    customizing authenticationmanager

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

//    customizing a username and password

//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails user1= User
//                .withDefaultPasswordEncoder()
//                .username("nirali")
//                .password("N@iral1")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user1);
//    }
}
