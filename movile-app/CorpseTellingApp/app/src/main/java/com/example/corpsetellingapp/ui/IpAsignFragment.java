package com.example.corpsetellingapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.corpsetellingapp.R;

public class IpAsignFragment extends Fragment {

    private EditText editTextServerIp;
    private Button buttonAssignServerIp;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ConnectionPrefs";
    private static final String KEY_SERVER_IP = "server_ip";
    private static final int SERVER_PORT = 4567;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ip_asign, container, false);

        editTextServerIp = view.findViewById(R.id.editTextServerIp);
        buttonAssignServerIp = view.findViewById(R.id.buttonAssignServerIp);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        loadSavedServerIp();

        buttonAssignServerIp.setOnClickListener(v -> {
            String ipInput = editTextServerIp.getText().toString().trim();
            if (TextUtils.isEmpty(ipInput)) {
                Toast.makeText(getContext(), getString(R.string.prompt_enter_server_ip), Toast.LENGTH_SHORT).show();
                return;
            }

            String ipWithPort = ipInput + ":" + SERVER_PORT;
            sharedPreferences.edit().putString(KEY_SERVER_IP, ipWithPort).apply();

            Toast.makeText(getContext(), getString(R.string.server_ip_saved, ipWithPort), Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadSavedServerIp() {
        String savedIpPort = sharedPreferences.getString(KEY_SERVER_IP, "");
        if (!savedIpPort.isEmpty()) {
            int colonIndex = savedIpPort.indexOf(':');
            if (colonIndex != -1) {
                String ipOnly = savedIpPort.substring(0, colonIndex);
                editTextServerIp.setText(ipOnly);
            } else {
                editTextServerIp.setText(savedIpPort);
            }
        }
    }
}
