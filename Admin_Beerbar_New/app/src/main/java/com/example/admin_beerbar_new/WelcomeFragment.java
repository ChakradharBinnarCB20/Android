package com.example.admin_beerbar_new;


import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {

    TextView txt_wine_shop_name,txt_user;
    String str_wine_shop,user_name,str_compdesc;
    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences ss = getActivity().getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");

        SharedPreferences sp = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        user_name = sp.getString("user_name", "");
        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        str_wine_shop = sp1.getString("str_wine_shop", "");


        txt_user=(TextView)view.findViewById(R.id.txt_user);
        txt_wine_shop_name=(TextView)view.findViewById(R.id.txt_wine_shop_name);
        txt_wine_shop_name.setText(str_compdesc);
        txt_user.setText("Welcome:"+user_name);
    }
}
