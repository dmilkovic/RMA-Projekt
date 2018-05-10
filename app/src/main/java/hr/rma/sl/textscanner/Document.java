package hr.rma.sl.textscanner;

import android.util.Log;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
        import java.util.Map;

public class Document {
    private String id;
    private String name;
    private String surname;
    private String address;
    private String documentNumber;
    private String validUntil;
    private String gender;
    private String birthday;
    private String dateOfIssue;
    private String state;
    private String oib;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public String getoib() {
        return oib;
    }

    public void setoib(String oib) {
        this.oib = oib;
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ID="+getId()+"\n");
        sb.append("Name="+getName()+"\n");
        sb.append("Address="+getAddress()+"\n");
        sb.append("Birthday="+getBirthday()+"\n");
        sb.append("Document number="+getDocumentNumber()+"\n");
        sb.append("OIB="+getoib()+"\n");
        return sb.toString();
    }
}