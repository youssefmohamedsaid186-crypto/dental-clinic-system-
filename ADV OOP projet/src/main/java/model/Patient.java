package model;

import java.time.LocalDate;

public class Patient {
    private int id;
    private Integer userId; // Nullable if patient doesn't have an online portal user account
    private String name;
    private String phone;
    private String email;
    private LocalDate dob;
    private String gender;
    private String address;

    public Patient() {
    }

    public Patient(int id, Integer userId, String name, String phone, String email, LocalDate dob, String gender, String address) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
    }

    public Patient(String name, String phone, String email, LocalDate dob, String gender, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
