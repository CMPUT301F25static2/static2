package com.ualberta.eventlottery.model;

public class Entrant extends User{

    public Entrant() {
    }

    public Entrant(String userId, String name, String email) {
        super(userId, name, email);
    }

    public Entrant(String userId, String name, String email, String phone) {
        super(userId, name, email, phone);
    }

    public Entrant(String userId, String name, String email, String phone, String favRecCenter) {
        super(userId, name, email, phone, favRecCenter);
    }
}
