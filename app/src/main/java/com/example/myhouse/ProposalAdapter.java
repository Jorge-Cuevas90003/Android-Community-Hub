package com.example.myhouse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class ProposalAdapter extends RecyclerView.Adapter<ProposalAdapter.ProposalViewHolder> {

    private final List<Proposal> proposalList;
    private final OnProposalInteractionListener listener;
    private final String houseNumber;

    public ProposalAdapter(List<Proposal> proposalList, OnProposalInteractionListener listener, String houseNumber) {
        this.proposalList = proposalList;
        this.listener = listener;
        this.houseNumber = houseNumber;
    }

    @NonNull
    @Override
    public ProposalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proposal, parent, false);
        return new ProposalViewHolder(view);
    }@Override
    public void onBindViewHolder(@NonNull ProposalViewHolder holder, int position) {
        Proposal proposal = proposalList.get(position);


        holder.tvTitle.setText(proposal.getTitle());
        holder.tvDescription.setText(proposal.getDescription());
        holder.tvStatus.setText(proposal.getStatus());


        holder.tvCreator.setText("Creador: Casa " + proposal.getCreatorHouseNumber());


        String votingStats = "A Favor: " + proposal.getVotes() + " | En Contra: " + proposal.getVotesAgainst();
        holder.tvVotingStats.setText(votingStats);


        long remainingTimeMillis = proposal.getExpirationTimestamp() - new Date().getTime();
        if (remainingTimeMillis > 0) {
            long remainingDays = remainingTimeMillis / (1000 * 60 * 60 * 24);
            long remainingHours = (remainingTimeMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);


            String timeRemainingText = String.format("Tiempo restante: %d días, %d horas",
                    remainingDays, remainingHours);
            holder.tvTimeRemaining.setText(timeRemainingText);
        } else {
            holder.tvTimeRemaining.setText("Tiempo expirado");
        }



        holder.btnApprove.setEnabled(false);
        holder.btnDisapprove.setEnabled(false);


        proposal.canVote(houseNumber, canVote -> {
            if (canVote) {
                holder.btnApprove.setEnabled(true);
                holder.btnDisapprove.setEnabled(true);
            } else {
                holder.btnApprove.setEnabled(false);
                holder.btnDisapprove.setEnabled(false);
            }
        });

        holder.btnApprove.setOnClickListener(v -> listener.onVote(proposal, true));
        holder.btnDisapprove.setOnClickListener(v -> listener.onVote(proposal, false));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(proposal));
    }



    @Override
    public int getItemCount() {
        return proposalList.size();
    }

    public interface OnProposalInteractionListener {
        void onVote(Proposal proposal, boolean isApproved);
        void onDelete(Proposal proposal);
    }

    public static class ProposalViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvStatus, tvCreator, tvVotingStats, tvTimeRemaining;
        Button btnApprove, btnDisapprove, btnDelete;

        public ProposalViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_proposal_title);
            tvDescription = itemView.findViewById(R.id.tv_proposal_description);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCreator = itemView.findViewById(R.id.tv_creator);
            tvVotingStats = itemView.findViewById(R.id.tv_voting_stats);
            tvTimeRemaining = itemView.findViewById(R.id.tv_time_remaining);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnDisapprove = itemView.findViewById(R.id.btn_reject);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

}
