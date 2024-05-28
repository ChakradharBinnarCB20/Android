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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Date_Rrand_Wise_Sale_Summary_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc;
    TextView txt_total,txt_sale_total;
    Double total=0.0;
    Double sale_total=0.0;
    Double ttl=0.0;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String IMEINumber,cdate;
    int m_TAB_CODE;
    String frm_date,category,to_date,db;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_brand_wise_sale_summary_report);


        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");

        SharedPreferences sp =getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        Bundle b=getIntent().getExtras();
        try
        {
            frm_date=b.getString("frm_date");
            to_date=b.getString("to_date");
            category=b.getString("category");

        }catch(Exception e) { }



        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_category = (TextView) toolbar.findViewById(R.id.toolbar_category);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc +"\n"+"Size Wise Summary");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(frm_date);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(to_date);
        toolbar_to_date.setTextColor(0xFFFFFFFF);
        toolbar_category.setText(category);
        toolbar_category.setTextColor(0xFFFFFFFF);

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
        txt_sale_total = (TextView) findViewById(R.id.txt_sale_total);

        connectionClass = new Config();
        try {

            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else
            {

                qry=" select '' as doc_type,(select size_desc from sizemast where size_code = tabreportparameters.amount_2)as size_desc,ltrim(str(sum(tot_sale),8,0)) as tot_sale,ltrim(str(sum(amount),12,2)) as item_value,liqr_code,(select seq_no from sizemast where size_code=tabreportparameters.amount_2) as seq_no,1 as sqno from tabreportparameters where tab_code="+m_TAB_CODE+" and item_code<>'' group by liqr_code,doc_type,amount_2 union select doc_type,'' as size_desc,'' as tot_sale,'' as item_value,liqr_code,0 as seq_no,0 as sqno from tabreportparameters where tab_code="+m_TAB_CODE+" and item_code='' and amount = 0 union select doc_type,'' as size_desc,ltrim(str(tot_sale,8,2)) as tot_sale,ltrim(str(amount,12,2)) as item_value,liqr_code,0 as seq_no,2 as sqno from tabreportparameters where tab_code="+m_TAB_CODE+" and item_code='' and amount <> 0 order by liqr_code,sqno,seq_no";



                Log.d("qry",qry);
                PreparedStatement ps = con.prepareStatement(qry);
                ResultSet rs = ps.executeQuery();
                total=0.0;
                sale_total=0.0;
                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("doc_type", rs.getString("doc_type"));
                    map.put("size_desc", rs.getString("size_desc"));
                    //map.put("tot_sale", rs.getString("tot_sale"));
                    //NumberFormat nn =new DecimalFormat(".00");
                    map.put("tot_sale", rs.getString("tot_sale"));
                    map.put("item_value", rs.getString("item_value"));
                    // map.put("AMOUNT_1", rs.getString("AMOUNT_1"));
                    //map.put("amount", rs.getString("amount"));
                    map.put("sqno", rs.getString("sqno"));

                    if(rs.getString("sqno").equals("2")) {
                        total = total + Double.parseDouble(rs.getString("item_value"));
                        sale_total = sale_total + Double.parseDouble(rs.getString("tot_sale"));
                    }
                    menu_card_arryList.add(map);
                    NumberFormat n =new DecimalFormat(".00");
                    txt_total.setText(""+n.format(total));
                    txt_sale_total.setText(""+n.format(sale_total));
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

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_summary_date_brand_wise_sale_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("doc_type"));
            holder.list_d2.setText(attendance_list.get(position).get("size_desc"));
            holder.list_d3.setText(attendance_list.get(position).get("tot_sale"));
            holder.list_d4.setText(attendance_list.get(position).get("item_value"));
            if(attendance_list.get(position).get("sqno").equals("2"))
            {
                holder.list_d1.setTextColor(Color.RED);
                holder.list_d3.setTextColor(Color.RED);
                holder.list_d4.setTextColor(Color.RED);
            }
            else
            {
                holder.list_d1.setTextColor(Color.BLACK);
                holder.list_d3.setTextColor(Color.BLACK);
                holder.list_d4.setTextColor(Color.BLACK);
            }

            if(attendance_list.get(position).get("sqno").equals("0"))
            {
                holder.list_d1.setTextColor(Color.BLUE);

            }

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3,list_d4;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);

            }
        }
    }




}
