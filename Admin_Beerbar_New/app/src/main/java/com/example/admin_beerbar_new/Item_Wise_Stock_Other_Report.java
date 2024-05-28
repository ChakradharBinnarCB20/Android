package com.example.admin_beerbar_new;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Item_Wise_Stock_Other_Report extends AppCompatActivity {
    String m_pricetype, cdate, Query_date;
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress, portnumber, str_compdesc, str_price_type_title;
    TextView txt_total;
    Double total = 0.0;
    Double ttl = 0.0;

    String qry = "", IMEINumber, systemDate;
    Connection con;
    PreparedStatement ps1;
    TextView txt_imfl_total,txt_country_total,txt_beer_strong_total,txt_beer_mild_total,txt_wine_total,txt_cold_drinks_total,txt_retil_dis_total;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    Toolbar toolbar;
    int m_TAB_CODE;
    String check_id,from_date, to_date,loc,db;
    PreparedStatement ps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_wise_stock_other_report);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        systemDate = df.format(c);

        Bundle b = getIntent().getExtras();
        try {
            check_id = b.getString("checklist");
            from_date = b.getString("from_date");
            to_date = b.getString("to_date");
            loc = b.getString("loc");

        } catch (Exception e) { }
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        TextView toolbar_location = (TextView) toolbar.findViewById(R.id.toolbar_location);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Item Wise Stock Within Period (Other)");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(to_date);
        toolbar_to_date.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(from_date);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);
        toolbar_location.setText(loc);
        toolbar_location.setTextColor(0xFFFFFFFF);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences sp1 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);


//---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getApplicationContext(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        txt_total = (TextView) findViewById(R.id.txt_total);

        connectionClass = new Config();
        try {
            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            } else {    //qry="select ac_head_id,(select gl_desc+','+plac_desc from glmast,placmast where glmast.plac_code=placmast.plac_code and glmast.ac_head_id=tabreportparameters.ac_head_id)as gl_desc,sum(amount) as m_clbal from tabreportparameters where TAB_CODE ="+m_TAB_CODE+" group by ac_head_id,gl_desc having sum(amount) <>0 order by gl_desc";

                ps = con.prepareStatement("select  menuitem_desc AS brand_name,case when gl_opbal <> 0 then ltrim(str(gl_opbal,12,0)) else '' end as opening_qty,case when amount_1 <> 0 then ltrim(str(amount_1,12,0)) else '' end as rcv_qty,case when tot_sale <> 0 then ltrim(str(tot_sale,12,0)) else '' end as issu_qty,case when gl_clbal <> 0 then ltrim(str(gl_clbal,12,0)) else '' end as closing_qty,'1' as seq,menu_desc from tabreportparameters,menucarditemmast,menumast where menucarditemmast.menuitem_code=tabreportparameters.item_code and menumast.menu_code= menucarditemmast.menu_code and tab_code="+m_TAB_CODE+" AND liqr_code in ("+check_id+") union select  'TYPE :'+ menu_desc,'','','','', '0' as seq,menu_desc from tabreportparameters,menumast where menumast.menu_code=tabreportparameters.liqr_code and tab_code="+m_TAB_CODE+" and liqr_code in ("+check_id+")  order by menu_desc,seq,menuitem_desc");
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                   {
                        map.put("brand_name", rs.getString("brand_name"));
                        map.put("opening_qty", rs.getString("opening_qty"));
                        map.put("rcv_qty", rs.getString("rcv_qty"));
                        map.put("issu_qty", rs.getString("issu_qty"));
                        map.put("closing_qty", rs.getString("closing_qty"));
                        map.put("seq", rs.getString("seq"));

                        menu_card_arryList.add(map);
                    }

                }

            }
            pbbar.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wise_stock_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("brand_name"));
            holder.list_d2.setText(attendance_list.get(position).get("opening_qty"));
            holder.list_d3.setText(attendance_list.get(position).get("rcv_qty"));
            holder.list_d4.setText(attendance_list.get(position).get("issu_qty"));
            holder.list_d5.setText(attendance_list.get(position).get("closing_qty"));
            if(attendance_list.get(position).get("seq").equals("0"))
            {
                holder.list_d1.setTextColor(Color.BLUE);
            }
            else {
                holder.list_d1.setTextColor(Color.BLACK);
            }

        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3,list_d4, list_d5;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);

            }
        }
    }
}