package com.ingroinfo.trainProject.Controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ingroinfo.trainProject.Repository.UserRepository;
import com.ingroinfo.trainProject.entities.User;
import com.ingroinfo.trainProject.service.EmailService;

@Controller
public class ForgotController {

	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	//email id form open handler
	@RequestMapping("/forgot")
	public String OpenEmailForm() {
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session) {
		System.out.println("EMAIL "+email);
		//generating otp for 4 digits
		

		int otp = random.nextInt(9999);
		System.out.println("OTP "+otp);
		
		//write code for send otp to email...
		
		String subject="OTP FROM INDAIN RAILWAY";
		String message=""
				+"<div style='border:1px solid #e2e2e2; padding:20px'>"
				+"<h1>"
				+"OTP is = "
				+"<b>"+otp
				+"</n>"
				+"</h1>"
				+"</div>";
		String to=email;
		
		boolean flag = this.emailService.sendEmail(message, subject, to);
		
		if(flag) {
			
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
			
		}else {
			
			session.setAttribute("message", "Check your Email id!!!");
			return "forgot_email_form";
		}
		
	}
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {
		int myOtp =(int)session.getAttribute("myotp");
		String email= (String)session.getAttribute("email");
		
		if(myOtp==otp) {
			//password change form
			User user = this.userRepository.getUserByUserEmail(email);
			if(user==null) {
				//send error message
				session.setAttribute("message", "User does not exit with this email!!!");
				
				return "verify_otp";
				
			}else {
				//send change password form
			}
			
			return "password_change_form";
		}else {
			session.setAttribute("message", "You have entered wrong OTP!!!");
			return "verify_otp";
		}
		
	}
	
	//change password 
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		String email= (String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserEmail(email);
		user.setPassword(this.bcryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		
		return "redirect:/loginPage?change=password changed successfully...";
	}
}
