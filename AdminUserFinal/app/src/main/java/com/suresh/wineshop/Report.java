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

public class Report extends AppCompatActivity {
   String check_id="",from_date,to_date,stock_in_radioButton;
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,IMEINumber,db;
    TextView txt_total;
    double total=0;
    int ttl=0;
    int m_TAB_CODE;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String str_compdesc;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");

        Bundle b = getIntent().getExtras();
        try {
            check_id = b.getString("checklist");
            from_date = b.getString("from_date");
            to_date = b.getString("to_date");
           // Show_report(check_id);
            stock_in_radioButton=b.getString("stock_in_radioButton");//Bottles//Cases
            Log.d("check_id",check_id);
        } catch (Exception e) { }
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText(""+str_compdesc+""+"\n"+"Sale Trend Analysis Report");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(to_date);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(from_date);


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
        // ArrayList<HashMap<String, String>> myList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("checklist");


        try {
            connectionClass = new Config();
            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            //qry="select (select brnd_desc as brand from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc as size from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code)) as size_desc,(select size_code from itemmast where item_code=tabreportparameters.item_code)as size_code,(SELECT seq_no FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE = tabreportparameters.ITEM_CODE))as seq_no ,(select liqr_desc from liqrmast where liqr_code IN(SELECT liqr_code FROM ITEMMAST WHERE ITEM_CODE = tabreportparameters.ITEM_CODE))as liqr_desc,(select liqr_code from liqrmast where liqr_code IN(SELECT liqr_code FROM ITEMMAST WHERE ITEM_CODE = tabreportparameters.ITEM_CODE))as liqr_code,rtrim(ltrim(str(sum(tot_sale),12,0))) as net_qty from tabreportparameters where TAB_CODE="+m_TAB_CODE+" and item_code in(select item_code from itemmast where liqr_code in ("+check_id+"))  group by tabreportparameters.ITEM_CODE  having sum(tot_sale) <> 0 order by liqr_desc,brnd_desc,seq_no,net_qty desc";
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else {
                if (stock_in_radioButton.equals("Bottles")) {
                    qry = "select brnd_desc,size_desc,ltrim(str(tot_sale,8,0)) as tot_sale,tabreportparameters.liqr_code,1 as sqno,SEQ_NO from tabreportparameters,itemmast,brndmast,sizemast where tabreportparameters.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code and tabreportparameters.liqr_code in(" + check_id + ") and tab_code=" + m_TAB_CODE + " union select doc_type,'','',liqr_code,0 as sqno,0 AS SEQ_NO from tabreportparameters where liqr_code in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and tot_sale = 0 union select doc_type,'',ltrim(str(tot_sale,8,0)),liqr_code,2 as sqno,9999 AS SEQ_NO from tabreportparameters where liqr_code in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and tot_sale <> 0  order by tabreportparameters.liqr_code,sqno,brnd_desc,SEQ_NO";
                }
                else
                {
                    qry = "select brnd_desc,size_desc,gl_desc as tot_sale,tabreportparameters.liqr_code,1 as sqno,SEQ_NO from tabreportparameters,itemmast,brndmast,sizemast where tabreportparameters.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code and tabreportparameters.liqr_code in(" + check_id + ") and tab_code=" + m_TAB_CODE + " union select doc_type,'','',liqr_code,0 as sqno,0 AS SEQ_NO from tabreportparameters where liqr_code in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and tot_sale = 0 union select doc_type,'',ltrim(str(gl_clbal,8,2)),liqr_code,2 as sqno,9999 AS SEQ_NO from tabreportparameters where liqr_code in(" + check_id + ") and item_code ='' and tab_code=" + m_TAB_CODE + " and tot_sale <> 0  order by tabreportparameters.liqr_code,sqno,brnd_desc,SEQ_NO";
                }

                Log.d("qry", qry);
                PreparedStatement ps = con.prepareStatement(qry);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("brnd_desc", rs.getString("brnd_desc"));
                    map.put("size_desc", rs.getString("size_desc"));
                    map.put("tot_sale", rs.getString("tot_sale"));
                    map.put("sqno", rs.getString("sqno"));

                    menu_card_arryList.add(map);
                    if(rs.getString("sqno").equals("2")) {
                        total = total + Double.parseDouble(rs.getString("tot_sale"));
                        NumberFormat nff = new DecimalFormat(".00");
                        txt_total.setText(""+nff.format(total));
                       // txt_total.setText("" + total);
                    }

                }
                con.close();
            }
        }
        catch(Exception e)
        {

        }
        pbbar.setVisibility(View.GONE);
        Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

        if (attendance_recyclerAdapter != null) {
            attendance_recyclerAdapter.notifyDataSetChanged();
            System.out.println("Adapter " + attendance_recyclerAdapter.toString());
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_item.setText(attendance_list.get(position).get("brnd_desc"));
            holder.list_size.setText(attendance_list.get(position).get("size_desc"));
           // holder.list_item_type.setText(attendance_list.get(position).get("7"));
            holder.list_rate.setText(attendance_list.get(position).get("tot_sale"));

            if(attendance_list.get(position).get("sqno").equals("2"))
            {
                holder.list_item.setTextColor(Color.RED);
                holder.list_rate.setTextColor(Color.RED);

            }
            else if(attendance_list.get(position).get("sqno").trim().equals("0"))
            {
                holder.list_item.setTextColor(Color.BLUE);

            }
            else
            {
                holder.list_size.setTextColor(Color.BLACK);
                holder.list_rate.setTextColor(Color.BLACK);
                holder.list_item.setTextColor(Color.BLACK);
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
