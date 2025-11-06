package com.ualberta.eventlottery.model;

public class Entrant extends User{

    public Entrant() {
    }

    public Entrant(String userId, String name, String email, String fcmToken) {
        super(userId, name, email, fcmToken);
    }

    public Entrant(String userId, String name, String email, String phone,String fcmToken) {
        super(userId, name, email, phone,fcmToken);
    }

    public Entrant(String userId, String name, String email, String phone, String favRecCenter,String fcmToken) {
        super(userId, name, email, phone, favRecCenter,fcmToken);
    }
}
