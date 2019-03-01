package com.capstone.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import javax.mail.PasswordAuthentication;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.capstone.model.User;
import com.capstone.service.UserService;
import com.capstone.utility.SecurityUtility;
import com.sendgrid.*;

@Controller
public class AuthenticationController {

	@Autowired
	UserService userService;

	@Autowired
	BCryptPasswordEncoder encoder;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Value("${spring.mail.password}")
	private String fromEmailPassword;

	@Value("${spring.mail.host}")
	private String mailHost;

	@Value("${spring.mail.port}")
	private String mailPost;

	@RequestMapping(value = { "/index", "/" }, method = RequestMethod.GET)
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index"); // resources/template/index.html
		return modelAndView;
	}

	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();

		modelAndView.setViewName("login"); // resources/template/login.html
		return modelAndView;
	}

	@RequestMapping(value = { "/forgotPassword" }, method = RequestMethod.GET)
	public ModelAndView forgotPassword() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);

		modelAndView.setViewName("forgotPassword"); // resources/template/forgotPassword.html
		return modelAndView;
	}

	@RequestMapping(value = { "/forgotPassword" }, method = RequestMethod.POST)
	public ModelAndView forgotPasswordRetrieval(User user, ModelMap modelMap)
			throws IOException {

		ModelAndView modelAndView = new ModelAndView();
		if (userService.isUserAlreadyPresent(user)) {
			user = userService.findByUsername(user.getEmail());
			String passwordNew = SecurityUtility.randomPassword();
			user.setPassword(passwordNew);
			userService.saveUser(user);

                        Email from = new Email(fromEmail);
                        String subject = "Password recovery from One Stop Shoppe";
                        Email to = new Email(user.getEmail());
                        Content content = new Content("text/plain", " Your new password is: " + passwordNew);
                        Mail mail = new Mail(from, subject, to, content);
                        String api_key = "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";
                        SendGrid sg = new SendGrid(api_key);

                        Request request = new Request();
                        try {
                           request.method = Method.POST;
                           request.endpoint = "mail/send";
                           request.body = mail.build();
                           Response response = sg.api(request);
                           System.out.println(response.statusCode);
                           System.out.println(response.body);
                           System.out.println(response.headers);
                         } catch (IOException ex) {
                           throw ex;
                         }

			modelAndView.addObject("successMessage", "The password has been sent to your email. please login !!");
			modelAndView.setViewName("login");
			return modelAndView;
		} else {

			modelAndView.addObject("successMessage",
					"The email address you entered is not registered. Please enter a valid email");
			modelAndView.setViewName("forgotPassword");
			return modelAndView;
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("register"); // resources/template/register.html
		return modelAndView;
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home"); 
		return modelAndView;
	}

	@RequestMapping(value = { "/register" }, method = { RequestMethod.POST })
	public ModelAndView registerUser(@Valid User user, BindingResult bindingResult, ModelMap modelMap) {
		ModelAndView modelAndView = new ModelAndView();

		if (bindingResult.hasErrors()) {
			modelAndView.addObject("successMessage", "Please enter the right information");
			modelMap.addAttribute("bindingResult", bindingResult);

		} else if (userService.isUserAlreadyPresent(user)) {

			modelAndView.addObject("successMessage", "The user already exists");
			modelAndView.setViewName("register");
			return modelAndView;

		} else {
			userService.saveUser(user);
			modelAndView.addObject("email", user.getEmail());
			modelAndView.addObject("password", user.getPassword());
			modelAndView.addObject("successMessage", "The user is registered successfully, Please login.");
			modelAndView.setViewName("login");

		}

		return modelAndView;
	}

}
