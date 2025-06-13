package com.example.myhouse;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class Votos extends AppCompatActivity implements AddProposalDialog.OnProposalAddedListener {

    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton fabAddProposal;
    private DatabaseReference databaseReference;
    private String houseNumber;
    private ProposalAdapter adapter;
    private List<Proposal> proposalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votos);


        houseNumber = getIntent().getStringExtra("HOUSE_NUMBER");
        proposalList = new ArrayList<>();

        initializeViews();
        setupFirebase();
        setupRecyclerView();
        setupFab();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAddProposal = findViewById(R.id.fabAddProposal);
    }

    private void setupFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("proposals");
        loadProposals();
    }

    private void setupRecyclerView() {
        adapter = new ProposalAdapter(proposalList, new ProposalAdapter.OnProposalInteractionListener() {
            @Override
            public void onVote(Proposal proposal, boolean isApproved) {
                handleVote(proposal, isApproved);
            }

            @Override
            public void onDelete(Proposal proposal) {
                deleteProposal(proposal);
            }
        }, houseNumber);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        fabAddProposal.setOnClickListener(v -> {
            AddProposalDialog dialog = new AddProposalDialog();
            dialog.show(getSupportFragmentManager(), "AddProposalDialog");
        });
    }

    private void loadProposals() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                proposalList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Proposal proposal = dataSnapshot.getValue(Proposal.class);
                    if (proposal != null) {
                        proposal.setId(dataSnapshot.getKey());
                        proposalList.add(proposal);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Votos.this,
                        "Error al cargar propuestas: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleVote(Proposal proposal, boolean isApproved) {
        if (proposal.getId() != null) {
            DatabaseReference proposalRef = databaseReference.child(proposal.getId());


            proposalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Proposal currentProposal = dataSnapshot.getValue(Proposal.class);
                    if (currentProposal == null) return;


                    currentProposal.canVote(houseNumber, canVote -> {
                        if (!canVote) {
                            Toast.makeText(Votos.this,
                                    "Ya has votado en esta propuesta o la votación ha expirado",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            currentProposal.addVote(houseNumber, isApproved);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Votos.this,
                            "Error al consultar propuesta: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void deleteProposal(Proposal proposal) {
        if (proposal.getId() != null) {

            if (!proposal.canDelete(houseNumber)) {
                Toast.makeText(Votos.this,
                        "Solo el creador puede eliminar esta propuesta",
                        Toast.LENGTH_SHORT).show();
                return;
            }


            databaseReference.child(proposal.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(Votos.this,
                            "Propuesta eliminada", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Votos.this,
                            "Error al eliminar: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
        }
    }


    @Override
    public void onProposalAdded(String title, String description, long duration) {
        String proposalId = databaseReference.push().getKey();
        if (proposalId != null) {

            Proposal newProposal = new Proposal(
                    proposalId,
                    title,
                    description,
                    houseNumber,
                    1,
                    duration
            );

            databaseReference.child(proposalId).setValue(newProposal)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this,
                            "Propuesta añadida", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this,
                            "Error al añadir propuesta: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
        }
    }
}
