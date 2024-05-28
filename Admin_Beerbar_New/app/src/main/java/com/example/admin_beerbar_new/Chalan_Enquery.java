package com.example.admin_beerbar_new;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Chalan_Enquery extends AppCompatActivity {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date,btn_report;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="";
    ProgressBar pbbar;
    Toolbar toolbar;
    Connection con;
    Config connectionClass;
    SharedPreferences sp;
    String con_ipaddress ,portnumber,db;
    PreparedStatement ps1;
    int m_TAB_CODE;
    String query="";
    TextView txt_back,txt_reprint;
    String doc_no,doc_dt,ac_head_id,daily_rcp_docno;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    static RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    static RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    ArrayList<HashMap<String, String>> data_list;
    AlertDialog dialog;
    Button btn_save,btn_yes,btn_no,btn_cancl;
    //================Recyclerview swap======================
    ArrayList<HashMap<String, String>> swap_arryList;
    private RecyclerView.LayoutManager layoutManager_swap;
    tswap_recyclerAdapter swap_recyclerAdapter;
    private RecyclerView recycler_swap_list;
    HashMap<String, String> smap;
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chalan_enquery);

        //-------------------------------------------------
        SharedPreferences sp1 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Chalan Enquiry");
        // txt_user.setText(str_waiter);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        //---------------------Recyclerview 1-----------------------------------------
        data_list = new ArrayList<HashMap<String, String>>();
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(Chalan_Enquery.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(Chalan_Enquery.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);

        //---------------------Recyclerview swap-----------------------------------------
        swap_arryList = new ArrayList<HashMap<String, String>>();
        recycler_swap_list = (RecyclerView)findViewById(R.id.recycler_swap_list);
        layoutManager_swap = new LinearLayoutManager(Chalan_Enquery.this, RecyclerView.VERTICAL, false);
        recycler_swap_list.setLayoutManager(layoutManager_swap);
        swap_recyclerAdapter=new tswap_recyclerAdapter(Chalan_Enquery.this,swap_arryList);
        recycler_swap_list.setAdapter(swap_recyclerAdapter);

        txt_back=(TextView)findViewById(R.id.txt_back);
        txt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        connectionClass = new Config();
        con = connectionClass.CONN(con_ipaddress, portnumber,db);
        if (con == null) {
            Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
        }
        edt_frm_date=(TextView) findViewById(R.id.edt_frm_date);
        edt_to_date=(TextView) findViewById(R.id.edt_to_date);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        formattedDate = df.format(c);
        edt_to_date.setText(formattedDate);
        edt_frm_date.setText(formattedDate);
        Date  d = Calendar.getInstance().getTime();

        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Temp_frm_date=out.format(d);

        Date dd = Calendar.getInstance().getTime();
        SimpleDateFormat ot = new SimpleDateFormat("MM/dd/yyyy");
        Temp_to_date=ot.format(dd);

        edt_frm_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                menu_card_arryList.clear();
                DatePickerDialog datePickerDialog = new DatePickerDialog(Chalan_Enquery.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edt_frm_date.setText(""+str_day + "/" + str_month + "/" + year);
                                Temp_frm_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                //edt_frm_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);
                                Toast.makeText(Chalan_Enquery.this, ""+str_day + "/" + str_month + "/" + year, Toast.LENGTH_SHORT).show();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        edt_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                menu_card_arryList.clear();
                DatePickerDialog datePickerDialog = new DatePickerDialog(Chalan_Enquery.this,R.style.AppCompatAlertDialogStyle,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edt_to_date.setText(""+str_day + "/" + str_month + "/" + year);
                                Temp_to_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                //edt_to_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);
                                Toast.makeText(Chalan_Enquery.this, ""+str_day + "/" + str_month + "/" + year, Toast.LENGTH_SHORT).show();
                            }
                        }, mYear, mMonth, mDay);

                try {
                    //String sDate1="12/02/2020";
                    String sDate1=edt_frm_date.getText().toString();
                    Date date=new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
                    datePickerDialog.getDatePicker().setMinDate(date.getTime());
                }catch (Exception e)
                {

                }

                //datePickerDialog.getDatePicker().setMinDate(d.getTime());
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        btn_report=(TextView)findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    sale_report(Temp_frm_date,Temp_to_date);
                      if(menu_card_arryList.isEmpty()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(Chalan_Enquery.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Alert");
                        builder.setIcon(R.drawable.warn);
                        builder.setMessage("No Record Found.");

                        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    }
            }
        });

    }
    //=============Alert=================

    //==================================================
    public void sale_report(String temp_frm_date,String temp_to_date) {

        try {
            pbbar.setVisibility(View.VISIBLE);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            } else {

               query="select ac_head_id,doc_no,convert(varchar(10),doc_dt,101) as doc_dt,(select gl_desc from glmast where ac_head_id=CHALANITEM.ac_head_id)as gl_desc,(select party_add1+','+party_add2+','+party_add3 from glmast where ac_head_id=CHALANITEM.ac_head_id)as address,(select plac_desc from placmast where plac_code in (select plac_code from glmast where ac_head_id=CHALANITEM.ac_head_id))as plac_desc,(select plac_code from placmast where plac_code > 0 and plac_code in (select plac_code from glmast where ac_head_id=CHALANITEM.ac_head_id))as plac_Code,CHANGE_CASE_QTY,convert(varchar(10),doc_dt,101),ltrim(str((select sum(basic_amt) from CHALANITEM a where a.doc_no=CHALANITEM.doc_no and a.doc_dt=CHALANITEM.doc_dt)+(select top 1 add_amount from CHALANITEM a where a.doc_no=CHALANITEM.doc_no and a.doc_dt=CHALANITEM.doc_dt),12,2)) as amount,LOCT_CODE from CHALANITEM where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+temp_frm_date+"' and '"+temp_to_date+"' and comp_code=1 and doc_srno = 1 order by doc_dt,doc_no";
                Log.d("qqqq",""+query);
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map = new HashMap<String, String>();
                    String doc_no  =rs.getString("doc_no");
                    String doc_dt  =rs.getString("doc_dt");
                    String gl_desc  =rs.getString("gl_desc");
                    String plac_desc  =rs.getString("plac_desc");
                    String amount  =rs.getString("amount");
                    String ac_head_id  =rs.getString("ac_head_id");

                    map.put("doc_no", doc_no);
                    map.put("doc_dt", doc_dt);
                    map.put("gl_desc", gl_desc);
                    map.put("plac_desc", plac_desc);
                    map.put("amount", amount);
                    map.put("ac_head_id", ac_head_id);
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
    //====================================================
    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }
        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chalan_list_enquiry, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("doc_no"));
            holder.list_d2.setText(attendance_list.get(position).get("doc_dt"));
            holder.list_d3.setText(attendance_list.get(position).get("gl_desc"));
            holder.list_d4.setText(attendance_list.get(position).get("plac_desc"));
            holder.list_d5.setText(attendance_list.get(position).get("amount"));

            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doc_no=attendance_list.get(position).get("doc_no");
                    doc_dt=attendance_list.get(position).get("doc_dt");
                    enquiry_report(doc_no,doc_dt);
                   // menu_card_arryList.remove(position);
                  //  attendance_recyclerAdapter.notifyDataSetChanged();
                }
            });
            holder.lin.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    doc_no=attendance_list.get(position).get("doc_no");
                    doc_dt=attendance_list.get(position).get("doc_dt");
                    ac_head_id=attendance_list.get(position).get("ac_head_id");
                    cashmemo(doc_dt);
                    if(flag==0) {
                        Intent i = new Intent(getApplicationContext(), Enquery_Edit.class);
                        i.putExtra("doc_no", doc_no);
                        i.putExtra("doc_dt", doc_dt);
                        i.putExtra("ac_head_id", ac_head_id);
                        startActivity(i);
                        finish();
                    }
                    return false;
                }
            });
        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }
        public int getItemViewType(int position) {  return position; }
        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2,list_d3,list_d4,list_d5;

            LinearLayout lin;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1= (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);

            }
        }
    }
    //=====Gte Location code ======================================
    void cashmemo(String doc_dt) {
        try {
            flag=0;
            String query = "select doc_dt from cashmemo where cast(convert(varchar(10),doc_dt,101) as datetime)>='"+doc_dt+"' and comp_code=1";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                flag=1;
            }
            if(flag==1)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(Chalan_Enquery.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setIcon(R.drawable.alert);
                alertDialog.setMessage("Cash Memos Of License Holders From This Date Is Already Done Hence Can Not Be Accepted");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        } catch (Exception e) {
        };
    }
    //-------------------------------------------------------------
    //---------------------------------------------------------------------------
    public void enquiry_report(String doc_no,String doc_date) {

        try {
            pbbar.setVisibility(View.VISIBLE);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            } else {
                //query="select wholesale_doc_no,convert(varchar(10),doc_dt,103) as docdate,(select gl_desc+', '+plac_desc from glmast,placmast where ac_head_id=countersaleitem.ac_head_id and glmast.plac_code=placmast.plac_code)as gldesc,ltrim(str(sum(item_value),12,2)) as itemvalue,ac_head_id,doc_dt,LTRIM(STR(max(discount_per),12,2)) as disper,LTRIM(STR(max(discount_AMOUNT),12,2)) as discountamt,LTRIM(STR(max(net_AMOUNT),12,2)) as netamt,'',LTRIM(STR(max(add_AMOUNT),12,2)) as addamt from countersaleitem where doc_dt between '"+temp_frm_date+"' and '"+temp_to_date+"' and tran_type=1 and comp_code=1 and wholesale_doc_no<>0 group by wholesale_doc_no,doc_dt,ac_head_id ORDER BY wholesale_doc_no,doc_dt";
               // query="select doc_no,doc_dt,doc_srno,ac_head_id,chalanitem.item_code,brnd_desc_w,quantity,bottle_qty,case_qty,chalanitem.mrp,basic_rate,basic_amt from chalanitem,itemmast where  itemmast.item_code=chalanitem.item_code and  doc_no="+doc_no+" and comp_code= 1 and doc_dt='"+doc_date+"' ";
                query="select doc_no,convert(varchar(10),doc_dt,103)as doc_dt,CHALANITEM.item_code,((select brnd_desc from brndmast where brnd_code > 0 and brnd_code=itemmast.brnd_code)+' ['+(select liqr_desc from liqrmast where liqr_code=itemmast.liqr_code)+']')as brnd_name,size_desc,ltrim(str(quantity,12,2)),bottle_qty,CHALANITEM.case_qty,CHALANITEM.mrp,basic_rate,basic_amt,doc_srno,add_amount from CHALANITEM,itemmast,sizemast where CHALANITEM.item_code=itemmast.item_code and sizemast.size_code=itemmast.size_code and doc_dt='"+doc_date+"' and doc_no ="+doc_no+" and comp_code=1 order by doc_srno";
                Log.d("eqqqq",""+query);
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                swap_arryList.clear();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map = new HashMap<String, String>();
                    String item_code  =rs.getString("item_code");
                   // String brnd_desc_w  =rs.getString("brnd_desc_w");
                    String size_desc  =rs.getString("size_desc");
                    String brnd_desc  =rs.getString("brnd_name");
                    String case_qty  =rs.getString("case_qty");
                    String bottle_qty  =rs.getString("bottle_qty");
                    String doc_nno  =rs.getString("doc_no");
                    String mrp  =rs.getString("mrp");
                   // String ac_head_id  =rs.getString("ac_head_id");

                    map.put("item_code", item_code);
                    map.put("size_desc", size_desc);
                    map.put("brnd_desc", brnd_desc);
                    map.put("case_qty", case_qty);
                    map.put("bottle_qty", bottle_qty);
                    map.put("doc_no", doc_nno);
                    map.put("mrp", mrp);
                //    map.put("ac_head_id", ac_head_id);
                    swap_arryList.add(map);
                }

            }
            pbbar.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + swap_arryList.toString());
            if (swap_recyclerAdapter != null) {
                swap_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + swap_recyclerAdapter.toString());
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }
    //---------------------------------------------------------
    public class tswap_recyclerAdapter extends RecyclerView.Adapter<tswap_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public tswap_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swap_chalan_enqry_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {
            // holder.list_d1.setText(attendance_list.get(position).get("item_code"));
            holder.list_d1.setText(attendance_list.get(position).get("brnd_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("size_desc"));
            holder.list_d3.setText(attendance_list.get(position).get("case_qty"));
            holder.list_d4.setText(attendance_list.get(position).get("bottle_qty"));
            holder.list_d5.setText(attendance_list.get(position).get("mrp"));

        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3,list_d4,list_d5;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                 this.list_d1= (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);


            }
        }
    }


}