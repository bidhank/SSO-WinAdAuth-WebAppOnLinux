package com.bidhan;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping(value = "/")
public class MainController {

  @GetMapping(value = "/")
  @ResponseBody
  public String  foo(Principal principal) {

    if (principal != null) {
      return String.format("Welcome to Web App %s", principal.getName());

    } else {
      return "Oops, You are NOT a member of Active Directory Domain !!";
    }
  }
}
