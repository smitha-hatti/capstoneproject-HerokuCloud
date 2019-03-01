package com.capstone.utility;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.servlet.ServletContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.capstone.model.Cart;

@Component
public class EmailConstructor {

	@Autowired
	private TemplateEngine templateEngine;

	public String constructOrderConfirmationEmail(List<Cart> cartItemsByUserID, BigDecimal grandTotal) {

		Context context = new Context();
		context.setVariable("cartItemsByUserID", cartItemsByUserID);
		context.setVariable("grandTotal", grandTotal);
		String text = templateEngine.process("orderConfirmationEmail", context);

		return text;
	}

}
