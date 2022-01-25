package com.bidhan;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .exceptionHandling()
        .authenticationEntryPoint(spnegoEntryPoint())
        .and()
        .authorizeRequests()
        .anyRequest().authenticated()

        .and()
        .logout()
        .permitAll()
        .and()
        .addFilterBefore(
            spnegoAuthenticationProcessingFilter(),
            BasicAuthenticationFilter.class);
  }

  @Bean
  public SpnegoEntryPoint spnegoEntryPoint() {
    return new SpnegoEntryPoint("/");
  }

  @Bean
  public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter() {
    SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
    try {
      AuthenticationManager authenticationManager = authenticationManagerBean();
      filter.setAuthenticationManager(authenticationManager);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return filter;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .authenticationProvider(kerberosAuthenticationProvider())
        .authenticationProvider(kerberosServiceAuthenticationProvider());
  }

  @Bean
  public KerberosAuthenticationProvider kerberosAuthenticationProvider() {
    KerberosAuthenticationProvider provider = new KerberosAuthenticationProvider();
    SunJaasKerberosClient client = new SunJaasKerberosClient();
    client.setDebug(true);
    provider.setKerberosClient(client);
    provider.setUserDetailsService(dummyUserDetailsService());
    return provider;
  }

  @Bean
  public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
    KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
    provider.setTicketValidator(sunJaasKerberosTicketValidator());
    provider.setUserDetailsService(dummyUserDetailsService());
    return provider;
  }

  @Bean
  public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
    SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
    ticketValidator.setServicePrincipal("HTTP/hostwebapp@valuetank.com"); // SPN
    ticketValidator.setKeyTabLocation(new FileSystemResource("/opt/apache-tomcat-8.5.72/conf/tom.keytab"));// KeyTab Location
    ticketValidator.setDebug(true);
    return ticketValidator;
  }

  @Bean
  public DummyUserDetailsService dummyUserDetailsService() {
    return new DummyUserDetailsService();
  }
}
