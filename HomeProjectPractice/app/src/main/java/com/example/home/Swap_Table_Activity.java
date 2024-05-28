package com.example.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;

public class Swap_Table_Activity extends AppCompatActivity {
    //================Recyclerview 2======================
    ArrayList<HashMap<String, String>> bill_arryList;
    private RecyclerView.LayoutManager layoutManager_bill;
    tbill_recyclerAdapter bill_recyclerAdapter;
    private RecyclerView recycler_bill_list;

    //================Recyclerview swap======================
    ArrayList<HashMap<String, String>> swap_arryList;
    private RecyclerView.LayoutManager layoutManager_swap;
    tswap_recyclerAdapter swap_recyclerAdapter;
    private RecyclerView recycler_swap_list;
    Toolbar toolbar;
    ProgressDialog progressDoalog;
    TextView txt_tbl_bill;
    IMEI_Activity m_com;
    IMEI_Activity connectionClass;
    String con_ipaddress ,portnumber,TBNO_DESC,str_compcode,str_compdesc;
    ProgressBar pbbar;
    float m_compcode;
    HashMap<String, String> map;
    HashMap<String, String> smap;
    float TBNO_CODE;
    Spinner sp_table;
    String m_table_name,m_table_code;
    Button btn_shift,btn_unshift,btn_swap;
    String doc_dt,m_lastdocsrno,flag,doc_dt_display,menu_option,m_item_code,m_doc_srno;
    String tab_user_code;
    String m_tempfile ="TEMPSWAP";
    Float m_tbusercode;
    int m_bill_no=0;
    int n_bill_no=0;
    int m_fullswapyn=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_table);

        SharedPreferences s = getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        tab_user_code = s.getString("tab_user_code", "");
        m_tbusercode=Float.parseFloat(s.getString("tab_user_code", ""));
        Log.d("tab_user_code",tab_user_code);

        SharedPreferences sp11 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp11.getString("ipaddress", "");
        portnumber = sp11.getString("portnumber", "");
        SharedPreferences ss = getSharedPreferences("COMP_CODE", MODE_PRIVATE);
        str_compcode = ss.getString("COMP_CODE", "");
        str_compdesc = ss.getString("COMP_DESC", "");
        m_compcode = Float.parseFloat(str_compcode);
        m_tempfile = m_tempfile + Math.round(m_tbusercode);

        SharedPreferences sp = getSharedPreferences("HOME_DATA", MODE_PRIVATE);
        TBNO_CODE = Float.parseFloat(sp.getString("TBNO_CODE", ""));
        TBNO_DESC = sp.getString("TBNO_DESC", "");
        Bundle b=getIntent().getExtras();
        try
        {
            doc_dt=b.getString("doc_dt");
            m_lastdocsrno=b.getString("m_lastdocsrno");
            doc_dt_display=b.getString("doc_dt_display");
            menu_option=b.getString("menu_option");
            flag=b.getString("flag");
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "105"+e, Toast.LENGTH_SHORT).show();
        }
        //------------------------Toolbar-------------------------------------------
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            layoutManager_bill = new LinearLayoutManager(Swap_Table_Activity.this, RecyclerView.VERTICAL, false);
            recycler_bill_list.setLayoutManager(layoutManager_bill);
            bill_recyclerAdapter = new tbill_recyclerAdapter(Swap_Table_Activity.this, bill_arryList);
            recycler_bill_list.setAdapter(bill_recyclerAdapter);

           // txt_tbl_bill = (TextView) findViewById(R.id.txt_tbl_bill);
            //------------------------------------------------------------------------------------------
            //---------------------Recyclerview swap-----------------------------------------
            swap_arryList = new ArrayList<HashMap<String, String>>();
            recycler_swap_list = (RecyclerView) findViewById(R.id.recycler_swap_list);
            layoutManager_swap = new LinearLayoutManager(Swap_Table_Activity.this, RecyclerView.VERTICAL, false);
            recycler_swap_list.setLayoutManager(layoutManager_swap);
            swap_recyclerAdapter=new tswap_recyclerAdapter(Swap_Table_Activity.this,swap_arryList);
            recycler_swap_list.setAdapter(swap_recyclerAdapter);

          //  txt_tbl_bill = (TextView) findViewById(R.id.txt_tbl_bill);
            //------------------------------------------------------------------------------------------
        }
        sp_table=(Spinner) findViewById(R.id.sp_table);
        btn_shift=(Button)findViewById(R.id.btn_shift);
        btn_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_shift.setVisibility(View.INVISIBLE);
                m_fullswapyn=1;
                btn_swap.setVisibility(View.VISIBLE);
                btn_unshift.setVisibility(View.VISIBLE);
                int m_exit_yn=0;
                while(true) {
                    bill_arryList.clear();
                    bill_recyclerAdapter.notifyDataSetChanged();
                    swap_arryList.clear();
                    swap_recyclerAdapter.notifyDataSetChanged();
                    try {
                        Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                        if (con == null) {
                            Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                        } else {
                            PreparedStatement ps = con.prepareStatement("select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,doc_srno,print_kot_yn,print_yn,LEFT(CONVERT(VARCHAR(10),tran_date,8),5) as entrytime,item_code from countersaleitem where doc_dt = '" + doc_dt + "' and comp_code = " + m_compcode + " and tbno_code <> 0 and tbno_code=" + TBNO_CODE + " order by doc_srno desc");
                            ResultSet rs = ps.executeQuery();

                            //ArrayList data1 = new ArrayList();
                            while (rs.next()) {
                                smap = new HashMap<String, String>();
                                smap.put("ITEM", rs.getString("item_name"));
                                smap.put("LSIZE", rs.getString("size_desc"));
                                smap.put("QTY", rs.getString("qty"));
                                smap.put("entrytime", rs.getString("entrytime"));
                                smap.put("DOC_SRNO", rs.getString("doc_srno"));
                                smap.put("PRINT_KOT_YN", rs.getString("print_kot_yn"));
                                smap.put("PRINT_YN", rs.getString("print_yn"));
                                smap.put("ITEM_CODE", rs.getString("item_code"));


                                //-------------------------------------------
                                swap_arryList.add(smap);

                            }
                            PreparedStatement ps1 = con.prepareStatement("delete from "+m_tempfile+"");
                            ps1.executeUpdate();

                            PreparedStatement ps2 = con.prepareStatement("insert into "+m_tempfile+" select * from countersaleitem where doc_dt = '" + doc_dt + "' and comp_code = " + m_compcode + " and tbno_code <> 0 and tbno_code=" + TBNO_CODE + " ");
                            ps2.executeUpdate();
                            m_exit_yn=1;

                        }
                    }catch (Exception e){ Toast.makeText(getApplicationContext(), "164"+e, Toast.LENGTH_SHORT).show();}
                    if(m_exit_yn==1)
                    {
                        break;
                    }
                }


            }
        });

        btn_swap=(Button)findViewById(R.id.btn_swap);
        btn_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int m_exit_yn=0;
                while(true) {
                    try {
                        Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                        if (con == null) {
                            Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                        } else {
                               PreparedStatement ps1 = con.prepareStatement("select isnull(max(bill_no),0) as bill_no from countersaleitem where doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+m_table_code+" ");
                               ResultSet rs = ps1.executeQuery();
                               while (rs.next()) {
                                    n_bill_no = rs.getInt("bill_no");
                                    }
                            if (m_bill_no >0 && n_bill_no==0 && m_fullswapyn ==1) {
                                PreparedStatement ps = con.prepareStatement("update countersaleitem set tbno_code = " + m_table_code + ", from_tbno_code = case when len(from_tbno_code) = 0 then ltrim(str(tbno_code)) else from_tbno_code + ',' + ltrim(str(tbno_code)) end where ltrim(str(doc_srno)) + '-' + item_code + '-' + ltrim(str(tbno_code)) in(select ltrim(str(doc_srno)) + '-' + item_code + '-' + ltrim(str(tbno_code))from "+m_tempfile+")");
                                ps.executeUpdate();

                            }
                            else
                            {
                                PreparedStatement ps = con.prepareStatement("update countersaleitem set bill_no = 0, tbno_code = " + m_table_code + ", from_tbno_code = case when len(from_tbno_code) = 0 then ltrim(str(tbno_code)) else from_tbno_code + ',' + ltrim(str(tbno_code)) end where ltrim(str(doc_srno)) + '-' + item_code + '-' + ltrim(str(tbno_code)) in(select ltrim(str(doc_srno)) + '-' + item_code + '-' + ltrim(str(tbno_code))from "+m_tempfile+")");
                                ps.executeUpdate();

                            }
                            m_exit_yn=1;

                            //=====(8/12/2022)=====================
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "247" + e, Toast.LENGTH_SHORT).show();
                    }
                    if (m_exit_yn == 1) {
                        break;
                    }
                }
                Intent i =new Intent(getApplicationContext(),Home.class);
                startActivity(i);
                finish();
            }
        });
        btn_unshift=(Button)findViewById(R.id.btn_unshift);
        btn_unshift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_shift.setVisibility(View.VISIBLE);
                m_fullswapyn=0;
                btn_swap.setVisibility(View.INVISIBLE);
                btn_unshift.setVisibility(View.INVISIBLE);
                int m_exit_yn=0;
                while(true) {
                    bill_arryList.clear();
                    bill_recyclerAdapter.notifyDataSetChanged();
                    swap_arryList.clear();
                    swap_recyclerAdapter.notifyDataSetChanged();
                    try {
                        Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                        if (con == null) {
                            Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                        } else {
                            PreparedStatement ps = con.prepareStatement("select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,doc_srno,print_kot_yn,print_yn,LEFT(CONVERT(VARCHAR(10),tran_date,8),5) as entrytime,item_code from countersaleitem where doc_dt = '" + doc_dt + "' and comp_code = " + m_compcode + " and tbno_code <> 0 and tbno_code=" + TBNO_CODE + " order by doc_srno desc");
                            ResultSet rs = ps.executeQuery();

                            //ArrayList data1 = new ArrayList();
                            while (rs.next()) {
                                map = new HashMap<String, String>();
                                map.put("ITEM", rs.getString("item_name"));
                                map.put("LSIZE", rs.getString("size_desc"));
                                map.put("QTY", rs.getString("qty"));
                                map.put("entrytime", rs.getString("entrytime"));
                                map.put("DOC_SRNO", rs.getString("doc_srno"));
                                map.put("PRINT_KOT_YN", rs.getString("print_kot_yn"));
                                map.put("PRINT_YN", rs.getString("print_yn"));
                                map.put("ITEM_CODE", rs.getString("item_code"));


                                //-------------------------------------------
                                bill_arryList.add(map);
                            }
                            PreparedStatement ps1 = con.prepareStatement("delete from " + m_tempfile + "");
                            ps1.executeUpdate();
                            m_exit_yn=1;
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "247" + e, Toast.LENGTH_SHORT).show();
                    }
                    if (m_exit_yn == 1) {
                        break;
                    }
                }

            }
        });
        btn_swap=(Button)findViewById(R.id.btn_swap);
       // new load_spinner_table().execute();
        load_bill_data();
        new load_spinner_table().execute();

    }

    public void load_bill_data() {
        // ttl=0;
        // bill_arryList.clear();
        progressDoalog = new ProgressDialog(Swap_Table_Activity.this);
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

                PreparedStatement ps = con.prepareStatement("select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,doc_srno,print_kot_yn,print_yn,LEFT(CONVERT(VARCHAR(10),tran_date,8),5) as entrytime,item_code,isnull((select convert(int,max(bill_no)) from countersaleitem a where a.doc_dt = '"+doc_dt+"' and a.comp_code = "+m_compcode+" and a.tbno_code <> 0 and a.tbno_code="+TBNO_CODE+"),0) as bill_no from countersaleitem where doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+TBNO_CODE+" order by doc_srno desc");
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("ITEM", rs.getString("item_name"));
                    map.put("LSIZE", rs.getString("size_desc"));
                    map.put("QTY", rs.getString("qty"));
                    map.put("entrytime", rs.getString("entrytime"));
                    map.put("DOC_SRNO", rs.getString("doc_srno"));
                    map.put("PRINT_KOT_YN", rs.getString("print_kot_yn"));
                    map.put("PRINT_YN", rs.getString("print_yn"));
                    map.put("ITEM_CODE", rs.getString("item_code"));
                    m_bill_no = rs.getInt("bill_no");
                    //-------------------------------------------
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
            Toast.makeText(this, "308" + e, Toast.LENGTH_SHORT).show();
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
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swap_table, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            // holder.contact_list_id.setText(attendance_list.get(position).get("A"));
            holder.name.setText(attendance_list.get(position).get("ITEM"));
            holder.lsize.setText(attendance_list.get(position).get("LSIZE"));
            holder.qty.setText(attendance_list.get(position).get("QTY"));
            holder.time.setText(attendance_list.get(position).get("entrytime"));
            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    m_item_code=attendance_list.get(position).get("ITEM_CODE");
                    m_doc_srno=attendance_list.get(position).get("DOC_SRNO");
                    int m_exit_yn=0;
                    while(true) {
                        try {
                            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                            if (con == null) {
                                Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                            } else {
                                PreparedStatement ps = con.prepareStatement("insert into " + m_tempfile + " select * from countersaleitem where doc_srno = " + m_doc_srno + " and item_code = " + m_item_code + " and tbno_code = " + TBNO_CODE + " ");
                                ps.executeUpdate();
                                m_exit_yn=1;

                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "350" + e, Toast.LENGTH_SHORT).show();
                        }
                     if(m_exit_yn==1)
                     {
                         break;
                     }
                    }
                    smap= new HashMap<String, String>();
                    smap.put("ITEM",attendance_list.get(position).get("ITEM"));
                    smap.put("LSIZE",attendance_list.get(position).get("LSIZE"));
                    smap.put("QTY",attendance_list.get(position).get("QTY"));
                    smap.put("entrytime",attendance_list.get(position).get("entrytime"));
                    smap.put("DOC_SRNO",m_doc_srno);
                    smap.put("PRINT_KOT_YN", attendance_list.get(position).get("print_kot_yn"));
                    smap.put("PRINT_YN", attendance_list.get(position).get("print_yn"));
                    smap.put("ITEM_CODE", m_item_code);

                    swap_arryList.add(smap);
                    swap_recyclerAdapter.notifyDataSetChanged();

                   // attendance_list.remove(position);
                    bill_arryList.remove(position);
                    bill_recyclerAdapter.notifyDataSetChanged();

                    btn_swap.setVisibility(View.VISIBLE);
                    btn_unshift.setVisibility(View.VISIBLE);
                    m_fullswapyn=0;

                    if(bill_arryList.size()==0)
                    {
                        btn_shift.setVisibility(View.INVISIBLE);
                        m_fullswapyn=1;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name, qty,  lsize,time;
            ImageView action;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.lsize = (TextView) itemView.findViewById(R.id.list_d2);
                this.qty = (TextView) itemView.findViewById(R.id.list_d3);
                this.time = (TextView) itemView.findViewById(R.id.list_d4);
                this.action = (ImageView) itemView.findViewById(R.id.imp_swap);

            }
        }
    }

    public class tswap_recyclerAdapter extends RecyclerView.Adapter<tswap_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public tswap_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swap_table_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.name.setText(attendance_list.get(position).get("ITEM"));
            holder.lsize.setText(attendance_list.get(position).get("LSIZE"));
            holder.qty.setText(attendance_list.get(position).get("QTY"));
            holder.time.setText(attendance_list.get(position).get("entrytime"));
            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    m_item_code=attendance_list.get(position).get("ITEM_CODE");
                    m_doc_srno=attendance_list.get(position).get("DOC_SRNO");
                    m_fullswapyn=0;
                    int m_exit_yn=0;
                    while(true) {
                        try {
                            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                            if (con == null) {
                                Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                            } else {
                                PreparedStatement ps = con.prepareStatement("delete from "+m_tempfile+" where doc_srno = "+m_doc_srno+" and item_code = "+m_item_code+" and tbno_code = "+TBNO_CODE+" ");
                                ps.executeUpdate();
                                m_exit_yn=1;

                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "350" + e, Toast.LENGTH_SHORT).show();
                        }
                        if(m_exit_yn==1)
                        {
                            break;
                        }
                    }
                    map= new HashMap<String, String>();
                    map.put("ITEM",attendance_list.get(position).get("ITEM"));
                    map.put("LSIZE",attendance_list.get(position).get("LSIZE"));
                    map.put("QTY",attendance_list.get(position).get("QTY"));
                    map.put("entrytime",attendance_list.get(position).get("entrytime"));
                    map.put("DOC_SRNO", m_doc_srno);
                    map.put("PRINT_KOT_YN", attendance_list.get(position).get("print_kot_yn"));
                    map.put("PRINT_YN", attendance_list.get(position).get("print_yn"));
                    map.put("ITEM_CODE",m_item_code);

                    bill_arryList.add(map);
                    bill_recyclerAdapter.notifyDataSetChanged();

                    //attendance_list.remove(position);
                    swap_arryList.remove(position);
                    swap_recyclerAdapter.notifyDataSetChanged();
                    btn_shift.setVisibility(View.VISIBLE);
                    if(swap_arryList.size()==0)
                    {
                        btn_swap.setVisibility(View.INVISIBLE);
                        btn_unshift.setVisibility(View.INVISIBLE);
                    }


                }

            });

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name, qty,  lsize,time;
            ImageView action;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.lsize = (TextView) itemView.findViewById(R.id.list_d2);
                this.qty = (TextView) itemView.findViewById(R.id.list_d3);
                this.time = (TextView) itemView.findViewById(R.id.list_d4);
                this.action = (ImageView) itemView.findViewById(R.id.imp_swap);

            }
        }
    }
    public class load_spinner_table extends AsyncTask<String, String, String> {
        List<Map<String, String>> sp_data = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
                try {
                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                    if (con == null) {
                        Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                    } else {
                        PreparedStatement ps = con.prepareStatement("if object_id('"+m_tempfile+"') is not null begin drop table " + m_tempfile + " end");
                        ps.executeUpdate();

                    }
                } catch (Exception e) {
                }


            try {

                Connection con = connectionClass.CONN(con_ipaddress,portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    //String query="select size_code,size_desc from sizemast";
                    String query = "SELECT TBNO_CODE,TBNO_DESC FROM TBNOMAST WHERE TBNO_CODE <> "+TBNO_CODE+" AND TBNO_CODE IN(SELECT TBNO_CODE FROM TABRIGHTS WHERE TABUSER_CODE="+tab_user_code+")";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));

                        sp_data.add(data);


                    }


                    String query1 = "SELECT * INTO "+m_tempfile+" FROM COUNTERSALEITEM WHERE TBNO_CODE = 9999999999";
                    PreparedStatement ps1 = con.prepareStatement(query1);
                    ps1.executeUpdate();

                }  //z = "Success";


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getApplicationContext(), sp_data, R.layout.spin, from, views);
            sp_table.setAdapter(spnr_data);
            sp_table.setSelection(0);
            sp_table.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    m_table_name = (String) obj.get("A");
                    m_table_code = (String) obj.get("B");
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                PreparedStatement ps = con.prepareStatement("drop table "+m_tempfile+"");
                ps.executeUpdate();

            }
        }catch (Exception e){}


    }
}
