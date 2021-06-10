package com.exitoms.app.models;

public class OrderDetails {

    private String ID;
    private String Amount;
    private String Date;
    private String Customer;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }


    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String Customer) {
        this.Customer = Customer;
    }


    public String getAmount() {
        return Amount;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }


}