package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.LoginForm;
import com.recicar.marketplace.dto.UserRegistrationDto;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.legal.LegalDocumentMetadata;
import com.recicar.marketplace.security.PasswordPolicy;
import com.recicar.marketplace.security.SessionAuthVersionFilter;
import com.recicar.marketplace.service.UserService;
import com.recicar.marketplace.service.auth.EmailVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final UserDetailsService userDetailsService;

    public AuthController(
            UserService userService,
            EmailVerificationService emailVerificationService,
            UserDetailsService userDetailsService) {
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "session", required = false) String session,
                                @RequestParam(value = "oauth", required = false) String oauth,
                                Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("registrationForm", new UserRegistrationDto());
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos.");
        }
        if ("expired".equals(session)) {
            model.addAttribute("message", "Tu sesión ha caducado. Vuelve a iniciar sesión.");
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión correctamente.");
        }
        if ("oauthemail".equals(oauth) || "oauth".equals(error)) {
            model.addAttribute("error", "No se pudo obtener el correo desde el proveedor OAuth.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new UserRegistrationDto());
        model.addAttribute("legalVersion", LegalDocumentMetadata.VERSION_LABEL);
        model.addAttribute("legalEffectiveDate", LegalDocumentMetadata.EFFECTIVE_DATE);
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationForm") UserRegistrationDto registrationDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("legalVersion", LegalDocumentMetadata.VERSION_LABEL);
            model.addAttribute("legalEffectiveDate", LegalDocumentMetadata.EFFECTIVE_DATE);
            return "auth/register";
        }

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Las contraseñas no coinciden.");
            model.addAttribute("legalVersion", LegalDocumentMetadata.VERSION_LABEL);
            model.addAttribute("legalEffectiveDate", LegalDocumentMetadata.EFFECTIVE_DATE);
            return "auth/register";
        }

        if (!PasswordPolicy.isAcceptable(registrationDto.getPassword())) {
            result.rejectValue("password", "error.passwordpolicy", PasswordPolicy.requirementSummaryEs());
            model.addAttribute("legalVersion", LegalDocumentMetadata.VERSION_LABEL);
            model.addAttribute("legalEffectiveDate", LegalDocumentMetadata.EFFECTIVE_DATE);
            return "auth/register";
        }

        if (userService.existsByEmail(registrationDto.getEmail())) {
            result.reject("error.registration", "No se pudo completar el registro.");
            model.addAttribute("legalVersion", LegalDocumentMetadata.VERSION_LABEL);
            model.addAttribute("legalEffectiveDate", LegalDocumentMetadata.EFFECTIVE_DATE);
            return "auth/register";
        }

        try {
            userService.registerUser(registrationDto);
            redirectAttributes.addFlashAttribute("message",
                    "Cuenta creada. Revisa tu correo para verificar el email antes de iniciar sesión.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("legalVersion", LegalDocumentMetadata.VERSION_LABEL);
            model.addAttribute("legalEffectiveDate", LegalDocumentMetadata.EFFECTIVE_DATE);
            return "auth/register";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo completar el registro. Inténtalo de nuevo.");
            model.addAttribute("legalVersion", LegalDocumentMetadata.VERSION_LABEL);
            model.addAttribute("legalEffectiveDate", LegalDocumentMetadata.EFFECTIVE_DATE);
            return "auth/register";
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = emailVerificationService.verifyAndActivate(token);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El enlace de verificación no es válido o ha caducado.");
            return "redirect:/login";
        }
        User user = userOpt.get();
        try {
            UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    details, null, details.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
            session.setAttribute(SessionAuthVersionFilter.SESSION_AUTH_VERSION_ATTR, user.getAuthVersion());
            redirectAttributes.addFlashAttribute("message", "Correo verificado. Ya has iniciado sesión.");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Correo verificado. Ya puedes iniciar sesión.");
            return "redirect:/login";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                          RedirectAttributes redirectAttributes) {
        try {
            userService.initiatePasswordReset(email);
            redirectAttributes.addFlashAttribute("message",
                    "Si existe una cuenta con ese correo, hemos enviado instrucciones.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ha ocurrido un error. Inténtalo de nuevo.");
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                        @RequestParam("password") String password,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/reset-password?token=" + token;
        }
        if (!PasswordPolicy.isAcceptable(password)) {
            redirectAttributes.addFlashAttribute("error", PasswordPolicy.requirementSummaryEs());
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/reset-password?token=" + token;
        }
        boolean ok = userService.resetPassword(token, password);
        if (ok) {
            redirectAttributes.addFlashAttribute("message", "Contraseña actualizada. Inicia sesión con la nueva clave.");
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("error", "Enlace inválido o caducado.");
        return "redirect:/forgot-password";
    }
}
