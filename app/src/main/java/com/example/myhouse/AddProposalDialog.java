package com.example.myhouse;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.TimeUnit;

public class AddProposalDialog extends DialogFragment {

    public interface OnProposalAddedListener {
        void onProposalAdded(String title, String description, long duration);
    }

    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private AutoCompleteTextView durationAutoComplete;
    private OnProposalAddedListener listener;
    private long selectedDuration;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_proposal, null);

        titleEditText = view.findViewById(R.id.proposalTitle);
        descriptionEditText = view.findViewById(R.id.proposalDescription);
        durationAutoComplete = view.findViewById(R.id.durationSpinner);

        setupDurationAutoComplete();

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> submitProposal());

        builder.setView(view)
                .setTitle("Nueva Propuesta");

        return builder.create();
    }

    private void setupDurationAutoComplete() {
        String[] durations = {
                "1 día",
                "3 días",
                "1 semana",
                "2 semanas",
                "1 mes"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                durations
        );
        durationAutoComplete.setAdapter(adapter);

        durationAutoComplete.setText(durations[0], false);
        selectedDuration = TimeUnit.DAYS.toMillis(1);

        durationAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            switch(position) {
                case 0:
                    selectedDuration = TimeUnit.DAYS.toMillis(1);
                    break;
                case 1:
                    selectedDuration = TimeUnit.DAYS.toMillis(3);
                    break;
                case 2:
                    selectedDuration = TimeUnit.DAYS.toMillis(7);
                    break;
                case 3:
                    selectedDuration = TimeUnit.DAYS.toMillis(14);
                    break;
                case 4:
                    selectedDuration = TimeUnit.DAYS.toMillis(30);
                    break;
            }
        });
    }

    private void submitProposal() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleEditText.setError("El título es requerido");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError("La descripción es requerida");
            return;
        }

        if (getActivity() instanceof OnProposalAddedListener) {
            listener = (OnProposalAddedListener) getActivity();
            listener.onProposalAdded(title, description, selectedDuration);
        }

        dismiss();
    }
}