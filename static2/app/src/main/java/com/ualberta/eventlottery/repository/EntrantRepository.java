package com.ualberta.eventlottery.repository;

import com.ualberta.eventlottery.model.Entrant;

import java.util.ArrayList;
import java.util.List;

public class EntrantRepository {
    private static EntrantRepository instance;
    private List<Entrant> entrantCache;

    private EntrantRepository() {
        entrantCache = new ArrayList<>();
        initializeSampleData();
    }

    public static synchronized EntrantRepository getInstance() {
        if (instance == null) {
            instance = new EntrantRepository();
        }
        return instance;
    }

    private void initializeSampleData() {
        Entrant entrant1 = new Entrant("entrant1", "John Doe", "john.doe@ualberta.ca", "780-111-1111");
        Entrant entrant2 = new Entrant("entrant2", "Jane Smith", "jane.smith@ualberta.ca", "780-111-1112");
        Entrant entrant3 = new Entrant("entrant3", "Bob Johnson", "bob.johnson@ualberta.ca", "780-111-1113");
        Entrant entrant4 = new Entrant("entrant4", "Alice Brown", "alice.brown@ualberta.ca", "780-111-1114");
        Entrant entrant5 = new Entrant("entrant5", "Charlie Wilson", "charlie.wilson@ualberta.ca", "780-111-1115");
        Entrant entrant6 = new Entrant("entrant6", "Diana Lee", "diana.lee@ualberta.ca", "780-111-1116");
        Entrant entrant7 = new Entrant("entrant7", "Edward Zhang", "edward.zhang@ualberta.ca", "780-111-1117");
        Entrant entrant8 = new Entrant("entrant8", "Fiona Chen", "fiona.chen@ualberta.ca", "780-111-1118");
        Entrant entrant9 = new Entrant("entrant9", "George Kumar", "george.kumar@ualberta.ca", "780-111-1119");
        Entrant entrant10 = new Entrant("entrant10", "Helen Park", "helen.park@ualberta.ca", "780-111-1120");
        Entrant entrant11 = new Entrant("entrant11", "Ivan Rodriguez", "ivan.rodriguez@ualberta.ca", "780-111-1121");
        Entrant entrant12 = new Entrant("entrant12", "Julia Kim", "julia.kim@ualberta.ca", "780-111-1122");
        Entrant entrant13 = new Entrant("entrant13", "Kevin Wang", "kevin.wang@ualberta.ca", "780-111-1123");
        Entrant entrant14 = new Entrant("entrant14", "Lisa Taylor", "lisa.taylor@ualberta.ca", "780-111-1124");
        Entrant entrant15 = new Entrant("entrant15", "Mike Davis", "mike.davis@ualberta.ca", "780-111-1125");

        entrantCache.add(entrant1);
        entrantCache.add(entrant2);
        entrantCache.add(entrant3);
        entrantCache.add(entrant4);
        entrantCache.add(entrant5);
        entrantCache.add(entrant6);
        entrantCache.add(entrant7);
        entrantCache.add(entrant8);
        entrantCache.add(entrant9);
        entrantCache.add(entrant10);
        entrantCache.add(entrant11);
        entrantCache.add(entrant12);
        entrantCache.add(entrant13);
        entrantCache.add(entrant14);
        entrantCache.add(entrant15);
    }

    public Entrant findEntrantById(String entrantId) {
        for (Entrant entrant : entrantCache) {
            if (entrant.getUserId().equals(entrantId)) {
                return entrant;
            }
        }
        return null;
    }


    public List<Entrant> getAllEntrants() {
        return new ArrayList<>(entrantCache);
    }

    public void addEntrant(Entrant entrant) {
        entrantCache.add(entrant);
    }

    public boolean updateEntrant(Entrant updatedEntrant) {
        for (int i = 0; i < entrantCache.size(); i++) {
            if (entrantCache.get(i).getUserId().equals(updatedEntrant.getUserId())) {
                entrantCache.set(i, updatedEntrant);
                return true;
            }
        }
        return false;
    }

    public boolean deleteEntrant(String entrantId) {
        for (int i = 0; i < entrantCache.size(); i++) {
            if (entrantCache.get(i).getUserId().equals(entrantId)) {
                entrantCache.remove(i);
                return true;
            }
        }
        return false;
    }
}