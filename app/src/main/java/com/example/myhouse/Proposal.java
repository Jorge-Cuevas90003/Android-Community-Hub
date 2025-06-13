package com.example.myhouse;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Proposal {
    private String id;
    private String title;
    private String description;
    private String creatorHouseNumber;
    private int votes;
    private int votesAgainst;
    private String status;
    private long creationTimestamp;
    private long expirationTimestamp;
    private int requiredVotesThreshold;

    private transient DatabaseReference databaseReference;


    public Proposal() {
        creationTimestamp = new Date().getTime();
        status = "Pendiente";
        requiredVotesThreshold = 3;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    public Proposal(String id, String title, String description, String creatorHouseNumber,
                    int requiredVotesThreshold, long expirationDuration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creatorHouseNumber = creatorHouseNumber;
        this.votes = 0;
        this.votesAgainst = 0;
        this.creationTimestamp = new Date().getTime();
        this.status = "Pendiente";
        this.requiredVotesThreshold = requiredVotesThreshold;
        this.expirationTimestamp = creationTimestamp + expirationDuration;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    public void canVote(String houseNumber, OnVoteCheckListener listener) {
        databaseReference.child("proposals").child(id).child("votedHouses").child(houseNumber)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        listener.onVoteCheck(false);
                    } else {
                        listener.onVoteCheck(true);
                    }
                });
    }



    public void addVote(String houseNumber, boolean isApproved) {

        canVote(houseNumber, canVote -> {
            if (!canVote) {
                System.out.println("La casa ya votó o la votación expiró.");
                return;
            }


            Map<String, Object> updates = new HashMap<>();
            updates.put("votedHouses/" + houseNumber, isApproved ? "approved" : "rejected");


            if (isApproved) {
                votes++;
                updates.put("votes", votes);
            } else {
                votesAgainst++;
                updates.put("votesAgainst", votesAgainst);
            }


            databaseReference.child("proposals").child(id).updateChildren(updates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("Voto registrado correctamente.");
                            checkVotingStatus();
                        } else {
                            System.err.println("Error al registrar el voto: " + task.getException());
                        }
                    });
        });
    }



    private void checkVotingStatus() {
        long currentTime = new Date().getTime();

        if (currentTime > expirationTimestamp) {
            status = "Expirado";
        } else if (votes >= requiredVotesThreshold) {
            status = "Aprobado";
        } else if (votesAgainst >= requiredVotesThreshold) {
            status = "Rechazado";
        }


        databaseReference.child("proposals").child(id).child("status").setValue(status);
    }


    public interface OnVoteCheckListener {
        void onVoteCheck(boolean canVote);
    }

    public boolean canDelete(String houseNumber) {
        return houseNumber.equals(creatorHouseNumber);
    }

    public String getCreatorHouseNumber() {
        return creatorHouseNumber;
    }

    public void setCreatorHouseNumber(String creatorHouseNumber) {
        this.creatorHouseNumber = creatorHouseNumber;
    }

    public int getVotesAgainst() {
        return votesAgainst;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public int getVotes() {
        return votes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setExpirationTimestamp(long expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }
}
