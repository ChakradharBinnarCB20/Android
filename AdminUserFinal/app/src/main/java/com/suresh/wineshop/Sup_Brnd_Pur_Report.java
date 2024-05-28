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
import java.util.ArrayList;
import java.util.HashMap;

public class Sup_Brnd_Pur_Report extends AppCompatActivity {
   String check_id="",from_date,to_date,stock_in_radioButton,ac_head_id,Temp_frm_date,Temp_to_date;
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
    PreparedStatement ps1;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sup_report);
        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");

        Bundle b = getIntent().getExtras();
        try {
            ac_head_id = b.getString("ac_head_id");
            check_id = b.getString("checklist");
            from_date = b.getString("from_date");
            Temp_frm_date = b.getString("qfrom_date");
            to_date = b.getString("to_date");
            Temp_to_date = b.getString("qto_date");

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
        toolbar_title.setText(""+str_compdesc+""+"\n"+"Supplier Wise Brand Wise Purchase");
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
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else {

                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET DOC_TYPE = (SELECT GL_DESC FROM GLMAST WHERE GLMAST.AC_HEAD_ID = TABREPORTPARAMETERS.AC_HEAD_ID AND TAB_CODE = "+m_TAB_CODE+") WHERE TAB_CODE="+m_TAB_CODE+" ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET GL_DESC = (SELECT GL_DESC FROM GLMAST WHERE GLMAST.AC_HEAD_ID = TABREPORTPARAMETERS.AC_HEAD_ID AND TAB_CODE = "+m_TAB_CODE+") WHERE TAB_CODE="+m_TAB_CODE+" AND LEN(LTRIM(RTRIM(ITEM_CODE))) = 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET GL_DESC = (SELECT BRND_DESC FROM ITEMMAST,BRNDMAST WHERE ITEMMAST.ITEM_CODE = TABREPORTPARAMETERS.ITEM_CODE AND ITEMMAST.BRND_CODE = BRNDMAST.BRND_CODE AND TAB_CODE = "+m_TAB_CODE+") WHERE TAB_CODE="+m_TAB_CODE+" AND LEN(LTRIM(RTRIM(ITEM_CODE))) <> 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET DIS_AMOUNT = (SELECT SEQ_NO FROM ITEMMAST,SIZEMAST WHERE ITEMMAST.ITEM_CODE = TABREPORTPARAMETERS.ITEM_CODE AND ITEMMAST.SIZE_CODE = SIZEMAST.SIZE_CODE AND TAB_CODE = "+m_TAB_CODE+") WHERE TAB_CODE="+m_TAB_CODE+" AND LEN(LTRIM(RTRIM(ITEM_CODE))) <> 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET MAIN_LIQR_DESC = (SELECT SIZE_DESC FROM ITEMMAST,SIZEMAST WHERE ITEMMAST.ITEM_CODE = TABREPORTPARAMETERS.ITEM_CODE AND ITEMMAST.SIZE_CODE = SIZEMAST.SIZE_CODE AND TAB_CODE = "+m_TAB_CODE+") WHERE TAB_CODE="+m_TAB_CODE+" AND LEN(LTRIM(RTRIM(ITEM_CODE))) <> 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(GL_DESC,gl_clbal,gl_opbal,doc_no,ac_head_id,DOC_TYPE,MAIN_LIQR_DESC,DIS_AMOUNT,TAB_CODE) SELECT GL_DESC,sum(gl_clbal),sum(gl_opbal),DOC_NO,ac_head_id,DOC_TYPE,'ZZZZZZZZZ',99999999,"+m_TAB_CODE+" from TABREPORTPARAMETERS WHERE tab_code = "+m_TAB_CODE+" and LEN(LTRIM(RTRIM(ITEM_CODE))) <> 0 group by GL_DESC,AC_HEAD_ID,DOC_TYPE,DOC_NO");
                ps1.executeUpdate();
                qry = "select CASE WHEN MAIN_LIQR_DESC <> 'ZZZZZZZZZ' THEN GL_DESC ELSE 'Brand Wise Total==>' END AS HEAD1,CASE WHEN MAIN_LIQR_DESC <> 'ZZZZZZZZZ' THEN MAIN_LIQR_DESC ELSE '' END AS SIZEDESC,case when FLOOR(GL_CLBAL) <> 0 then ltrim(str(FLOOR(GL_CLBAL))) else '' end + case when GL_CLBAL-FLOOR(GL_CLBAL) <> 0 then REPLACE(ltrim(RTRIM(REPLACE(str(GL_CLBAL-FLOOR(GL_CLBAL),12,4), '0',' '))),' ','0') else '' end as CASES,CASE WHEN GL_OPBAL <> 0 THEN LTRIM(STR(GL_OPBAL)) ELSE '' END AS BOTTLES FROM TABREPORTPARAMETERS WHERE TAB_CODE="+m_TAB_CODE+" ORDER BY DOC_NO,DOC_TYPE,GL_DESC,DIS_AMOUNT";
                Log.d("qqqqry", qry);
                ps1 = con.prepareStatement(qry);
                ResultSet rs = ps1.executeQuery();

                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("HEAD1", rs.getString("HEAD1"));
                    map.put("SIZEDESC", rs.getString("SIZEDESC"));
                    map.put("BOTTLES", rs.getString("BOTTLES"));
                    map.put("CASES", rs.getString("CASES"));


                    menu_card_arryList.add(map);


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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sup_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_item.setText(attendance_list.get(position).get("HEAD1"));
            holder.list_size.setText(attendance_list.get(position).get("SIZEDESC"));
            holder.list_botal.setText(attendance_list.get(position).get("BOTTLES"));
            holder.list_case.setText(attendance_list.get(position).get("CASES"));

          if(attendance_list.get(position).get("SIZEDESC").trim().equals(""))
            {
                holder.list_item.setTextColor(Color.BLUE);
                holder.list_size.setTextColor(Color.BLUE);
                holder.list_botal.setTextColor(Color.BLUE);
                holder.list_case.setTextColor(Color.BLUE);
            }
            else
            {   holder.list_item.setTextColor(Color.BLACK);
                holder.list_size.setTextColor(Color.BLACK);
                holder.list_botal.setTextColor(Color.BLACK);
                holder.list_case.setTextColor(Color.BLACK);
            }

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_item, list_size, list_botal, list_rate, list_case;
            TableLayout layout_gird_position;
            ImageView img_1, img_2, img_3;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_item = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_size = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_botal = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_case = (TextView) itemView.findViewById(R.id.list_d4);
              //  this.list_item_type = (TextView) itemView.findViewById(R.id.list_d5);

            }
        }
    }
}
