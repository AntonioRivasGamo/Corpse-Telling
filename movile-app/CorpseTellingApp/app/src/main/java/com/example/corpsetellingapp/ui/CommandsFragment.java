package com.example.corpsetellingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.corpsetellingapp.R;

public class CommandsFragment extends Fragment {

    public CommandsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_commands, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner commandSpinner = view.findViewById(R.id.commandSpinner);
        EditText commandInput = view.findViewById(R.id.commandInput);
        Button sendCommandButton = view.findViewById(R.id.sendCommandButton);

        String[] commandList = {
                "Summon",
                "Gamemode",
                "Lightning",
                "Effect",
                "Gamerule",
                "Spawnpoint",
                "Teleport",
                "Give",
                "Ocelot zombie LOL"
        };

        String player = getString(R.string.command_player);
        String sheepLol = getString(R.string.command_sheep_lol);
        String loreText = getString(R.string.command_lore);

        String[] commandTemplates = {
                "execute as [player] at @s run summon minecraft:[entity] ~ ~ ~",
                "gamemode [mode] [player]",
                "execute as [player] at @s run summon minecraft:lightning_bolt ~ ~ ~",
                "effect [player] minecraft:[effect] [duration] [level]",
                "gamerule <gamerule> <value>",
                "spawnpoint [" + player + "] [x] [y] [z]",
                "tp [" + player + "] [new place]",
                "give [" + player + "] minecraft:[item] [number]",
                "execute as [" + player + "] at @s run summon ocelot ~ ~ ~ " +
                        "{Trusting:1,Passengers:[" +
                        "{id:zombie_villager," +
                        "VillagerData:{type:swamp,profession:nitwit,level:5}," +
                        "IsBaby:1," +
                        "CustomName:'[" +
                        "{\"text\":\"P\",\"bold\":true,\"color\":\"#ff0000\"}," +
                        "{\"text\":\"E P\",\"color\":\"#fff55]\"}," +
                        "{\"text\":\"E\",\"color\":\"#fff55]\"}" +
                        "]'," +
                        "Health:200," +
                        "PersistenceRequired:1b," +
                        "HandItems:[" +
                        "{id:end_rod,components:{custom_name:'[\"\",{\"text\":\"" + sheepLol + "\",\"italic\":false}]'," +
                        "lore:['[\"\",{\"text\":\"" + loreText + "\",\"italic\":false}]']," +
                        "rarity:epic," +
                        "enchantments:{levels:{bane_of_arthropods:20,binding_curse:1,blast_protection:10,looting:10," +
                        "projectile_protection:10,punch:20,sharpness:20}}},count:1}," +
                        "{id:sticky_piston,count:1}]," +
                        "HandDropChances:[1f,1f]," +
                        "ArmorItems:[{},{},{},{id:milk_bucket,count:1}]," +
                        "ArmorDropChances:[0f,0f,0f,0f]," +
                        "attributes:[{id:\"generic.max_health\",base:200f}]" +
                        "}]}"

        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, commandList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commandSpinner.setAdapter(adapter);

        commandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                commandInput.setText(commandTemplates[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sendCommandButton.setOnClickListener(v -> {
            String command = commandInput.getText().toString().trim();
            if (!command.isEmpty()) {
                HttpPetition.sendCommand(command, getContext());
            }
        });
    }
}
