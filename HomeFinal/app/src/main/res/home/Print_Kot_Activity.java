package com.example.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.HashMap;

public class Print_Kot_Activity extends AppCompatActivity {
    //================Recyclerview 2======================
    ArrayList<HashMap<String, String>> bill_arryList;
    private RecyclerView.LayoutManager layoutManager_bill;
    tbill_recyclerAdapter bill_recyclerAdapter;
    private RecyclerView recycler_bill_list;
    Toolbar toolbar;
    ProgressDialog progressDoalog;
    TextView txt_tbl_bill,txt_kot_print;
    IMEI_Activity m_com;
    IMEI_Activity connectionClass;
    String con_ipaddress ,portnumber,TBNO_DESC,str_compcode,str_compdesc;
    ProgressBar pbbar;
    float m_compcode;
    int m_EVERY_ENTRY_KOT_YN=0;
    HashMap<String, String> map;
    float TBNO_CODE;
    String doc_dt,m_lastdocsrno,flag,query,doc_dt_display,menu_option;
    String m_print_kot_yn;
    String m_print_bill_yn;
    String m_itemcode,doc_no,tab_user_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kot_print_report);
        SharedPreferences st = getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        tab_user_code = st.getString("tab_user_code", "");

        SharedPreferences sp11 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp11.getString("ipaddress", "");
        portnumber = sp11.getString("portnumber", "");
        SharedPreferences ss = getSharedPreferences("COMP_CODE", MODE_PRIVATE);
        str_compcode = ss.getString("COMP_CODE", "");
        str_compdesc = ss.getString("COMP_DESC", "");
        m_compcode = Float.parseFloat(str_compcode);

        SharedPreferences sp2 = getSharedPreferences("Profile_data", MODE_PRIVATE);
        m_EVERY_ENTRY_KOT_YN = sp2.getInt("m_EVERY_ENTRY_KOT_YN", 0);
        Log.d("pppp", "m_EVERY_ENTRY_KOT_YN "+m_EVERY_ENTRY_KOT_YN);

        SharedPreferences sp = getSharedPreferences("HOME_DATA", MODE_PRIVATE);
        TBNO_CODE = Float.parseFloat(sp.getString("TBNO_CODE", ""));
        TBNO_DESC = sp.getString("TBNO_DESC", "");

        SharedPreferences s = getSharedPreferences("MENU_DATA", MODE_PRIVATE);
        doc_dt=s.getString("doc_dt","");
        m_lastdocsrno=s.getString("m_lastdocsrno","");
        doc_dt_display=s.getString("doc_dt_display","");
        menu_option=s.getString("menu_option","");
        flag=s.getString("flag","");
        Bundle b=getIntent().getExtras();
        try
        {
            doc_dt=b.getString("doc_dt");
            m_lastdocsrno=b.getString("m_lastdocsrno");
            doc_dt_display=b.getString("doc_dt_display");
            menu_option=b.getString("menu_option");
            flag=b.getString("flag");
        }catch(Exception e){}
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TextView tbl_bill = (TextView) toolbar.findViewById(R.id.txt_total);//title
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);//date
        TextView txt_option = (TextView) toolbar.findViewById(R.id.txt_option);//menu

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Table : "+TBNO_DESC);
        toolbar_date.setText("Date : "+doc_dt_display);
        txt_option.setText(menu_option);
        TextView txt_hotel_name = (TextView) toolbar.findViewById(R.id.txt_hotel_name);
        txt_hotel_name.setText(str_compdesc);
        txt_hotel_name.setTextColor(0xFFFFFFFF);
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_date.setTextColor(0xFFFFFFFF);
        txt_option.setTextColor(0xFFFFFFFF);
        pbbar = (ProgressBar) findViewById(R.id.pgb);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

            //---------------------Recyclerview 2-----------------------------------------
            bill_arryList = new ArrayList<HashMap<String, String>>();
            recycler_bill_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
            layoutManager_bill = new LinearLayoutManager(Print_Kot_Activity.this, RecyclerView.VERTICAL, false);
            recycler_bill_list.setLayoutManager(layoutManager_bill);
            bill_recyclerAdapter = new tbill_recyclerAdapter(Print_Kot_Activity.this, bill_arryList);
            recycler_bill_list.setAdapter(bill_recyclerAdapter);

            //txt_tbl_bill = (TextView) findViewById(R.id.txt_tbl_bill);
            //------------------------------------------------------------------------------------------
        }

        txt_kot_print = (TextView) findViewById(R.id.txt_kot_print);
        txt_kot_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "INSERT INTO TBNOFROMTABFORKOT(TBNO_CODE,ACTION_TYPE,DOC_SRNO_UPTO,TABUSER_CODE) SELECT DISTINCT TBNO_CODE,1,ISNULL((SELECT MAX(DOC_SRNO) FROM COUNTERSALEITEM A WHERE A.TBNO_CODE=" + TBNO_CODE + " AND A.PRINT_KOT_YN=0 and a.FROM_TAB_YN =1),0),"+tab_user_code+" FROM COUNTERSALEITEM WHERE TBNO_CODE=" + TBNO_CODE + " AND PRINT_KOT_YN=0 and FROM_TAB_YN =1";
                int m_exit_yn=0;
                while (true) {
                    try {
                        Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                        if (con == null) {
                            Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
                        } else {
                            PreparedStatement ps = con.prepareStatement(query);
                            ps.executeUpdate();
                            m_exit_yn=1;
                        }
                    } catch (Exception e) {
                    }
                    if (m_exit_yn==1){
                        break;
                    }
                }
                finish();
            }
        });

          //  query="select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty from countersaleitem where print_kot_yn=0 and doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+TBNO_CODE+" order by doc_srno";


        load_print_kot_data();
    }

    public void load_print_kot_data() {
        // ttl=0;
        // bill_arryList.clear();
        progressDoalog = new ProgressDialog(Print_Kot_Activity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        connectionClass = new IMEI_Activity();
        try {
            progressDoalog.dismiss();
            pbbar.setVisibility(View.VISIBLE);
            //bill_arryList.clear();
            Connection con = connectionClass.CONN(con_ipaddress,portnumber);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                //String query = "select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc+' ['+(select liqr_desc from liqrmast where liqr_code=itemmast.liqr_code)+']' from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,ltrim(str(rate,9,2)) as rate,ltrim(str(item_value,12,2)) as item_value,doc_srno,print_kot_yn,print_yn from countersaleitem where doc_dt = '12/21/2019' and comp_code = 1 and tbno_code <> 0 and tbno_code=20 order by doc_srno";
                //String query = "\n" +
                //       "select * from SURESH_TESTING where TBL_NO='"+TBNO_DESC+"'";
                String qry="select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,doc_srno,LEFT(CONVERT(VARCHAR(10),tran_date,8),5) as entrytime,item_code from countersaleitem where FROM_TAB_YN = 1 AND print_kot_yn=0 and doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+TBNO_CODE+" order by doc_srno";
               // String qry="select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty from countersaleitem where FROM_TAB_YN = 1 AND print_kot_yn=0 and doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+TBNO_CODE+" order by doc_srno";
                Log.d("kot..................",""+qry);
                PreparedStatement ps = con.prepareStatement(qry);
               // PreparedStatement ps = con.prepareStatement("select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty from countersaleitem where FROM_TAB_YN = 1 AND print_kot_yn=0 and doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+TBNO_CODE+" order by doc_srno");
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                txt_kot_print.setVisibility(View.INVISIBLE);
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("ITEM", rs.getString("item_name"));
                    map.put("LSIZE", rs.getString("size_desc"));
                    map.put("QTY", rs.getString("qty"));
                    map.put("DOC_SRNO", rs.getString("doc_srno"));
                    map.put("entrytime", rs.getString("entrytime"));
                    map.put("ITEM_CODE", rs.getString("ITEM_CODE"));
                    txt_kot_print.setVisibility(View.VISIBLE);
                    bill_arryList.add(map);
                }
            }
            pbbar.setVisibility(View.GONE);
            Log.d("bill_arryList_Data", "" + bill_arryList.toString());

            if (bill_recyclerAdapter != null) {
                bill_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + bill_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }

    public class tbill_recyclerAdapter extends RecyclerView.Adapter<tbill_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public tbill_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public tbill_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kot_print_list, parent, false);
            tbill_recyclerAdapter.Pex_ViewHolder viewHolder = new tbill_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final tbill_recyclerAdapter.Pex_ViewHolder holder, final int position) {

            holder.name.setText(attendance_list.get(position).get("ITEM"));
            holder.lsize.setText(attendance_list.get(position).get("LSIZE"));
            holder.qty.setText(attendance_list.get(position).get("QTY"));
            holder.time.setText(attendance_list.get(position).get("entrytime"));
            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doc_no = attendance_list.get(position).get("DOC_SRNO");
                    m_itemcode = attendance_list.get(position).get("ITEM_CODE");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Print_Kot_Activity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.delete);
                    builder.setMessage("Are you sure you want to delete?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int m_exit_yn=0;
                            while (true) {
                                try {
                                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                                    if (con == null) {
                                        Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                                    } else {
                                        PreparedStatement ps = con.prepareStatement("insert into countersaleitemcancel select * from countersaleitem where doc_srno = "+doc_no+" and doc_dt = '"+doc_dt+"' ");
                                        ps.executeUpdate();
                                        ps = con.prepareStatement("delete from countersaleitem where doc_srno = "+doc_no+" and doc_dt = '"+doc_dt+"'");
                                        ps.executeUpdate();
                                        ps = con.prepareStatement("delete from liqrrecipetran where doc_no = "+doc_no+" and doc_dt = '"+doc_dt+"' ");
                                        ps.executeUpdate();
                                        ps = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','COUNTERSALEITEM',1,'0',''");
                                        ps.executeUpdate();
                                        ps = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','LIQRRECIPETRAN',1,'0',''");
                                        ps.executeUpdate();
                                        m_exit_yn=1;
                                    }
                                } catch (Exception e) {
                                }
                                if (m_exit_yn==1){
                                    break;
                                }
                            }
                            bill_arryList.remove(position);
                            bill_recyclerAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            Intent i=new Intent(getApplicationContext(),Print_Kot_Activity.class);
                            startActivity(i);
                            finish();

                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name, qty, lsize,time;
            ImageView img_delete;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.lsize = (TextView) itemView.findViewById(R.id.list_d2);
                this.qty = (TextView) itemView.findViewById(R.id.list_d3);
                this.time = (TextView) itemView.findViewById(R.id.list_d4);
                this.img_delete = (ImageView) itemView.findViewById(R.id.img_delete);

            }
        }
    }

}
