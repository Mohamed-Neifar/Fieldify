package com.example.fieldify.data;

import com.example.fieldify.R;
import com.example.fieldify.model.Invitation;
import com.example.fieldify.model.Reservation;
import com.example.fieldify.model.Terrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockDataRepository {

    private static MockDataRepository instance;

    private final List<Terrain> terrains;
    private final List<Reservation> reservations;
    private final List<Invitation> invitations;

    private MockDataRepository() {
        terrains = new ArrayList<>();
        reservations = new ArrayList<>();
        invitations = new ArrayList<>();

        initMockData();
    }

    public static synchronized MockDataRepository getInstance() {
        if (instance == null) {
            instance = new MockDataRepository();
        }
        return instance;
    }

    private void initMockData() {
        List<String> standardEquipments = Arrays.asList("Parking", "Vestiaires", "Éclairage LED", "Café", "Douches", "Location matériel");

        // 1. Complexe Sportif Sfax (Football)
        Terrain t1 = new Terrain(
                "t1",
                "Complexe Sportif Sfax",
                "Football",
                "Route de Teniour, Sfax",
                "Route de Teniour Km 4, Sfax, Tunisie",
                70.0,
                4.8,
                "Terrain en gazon synthétique de dernière génération 5v5 et 7v7, avec éclairage LED haute intensité et vestiaires haut de gamme.",
                standardEquipments,
                Arrays.asList(R.drawable.footbalfieldimg1, R.drawable.footbalfieldimg2, R.drawable.footbalfieldimg3, R.drawable.footbalfieldimg4),
                false
        );

        // 2. Sfax Padel Club (Padel)
        Terrain t2 = new Terrain(
                "t2",
                "Sfax Padel Club",
                "Padel",
                "Route de la Soukra, Sfax",
                "Route de la Soukra Km 3.5, Sfax, Tunisie",
                80.0,
                4.9,
                "Pistes de Padel panoramiques aux normes internationales avec vitre renforcée, gazon bleu haute densité et lounge VIP.",
                standardEquipments,
                Arrays.asList(R.drawable.padelfield),
                false
        );

        // 3. Tennis Club Sfax (Tennis)
        Terrain t3 = new Terrain(
                "t3",
                "Tennis Club Sfax",
                "Tennis",
                "Route d'El Ain, Sfax",
                "Route d'El Ain Km 2, Sfax, Tunisie",
                50.0,
                4.7,
                "Courts de tennis en terre battue et surface rapide, idéal pour l'entraînement professionnel et les matchs amicaux.",
                standardEquipments,
                Arrays.asList(R.drawable.tennisfield),
                false
        );

        // 4. Basketball Arena Sfax (Basket-ball)
        Terrain t4 = new Terrain(
                "t4",
                "Basketball Arena Sfax",
                "Basket-ball",
                "Route de Tunis, Sfax",
                "Route de Tunis Km 5, Sfax, Tunisie",
                60.0,
                4.6,
                "Terrain de Basket couvert et plein air avec parquets amortissants professionnels et paniers réglables FIBA.",
                standardEquipments,
                Arrays.asList(R.drawable.basketballfield),
                false
        );

        // 5. Beach Volley Resort (Volley-ball)
        Terrain t5 = new Terrain(
                "t5",
                "Beach Volley Resort",
                "Volley-ball",
                "Route de Saltania, Sfax",
                "Route de Saltania Km 3, Sfax, Tunisie",
                45.0,
                4.5,
                "Terrains de Beach Volley en sable fin siliceux blanc, douches extérieures et buvette rafraîchissante.",
                standardEquipments,
                Arrays.asList(R.drawable.volleyballfiled),
                false
        );

        // 6. Sfax Golf Academy (Golf)
        Terrain t6 = new Terrain(
                "t6",
                "Sfax Golf Academy",
                "Golf",
                "Route de Mahdia, Sfax",
                "Route de Mahdia Km 6, Sfax, Tunisie",
                120.0,
                4.9,
                "Parcours de Golf d'entraînement 9 trous, Driving Range et Putting Green supervisés par des coachs certifiés.",
                standardEquipments,
                Arrays.asList(R.drawable.golffield),
                false
        );

        // 7. Piscine Privée & Lounge (Piscine privée)
        Terrain t7 = new Terrain(
                "t7",
                "Piscine Privée & Lounge",
                "Piscine privée",
                "Route de Gremda, Sfax",
                "Route de Gremda Km 4, Sfax, Tunisie",
                150.0,
                5.0,
                "Espace aquatique privé avec piscine chauffée, transats, système sonore Bluetooth et service restauration privée.",
                standardEquipments,
                Arrays.asList(R.drawable.img_welcome_sports),
                false
        );

        terrains.add(t1);
        terrains.add(t2);
        terrains.add(t3);
        terrains.add(t4);
        terrains.add(t5);
        terrains.add(t6);
        terrains.add(t7);
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public void addTerrain(Terrain terrain) {
        if (terrain != null) {
            terrains.add(0, terrain);
        }
    }

    public Terrain getTerrainById(String id) {
        for (Terrain t : terrains) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return terrains.isEmpty() ? null : terrains.get(0);
    }

    public List<Terrain> getFavoriteTerrains() {
        List<Terrain> favorites = new ArrayList<>();
        for (Terrain t : terrains) {
            if (t.isFavorite()) {
                favorites.add(t);
            }
        }
        return favorites;
    }

    public void toggleFavorite(String terrainId) {
        for (Terrain t : terrains) {
            if (t.getId().equals(terrainId)) {
                t.setFavorite(!t.isFavorite());
                break;
            }
        }
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(0, reservation);
    }

    public void updateReservationStatus(String reservationId, String newStatus) {
        for (Reservation r : reservations) {
            if (r.getId().equals(reservationId)) {
                r.setStatus(newStatus);
                break;
            }
        }
    }

    public void addInvitedFriendToReservation(String reservationId, String friendName) {
        for (Reservation r : reservations) {
            if (r.getId().equals(reservationId)) {
                r.addInvitedFriend(friendName);
                break;
            }
        }
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public int getPendingInvitationsCount() {
        int count = 0;
        for (Invitation inv : invitations) {
            if ("En attente".equalsIgnoreCase(inv.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public void acceptInvitation(String invitationId) {
        for (Invitation inv : invitations) {
            if (inv.getId().equals(invitationId)) {
                inv.setStatus("Acceptée");
                reservations.add(0, new Reservation(
                        "r_" + System.currentTimeMillis(),
                        inv.getTerrain(),
                        inv.getDate(),
                        inv.getTimeSlot(),
                        "Confirmé",
                        new ArrayList<>(Arrays.asList(inv.getSenderName()))
                ));
                break;
            }
        }
    }

    public void refuseInvitation(String invitationId) {
        for (Invitation inv : invitations) {
            if (inv.getId().equals(invitationId)) {
                inv.setStatus("Refusée");
                break;
            }
        }
    }

    public List<Terrain> filterTerrains(String category, String searchQuery, double maxPrice, String zone) {
        List<Terrain> filtered = new ArrayList<>();
        for (Terrain t : terrains) {
            boolean matchesCategory = "Tous".equalsIgnoreCase(category) || category == null || category.isEmpty() || t.getCategory().equalsIgnoreCase(category);
            boolean matchesQuery = searchQuery == null || searchQuery.trim().isEmpty() || t.getName().toLowerCase().contains(searchQuery.toLowerCase()) || t.getZone().toLowerCase().contains(searchQuery.toLowerCase());
            boolean matchesPrice = maxPrice <= 0 || t.getPricePerHour() <= maxPrice;
            boolean matchesZone = zone == null || zone.isEmpty() || "Toutes".equalsIgnoreCase(zone) || t.getZone().toLowerCase().contains(zone.toLowerCase());

            if (matchesCategory && matchesQuery && matchesPrice && matchesZone) {
                filtered.add(t);
            }
        }
        return filtered;
    }
}