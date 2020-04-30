package com.browser.myproxyvpn.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscriptionClass implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("userid")
    private String userid;
    @SerializedName("orderid")
    private String orderid;
    @SerializedName("packageid")
    private String packageid;
    @SerializedName("server")
    private String server;
    @SerializedName("regdate")
    private String regdate;
    @SerializedName("domain")
    private String domain;
    @SerializedName("paymentmethod")
    private String paymentmethod;
    @SerializedName("firstpaymentamount")
    private String firstpaymentamount;
    @SerializedName("amount")
    private String amount;
    @SerializedName("billingcycle")
    private String billingcycle;
    @SerializedName("nextduedate")
    private String nextduedate;
    @SerializedName("nextinvoicedate")
    private String nextinvoicedate;
    @SerializedName("termination_date")
    private String termination_date;
    @SerializedName("completed_date")
    private String completed_date;
    @SerializedName("domainstatus")
    private String domainstatus;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("notes")
    private String notes;
    @SerializedName("subscriptionid")
    private String subscriptionid;
    @SerializedName("promoid")
    private String promoid;
    @SerializedName("suspendreason")
    private String suspendreason;
    @SerializedName("overideautosuspend")
    private String overideautosuspend;
    @SerializedName("overidesuspenduntil")
    private String overidesuspenduntil;
    @SerializedName("dedicatedip")
    private String dedicatedip;
    @SerializedName("assignedips")
    private String assignedips;
    @SerializedName("ns1")
    private String ns1;
    @SerializedName("ns2")
    private String ns2;
    @SerializedName("diskusage")
    private String diskusage;
    @SerializedName("disklimit")
    private String disklimit;
    @SerializedName("bwusage")
    private String bwusage;
    @SerializedName("bwlimit")
    private String bwlimit;
    @SerializedName("lastupdate")
    private String lastupdate;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("updated_at")
    private String updated_at;

    public String getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public String getOrderid() {
        return orderid;
    }

    public String getPackageid() {
        return packageid;
    }

    public String getServer() {
        return server;
    }

    public String getRegdate() {
        return regdate;
    }

    public String getDomain() {
        return domain;
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public String getFirstpaymentamount() {
        return firstpaymentamount;
    }

    public String getAmount() {
        return amount;
    }

    public String getBillingcycle() {
        return billingcycle;
    }

    public String getNextduedate() {
        return nextduedate;
    }

    public String getNextinvoicedate() {
        return nextinvoicedate;
    }

    public String getTermination_date() {
        return termination_date;
    }

    public String getCompleted_date() {
        return completed_date;
    }

    public String getDomainstatus() {
        return domainstatus;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNotes() {
        return notes;
    }

    public String getSubscriptionid() {
        return subscriptionid;
    }

    public String getPromoid() {
        return promoid;
    }

    public String getSuspendreason() {
        return suspendreason;
    }

    public String getOverideautosuspend() {
        return overideautosuspend;
    }

    public String getOveridesuspenduntil() {
        return overidesuspenduntil;
    }

    public String getDedicatedip() {
        return dedicatedip;
    }

    public String getAssignedips() {
        return assignedips;
    }

    public String getNs1() {
        return ns1;
    }

    public String getNs2() {
        return ns2;
    }

    public String getDiskusage() {
        return diskusage;
    }

    public String getDisklimit() {
        return disklimit;
    }

    public String getBwusage() {
        return bwusage;
    }

    public String getBwlimit() {
        return bwlimit;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
