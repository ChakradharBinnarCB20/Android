package com.suresh.wineshop;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
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
import java.util.Map;

public class Sale_Trend_Analysis_Report extends AppCompatActivity {
   String check_id="";
    Config connectionClass;
    Connection con;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,db;
    TextView txt_total;
    int total=0;
    int ttl=0;
    int group_ttl=0;
    String qry="";
    Toolbar toolbar;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Sale Trend Analysis Report"+"\n"+"("+str_compdesc+")");
        toolbar_title.setTextColor(0xFFFFFFFF);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        pbbar = (ProgressBar)findViewById(R.id.pgb);
        SharedPreferences sp1 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");


        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getApplicationContext(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        txt_total=(TextView)findViewById(R.id.txt_total);
        ArrayList<HashMap<String, String>> myList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("checklist");

        for (HashMap<String, String> map1 : myList)
            for (Map.Entry<String, String> mapEntry : map1.entrySet()) {
                if (mapEntry.getKey().equals("liqr_code")) {

                         check_id=mapEntry.getValue();
                         connectionClass = new Config();
                         try {
                             pbbar.setVisibility(View.VISIBLE);
                             con = connectionClass.CONN(con_ipaddress, portnumber,db);
                             if (con == null) {
                                 Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
                             }
                             else
                                 {
                                 qry="select (select brnd_desc as brand from brndmast where brnd_code in(select brnd_code from itemmast where item_code=reportparameters.item_code))as brnd_desc,(select size_desc as size from sizemast where size_code in(select size_code from itemmast where item_code=reportparameters.item_code)) as size_desc,(select size_code from itemmast where item_code=reportparameters.item_code)as size_code,(SELECT seq_no FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE = reportparameters.ITEM_CODE))as seq_no ,(select liqr_desc from liqrmast where liqr_code IN(SELECT liqr_code FROM ITEMMAST WHERE ITEM_CODE = reportparameters.ITEM_CODE))as liqr_desc,(select liqr_code from liqrmast where liqr_code IN(SELECT liqr_code FROM ITEMMAST WHERE ITEM_CODE = reportparameters.ITEM_CODE))as liqr_code,sum(tot_sale) as net_qty from reportparameters where TAB_YN=1 and item_code in(select item_code from itemmast where liqr_code = "+check_id+")  group by reportparameters.ITEM_CODE  having sum(tot_sale) <> 0 order by liqr_desc,brnd_desc,seq_no,net_qty desc";
                                 Log.d("qry",qry);
                                 PreparedStatement ps = con.prepareStatement(qry);
                                 ResultSet rs = ps.executeQuery();
                                 HashMap<String, String> map2 = new HashMap<String, String>();
                                 while (rs.next())
                                 {
                                     HashMap<String, String> map = new HashMap<String, String>();
                                     total = total + (rs.getInt(7));
                                     ttl = ttl + (rs.getInt(7));
                                     map.put("1", rs.getString(1));
                                     map.put("2", rs.getString(2));
                                     map.put("3", rs.getString(3));
                                     map.put("4", rs.getString(4));
                                     map.put("5", rs.getString(5));
                                     map.put("6", rs.getString(6));
                                     map.put("7", rs.getString(7));
                                     menu_card_arryList.add(map);
                                 }
                                 group_ttl=ttl;
                                     //String redString = getResources().getString(R.string.total);
                                     //redColorTextView.setText(Html.fromHtml(redString));
                                     map2.put("1", "");
                                     map2.put("2", "Total");
                                     map2.put("3","");
                                     map2.put("4", "");
                                     map2.put("5", "");
                                     map2.put("6", "");
                                     map2.put("7", ""+group_ttl);
                                     NumberFormat nf1 =new DecimalFormat(".00");
                                     txt_total.setText(""+nf1.format(total));
                                     menu_card_arryList.add(map2);
                                     ttl=0;
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
                       //--------------------------------------------
                    }
                }
                   // Toast.makeText(getApplicationContext(), "check_id: " + check_id, Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_item.setText(attendance_list.get(position).get("1"));
            holder.list_size.setText(attendance_list.get(position).get("2"));
           // holder.list_item_type.setText(attendance_list.get(position).get("7"));
            holder.list_rate.setText(attendance_list.get(position).get("7"));
            if(attendance_list.get(position).get("2").equals("Total"))
            {
                holder.list_size.setTextColor(Color.RED);
                holder.list_rate.setTextColor(Color.RED);
            }
            else
            {
                holder.list_size.setTextColor(Color.BLACK);
                holder.list_rate.setTextColor(Color.BLACK);
            }

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_item, list_size, list_menu_type, list_rate, list_item_type;
            TableLayout layout_gird_position;
            ImageView img_1, img_2, img_3;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_item = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_size = (TextView) itemView.findViewById(R.id.list_d2);
               // this.list_menu_type = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_rate = (TextView) itemView.findViewById(R.id.list_d3);
              //  this.list_item_type = (TextView) itemView.findViewById(R.id.list_d5);

            }
        }
    }
}
