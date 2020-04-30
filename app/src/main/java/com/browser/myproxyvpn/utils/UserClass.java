package com.browser.myproxyvpn.utils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserClass {

    @SerializedName("id")
    private String id;
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("companyname")
    private String companyname;
    @SerializedName("email")
    private String email;
    @SerializedName("address1")
    private String address1;
    @SerializedName("address2")
    private String address2;
    @SerializedName("city")
    private String city;
    @SerializedName("state")
    private String state;
    @SerializedName("postcode")
    private String postcode;
    @SerializedName("country")
    private String country;
    @SerializedName("phonenumber")
    private String phonenumber;
    @SerializedName("password")
    private String password;
    @SerializedName("authmodule")
    private String authmodule;
    @SerializedName("authdata")
    private String authdata;
    @SerializedName("currency")
    private String currency;
    @SerializedName("defaultgateway")
    private String defaultgateway;
    @SerializedName("credit")
    private String credit;
    @SerializedName("taxexempt")
    private String taxexempt;
    @SerializedName("latefeeoveride")
    private String latefeeoveride;
    @SerializedName("overideduenotices")
    private String overideduenotices;
    @SerializedName("separateinvoices")
    private String separateinvoices;
    @SerializedName("disableautocc")
    private String disableautocc;
    @SerializedName("datecreated")
    private String datecreated;
    @SerializedName("notes")
    private String notes;
    @SerializedName("billingcid")
    private String billingcid;
    @SerializedName("securityqid")
    private String securityqid;
    @SerializedName("securityqans")
    private String securityqans;
    @SerializedName("groupid")
    private String groupid;
    @SerializedName("cardtype")
    private String cardtype;
    @SerializedName("cardlastfour")
    private String cardlastfour;
    @SerializedName("cardnum")
    private String cardnum;
    @SerializedName("startdate")
    private String startdate;
    @SerializedName("expdate")
    private String expdate;
    @SerializedName("issuenumber")
    private String issuenumber;
    @SerializedName("bankname")
    private String bankname;
    @SerializedName("banktype")
    private String banktype;
    @SerializedName("bankcode")
    private String bankcode;
    @SerializedName("bankacct")
    private String bankacct;
    @SerializedName("gatewayid")
    private String gatewayid;
    @SerializedName("lastlogin")
    private String lastlogin;
    @SerializedName("ip")
    private String ip;
    @SerializedName("host")
    private String host;
    @SerializedName("status")
    private String status;
    @SerializedName("language")
    private String language;
    @SerializedName("pwresetkey")
    private String pwresetkey;
    @SerializedName("emailoptout")
    private String emailoptout;
    @SerializedName("marketing_emails_opt_in")
    private String marketing_emails_opt_in;
    @SerializedName("overrideautoclose")
    private String overrideautoclose;
    @SerializedName("allow_sso")
    private String allow_sso;
    @SerializedName("email_verified")
    private String email_verified;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("pwresetexpiry")
    private String pwresetexpiry;
    @SerializedName("subscribed_package_detail")
    private SubscriptionClass subscribed_package_detail;

    public String getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getCompanyname() {
        return companyname;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCountry() {
        return country;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthmodule() {
        return authmodule;
    }

    public String getAuthdata() {
        return authdata;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDefaultgateway() {
        return defaultgateway;
    }

    public String getCredit() {
        return credit;
    }

    public String getTaxexempt() {
        return taxexempt;
    }

    public String getLatefeeoveride() {
        return latefeeoveride;
    }

    public String getOverideduenotices() {
        return overideduenotices;
    }

    public String getSeparateinvoices() {
        return separateinvoices;
    }

    public String getDisableautocc() {
        return disableautocc;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public String getNotes() {
        return notes;
    }

    public String getBillingcid() {
        return billingcid;
    }

    public String getSecurityqid() {
        return securityqid;
    }

    public String getSecurityqans() {
        return securityqans;
    }

    public String getGroupid() {
        return groupid;
    }

    public String getCardtype() {
        return cardtype;
    }

    public String getCardlastfour() {
        return cardlastfour;
    }

    public String getCardnum() {
        return cardnum;
    }

    public String getStartdate() {
        return startdate;
    }

    public String getExpdate() {
        return expdate;
    }

    public String getIssuenumber() {
        return issuenumber;
    }

    public String getBankname() {
        return bankname;
    }

    public String getBanktype() {
        return banktype;
    }

    public String getBankcode() {
        return bankcode;
    }

    public String getBankacct() {
        return bankacct;
    }

    public String getGatewayid() {
        return gatewayid;
    }

    public String getLastlogin() {
        return lastlogin;
    }

    public String getIp() {
        return ip;
    }

    public String getHost() {
        return host;
    }

    public String getStatus() {
        return status;
    }

    public String getLanguage() {
        return language;
    }

    public String getPwresetkey() {
        return pwresetkey;
    }

    public String getEmailoptout() {
        return emailoptout;
    }

    public String getMarketing_emails_opt_in() {
        return marketing_emails_opt_in;
    }

    public String getOverrideautoclose() {
        return overrideautoclose;
    }

    public String getAllow_sso() {
        return allow_sso;
    }

    public String getEmail_verified() {
        return email_verified;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getPwresetexpiry() {
        return pwresetexpiry;
    }

    public SubscriptionClass getSubscribed_package_detail() {
        return subscribed_package_detail;
    }
}
