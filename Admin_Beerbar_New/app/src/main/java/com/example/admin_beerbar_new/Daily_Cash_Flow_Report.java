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

public class Daily_Cash_Flow_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,IMEINumber;
    TextView txt_total,txt_debit_total;
    Double debit_total=0.0;
    Double credit_total=0.0;
    Double ttl=0.0;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String date,db,to_date;
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
        setContentView(R.layout.daily_cash_expence_report);

        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber","");
        Bundle b=getIntent().getExtras();
        try
        {
            date=b.getString("date");
        }catch (Exception e)
        {

        }
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Date Wise Cash Flow");
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

        connectionClass = new Config();
        try {
            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else
            {    //qry="select ac_head_id,(select gl_desc+','+plac_desc from glmast,placmast where glmast.plac_code=placmast.plac_code and glmast.ac_head_id=tabreportparameters.ac_head_id)as gl_desc,sum(amount) as m_clbal from tabreportparameters where TAB_CODE ="+m_TAB_CODE+" group by ac_head_id,gl_desc having sum(amount) <>0 order by gl_desc";
                qry="select item_code,gl_desc,case when amount_1 <> 0 then ltrim(str(amount_1,12,2)) else '' end as amount_1,case when amount <> 0 then ltrim(str(amount,12,2)) else '' end as amount,convert(int,tot_sale) as tot_sale from tabreportparameters where tab_code=" + m_TAB_CODE +" and crdr_cd='' order by doc_no";
                Log.d("qry",qry);
                PreparedStatement ps = con.prepareStatement(qry);
                ResultSet rs = ps.executeQuery();
                ttl=0.00;
                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();    

                    map.put("item_code", rs.getString("item_code"));
                    map.put("gl_desc", rs.getString("gl_desc"));
                    map.put("amount_1", rs.getString("amount_1"));
                    map.put("amount", rs.getString("amount"));
                    map.put("tot_sale", rs.getString("tot_sale"));
                    if(!rs.getString("amount").equals("")) {
                        if (rs.getString("item_code").equals("Food Amount") || rs.getString("item_code").equals("Bar Amount") || rs.getString("item_code").equals("CGST Amount") || rs.getString("item_code").equals("SGST Amount") || rs.getString("item_code").equals("VAT Amount")) {
                            ttl = ttl + Double.parseDouble(rs.getString("amount"));
                            Log.d("tttt", "" + ttl);
                        }
                        if (rs.getString("item_code").equals("Less Discount")) {
                            ttl = ttl - Double.parseDouble(rs.getString("amount"));
                            Log.d("tttt", "" + ttl);
                        }
                    }
            //map.put("item_code", "Total");
                    /*if(rs.getString("item_code").equals("VAT Amount"))
                    {
                        map.put("item_code", "Total");
                        NumberFormat nf1 =new DecimalFormat(".00");
                        map.put("Total", ""+nf1.format(ttl));

                    }*/

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

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_cash_flow_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {
            holder.list_d1.setText(attendance_list.get(position).get("item_code"));
            Log.d("iiiii",attendance_list.get(position).get("item_code"));
            holder.list_d2.setText(attendance_list.get(position).get("gl_desc"));
            holder.list_d3.setText(attendance_list.get(position).get("amount_1"));
            holder.list_d4.setText(attendance_list.get(position).get("amount"));
            holder.list_d1.setTextColor(Color.BLACK);
            holder.list_d2.setTextColor(Color.BLACK);
            holder.list_d4.setTextColor(Color.BLACK);
            if(attendance_list.get(position).get("tot_sale").equals("1")||attendance_list.get(position).get("item_code").equals("Total"))
            {
                holder.list_d1.setTextColor(Color.BLUE);
                holder.list_d2.setTextColor(Color.BLUE);
                holder.list_d4.setTextColor(Color.RED);
            }
            if(attendance_list.get(position).get("item_code").equals("Add Sales"))
            {
                NumberFormat nf1 =new DecimalFormat(".00");
                holder.list_d4.setText(""+nf1.format(ttl));

            }
           /* if(attendance_list.get(position).get("item_code").equals("Total"))
            {
                holder.list_d4.setText(attendance_list.get(position).get("Total"));

            }
*/
           /* if(attendance_list.get(position).get("item_code").equals("Food Amount")||attendance_list.get(position).get("item_code").equals("Bar Amount")||attendance_list.get(position).get("item_code").equals("CGST Amount")||attendance_list.get(position).get("item_code").equals("SGST Amount")||attendance_list.get(position).get("item_code").equals("VAT Amount")) {
                ttl =ttl+Double.parseDouble(attendance_list.get(position).get("amount"));
                Log.d("tttt",""+ttl);
            }
            if(attendance_list.get(position).get("item_code").equals("Less Discount"))
            {
                ttl=ttl-Double.parseDouble(attendance_list.get(position).get("amount"));
                Log.d("tttt",""+ttl);

            }*/
           // holder.list_d4.setText(""+ttl);
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
