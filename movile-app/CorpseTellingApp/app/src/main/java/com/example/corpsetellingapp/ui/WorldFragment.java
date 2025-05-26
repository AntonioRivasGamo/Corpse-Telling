package com.example.corpsetellingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.corpsetellingapp.R;

public class WorldFragment extends Fragment {

    public WorldFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_world, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnPeaceful = view.findViewById(R.id.btnPeaceful);
        Button btnEasy = view.findViewById(R.id.btnEasy);
        Button btnNormal = view.findViewById(R.id.btnNormal);
        Button btnHard = view.findViewById(R.id.btnHard);

        btnPeaceful.setText(getString(R.string.difficulty_peaceful));
        btnEasy.setText(getString(R.string.difficulty_easy));
        btnNormal.setText(getString(R.string.difficulty_normal));
        btnHard.setText(getString(R.string.difficulty_hard));

        btnPeaceful.setOnClickListener(v -> HttpPetition.sendCommand("peaceful", getContext()));
        btnEasy.setOnClickListener(v -> HttpPetition.sendCommand("easy", getContext()));
        btnNormal.setOnClickListener(v -> HttpPetition.sendCommand("normal", getContext()));
        btnHard.setOnClickListener(v -> HttpPetition.sendCommand("hard", getContext()));

        Button btnClear = view.findViewById(R.id.btnClear);
        Button btnRain = view.findViewById(R.id.btnRain);
        Button btnStorm = view.findViewById(R.id.btnStorm);

        btnClear.setText(getString(R.string.weather_clear));
        btnRain.setText(getString(R.string.weather_rain));
        btnStorm.setText(getString(R.string.weather_storm));

        btnClear.setOnClickListener(v -> HttpPetition.sendCommand("clear", getContext()));
        btnRain.setOnClickListener(v -> HttpPetition.sendCommand("rain", getContext()));
        btnStorm.setOnClickListener(v -> HttpPetition.sendCommand("storm", getContext()));

        Button btnDay = view.findViewById(R.id.btnDay);
        Button btnNoon = view.findViewById(R.id.btnNoon);
        Button btnNight = view.findViewById(R.id.btnNight);
        Button btnMidnight = view.findViewById(R.id.btnMidnight);

        btnDay.setText(getString(R.string.time_day));
        btnNoon.setText(getString(R.string.time_noon));
        btnNight.setText(getString(R.string.time_night));
        btnMidnight.setText(getString(R.string.time_midnight));

        btnDay.setOnClickListener(v -> HttpPetition.sendCommand("day", getContext()));
        btnNoon.setOnClickListener(v -> HttpPetition.sendCommand("noon", getContext()));
        btnNight.setOnClickListener(v -> HttpPetition.sendCommand("night", getContext()));
        btnMidnight.setOnClickListener(v -> HttpPetition.sendCommand("midnight", getContext()));
    }
}
