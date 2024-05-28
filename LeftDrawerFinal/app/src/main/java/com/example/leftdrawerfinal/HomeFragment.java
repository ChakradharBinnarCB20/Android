package com.example.leftdrawerfinal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    Button btn;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view =inflater.inflate(R.layout.fragment_home, container, false);

        btn = view.findViewById(R.id.btnHello);
        
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You Pressed hello...!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}