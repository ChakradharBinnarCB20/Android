package com.suresh.wineshop;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Date_Brand_Wise_Sale_Report extends AppCompatActivity {
   String check_id="";
    String frm_date,category,to_date,query_type,chksummary,stock_wise_radioButton,stock_in_radioButton;
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc, str_chk_brand_name ,str_chk_size;
    TextView txt_total,txt_sale_total;
    Double total=0.0;
    Double sale_total=0.0;
    Double group_ttl=0.0;
    int txt_per=0;
    String qry="",IMEINumber,systemDate,db;
    Connection con;
    PreparedStatement ps1;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    Toolbar toolbar;
    int m_TAB_CODE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_brand_wise_sale_report);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
       // lin_summary_hide=(LinearLayout)findViewById(R.id.lin_summary_hide);
       // lin_heading_hide=(LinearLayout)findViewById(R.id.lin_heading_hide);
        Bundle b=getIntent().getExtras();
        try
        {
            str_chk_brand_name=b.getString("str_chk_brand_name");
            str_chk_size=b.getString("str_chk_size");
            check_id=b.getString("checklist");
            frm_date=b.getString("frm_date");
            to_date=b.getString("to_date");
            category=b.getString("category");
            query_type=b.getString("query_type");
            Log.d("check_id",check_id);
        }catch(Exception e) { }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        systemDate = df.format(c);

        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_category = (TextView) toolbar.findViewById(R.id.toolbar_category);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc +"\n"+"Date Wise Brand Wise Sale");
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
        pbbar = (ProgressBar)findViewById(R.id.pgb);
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
        txt_total=(TextView)findViewById(R.id.txt_total);
        txt_sale_total=(TextView)findViewById(R.id.txt_sale_total);


         // Log.d("check_id....",check_id);
           connectionClass = new Config();
          try {
                    NumberFormat nf = new DecimalFormat(".00");
                   pbbar.setVisibility(View.VISIBLE);
                   con = connectionClass.CONN(con_ipaddress, portnumber,db);
                   if (con == null) {
                      Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
                        }
                       else
                           {
                               if(query_type.equals("brand")) {
                                   if (str_chk_brand_name.equals("1")) {
                                       qry = "select brnd_desc,size_desc,ltrim(str(tot_sale,8,0)) as tot_sale,ltrim(str(AMOUNT_1,12,2)) as AMOUNT_1,ltrim(str(amount,12,2)) as amount,tabreportparameters.liqr_code,1 as sqno,SEQ_NO from tabreportparameters,itemmast,brndmast,sizemast where tabreportparameters.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code and tabreportparameters.liqr_code in(" + check_id + ") and tab_code=" + m_TAB_CODE + " union select doc_type,'','','','',liqr_code,0 as sqno,0 AS SEQ_NO from tabreportparameters where liqr_code in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and amount = 0 union select doc_type,'',ltrim(str(tot_sale,8,0)),'',ltrim(str(amount,12,2)),liqr_code,2 as sqno,9999 AS SEQ_NO from tabreportparameters where liqr_code in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and amount <> 0  order by tabreportparameters.liqr_code,sqno,brnd_desc,SEQ_NO";

                                   } else {
                                       qry = "select brnd_desc,size_desc,ltrim(str(tot_sale,8,0)) as tot_sale,ltrim(str(AMOUNT_1,12,2)) as AMOUNT_1,ltrim(str(amount,12,2)) as amount,tabreportparameters.liqr_code,1 as sqno,SEQ_NO from tabreportparameters,itemmast,brndmast,sizemast where tabreportparameters.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code and tabreportparameters.liqr_code in(" + check_id + ") and tab_code=" + m_TAB_CODE + " union select doc_type,'','','','',liqr_code,0 as sqno,0 AS SEQ_NO from tabreportparameters where liqr_code in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and amount = 0 union select doc_type,'',ltrim(str(tot_sale,8,0)),'',ltrim(str(amount,12,2)),liqr_code,2 as sqno,9999 AS SEQ_NO from tabreportparameters where liqr_code in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and amount <> 0  order by tabreportparameters.liqr_code,sqno,SEQ_NO,brnd_desc";
                                   }
                               }
                               else
                               {
                                   if (str_chk_brand_name.equals("1")) {
                                       qry = "select brnd_desc,size_desc,ltrim(str(tot_sale,8,0)) as tot_sale,ltrim(str(AMOUNT_1,12,2)) as AMOUNT_1,ltrim(str(amount,12,2)) as amount,tabreportparameters.gl_desc,1 as sqno,SEQ_NO from tabreportparameters,itemmast,brndmast,sizemast where tabreportparameters.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code and tabreportparameters.ac_head_id in(" + check_id + ") and tab_code=" + m_TAB_CODE + " union select gl_desc,'','','','',gl_desc,0 as sqno,0 AS SEQ_NO from tabreportparameters where ac_head_id in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and amount = 0 union select doc_type,'',ltrim(str(tot_sale,8,0)),'',ltrim(str(amount,12,2)),gl_desc,2 as sqno,9999 AS SEQ_NO from tabreportparameters where ac_head_id in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and amount <> 0  order by tabreportparameters.gl_desc,sqno,brnd_desc,SEQ_NO";
                                   }
                                   else
                                   {
                                       qry = "select brnd_desc,size_desc,ltrim(str(tot_sale,8,0)) as tot_sale,ltrim(str(AMOUNT_1,12,2)) as AMOUNT_1,ltrim(str(amount,12,2)) as amount,tabreportparameters.gl_desc,1 as sqno,SEQ_NO from tabreportparameters,itemmast,brndmast,sizemast where tabreportparameters.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code and tabreportparameters.ac_head_id in(" + check_id + ") and tab_code=" + m_TAB_CODE + " union select gl_desc,'','','','',gl_desc,0 as sqno,0 AS SEQ_NO from tabreportparameters where ac_head_id in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and amount = 0 union select doc_type,'',ltrim(str(tot_sale,8,0)),'',ltrim(str(amount,12,2)),gl_desc,2 as sqno,9999 AS SEQ_NO from tabreportparameters where ac_head_id in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and amount <> 0  order by tabreportparameters.gl_desc,sqno,SEQ_NO,brnd_desc";
                                   }
                                   }
                               // qry="select brnd_desc,size_desc,tot_sale,AMOUNT_1,amount,tabreportparameters.liqr_code,0 as sqno,SEQ_NO from tabreportparameters,itemmast,brndmast,sizemast where tabreportparameters.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code and tabreportparameters.liqr_code in(1,5) and tab_code=4 union select distinct doc_type,'',0,0,amount,liqr_code,1 as sqno,9999 AS SEQ_NO from tabreportparameters where liqr_code in(1,5) and item_code ='' and tab_code=4 order by tabreportparameters.liqr_code,sqno,brnd_desc,SEQ_NO";

                                 Log.d("qry",qry);
                                 PreparedStatement ps = con.prepareStatement(qry);
                                 ResultSet rs = ps.executeQuery();
                                 while (rs.next())
                                 {
                                     HashMap<String, String> map = new HashMap<String, String>();
                                     map.put("brnd_desc", rs.getString("brnd_desc"));
                                     map.put("size_desc", rs.getString("size_desc"));
                                    //map.put("tot_sale", rs.getString("tot_sale"));
                                     //NumberFormat nn =new DecimalFormat(".00");
                                     map.put("tot_sale", rs.getString("tot_sale"));
                                     map.put("AMOUNT_1", rs.getString("AMOUNT_1"));
                                    // map.put("AMOUNT_1", rs.getString("AMOUNT_1"));
                                     map.put("amount", rs.getString("amount"));
                                     map.put("sqno", rs.getString("sqno"));

                                     if(rs.getString("sqno").equals("2")) {
                                         total = total + Double.parseDouble(rs.getString("amount"));
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
                     con.close();
                         } catch (Exception e) {
               Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                }
               }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.closing_stock_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_item.setText(attendance_list.get(position).get("brnd_desc"));
            holder.list_size.setText(attendance_list.get(position).get("size_desc"));
            holder.list_sale.setText(attendance_list.get(position).get("tot_sale"));
            holder.list_rate.setText(attendance_list.get(position).get("AMOUNT_1"));
            holder.list_value.setText(attendance_list.get(position).get("amount"));
            if(attendance_list.get(position).get("sqno").equals("2"))
            {
                holder.list_item.setTextColor(Color.RED);
                holder.list_sale.setTextColor(Color.RED);
                holder.list_value.setTextColor(Color.RED);
            }
            else
            {
                holder.list_item.setTextColor(Color.BLACK);
                holder.list_sale.setTextColor(Color.BLACK);
                holder.list_value.setTextColor(Color.BLACK);
            }

            if(attendance_list.get(position).get("sqno").equals("0"))
            {
                holder.list_item.setTextColor(Color.BLUE);

            }


        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_item, list_size, list_sale, list_rate, list_value;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_item = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_size = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_sale = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_rate = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_value = (TextView) itemView.findViewById(R.id.list_d5);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        qry="";
    }
}
