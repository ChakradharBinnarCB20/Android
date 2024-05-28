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

public class Food_Group_Total_Report extends AppCompatActivity {
   String check_id="",from_date,to_date,db;
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,IMEINumber;
    TextView txt_total;
    double total=0;
    int ttl=0;
    int m_TAB_CODE;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String str_compdesc;
    int srno=0;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_group_food_report);
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

        } catch (Exception e) { }
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText(""+str_compdesc+""+"\n"+"Sale Register-Food");
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

                    qry = "SELECT '' AS ITEM_CODE,GL_DESC,LTRIM(STR(AMOUNT_1)) AS QTY,LTRIM(STR(AMOUNT)) AS VALUE,ITEM_CODE,0 AS SEQ FROM TABREPORTPARAMETERS WHERE TAB_CODE="+m_TAB_CODE+" AND GL_DESC <> ' ' UNION SELECT '',ITEM_CODE,'Group Total' as GL_DESC,LTRIM(STR(AMOUNT)),ITEM_CODE,1 AS SEQ FROM TABREPORTPARAMETERS WHERE TAB_CODE="+m_TAB_CODE+" AND GL_DESC = ' ' ORDER BY ITEM_CODE,SEQ";


                Log.d("qry", qry);
                PreparedStatement ps = con.prepareStatement(qry);
                ResultSet rs = ps.executeQuery();
                srno=0;
                while (rs.next()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    if (!rs.getString("QTY").equals("Group Total")){
                        ++srno;
                        map.put("srno", ""+srno);
                        total = total + (Double.parseDouble(rs.getString("VALUE")));
                    }

                    map.put("GL_DESC", rs.getString("GL_DESC"));
                    map.put("QTY", rs.getString("QTY"));
                    map.put("VALUE", rs.getString("VALUE"));


                    menu_card_arryList.add(map);


                }
                NumberFormat nf1 =new DecimalFormat(".00");
                txt_total.setText(""+nf1.format(total));
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_group_total_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("srno"));
            holder.list_d2.setText(attendance_list.get(position).get("GL_DESC"));
            holder.list_d3.setText(attendance_list.get(position).get("QTY"));
            holder.list_d4.setText(attendance_list.get(position).get("VALUE"));
            holder.list_d2.setTextColor(Color.BLACK);
            holder.list_d3.setTextColor(Color.BLACK);
            holder.list_d4.setTextColor(Color.BLACK);
            if(attendance_list.get(position).get("QTY").equals("Group Total"))
            {
                holder.list_d2.setTextColor(Color.BLUE);
                holder.list_d3.setTextColor(Color.BLUE);
                holder.list_d4.setTextColor(Color.RED);
            }
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3, list_d4, list_item_type;
            TableLayout layout_gird_position;
            ImageView img_1, img_2, img_3;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
               // this.list_menu_type = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);

            }
        }
    }
}
