package com.example.corpsetellingapp.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.corpsetellingapp.R;

import java.util.List;

public class PlayersFragment extends Fragment {

    private LinearLayout playersContainer;

    public PlayersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playersContainer = view.findViewById(R.id.playersContainer);

        loadPlayers();
    }

    private void loadPlayers() {
        playersContainer.removeAllViews();

        HttpPetition.getPlayers(requireContext(), new HttpPetition.PlayersCallback() {
            @Override
            public void onResult(List<String> playerNames) {
                requireActivity().runOnUiThread(() -> {
                    playersContainer.removeAllViews();
                    for (String playerName : playerNames) {
                        Button btn = new Button(requireContext());
                        btn.setText(playerName);

                        btn.setOnClickListener(v -> showOptionsDialog(playerName));

                        playersContainer.addView(btn);
                    }
                });
            }
        });
    }

    private void showOptionsDialog(String playerName) {
        final String[] options = {
                getString(R.string.action_ban),
                getString(R.string.action_kick),
                getString(R.string.action_kill)
        };

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.actions_for_player, playerName))
                .setItems(options, (dialog, which) -> {
                    String command = "";
                    switch (which) {
                        case 0:
                            command = "ban " + playerName;
                            break;
                        case 1:
                            command = "kick " + playerName;
                            break;
                        case 2:
                            command = "kill " + playerName;
                            break;
                    }
                    if (!command.isEmpty()) {
                        HttpPetition.sendCommand(command, requireContext());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
