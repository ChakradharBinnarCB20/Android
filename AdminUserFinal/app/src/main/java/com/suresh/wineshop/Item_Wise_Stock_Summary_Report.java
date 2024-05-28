package com.suresh.wineshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

public class Item_Wise_Stock_Summary_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,db;
    TextView txt_total;
    Double total=0.0;
    Double ttl=0.0;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String IMEINumber,cdate,m_fdate,m_tdate;
    int m_TAB_CODE;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    Double ottl=0.0,rttl=0.0,sttl=0.0,cttl=0.0;
    TextView txt_total1,txt_total2,txt_total3,txt_total4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_wise_stock_within_peroid_summary);

        Bundle bb = getIntent().getExtras();
        try {
            //check_id = b.getString("checklist");
            cdate=bb.getString("cdate");
            m_fdate=bb.getString("fdate");
            m_tdate=bb.getString("tdate");
            // Show_report(check_id);
        } catch (Exception e) { }

        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");

        SharedPreferences sp =getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        //------------------------Toolbar-------------------------------------------
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText(""+str_compdesc+""+"\n"+"Item Wise Size Summary");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(m_fdate);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(m_tdate);
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
        txt_total1=(TextView)findViewById(R.id.txt_total1);
        txt_total2=(TextView)findViewById(R.id.txt_total2);
        txt_total3=(TextView)findViewById(R.id.txt_total3);
        txt_total4=(TextView)findViewById(R.id.txt_total4);

        connectionClass = new Config();
        try {
            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else
            {
                PreparedStatement ps = con.prepareStatement("select size_desc,convert(varchar(10),sum(amount_1)) as amount_1,convert(varchar(10),sum(amount_2+amount_3)) as purqty,convert(varchar(10),sum(tot_sale)) as tot_sale,convert(varchar(10),sum(amount)) as cl_balance from tabreportparameters,itemmast,sizemast where tabreportparameters.item_code = itemmast.item_code and itemmast.size_code = sizemast.size_code and tabreportparameters.tab_code = "+m_TAB_CODE+" group by itemmast.size_code,size_desc,seq_no order by seq_no");
              //  PreparedStatement ps = con.prepareStatement("select size_desc,sum(amount_1) as amount_1,sum(amount_2+amount_3) as purqty,sum(tot_sale) as tot_sale,sum(amount) as cl_balance from tabreportparameters,itemmast,sizemast where tabreportparameters.item_code = itemmast.item_code and itemmast.size_code = sizemast.size_code and tabreportparameters.tab_code = "+m_TAB_CODE+" group by itemmast.size_code,size_desc,seq_no order by seq_no");
                ResultSet rs = ps.executeQuery();
                total=0.0;
                int cnt=0;
                ottl=0.0;rttl=0.0;sttl=0.0;cttl=0.0;
                while (rs.next())
                {
                    cnt++;
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("size", rs.getString(1));
                    map.put("op", rs.getString(2));
                    map.put("rc", rs.getString(3));
                    map.put("sl", rs.getString(4));
                    map.put("cl", rs.getString(5));
                    map.put("srno", ""+cnt);
                  //  Double val=(Double.parseDouble(rs.getString(2)));
                    ottl = ottl + Double.parseDouble(rs.getString(2));
                    rttl = rttl + Double.parseDouble(rs.getString(3));
                    sttl = sttl + Double.parseDouble(rs.getString(4));
                    cttl = cttl + Double.parseDouble(rs.getString(5));
                    menu_card_arryList.add(map);
                    NumberFormat n = new DecimalFormat(".0");
                    txt_total1.setText("" + n.format(ottl));
                    txt_total2.setText("" + n.format(rttl));
                    txt_total3.setText("" + n.format(sttl));
                    txt_total4.setText("" + n.format(cttl));
                }


            }
            pbbar.setVisibility(View.GONE);


            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }
            con.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wise_stock__within_prdlist_smry, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("srno"));
            holder.list_d2.setText(attendance_list.get(position).get("size"));
            holder.list_d3.setText(attendance_list.get(position).get("op"));
            holder.list_d4.setText(attendance_list.get(position).get("rc"));
            holder.list_d5.setText(attendance_list.get(position).get("sl"));
            holder.list_d6.setText(attendance_list.get(position).get("cl"));

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3,list_d4,list_d5,list_d6;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);
                this.list_d6 = (TextView) itemView.findViewById(R.id.list_d6);

            }
        }
    }




}
