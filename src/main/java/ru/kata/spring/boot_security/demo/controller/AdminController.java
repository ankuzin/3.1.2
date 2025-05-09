package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String showAdminPanel(Principal principal, Model model) {
        User user = userService.findByUserName(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("roles", user.getRoles());


        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("newUser", new User());
            model.addAttribute("allRoles", roleRepository.findAll());
        }

        return "adminPanel";
    }

    @GetMapping("/new")
    public String newUserForm(Model model, Principal principal) {
        model.addAttribute("user", userService.findByUserName(principal.getName()));
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "addUser";
    }

    @PostMapping("/add")
    public String addUser(
            @RequestParam("username") String username,
            @RequestParam("address") String address,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("roles") List<String> roles
    ) {
        userService.createUser(username, address, email, password, Set.copyOf(roles));
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("roles", user.getRoles());

        model.addAttribute("editUser", userService.getUserById(id));
        model.addAttribute("allRoles", roleRepository.findAll());
        return "editUser";
    }

    @PostMapping("/update")
    public String updateUser(
            @RequestParam("id") Long id,
            @RequestParam("username") String username,
            @RequestParam("address") String address,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("roles") List<String> roles
    ) {
        userService.updateUser(id, username, address, email, password, Set.copyOf(roles));
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }


}



