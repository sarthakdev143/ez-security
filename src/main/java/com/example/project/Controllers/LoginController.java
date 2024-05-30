package com.example.project.Controllers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.project.Entity.Role;
import com.example.project.Entity.User;
import com.example.project.Entity.UserDto;
import com.example.project.Repository.RoleRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Security.TbConstants;
import com.example.project.Services.UserServiceImpl;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/sign-up")
    public String registrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        System.out.println("User On Sign-up Page");
        return "sign-up";
    }
    
    @SuppressWarnings("null")
    @PostMapping("/sign-up")
    public String registration(@ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {
        // Check if the user already exists
        Optional<User> existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser.isPresent()) {
            // If the user already exists, reject the registration and return to the
            // registration page with an error message
            result.rejectValue("email", null, "User already registered !!!");
            return "/sign-up";
        }
        
        // If there are validation errors, return to the registration page with the
        // entered user data
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/sign-up";
        }

        // Find the user role in the database
        Optional<Role> userRole = roleRepository.findById(TbConstants.Roles.USER);

        // Create a set to store user roles
        Set<Role> userRoles = new HashSet<>();

        // If the user role is present in the database, add it to the set
        if (userRole.isPresent() && userRole != null) {
            userRoles.add(userRole.get());
        } else {
            // If the user role is not present, create it and save it to the database
            Role user = new Role();
            user.setId(TbConstants.Roles.USER);
            user.setName("ROLE_USER");

            Role admin = new Role();
            admin.setId(TbConstants.Roles.ADMIN);
            admin.setName("ROLE_ADMIN");

            roleRepository.save(admin);
            roleRepository.save(user);
            
            // Add the newly created roles to the set
            userRoles.add(admin);
            userRoles.add(user);
        }
        
        // Create a new user entity
        User newUser = new User();
        // Set the user roles
        newUser.setRoles(userRoles);
        // Set the user name, email, and encrypted password
        newUser.setUserName(userDto.getName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        System.out.println("\n\nSaving New User :-" + "\nName : " + userDto.getName() + "\nEmail : " + userDto.getEmail() + "\n");
        // Save the new user to the database
        userRepository.save(newUser);
        
        // Redirect to the registration page with a success message
        return "redirect:/sign-up?success";
    }
}
