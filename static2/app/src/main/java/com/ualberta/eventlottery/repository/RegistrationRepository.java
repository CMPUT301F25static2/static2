package com.ualberta.eventlottery.repository;

import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrationRepository {
    private static RegistrationRepository instance;
    private List<Registration> registrationCache;

    private RegistrationRepository() {
        registrationCache = new ArrayList<>();
        initializeSampleData();
    }

    public static synchronized RegistrationRepository getInstance() {
        if (instance == null) {
            instance = new RegistrationRepository();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Sample registrations for testing
        Registration reg1 = new Registration("reg1", "1", "entrant1");
        reg1.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg1.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 3)); // 3天前


        Registration reg2 = new Registration("reg2", "1", "entrant2");
        reg2.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg2.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 2)); // 2天前

        Registration reg3 = new Registration("reg3", "1", "entrant3");
        reg3.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg3.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 1)); // 1天前

        Registration reg4 = new Registration("reg4", "1", "entrant4");
        reg4.setStatus(EntrantRegistrationStatus.WAITING);
        reg4.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 4)); // 4天前

        Registration reg5 = new Registration("reg5", "1", "entrant5");
        reg5.setStatus(EntrantRegistrationStatus.WAITING);
        reg5.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 3)); // 3天前

        Registration reg6 = new Registration("reg6", "1", "entrant6");
        reg6.setStatus(EntrantRegistrationStatus.WAITING);
        reg6.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 2)); // 2天前

        Registration reg7 = new Registration("reg7", "1", "entrant7");
        reg7.setStatus(EntrantRegistrationStatus.SELECTED);
        reg7.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 5)); // 5天前

        Registration reg8 = new Registration("reg8", "1", "entrant8");
        reg8.setStatus(EntrantRegistrationStatus.SELECTED);
        reg8.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 4)); // 4天前

        Registration reg9 = new Registration("reg9", "1", "entrant9");
        reg9.setStatus(EntrantRegistrationStatus.DECLINED);
        reg9.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 6)); // 6天前
        reg9.setRespondedAt(new Date(System.currentTimeMillis() - 86400000 * 5)); // 5天前

        Registration reg10 = new Registration("reg10", "1", "entrant10");
        reg10.setStatus(EntrantRegistrationStatus.DECLINED);
        reg10.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 5)); // 5天前
        reg10.setRespondedAt(new Date(System.currentTimeMillis() - 86400000 * 4)); // 4天前

        Registration reg11 = new Registration("reg11", "2", "entrant1");
        reg11.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg11.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 7)); // 7天前

        Registration reg12 = new Registration("reg12", "2", "entrant2");
        reg12.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg12.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 6)); // 6天前

        Registration reg13 = new Registration("reg13", "2", "entrant3");
        reg13.setStatus(EntrantRegistrationStatus.WAITING);
        reg13.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 5)); // 5天前

        Registration reg14 = new Registration("reg14", "2", "entrant4");
        reg14.setStatus(EntrantRegistrationStatus.WAITING);
        reg14.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 4)); // 4天前

        Registration reg15 = new Registration("reg15", "2", "entrant5");
        reg15.setStatus(EntrantRegistrationStatus.SELECTED);
        reg15.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 3)); // 3天前

        Registration reg16 = new Registration("reg16", "3", "entrant6");
        reg16.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg16.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 10)); // 10天前

        Registration reg17 = new Registration("reg17", "3", "entrant7");
        reg17.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg17.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 9)); // 9天前

        Registration reg18 = new Registration("reg18", "3", "entrant8");
        reg18.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg18.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 8)); // 8天前

        // 添加所有注册记录到缓存
        registrationCache.add(reg1);
        registrationCache.add(reg2);
        registrationCache.add(reg3);
        registrationCache.add(reg4);
        registrationCache.add(reg5);
        registrationCache.add(reg6);
        registrationCache.add(reg7);
        registrationCache.add(reg8);
        registrationCache.add(reg9);
        registrationCache.add(reg10);
        registrationCache.add(reg11);
        registrationCache.add(reg12);
        registrationCache.add(reg13);
        registrationCache.add(reg14);
        registrationCache.add(reg15);
        registrationCache.add(reg16);
        registrationCache.add(reg17);
        registrationCache.add(reg18);
    }

    public Registration findRegistrationById(String registrationId) {
        for (Registration reg : registrationCache) {
            if (reg.getId().equals(registrationId)) {
                return reg;
            }
        }
        return null;
    }

    public Registration findRegistrationByEventAndUser(String eventId, String userId) {
        for (Registration reg : registrationCache) {
            if (reg.getEventId().equals(eventId) && reg.getEntrantId().equals(userId)) {
                return reg;
            }
        }
        return null;
    }

    public List<Registration> getRegistrationsByEvent(String eventId) {
        List<Registration> result = new ArrayList<>();
        for (Registration reg : registrationCache) {
            if (reg.getEventId().equals(eventId)) {
                result.add(reg);
            }
        }
        return result;
    }

    public List<Registration> getRegistrationsByEntrant(String entrantId) {
        List<Registration> result = new ArrayList<>();
        for (Registration reg : registrationCache) {
            if (reg.getEntrantId().equals(entrantId)) {
                result.add(reg);
            }
        }
        return result;
    }

    public List<Registration> getRegistrationsByStatus(String eventId, EntrantRegistrationStatus status) {
        List<Registration> result = new ArrayList<>();
        for (Registration reg : registrationCache) {
            if (reg.getEventId().equals(eventId) && reg.getStatus() == status) {
                result.add(reg);
            }
        }
        return result;
    }

    public void addRegistration(Registration registration) {
        registrationCache.add(registration);
    }

    public boolean updateRegistration(Registration updatedRegistration) {
        for (int i = 0; i < registrationCache.size(); i++) {
            if (registrationCache.get(i).getId().equals(updatedRegistration.getId())) {
                registrationCache.set(i, updatedRegistration);
                return true;
            }
        }
        return false;
    }

    public boolean deleteRegistration(String registrationId) {
        for (int i = 0; i < registrationCache.size(); i++) {
            if (registrationCache.get(i).getId().equals(registrationId)) {
                registrationCache.remove(i);
                return true;
            }
        }
        return false;
    }

    public int getRegistrationCountByStatus(String eventId, EntrantRegistrationStatus status) {
        int count = 0;
        for (Registration reg : registrationCache) {
            if (reg.getEventId().equals(eventId) && reg.getStatus() == status) {
                count++;
            }
        }
        return count;
    }
}