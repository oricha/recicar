package com.recicar.marketplace.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    private boolean agreeToTerms;

    private boolean agreePrivacyPolicy;

    /** Optional Cookie / GDPR consent (informational checkbox). */
    private boolean agreeCookiesPolicy;

    private boolean registeringAsVendor;

    @Size(max = 255)
    private String vendorBusinessName;

    @Size(max = 50)
    private String vendorTaxId;

    /** Google reCAPTCHA v3 / hCaptcha response — validated when app.auth.captcha.secret is set. */
    private String captchaToken;

    public UserRegistrationDto() {}

    public UserRegistrationDto(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @AssertTrue(message = "You must accept the terms and conditions")
    public boolean isTermsAcceptedForSubmission() {
        return agreeToTerms;
    }

    @AssertTrue(message = "You must accept the privacy policy")
    public boolean isPrivacyAcceptedForSubmission() {
        return agreePrivacyPolicy;
    }

    @AssertTrue(message = "Complete business name and tax id for vendor registration")
    public boolean isVendorSectionComplete() {
        if (!registeringAsVendor) {
            return true;
        }
        return vendorBusinessName != null && !vendorBusinessName.isBlank()
                && vendorTaxId != null && !vendorTaxId.isBlank();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAgreeToTerms() {
        return agreeToTerms;
    }

    public void setAgreeToTerms(boolean agreeToTerms) {
        this.agreeToTerms = agreeToTerms;
    }

    public boolean isAgreePrivacyPolicy() {
        return agreePrivacyPolicy;
    }

    public void setAgreePrivacyPolicy(boolean agreePrivacyPolicy) {
        this.agreePrivacyPolicy = agreePrivacyPolicy;
    }

    public boolean isAgreeCookiesPolicy() {
        return agreeCookiesPolicy;
    }

    public void setAgreeCookiesPolicy(boolean agreeCookiesPolicy) {
        this.agreeCookiesPolicy = agreeCookiesPolicy;
    }

    public boolean isRegisteringAsVendor() {
        return registeringAsVendor;
    }

    public void setRegisteringAsVendor(boolean registeringAsVendor) {
        this.registeringAsVendor = registeringAsVendor;
    }

    public String getVendorBusinessName() {
        return vendorBusinessName;
    }

    public void setVendorBusinessName(String vendorBusinessName) {
        this.vendorBusinessName = vendorBusinessName;
    }

    public String getVendorTaxId() {
        return vendorTaxId;
    }

    public void setVendorTaxId(String vendorTaxId) {
        this.vendorTaxId = vendorTaxId;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
