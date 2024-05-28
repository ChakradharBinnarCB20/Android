package com.example.admin_beerbar_new;

import android.content.Context;
import android.content.SharedPreferences;
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

public class Sale_Register_Liquor_Report extends AppCompatActivity {
    String m_pricetype, cdate, Query_date;
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress, portnumber, str_compdesc, str_price_type_title,db;
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
    String from_date, to_date,str_chk_monthly_summary;
    PreparedStatement ps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_register_liquor_report);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
        txt_imfl_total=(TextView)findViewById(R.id.txt_imfl_total);
        txt_country_total=(TextView)findViewById(R.id.txt_country_total);
        txt_beer_strong_total=(TextView)findViewById(R.id.txt_beer_strong_total);
        txt_beer_mild_total=(TextView)findViewById(R.id.txt_beer_mild_total);
        txt_wine_total=(TextView)findViewById(R.id.txt_wine_total);
        txt_cold_drinks_total=(TextView)findViewById(R.id.txt_cold_drinks_total);
        txt_retil_dis_total=(TextView)findViewById(R.id.txt_retil_dis_total);
        txt_total=(TextView)findViewById(R.id.txt_total);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        systemDate = df.format(c);

        Bundle bb = getIntent().getExtras();
        try {
            //check_id = b.getString("checklist");
            str_price_type_title = bb.getString("str_price_type_title");
            m_pricetype = bb.getString("m_pricetype");
            cdate = bb.getString("cdate");
            Query_date = bb.getString("Query_date");
            Log.d("ddd", cdate);
            Log.d("ddd", Query_date);
            // Show_report(check_id);
        } catch (Exception e) {
        }

        Bundle bd = getIntent().getExtras();
        try {
            //check_id = b.getString("checklist");
            from_date = bd.getString("from_date");
            to_date = bd.getString("to_date");
            str_chk_monthly_summary = bd.getString("str_chk_monthly_summary");
            // Show_report(check_id);
        } catch (Exception e) {
        }
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Daywise Valuewise Sale");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(to_date);
        toolbar_to_date.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(from_date);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);

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

                ps = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT = AMOUNT_1+AMOUNT_2+AMOUNT_3+AMOUNT_4+AMOUNT_5+AMOUNT_6-DIS_AMOUNT WHERE TAB_CODE="+m_TAB_CODE+"");
                ps.executeUpdate();
                ps = con.prepareStatement("DELETE FROM TABREPORTPARAMETERS WHERE AMOUNT = 0 AND TAB_CODE="+m_TAB_CODE+"");
                ps.executeUpdate();
                if (str_chk_monthly_summary.equals("0"))
                {
                    ps = con.prepareStatement("SELECT CONVERT(VARCHAR(10),DOC_DT,103) AS DOCDT,ltrim(str(AMOUNT_1,12,2)) as AMOUNT_1,ltrim(str(AMOUNT_2,12,2)) as AMOUNT_2,ltrim(str(AMOUNT_3,12,2)) as AMOUNT_3,ltrim(str(AMOUNT_4,12,2)) as AMOUNT_4,ltrim(str(AMOUNT_5,12,2)) as AMOUNT_5,ltrim(str(AMOUNT_6,12,2)) as AMOUNT_6,ltrim(str(DIS_AMOUNT,12,2)) as DIS_AMOUNT,ltrim(str(AMOUNT,12,2)) as AMOUNT,DOC_DT FROM TABREPORTPARAMETERS WHERE TAB_CODE=" + m_TAB_CODE + " UNION SELECT '01/01/1900' AS DOCDT,ltrim(str(SUM(AMOUNT_1),12,2)) as AMOUNT_1,ltrim(str(SUM(AMOUNT_2),12,2)) as AMOUNT_2,ltrim(str(SUM(AMOUNT_3),12,2)) as AMOUNT_3,ltrim(str(SUM(AMOUNT_4),12,2)) as AMOUNT_4,ltrim(str(SUM(AMOUNT_5),12,2)) as AMOUNT_5,ltrim(str(SUM(AMOUNT_6),12,2)) as AMOUNT_6,ltrim(str(SUM(DIS_AMOUNT),12,2)) as DIS_AMOUNT,ltrim(str(SUM(AMOUNT),12,2)) as AMOUNT,'12/31/9999' AS DOC_DT FROM TABREPORTPARAMETERS WHERE TAB_CODE=" + m_TAB_CODE + " ORDER BY DOC_DT");
                }
                else
                {
                    ps = con.prepareStatement("SELECT upper(replace(right(convert(varchar(11),doc_dt,106),8),' ','-')) AS DOCDT,ltrim(str(SUM(AMOUNT_1),12,2)) as AMOUNT_1,ltrim(str(SUM(AMOUNT_2),12,2)) as AMOUNT_2,ltrim(str(SUM(AMOUNT_3),12,2)) as AMOUNT_3,ltrim(str(SUM(AMOUNT_4),12,2)) as AMOUNT_4,ltrim(str(SUM(AMOUNT_5),12,2)) as AMOUNT_5,ltrim(str(SUM(AMOUNT_6),12,2)) as AMOUNT_6,ltrim(str(SUM(DIS_AMOUNT),12,2)) as DIS_AMOUNT,ltrim(str(SUM(AMOUNT),12,2)) as AMOUNT,year(doc_dt) AS DOCYEAR,month(doc_dt) AS DOCMONTH FROM TABREPORTPARAMETERS WHERE TAB_CODE=" + m_TAB_CODE + " group by upper(replace(right(convert(varchar(11),doc_dt,106),8),' ','-')),year(doc_dt),month(doc_dt) UNION SELECT '01/01/1900' AS DOCDT,ltrim(str(SUM(AMOUNT_1),12,2)) as AMOUNT_1,ltrim(str(SUM(AMOUNT_2),12,2)) as AMOUNT_2,ltrim(str(SUM(AMOUNT_3),12,2)) as AMOUNT_3,ltrim(str(SUM(AMOUNT_4),12,2)) as AMOUNT_4,ltrim(str(SUM(AMOUNT_5),12,2)) as AMOUNT_5,ltrim(str(SUM(AMOUNT_6),12,2)) as AMOUNT_6,ltrim(str(SUM(DIS_AMOUNT),12,2)) as DIS_AMOUNT,ltrim(str(SUM(AMOUNT),12,2)) as AMOUNT,9999 AS DOCYEAR,12 AS DOCMONTH FROM TABREPORTPARAMETERS WHERE TAB_CODE=" + m_TAB_CODE + " ORDER BY DOCYEAR,DOCMONTH");
                }


                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    if(!rs.getString("DOCDT").equals("01/01/1900")) {
                        map.put("DOCDT", rs.getString("DOCDT"));
                        map.put("AMOUNT_1", rs.getString("AMOUNT_1"));
                        map.put("AMOUNT_2", rs.getString("AMOUNT_2"));
                        map.put("AMOUNT_3", rs.getString("AMOUNT_3"));
                        map.put("AMOUNT_4", rs.getString("AMOUNT_4"));

                        menu_card_arryList.add(map);
                    }
                    else
                    {
                        txt_imfl_total.setText(rs.getString("AMOUNT_1"));
                        txt_country_total.setText(rs.getString("AMOUNT_2"));
                        txt_beer_strong_total.setText(rs.getString("AMOUNT_3"));
                        txt_beer_mild_total.setText(rs.getString("AMOUNT_4"));
                        txt_wine_total.setText(rs.getString("AMOUNT_5"));


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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_registere_liquor_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("DOCDT"));
            holder.list_d2.setText(attendance_list.get(position).get("AMOUNT_1"));
            holder.list_d3.setText(attendance_list.get(position).get("AMOUNT_2"));
            holder.list_d4.setText(attendance_list.get(position).get("AMOUNT_3"));
            holder.list_d5.setText(attendance_list.get(position).get("AMOUNT_4"));

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