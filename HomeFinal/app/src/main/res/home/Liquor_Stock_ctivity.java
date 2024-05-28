package com.example.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class Liquor_Stock_ctivity extends AppCompatActivity {

    SearchView searchView;
    String SubCodeStr;
    IMEI_Activity connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc;
    Toolbar toolbar;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquor__stock);
        searchView = (SearchView) findViewById(R.id.report_searchView);
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences sp11 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp11.getString("ipaddress", "");
        portnumber = sp11.getString("portnumber", "");

        SharedPreferences ss = getSharedPreferences("COMP_CODE", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(Liquor_Stock_ctivity.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(Liquor_Stock_ctivity.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        //------------------------------------------------------------------------------------------
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Liquor Stock");
        toolbar_title.setTextColor(0xFFFFFFFF);
        TextView txt_hotel_name = (TextView) toolbar.findViewById(R.id.txt_hotel_name);
        txt_hotel_name.setText(str_compdesc);
        txt_hotel_name.setTextColor(0xFFFFFFFF);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }


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
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                PreparedStatement ps = con.prepareStatement("SELECT BRND_DESC,SIZE_DESC,(SELECT LOCT_DESC FROM LOCTMAST WHERE LOCT_CODE=ONLNSTOK.LOCT_CODE) AS LOCT_DESC,CASE WHEN FLOOR(CL_BALANCE/CONVERT(INT,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10))) = 0 THEN '' ELSE  LTRIM(STR(FLOOR(CL_BALANCE/CONVERT(INT,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10))))) END AS SEALED, CASE WHEN CL_BALANCE - (FLOOR(CL_BALANCE/CONVERT(INT,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10)))*CONVERT(INT,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10))) = 0 THEN '' ELSE LTRIM(STR(CL_BALANCE - (FLOOR(CL_BALANCE/CONVERT(INT,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10)))*CONVERT(INT,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10))))) + ' ML' END AS LOOSE FROM ONLNSTOK,ITEMMAST,BRNDMAST,SIZEMAST WHERE ONLNSTOK.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND ITEMMAST.SIZE_CODE=SIZEMAST.SIZE_CODE AND ITEM_TYPE=1 AND CL_BALANCE > 0 and brnd_desc like '" + SubCodeStr + "%' ORDER BY BRND_DESC,SEQ_NO,LOCT_DESC");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("1", rs.getString(1));
                    map.put("2", rs.getString(2));
                    map.put("3", rs.getString(3));
                    map.put("4", rs.getString(4));
                    map.put("5", rs.getString(5));

                    menu_card_arryList.add(map);
                }
            }
            pbbar.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<Liquor_Stock_ctivity.atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Liquor_Stock_ctivity.atnds_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_, parent, false);
            Liquor_Stock_ctivity.atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new atnds_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Liquor_Stock_ctivity.atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {


            holder.list_item.setText(attendance_list.get(position).get("1"));
            holder.list_size.setText(attendance_list.get(position).get("2"));
            holder.list_menu_type.setText(attendance_list.get(position).get("3"));
            holder.list_rate.setText(attendance_list.get(position).get("4"));
            holder.list_item_type.setText(attendance_list.get(position).get("5"));


        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_item, list_size, list_menu_type, list_rate, list_item_type;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_item = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_size = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_menu_type = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_rate = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_item_type = (TextView) itemView.findViewById(R.id.list_d5);


            }
        }
    }
}
