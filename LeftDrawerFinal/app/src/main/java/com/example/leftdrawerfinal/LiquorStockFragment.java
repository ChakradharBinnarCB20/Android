package com.example.leftdrawerfinal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class LiquorStockFragment extends Fragment {

    Button btnGallary;

    public LiquorStockFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        btnGallary = view.findViewById(R.id.btnGal);

        btnGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Gallary...!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}