package com.suresh.wineshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Customer_Wise_Summary_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,IMEINumber,db;
    TextView txt_total,txt_debit_total;
    Double debit_total=0.0;
    Double credit_total=0.0;
    Double ttl=0.0;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String date,type;
    int m_TAB_CODE;
    int m_compcode;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_wise_summary_report);
         Bundle b=getIntent().getExtras();
         try
         {
             date=b.getString("date");
             type=b.getString("type");
         }catch (Exception e)
         {

         }
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber","");
                //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText(""+str_compdesc+"\n"+"Customer Wise Summary"+"("+type+")");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(date);
        toolbar_to_date.setTextColor(0xFFFFFFFF);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
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
        txt_debit_total = (TextView) findViewById(R.id.txt_debit_total);
        pbbar.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed((Runnable) () -> {
            report();
            pbbar.setVisibility(View.GONE);
        }, 4000);


    }
    void report()
    {
        connectionClass = new Config();
        try {
            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else
            {    //qry="select ac_head_id,(select gl_desc+','+plac_desc from glmast,placmast where glmast.plac_code=placmast.plac_code and glmast.ac_head_id=tabreportparameters.ac_head_id)as gl_desc,sum(amount) as m_clbal from tabreportparameters where TAB_CODE ="+m_TAB_CODE+" group by ac_head_id,gl_desc having sum(amount) <>0 order by gl_desc";
                qry="select glmast.gl_desc+','+plac_desc as gl_desc,amount as m_clbal from tabreportparameters,glmast,placmast where glmast.ac_head_id=tabreportparameters.ac_head_id and glmast.plac_code=placmast.plac_code and TAB_CODE ="+m_TAB_CODE+" order by glmast.gl_desc";
                Log.d("qry",qry);
                PreparedStatement ps = con.prepareStatement(qry);
                ResultSet rs = ps.executeQuery();

                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    if(Double.parseDouble(rs.getString("m_clbal"))>0)
                    {
                        NumberFormat nf1 =new DecimalFormat(".00");
                        //map.put("brnd_code", rs.getString("m_clbal"));
                        // Double val=(Double.parseDouble(rs.getString("m_clbal")));
                        Double val=(Double.parseDouble(rs.getString("m_clbal")));
                        map.put("brnd_code", ""+nf1.format(Math.abs(val)));
                        debit_total=debit_total+val;
                    }
                    else
                    {
                        NumberFormat nf1 =new DecimalFormat(".00");
                        Double val=(Double.parseDouble(rs.getString("m_clbal")));
                        map.put("ac_head_id", ""+nf1.format(Math.abs(val)));
                        credit_total=credit_total+val;
                    }
                    map.put("gl_desc", rs.getString("gl_desc"));
                    // map.put("ac_head_id", rs.getString("ac_head_id"));
                    // map.put("brnd_code", rs.getString(3));
                   /* Double val=(Double.parseDouble(rs.getString(2)));
                    total = total + val;
                    menu_card_arryList.add(map);*/
                    NumberFormat nf1 =new DecimalFormat(".00");
                    txt_debit_total.setText(""+nf1.format(debit_total));
                    txt_total.setText(""+nf1.format(Math.abs(credit_total)));
                    menu_card_arryList.add(map);
                }

                ttl=0.0;
            }
            pbbar.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }
            con.close();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_wise_summary_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("gl_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("brnd_code"));
            holder.list_d3.setText(attendance_list.get(position).get("ac_head_id"));
        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);

            }
        }
    }

}
