package com.example.home;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class Other_Stock_Fragment extends Fragment {

    SearchView searchView;
    String SubCodeStr;
    IMEI_Activity connectionClass;
    ProgressBar pbbar;
    HashMap<String, String> map;
    String con_ipaddress ,portnumber;
    ArrayList<HashMap<String, String>> data_list;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    //=====PDF=========================
    File cacheDir;
    final Context context = getActivity();
    private static final int PERMISSION_REQUEST_CODE = 1;
    Button btn_pdf_export;
    public Other_Stock_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_stock_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        data_list = new ArrayList<HashMap<String, String>>();
        searchView = (SearchView) view.findViewById(R.id.report_searchView);
        pbbar = (ProgressBar) view.findViewById(R.id.pgb);
        //btn_pdf_export=(Button)view.findViewById(R.id.btn_pdf_export);
//        btn_pdf_export.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  check_pdf_permission();
//            }
//        });
        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) view.findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getActivity(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        stock_search("");
        //------------------------------------------------------------------------------------------
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                //menu_card_arryList.clear();
                // new Attendance_list().execute();
                // All_menu_search();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 0) {

                    SubCodeStr = newText;
                    SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();
                    //subcodestr = subcodestr.replaceAll("\\s+", "% ").toLowerCase();
                    Log.d("ssss", SubCodeStr);

                    //new FetchSearchResult().execute();
                    stock_search(SubCodeStr);
                } else if (TextUtils.isEmpty(newText)) {
                    // lin_grid_visible.setVisibility(View.INVISIBLE);
                    // menu_card_arryList.clear();
                    // menu_search("");
                } else {
                    stock_search("");
                }
                return false;
            }
        });
    }


    public void stock_search(String SubCodeStr) {
        //SubCodeStr,m_ratetype
        // m_compcode,m_loctcode,m_maintainstockyn,m_gstreverseyn,
        connectionClass = new IMEI_Activity();
        connectionClass = new IMEI_Activity();
        try {
            pbbar.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                PreparedStatement ps = con.prepareStatement("SELECT MENUITEM_DESC,(SELECT LOCT_DESC FROM LOCTMAST WHERE LOCT_CODE=ONLNSTOK.LOCT_CODE) AS LOCT_DESC,CASE WHEN CL_BALANCE = 0 THEN '' ELSE LTRIM(STR(CL_BALANCE)) END AS CL_BALANCE FROM ONLNSTOK,MENUCARDITEMMAST WHERE ONLNSTOK.ITEM_CODE=MENUCARDITEMMAST.MENUITEM_CODE AND ITEM_TYPE=2 AND CL_BALANCE > 0 and MENUITEM_DESC like '" + SubCodeStr + "%' ORDER BY MENUITEM_DESC,LOCT_DESC");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                data_list.clear();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map = new HashMap<String, String>();
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("1", rs.getString(1));
                    map.put("2", rs.getString(2));
                    map.put("3", rs.getString(3));

                    menu_card_arryList.add(map);
                    data_list.add(map);
                }
            }
            pbbar.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_stock_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {


            holder.list_item.setText(attendance_list.get(position).get("1"));
            holder.list_size.setText(attendance_list.get(position).get("2"));
            holder.list_menu_type.setText(attendance_list.get(position).get("3"));
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_item, list_size, list_menu_type;


            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_item = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_size = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_menu_type = (TextView) itemView.findViewById(R.id.list_d3);



            }
        }
    }



    //------pdf--------------------
    //-----------------------------
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    //===================================================================
}
