package com.recicar.marketplace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Public company & legal branding (footer, about page, APIs).
 * Override via <code>app.company.*</code> in environment-specific config.
 */
@ConfigurationProperties(prefix = "app.company")
public class CompanyInfoProperties {

    private String displayName = "ReciCar";
    private String legalName = "recicar.es";
    private String copyrightYears = "2014-2026";
    private String tagline = "Marketplace de recambio de desguace y confianza entre particulares y profesionales.";
    private String address = "España (sede comercial a efectos informativos; consulte factura o aviso legal).";
    private String phone = "";
    private Social social = new Social();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getCopyrightYears() {
        return copyrightYears;
    }

    public void setCopyrightYears(String copyrightYears) {
        this.copyrightYears = copyrightYears;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Social getSocial() {
        return social;
    }

    public void setSocial(Social social) {
        this.social = social;
    }

    /**
     * One line for the footer, e.g. 2014-2026 © recicar.es
     */
    public String getCopyrightLine() {
        return copyrightYears + " © " + legalName;
    }

    public static class Social {
        private String facebook = "";
        private String youtube = "";
        private String instagram = "";

        public String getFacebook() {
            return facebook;
        }

        public void setFacebook(String facebook) {
            this.facebook = facebook;
        }

        public String getYoutube() {
            return youtube;
        }

        public void setYoutube(String youtube) {
            this.youtube = youtube;
        }

        public String getInstagram() {
            return instagram;
        }

        public void setInstagram(String instagram) {
            this.instagram = instagram;
        }
    }
}
