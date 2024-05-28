package com.example.admin_beerbar_new;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SearchView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.example.admin_beerbar_new.Class.TransparentProgressDialog;

public class Transfer_Note extends AppCompatActivity {
    SearchView searchView;
    String Temp_date,Query_date,m_frm_loct_desc,m_frm_loct_code,m_frm_code,m_to_loct_desc,m_to_loct_code,m_to_code,m_group_code,m_group_desc;
   // ConnectionClass connectionClass;
    ProgressBar pbbar;
    Toolbar toolbar;
    TextView txt_save,txt_cancel;
    String query="";
    TextView edt_date;
    int lastpos=0;
    int m_sale_loct_yn=0;
    TransparentProgressDialog pd;
    ArrayList<HashMap<String, String>> data_list;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    static RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    static RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    HashMap<String, String> dmap;
    //================Recyclerview swap======================
    ArrayList<HashMap<String, String>> swap_arryList;
    private RecyclerView.LayoutManager layoutManager_swap;
    tswap_recyclerAdapter swap_recyclerAdapter;
    private RecyclerView recycler_swap_list;
    HashMap<String, String> smap;
    EditText edt_search;
    TextView btn_search,btn_proceed,btn_greed_proceed;
    ArrayList<HashMap<String, String>> mBackupData;
    String user, cqty="",ac_head_id;
    int c,btl_qty;

    SharedPreferences sp;
    String formattedDate, str_month="",str_day,systemDate,m_purdate,str_radio;
    RadioButton rb,radio_bottle,radio_cases;
    Spinner sp_frm_loc,sp_to_loc,sp_group;
    ProgressDialog progressDoalog;
    final Context context = Transfer_Note.this;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String search_key="";
    PreparedStatement ps1;
    double m_TAB_CODE;
    String qry="";
    String s="";
    int dd1=0;
    String con_ipaddress ,portnumber,str_waiter,tab_user_code,db;

    SharedPreferences spf;
    String doc_date="";
    Double doc_no=0.0;
    int m_counter_sale_doc_srno=0;
    int m_counter_return_doc_srno=0;
    int doc_srno=0;
    int doc_srno_p=0;
    int doc_srno_n=0;
    int m_changeyn=0;
    int m_alert=0;
    int m_doc_srno=0;
    Connection con;
    Config connectionClass;
    String num;
    PreparedStatement ps;
    String a,item_code;
    int mYear, mMonth, mDay;
    TextView txt_cnm,txt_cp;
    //==================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_note_report);
        mBackupData = new ArrayList<>();
        txt_cancel = (TextView) findViewById(R.id.txt_cancel);
        txt_save = (TextView) findViewById(R.id.txt_save);
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        connectionClass = new Config();
        pd = new TransparentProgressDialog(Transfer_Note.this, R.drawable.hourglass);
        //---------------------Recyclerview 1-----------------------------------------
        data_list = new ArrayList<HashMap<String, String>>();
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(Transfer_Note.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(Transfer_Note.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        //---------------------Recyclerview swap-----------------------------------------
        swap_arryList = new ArrayList<HashMap<String, String>>();
        recycler_swap_list = (RecyclerView)findViewById(R.id.recycler_swap_list);
        layoutManager_swap = new LinearLayoutManager(Transfer_Note.this, RecyclerView.VERTICAL, false);
        recycler_swap_list.setLayoutManager(layoutManager_swap);
        swap_recyclerAdapter=new tswap_recyclerAdapter(Transfer_Note.this,swap_arryList);
        recycler_swap_list.setAdapter(swap_recyclerAdapter);
        //-------------------------------------------------
        Bundle b=getIntent().getExtras();
        try
        {
            user=b.getString("gl_desc");
            ac_head_id=b.getString("ac_head_id");
            Log.d("ac_head_id",ac_head_id+" "+user);
        }
        catch (Exception e) { }

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
        toolbar_title.setText("Chalan");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        con = connectionClass.CONN(con_ipaddress, portnumber,db);

        if (con == null) {
            Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
        }
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Transfer_Note.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.warn);
                builder.setMessage("Are You Sure You Want To Save The Record ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                save_data();
                                pd.dismiss();
                            }
                        }, 2000);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        pd.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                reload_group();
                                pd.dismiss();
                            }
                        }, 2000);
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });
        edt_search=(EditText)findViewById(R.id.edt_search);
        btn_search=(TextView) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_key="";
                if(!edt_search.getText().toString().equals("")) {
                    search_key = edt_search.getText().toString();
                }
                sale_report();
                attendance_recyclerAdapter.notifyDataSetChanged();

            }
        });
        Date cc = Calendar.getInstance().getTime();
        SimpleDateFormat dff = new SimpleDateFormat("dd/MM/yyyy");
        systemDate = dff.format(cc);
        //date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        formattedDate = df.format(c);
        System.out.println("Today Date => " + formattedDate);
        edt_date=(TextView)findViewById(R.id.edt_date);
        edt_date.setText(formattedDate);
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Temp_date=out.format(d);
        Query_date=Temp_date;
        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(Transfer_Note.this,R.style.AppCompatAlertDialogStyle,
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
                                edt_date.setText(""+str_day + "/" + str_month + "/" + year);

                                Temp_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                Query_date=Temp_date;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        //----------spinner--------------------------------------------------------


       // new load_to_loc_data().execute();
        //==============Proceed ========================
        btn_proceed=(TextView)findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_sale_loct_yn=0;
                try {
                    String query = "select count(*) as count from ipadmast where sale_loct_code=" + m_frm_loct_code + "";
                    ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        m_sale_loct_yn=Integer.parseInt(rs.getString("count"));
                    }
                    if(m_sale_loct_yn>0)
                    {
                        ps = con.prepareStatement("select convert(varchar(10),max(doc_dt),103) as datetime from countersaleitem");
                        ResultSet r = ps.executeQuery();
                        while (r.next()) {
                            edt_date.setText(r.getString("datetime"));
                        }
                        radio_bottle.setChecked(true);
                        String[] arrSplit = edt_date.getText().toString().split("/");
                        String dd = arrSplit[0];
                        String mm = arrSplit[1];
                        String yyyy = arrSplit[2];
                        Query_date=mm+"/"+dd+"/"+yyyy;
                        Log.d("Query_date",Query_date);
                    }
                }catch(Exception e){}
                //---------------------------
                sp_frm_loc.setEnabled(false);
                sp_to_loc.setEnabled(false);
                edt_date.setEnabled(false);
                btn_proceed.setVisibility(View.GONE);

                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        group_data();
                        pd.dismiss();
                    }
                }, 2000);
            }
        });
        //==============================================================
        txt_save.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 pd.show();
                 Handler handler = new Handler();
                 handler.postDelayed(new Runnable() {
                     public void run() {
                         save_data();
                         pd.dismiss();
                     }
                 }, 2000);
             }
         });
    }
    void save_data() {
        m_counter_sale_doc_srno = 0;
        m_counter_return_doc_srno = 0;
        Double d = 0.0;
        doc_no = 0.0;
        doc_srno = 0;
        doc_srno_p = 1;
        doc_srno_n = 1;
        int x = 0;
        int m_alert = 0;
        for (int k = 0; k < data_list.size(); k++) {
            map = (HashMap) data_list.get(k);
            map.get("diff");
            if (!map.get("diff").equals("")) {
                x = 1;
                break;
            }
        }
        if (x == 0) {
            m_alert = 1;
        } else {
            // Save Record in Countersale and CountersaleReturn Table
            if (m_sale_loct_yn > 0) {
                try {
                    ps1 = con.prepareStatement("select isnull(max(doc_srno),0) as doc_srno from countersaleitem where doc_dt='" + Query_date + "'");
                    ResultSet rr = ps1.executeQuery();
                    while (rr.next()) {
                        m_counter_sale_doc_srno = rr.getInt(1);
                    }
                    ps1 = con.prepareStatement("select isnull(max(doc_srno),0) as doc_srno from countersalereturnitem where doc_dt='" + Query_date + "'");
                    ResultSet r = ps1.executeQuery();
                    while (r.next()) {
                        m_counter_return_doc_srno = r.getInt(1);
                    }
                } catch (Exception e) {
                }
                for (int k = 0; k < data_list.size(); k++) {
                    map = (HashMap) data_list.get(k);
                    map.get("diff");
                    if (!map.get("diff").equals("")) {
                        d = Double.parseDouble(map.get("diff"));
                        if (d > 0) {
                            try {
                                m_counter_sale_doc_srno = m_counter_sale_doc_srno + 1;
                                ps1 = con.prepareStatement("UPDATE DOC_NO SET COUNTER_SALE_DOCNO=COUNTER_SALE_DOCNO+1 where '" + Query_date + "' BETWEEN from_year and to_year");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("SELECT COUNTER_SALE_DOCNO FROM DOC_NO where '" + Query_date + "' BETWEEN from_year and to_year");
                                ResultSet s = ps1.executeQuery();
                                while (s.next()) {
                                    ps1 = con.prepareStatement("INSERT INTO COUNTERSALEITEM (DOC_NO,DOC_DT,ITEM_CODE,QTY,RATE,ITEM_VALUE,DOC_SRNO,LOCT_CODE,STOCK_METHOD,COMP_CODE,MRP,CASHMEMO_PRICE) VALUES(" + s.getString("COUNTER_SALE_DOCNO") + ",'" + Query_date + "','" + map.get("item_code") + "'," + map.get("diff") + "," + map.get("SALE_RATE") + "," + map.get("diff") + " * " + map.get("SALE_RATE") + " , " + m_counter_sale_doc_srno + "," + m_frm_loct_code + ",1,1," + map.get("MRP") + "," + map.get("CASHMEMO_PRICE") + ")");
                                    ps1.executeUpdate();
                                    //=====Procedure Call ==========12/10/2021
                                    ps1 = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+s.getString("COUNTER_SALE_DOCNO")+",'"+Query_date+"','COUNTERSALEITEM',1,'0',''");
                                    ps1.executeUpdate();
                                }

                            } catch (Exception e) {
                            }
                        } else if (d < 0) {

                            try {
                                double qty = Double.parseDouble(map.get("diff"));
                                qty = Math.abs(qty);
                                m_counter_return_doc_srno = m_counter_return_doc_srno + 1;
                                ps1 = con.prepareStatement("UPDATE DOC_NO SET COUNTER_RETURN_DOCNO=COUNTER_RETURN_DOCNO+1 where '" + Query_date + "' BETWEEN from_year and to_year");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("SELECT COUNTER_RETURN_DOCNO FROM DOC_NO where '" + Query_date + "' BETWEEN from_year and to_year");
                                ResultSet s = ps1.executeQuery();
                                while (s.next()) {
                                    ps1 = con.prepareStatement("INSERT INTO COUNTERSALERETURNITEM (DOC_NO,DOC_DT,ITEM_CODE,QTY,RATE,ITEM_VALUE,DOC_SRNO,LOCT_CODE,STOCK_METHOD,COMP_CODE) VALUES(" + s.getString("COUNTER_RETURN_DOCNO") + ",'" + Query_date + "','" + map.get("item_code") + "'," + qty + "," + map.get("SALE_RATE") + "," + qty + " * " + map.get("SALE_RATE") + " , " + m_counter_return_doc_srno + "," + m_frm_loct_code + ",1,1)");
                                    ps1.executeUpdate();
                                    //=====Procedure Call ==========12/10/2021
                                    ps1 = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+s.getString("COUNTER_SALE_DOCNO")+",'"+Query_date+"','COUNTERSALERETURNITEM',1,'0',''");
                                    ps1.executeUpdate();
                                }

                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
            // Data save in Transfer Note Table
            else {
                try {
                    ps1 = con.prepareStatement("update tabreportparameters set qty_in_cases='',dis_amount=0,amount_2=0 where tab_code=" + m_TAB_CODE + "");
                    ps1.executeUpdate();
                } catch (Exception e) {
                }

                for (int k = 0; k < data_list.size(); k++) {
                    map = (HashMap) data_list.get(k);
                    map.get("diff");
                    if (!map.get("diff").equals("")) {
                        d = Double.parseDouble(map.get("diff"));
                        if (d > 0) {
                            doc_srno = doc_srno_p++;
                            m_frm_code = m_frm_loct_code;
                            m_to_code = m_to_loct_code;
                        } else if (d < 0) {
                            doc_srno = doc_srno_n++;
                            m_frm_code = m_to_loct_code;
                            m_to_code = m_frm_loct_code;
                        }
                        try {
                            String q = "update tabreportparameters set doc_type='" + m_frm_code + "', crdr_cd='" + m_to_code + "', dis_amount=" + map.get("diff") + ",amount_2=" + doc_srno + " where item_code='" + map.get("item_code") + "' and TAB_CODE=" + m_TAB_CODE + "";
                            ps1 = con.prepareStatement(q);
                            ps1.executeUpdate();
                        } catch (Exception e) {
                        }
                    }
                }
                try {
                    // Positive Record saving eg. Godown to Counter
                    ps1 = con.prepareStatement("update tabreportparameters set qty_in_cases = (select LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,(dis_amount/doc_no)))))) + '.' + LTRIM(STR(abs(dis_amount-(doc_no* CONVERT(INT,(CONVERT(FLOAT,(dis_amount/doc_no))))))))) where tab_code=" + m_TAB_CODE + " and dis_amount <> 0");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("select count(*) as cnt from tabreportparameters where dis_amount>0");
                    ResultSet rs = ps1.executeQuery();
                    int cnt = 0;
                    while (rs.next()) {
                        cnt = Integer.parseInt(rs.getString("cnt"));
                    }
                    if (cnt > 0) {

                        ps1 = con.prepareStatement("update doc_no set transfernote_docno=transfernote_docno+1 where '" + Query_date + "' BETWEEN from_year and to_year");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("INSERT INTO TRANSFERNOTE(DOC_NO,DOC_DT,DOC_SRNO,ITEM_CODE,FROM_LOCT_CODE,TO_LOCT_CODE,STOCK_METHOD,COMP_CODE,CASE_QTY,CASE_TRANSFER_QTY,BOTTLE_QTY,RATE,ITEM_VALUE) SELECT (select transfernote_docno from doc_no where '" + Query_date + "' BETWEEN from_year and to_year),'" + Query_date + "',AMOUNT_2,ITEM_CODE,DOC_TYPE,CRDR_CD,1,1,DOC_NO,convert(money,QTY_IN_CASES),DIS_AMOUNT,GL_OPBAL,GL_OPBAL*DIS_AMOUNT FROM TABREPORTPARAMETERS WHERE dis_amount >0 and TAB_CODE=" + m_TAB_CODE + " ORDER BY AMOUNT_2");
                        ps1.executeUpdate();

                          ps1 = con.prepareStatement("select transfernote_docno from doc_no where '" + Query_date + "' BETWEEN from_year and to_year");
                        ResultSet r = ps1.executeQuery();
                        while (r.next()) {
                            //=====Procedure Call ==========12/10/2021
                            ps1 = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+r.getString("transfernote_docno")+",'"+Query_date+"','TRANSFERNOTE',1,'0',''");
                            ps1.executeUpdate();
                        }
                    }
                    //Negative Rcord Save eg. Counter To Godown
                    ps1 = con.prepareStatement("select count(*) as cnt from tabreportparameters where dis_amount<0");
                    ResultSet r = ps1.executeQuery();
                    int cn = 0;
                    while (r.next()) {
                        cn = Integer.parseInt(r.getString("cnt"));
                    }
                    if (cn > 0) {
                        ps1 = con.prepareStatement("update doc_no set transfernote_docno=transfernote_docno+1 where '" + Query_date + "' BETWEEN from_year and to_year");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("INSERT INTO TRANSFERNOTE(DOC_NO,DOC_DT,DOC_SRNO,ITEM_CODE,FROM_LOCT_CODE,TO_LOCT_CODE,STOCK_METHOD,COMP_CODE,CASE_QTY,CASE_TRANSFER_QTY,BOTTLE_QTY,RATE,ITEM_VALUE)SELECT (select transfernote_docno from doc_no where '" + Query_date + "' BETWEEN from_year and to_year),'" + Query_date + "',AMOUNT_2,ITEM_CODE,DOC_TYPE,CRDR_CD,1,1,DOC_NO,abs(convert(money,QTY_IN_CASES)),abs(DIS_AMOUNT),GL_OPBAL,GL_OPBAL*abs(DIS_AMOUNT) FROM TABREPORTPARAMETERS WHERE dis_amount <0 and TAB_CODE=" + m_TAB_CODE + " ORDER BY AMOUNT_2");
                        ps1.executeUpdate();

                          ps1 = con.prepareStatement("select transfernote_docno from doc_no where '" + Query_date + "' BETWEEN from_year and to_year");
                        ResultSet rss = ps1.executeQuery();
                        while (rss.next()) {
                            //=====Procedure Call ==========12/10/2021
                            ps1 = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+rss.getString("transfernote_docno")+",'"+Query_date+"','TRANSFERNOTE',1,'0',''");
                            ps1.executeUpdate();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                }

            }
        }
        if (m_alert == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Transfer_Note.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Alert");
            builder.setIcon(R.drawable.warn);
            builder.setMessage("No Records Found For Updation");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            reload_group();
                            pd.dismiss();
                        }
                    }, 2000);
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
        } else {
            //-----------------Alert Dialog---------------------------
            AlertDialog.Builder builder = new AlertDialog.Builder(Transfer_Note.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Success");
            builder.setIcon(R.drawable.sucess);
            builder.setMessage("Record Save Successfully, Do You Want To Continue ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            reload_group();
                            pd.dismiss();
                        }
                    }, 2000);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }
   //====================================================
    public  void reload_group()
    {
        m_changeyn = 1;
        data_list.clear();
        menu_card_arryList.clear();
        attendance_recyclerAdapter.notifyDataSetChanged();
        swap_arryList.clear();
        swap_recyclerAdapter.notifyDataSetChanged();
        btn_proceed.setEnabled(true);
        btn_proceed.setVisibility(View.VISIBLE);

    }
    public void group_data()
    {
        try {
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
            } else {
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code= "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,brnd_code,TAB_CODE,liqr_code,amount_5,gl_clbal,gl_opbal,amount)select item_code,brnd_code,"+m_TAB_CODE+",liqr_code,size_code,sale_price,mrp,cashmemo_price from itemmast ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = isnull((select sum(op_qty) from opitstok where loct_code = " +m_frm_loct_code+  " and item_code=tabreportparameters.item_code),0) where tab_code = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + isnull((select sum(bottle_qty+free_qty) from puritem where ltrim(item_code)<>'' and loct_code = " +m_frm_loct_code+ " and item_code=tabreportparameters.item_code),0) where tab_code = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + isnull((select sum(bottle_qty) from transfernote where ltrim(item_code)<>'' and to_loct_code = " +m_frm_loct_code+ " and item_code=tabreportparameters.item_code),0) where tab_code = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + isnull((select sum(qty) from countersalereturnitem where sale_type = 0 and ltrim(item_code)<>'' and loct_code = "  +m_frm_loct_code+ " and item_code=tabreportparameters.item_code),0) where tab_code = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 - isnull((select sum(qty+breakage_qty) from countersaleitem where sale_type=0 and ltrim(item_code)<>'' and loct_code = "  +m_frm_loct_code+ " and item_code=tabreportparameters.item_code),0) where tab_code = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 - isnull((select sum(qty+breakage_qty) from provisionalsaleitem where sale_type=0 and ltrim(item_code)<>'' and loct_code = " +m_frm_loct_code+ " and item_code=tabreportparameters.item_code),0) where tab_code = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 - isnull((select sum(qty) from HOMEDELIVERYSALEITEM where sale_type=0 and ltrim(item_code)<>'' and loct_code = " +m_frm_loct_code+ " and item_code=tabreportparameters.item_code),0) where tab_code = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 - isnull((select sum(bottle_qty) from transfernote where ltrim(item_code)<>'' and from_loct_code = " +m_frm_loct_code+ " and item_code=tabreportparameters.item_code),0) where tab_code = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                //====to loct stock
                // from loct is Counter
                if(m_sale_loct_yn>0)
                {
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6=isnull((select sum(qty) from countersaleitem where sale_type = 0 and ac_head_id = 0 and tabreportparameters.item_code=countersaleitem.item_code and doc_dt='"+Query_date+"'),0) where tab_code = "+m_TAB_CODE+"");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6=amount_6-isnull((select sum(qty) from countersalereturnitem where sale_type = 0 and tabreportparameters.item_code=countersalereturnitem.item_code and doc_dt='"+Query_date+"'),0) where tab_code = "+m_TAB_CODE+"");
                    ps1.executeUpdate();
                }
                // from loct is Other than Counter
                else {
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = isnull((select sum(op_qty) from opitstok where loct_code = " + m_to_loct_code + " and item_code=tabreportparameters.item_code),0) where tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = amount_6 + isnull((select sum(bottle_qty+free_qty) from puritem where ltrim(item_code)<>'' and loct_code = " + m_to_loct_code + " and item_code=tabreportparameters.item_code),0) where tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = amount_6 + isnull((select sum(bottle_qty) from transfernote where ltrim(item_code)<>'' and to_loct_code = " + m_to_loct_code + " and item_code=tabreportparameters.item_code),0) where tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = amount_6 + isnull((select sum(qty) from countersalereturnitem where sale_type = 0 and ltrim(item_code)<>'' and loct_code = " + m_to_loct_code + " and item_code=tabreportparameters.item_code),0) where tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = amount_6 - isnull((select sum(qty+breakage_qty) from countersaleitem where sale_type=0 and ltrim(item_code)<>'' and loct_code = " + m_to_loct_code + " and item_code=tabreportparameters.item_code),0) where tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = amount_6 - isnull((select sum(qty+breakage_qty) from provisionalsaleitem where sale_type=0 and ltrim(item_code)<>'' and loct_code = " + m_to_loct_code + " and item_code=tabreportparameters.item_code),0) where tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = amount_6 - isnull((select sum(qty) from HOMEDELIVERYSALEITEM where sale_type=0 and ltrim(item_code)<>'' and loct_code = " + m_to_loct_code + " and item_code=tabreportparameters.item_code),0) where tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = amount_6 - isnull((select sum(bottle_qty) from transfernote where ltrim(item_code)<>'' and from_loct_code = " + m_to_loct_code + " and item_code=tabreportparameters.item_code),0) where tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    //====to loct stock
                }
                    ps1 = con.prepareStatement("delete from tabreportparameters where (amount_1 <= 0 and amount_6 <=0) and tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set doc_no = isnull((select case_qty from sizemast where size_code=tabreportparameters.amount_5),0) where tab_code = " + m_TAB_CODE + "");
                    ps1.executeUpdate();

            }
            sale_report();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
        }
    }
    public void sale_report() {
        try {
            pbbar.setVisibility(View.VISIBLE);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            } else {
                       ps1 = con.prepareStatement("update tabreportparameters set qty_in_cases =(select LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,(AMOUNT_1/doc_no)))))) + '.' + LTRIM(STR(abs(AMOUNT_1-(doc_no* CONVERT(INT,(CONVERT(FLOAT,(AMOUNT_1/doc_no))))))))) where tab_code="+m_TAB_CODE+" ");
                       ps1.executeUpdate();
                      /* ps1 = con.prepareStatement("update tabreportparameters set doc_type = (select LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,(AMOUNT_6/doc_no)))))) + '.' + LTRIM(STR(abs(AMOUNT_6-(doc_no* CONVERT(INT,(CONVERT(FLOAT,(AMOUNT_6/doc_no))))))))) where tab_code="+m_TAB_CODE+"");
                       ps1.executeUpdate();*/
                      // query="SELECT item_code,brnd_desc,size_desc,qty_in_cases as computer_stock,'' as actual_stock,'' as diff,doc_type as to_loct_stock,case_qty as caseqty,GL_CLBAL AS SALE_RATE,GL_OPBAL AS MRP,AMOUNT AS CASHMEMO_PRICE from tabreportparameters,brndmast,sizemast where tabreportparameters.brnd_code=brndmast.brnd_code and tabreportparameters.amount_5=sizemast.size_code and tab_code="+ m_TAB_CODE +" and brnd_desc like '" + search_key + "%'order by brnd_desc,seq_no";
                       query="SELECT item_code,brnd_desc,size_desc,convert(money,qty_in_cases) - isnull((select sum(qty) from tabtempstok where tabtempstok.item_code= tabreportparameters.item_code and tab_code=1 group by item_code),0) as computer_stock,'' as actual_stock,'' as diff,doc_type as to_loct_stock,case_qty as caseqty,GL_CLBAL AS SALE_RATE,GL_OPBAL AS MRP,AMOUNT AS CASHMEMO_PRICE from tabreportparameters,brndmast,sizemast where tabreportparameters.brnd_code=brndmast.brnd_code and tabreportparameters.amount_5=sizemast.size_code and tab_code="+ m_TAB_CODE +" and brnd_desc like '" + search_key + "%'order by brnd_desc,seq_no";

                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                data_list.clear();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map = new HashMap<String, String>();
                    map.put("item_code", rs.getString("item_code"));
                    map.put("brnd_desc", rs.getString("brnd_desc"));
                    map.put("size_desc", rs.getString("size_desc"));
                    map.put("computer_stock", rs.getString("computer_stock"));
                    map.put("to_loct_stock", rs.getString("to_loct_stock"));
                    map.put("actual_stock", rs.getString("actual_stock"));
                    map.put("diff", rs.getString("diff"));
                    map.put("caseqty", rs.getString("caseqty"));
                    map.put("SALE_RATE", rs.getString("SALE_RATE"));
                    map.put("MRP", rs.getString("MRP"));
                    map.put("CASHMEMO_PRICE", rs.getString("CASHMEMO_PRICE"));
                    menu_card_arryList.add(map);
                    data_list.add(map);
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
        LayoutInflater  inflater;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }
        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trns_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            holder.list_d2.setText(attendance_list.get(position).get("brnd_desc"));
            holder.list_d1.setText(attendance_list.get(position).get("size_desc"));
           // NumberFormat n = new DecimalFormat(".00");
           // holder.list_d3.setText(n.format(Double.parseDouble(attendance_list.get(position).get("computer_stock"))));
            holder.list_d3.setText(attendance_list.get(position).get("computer_stock"));
           /* if(!attendance_list.get(position).get("actual_stock").equals("")) {
                holder.list_d4.setText(attendance_list.get(position).get("actual_stock"));
                holder.list_d5.setText(attendance_list.get(position).get("diff"));
            }*/
            holder.list_d4.setText(attendance_list.get(position).get("actual_stock"));
           /* if(!attendance_list.get(position).get("btl").toString().equals("")) {
                holder.list_d6.setText(attendance_list.get(position).get("btl"));
            }*/
            holder.list_d5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (holder.list_d4.getText().toString().equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Transfer_Note.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Warning");
                        builder.setIcon(R.drawable.fail);
                        builder.setMessage("No Record Found To Add");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    } else {
                        double d1 = 0, d2 = 0;
                        if (!holder.list_d4.getText().toString().equals("")) {
                            d1 = Double.parseDouble(holder.list_d4.getText().toString());
                        }
                        d2 = Double.parseDouble(holder.list_d3.getText().toString());
                        if (d1 > d2) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Transfer_Note.this, R.style.AppCompatAlertDialogStyle);
                            builder.setTitle("Warning");
                            builder.setIcon(R.drawable.fail);
                            builder.setMessage("Stock Not Available.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    holder.list_d4.setText("");
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();
                                    holder.list_d4.setText("");
                                }
                            });
                            builder.setCancelable(false);
                            builder.show();
                        } else {
                            cqty="";
                            //--------------------------
                            num=String.valueOf(d1);
                           // NumberFormat n = new DecimalFormat("00.00");
                          //  num = n.format(s);
                            num=num.replace("0","");
                            try {
                                if(num.equals(".")) {  }
                                else if(num.startsWith(".")) {
                                    a = num.split("\\.")[1];
                                    dd1 = Integer.parseInt(a);
                                   // holder.list_d6.setText("" + dd1);
                                }else if(!num.startsWith(".")&& num.length()==2) {
                                    a = num.split("\\.")[0];
                                    dd1 = Integer.parseInt(a);
                                    cqty = attendance_list.get(position).get("caseqty").replace("0","");
                                    cqty = cqty.replace(".","");
                                    c = Integer.parseInt(cqty);
                                   // holder.list_d6.setText("" + dd1*c);
                                    dd1= dd1*c;
                                }
                                else {
                                    String a = num.split("\\.")[0];
                                    dd1 = Integer.parseInt(a);
                                    String b = num.split("\\.")[1];
                                    int dd2 = Integer.parseInt(b);
                                     cqty = attendance_list.get(position).get("caseqty").replace("0","");
                                     cqty = cqty.replace(".","");
                                     c = Integer.parseInt(cqty);
                                     btl_qty = c * dd1;
                                    btl_qty = btl_qty + dd2;
                                   // holder.list_d6.setText("" + btl_qty);
                                    dd1=btl_qty;
                                    Log.d("ssss",""+dd1);
                                }
                            }catch (Exception e){
                                Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                            }
                            //--------------------------
                            smap = new HashMap<String, String>();
                            item_code= attendance_list.get(position).get("item_code");
                            smap.put("item_code", item_code);
                            smap.put("brnd_desc", attendance_list.get(position).get("brnd_desc"));
                            smap.put("size_desc", attendance_list.get(position).get("size_desc"));
                            smap.put("computer_stock", attendance_list.get(position).get("computer_stock"));
                            smap.put("actual_stock", holder.list_d4.getText().toString());
                            smap.put("btl",""+dd1);
                            m_doc_srno=m_doc_srno+1;
                            smap.put("m_doc_srno",""+m_doc_srno);
                            smap.put("to_loct_stock", attendance_list.get(position).get("to_loct_stock"));
                            swap_arryList.add(smap);
                            swap_recyclerAdapter.notifyDataSetChanged();
                            // attendance_list.remove(position);
                            menu_card_arryList.remove(position);
                            attendance_recyclerAdapter.notifyDataSetChanged();
                            try
                            {

                               // ps1 = con.prepareStatement("INSERT INTO TABTEMPSTOK VALUES('"+item_code+"',"+ dd1 +","+m_TAB_CODE+","+ m_doc_srno +")");
                                ps1 = con.prepareStatement("INSERT INTO TABTEMPSTOK VALUES('"+item_code+"',"+ holder.list_d4.getText().toString() +","+m_TAB_CODE+","+ m_doc_srno +")");
                                ps1.executeUpdate();
                            }
                            catch(Exception e)
                            {
                                Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

           /* holder.list_d4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        lastpos=position;
                        edt_search.setText("");
                        search_key="";
                        holder.list_d2.setBackgroundResource(R.drawable.edit_text_border);
                        holder.list_d2.setTextColor(Color.BLACK);
                        holder.list_d3.setText(holder.list_d3.getText().toString());
                        holder.list_d4.setTextColor(Color.BLACK);
                        holder.list_d4.setBackgroundResource(R.drawable.edit_text_border);
                        if(!holder.list_d4.getText().toString().equals("")) {
                            lastpos=position;
                            double d=Double.parseDouble(holder.list_d3.getText().toString())-Double.parseDouble(holder.list_d4.getText().toString());
                            m_changeyn=0;
                            if(d>0) {
                               // String s=Double. toString(d);
                                if(str_radio.equals("bottle"))
                                {
                                    holder.list_d5.setText("" +(int)d);
                                    holder.list_d7.setText("" +(int)d);
                                }
                                else {
                                    NumberFormat n = new DecimalFormat("00.00");
                                    num = n.format(d).toString();
                                    holder.list_d5.setText(num.replace("0",""));
                                    num=num.replace("0","");
                                    try {
                                        if(num.equals(".")) {  }
                                        else if(num.startsWith(".")) {
                                            a = num.split("\\.")[1];
                                            int d1 = Integer.parseInt(a);
                                            holder.list_d7.setText("" + d1);
                                        }else if(!num.startsWith(".")&& num.length()==2) {
                                            a = num.split("\\.")[0];
                                            int d1 = Integer.parseInt(a);
                                            String cqty = attendance_list.get(position).get("caseqty");
                                            int c = Integer.parseInt(cqty.replaceAll("\\s", ""));
                                            holder.list_d7.setText("" + d1*c);
                                        }
                                        else {
                                            String a = num.split("\\.")[0];
                                            int d1 = Integer.parseInt(a);
                                            String b = num.split("\\.")[1];
                                            int d2 = Integer.parseInt(b);
                                            String cqty = attendance_list.get(position).get("caseqty");
                                            int c = Integer.parseInt(cqty.replaceAll("\\s", ""));
                                            int btl_qty = c * d1;
                                            btl_qty = btl_qty + d2;
                                            holder.list_d7.setText("" + btl_qty);
                                        }
                                    }catch (Exception e){
                                        Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else
                            {
                                Double d5=-(d);
                                Double d6=Double.parseDouble(holder.list_d6.getText().toString());
                                if( d5>d6)
                                {
                                    holder.list_d4.setText("");
                                    holder.list_d5.setText("");
                                    holder.list_d7.setText("");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Transfer_Note.this, R.style.AppCompatAlertDialogStyle);
                                    builder.setTitle("Warning");
                                    builder.setIcon(R.drawable.fail);
                                    if(m_sale_loct_yn>0)
                                    {
                                        builder.setMessage("No More Sale Record Present To Return");
                                    }
                                    else
                                    {
                                        builder.setMessage("Stock Not Available To Pull From To Location Stock");
                                    }
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                }
                                else
                                {
                                    if(str_radio.equals("bottle"))
                                    {
                                        holder.list_d5.setText("" +(int)d);
                                        holder.list_d7.setText("" +(int)d);
                                    }
                                    else
                                    {
                                        NumberFormat n = new DecimalFormat("00.00");
                                        num = n.format(d).toString();
                                        holder.list_d5.setText(num.replace("0",""));
                                        num=num.replace("0","");
                                        try {
                                            if(num.equals(".")) {
                                                holder.list_d5.setText("0");
                                                holder.list_d7.setText("0");
                                            }
                                           else if(num.startsWith("-.")) {
                                                 a = num.split("\\.")[1];
                                                 int d1 = Integer.parseInt(a);
                                                holder.list_d7.setText("" + d1*(-1));
                                            }
                                           else if(num.startsWith("-")&& num.length()==3) {
                                                a = num.split("\\.")[0];
                                                int d1 = Integer.parseInt(a);
                                                String cqty = attendance_list.get(position).get("caseqty");
                                                int c = Integer.parseInt(cqty.replaceAll("\\s", ""));
                                                holder.list_d7.setText("" + d1*c);
                                            }
                                            else {
                                                a = num.split("\\.")[0];
                                                int d1 = Integer.parseInt(a);
                                                b = num.split("\\.")[1];
                                                int d2 = Integer.parseInt(b);
                                                String cqty = attendance_list.get(position).get("caseqty");
                                                int c = Integer.parseInt(cqty.replaceAll("\\s", ""));
                                                d1 = -(d1);
                                                int btl_qty = c * d1;
                                                btl_qty = btl_qty + d2;
                                                holder.list_d7.setText("" + btl_qty*(-1));
                                            }
                                        }catch (Exception e){
                                            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            holder.list_d5.setText("");
                            holder.list_d7.setText("");
                        }
                    }
                      if(data_list.size()>0 && attendance_list.size()>0) {
                          Log.d("aaa", data_list.toString());
                          dmap = new HashMap<String, String>();
                          dmap.put("item_code", attendance_list.get(position).get("item_code"));
                          dmap.put("brnd_desc", attendance_list.get(position).get("brnd_desc"));
                          dmap.put("size_desc", attendance_list.get(position).get("size_desc"));
                          dmap.put("computer_stock", attendance_list.get(position).get("computer_stock"));
                          dmap.put("actual_stock", holder.list_d4.getText().toString());
                          //dmap.put("diff", holder.list_d5.getText().toString());
                          dmap.put("diff", holder.list_d7.getText().toString());
                          dmap.put("to_loct_stock", attendance_list.get(position).get("to_loct_stock"));
                          dmap.put("case_qty", attendance_list.get(position).get("case_qty"));
                          dmap.put("SALE_RATE", attendance_list.get(position).get("SALE_RATE"));
                          dmap.put("CASHMEMO_PRICE", attendance_list.get(position).get("CASHMEMO_PRICE"));
                          dmap.put("MRP", attendance_list.get(position).get("MRP"));
                          // data_list.remove(position);
                          // data_list.add(dmap);
                          data_list.set(position, dmap);

                          swap_arryList.add(smap);
                          swap_recyclerAdapter.notifyDataSetChanged();

                          // attendance_list.remove(position);
                          menu_card_arryList.remove(position);
                          attendance_recyclerAdapter.notifyDataSetChanged();
                      }
                }
            });*/
        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }
        public int getItemViewType(int position) {  return position; }
        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d6,list_d7, list_amt;
            EditText list_d3,list_d4;
            ImageView list_d5;
            LinearLayout lin;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1= (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d6= (TextView) itemView.findViewById(R.id.list_d6);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (EditText) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (EditText) itemView.findViewById(R.id.list_d4);
                //this.list_d5 = (ImageView) itemView.findViewById(R.id.list_d5);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trns_swap_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
           /* if (m_changeyn==1)
            {
                holder.list_d4.setText("");
            }*/

            Log.d("item_code",item_code);
            holder.list_d2.setText(attendance_list.get(position).get("brnd_desc"));
            holder.list_d1.setText(attendance_list.get(position).get("size_desc"));
            // NumberFormat n = new DecimalFormat(".00");
            // holder.list_d3.setText(n.format(Double.parseDouble(attendance_list.get(position).get("computer_stock"))));
            holder.list_d3.setText(attendance_list.get(position).get("computer_stock"));
            if(!attendance_list.get(position).get("actual_stock").toString().equals("")) {
                holder.list_d4.setText(attendance_list.get(position).get("actual_stock"));
            }
            if(!attendance_list.get(position).get("btl").toString().equals("")) {
                holder.list_d6.setText(attendance_list.get(position).get("btl"));
            }
            if(!attendance_list.get(position).get("m_doc_srno").toString().equals("")) {
                holder.list_d7.setText(attendance_list.get(position).get("m_doc_srno"));
            }
            holder.list_d5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item_code= attendance_list.get(position).get("item_code");
                  //  Toast.makeText(context, ""+item_code, Toast.LENGTH_SHORT).show();
                    map= new HashMap<String, String>();
                   // item_code= attendance_list.get(position).get("item_code");
                   // map.put("item_code",item_code);
                    map.put("item_code",attendance_list.get(position).get("item_code"));
                    map.put("brnd_desc",attendance_list.get(position).get("brnd_desc"));
                    map.put("size_desc",attendance_list.get(position).get("size_desc"));
                    map.put("computer_stock",attendance_list.get(position).get("computer_stock"));
                    map.put("actual_stock",attendance_list.get(position).get("actual_stock"));
                    map.put("btl",attendance_list.get(position).get("btl"));
                    map.put("m_doc_srno",attendance_list.get(position).get("m_doc_srno"));
                    map.put("to_loct_stock",attendance_list.get(position).get("to_loct_stock"));
                    menu_card_arryList.add(map);
                    attendance_recyclerAdapter.notifyDataSetChanged();
                    //attendance_list.remove(position);
                    swap_arryList.remove(position);
                    swap_recyclerAdapter.notifyDataSetChanged();
                    try
                    {
                       // String qry="DELETE FROM TABTEMPSTOK WHERE ITEM_CODE='"+item_code+"' AND QTY="+ holder.list_d4.getText().toString() +" AND TAB_CODE="+m_TAB_CODE+" AND DOC_SRNO="+ holder.list_d7.getText().toString()+"";
                       // Log.d("qqqq",qry);
                        ps1 = con.prepareStatement("DELETE FROM TABTEMPSTOK WHERE ITEM_CODE='"+item_code+"' AND QTY="+ holder.list_d4.getText().toString() +" AND TAB_CODE="+m_TAB_CODE+" AND DOC_SRNO="+ holder.list_d7.getText().toString()+"");
                        ps1.executeUpdate();
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
                    }
                }
            });

             //-------------Save Data------------------
            dmap = new HashMap<String, String>();
            dmap.put("item_code", attendance_list.get(position).get("item_code"));
            dmap.put("brnd_desc", attendance_list.get(position).get("brnd_desc"));
            dmap.put("size_desc", attendance_list.get(position).get("size_desc"));
            dmap.put("computer_stock", attendance_list.get(position).get("computer_stock"));
            dmap.put("diff", holder.list_d4.getText().toString());
            data_list.add(dmap);
           /* holder.list_d4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        lastpos=position;
                        edt_search.setText("");
                        search_key="";
                        holder.list_d2.setBackgroundResource(R.drawable.edit_text_border);
                        holder.list_d2.setTextColor(Color.BLACK);
                        holder.list_d3.setText(holder.list_d3.getText().toString());
                        holder.list_d4.setTextColor(Color.BLACK);
                        holder.list_d4.setBackgroundResource(R.drawable.edit_text_border);
                        if(!holder.list_d4.getText().toString().equals("")) {
                            lastpos=position;
                            double d=Double.parseDouble(holder.list_d3.getText().toString())-Double.parseDouble(holder.list_d4.getText().toString());
                            m_changeyn=0;
                            if(d>0) {
                                // String s=Double. toString(d);
                                if(str_radio.equals("bottle"))
                                {
                                    holder.list_d5.setText("" +(int)d);
                                    holder.list_d7.setText("" +(int)d);
                                }
                                else {
                                    NumberFormat n = new DecimalFormat("00.00");
                                    num = n.format(d).toString();
                                    holder.list_d5.setText(num.replace("0",""));
                                    num=num.replace("0","");
                                    try {
                                        if(num.equals(".")) {  }
                                        else if(num.startsWith(".")) {
                                            a = num.split("\\.")[1];
                                            int d1 = Integer.parseInt(a);
                                            holder.list_d7.setText("" + d1);
                                        }else if(!num.startsWith(".")&& num.length()==2) {
                                            a = num.split("\\.")[0];
                                            int d1 = Integer.parseInt(a);
                                            String cqty = attendance_list.get(position).get("caseqty");
                                            int c = Integer.parseInt(cqty.replaceAll("\\s", ""));
                                            holder.list_d7.setText("" + d1*c);
                                        }
                                        else {
                                            String a = num.split("\\.")[0];
                                            int d1 = Integer.parseInt(a);
                                            String b = num.split("\\.")[1];
                                            int d2 = Integer.parseInt(b);
                                            String cqty = attendance_list.get(position).get("caseqty");
                                            int c = Integer.parseInt(cqty.replaceAll("\\s", ""));
                                            int btl_qty = c * d1;
                                            btl_qty = btl_qty + d2;
                                            holder.list_d7.setText("" + btl_qty);
                                        }
                                    }catch (Exception e){
                                        Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else
                            {
                                Double d5=-(d);
                                Double d6=Double.parseDouble(holder.list_d6.getText().toString());
                                if( d5>d6)
                                {
                                    holder.list_d4.setText("");
                                    holder.list_d5.setText("");
                                    holder.list_d7.setText("");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Transfer_Note.this, R.style.AppCompatAlertDialogStyle);
                                    builder.setTitle("Warning");
                                    builder.setIcon(R.drawable.fail);
                                    if(m_sale_loct_yn>0)
                                    {
                                        builder.setMessage("No More Sale Record Present To Return");
                                    }
                                    else
                                    {
                                        builder.setMessage("Stock Not Available To Pull From To Location Stock");
                                    }
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                }
                                else
                                {
                                    if(str_radio.equals("bottle"))
                                    {
                                        holder.list_d5.setText("" +(int)d);
                                        holder.list_d7.setText("" +(int)d);
                                    }
                                    else
                                    {
                                        NumberFormat n = new DecimalFormat("00.00");
                                        num = n.format(d).toString();
                                        holder.list_d5.setText(num.replace("0",""));
                                        num=num.replace("0","");
                                        try {
                                            if(num.equals(".")) {
                                                holder.list_d5.setText("0");
                                                holder.list_d7.setText("0");
                                            }
                                            else if(num.startsWith("-.")) {
                                                a = num.split("\\.")[1];
                                                int d1 = Integer.parseInt(a);
                                                holder.list_d7.setText("" + d1*(-1));
                                            }
                                            else if(num.startsWith("-")&& num.length()==3) {
                                                a = num.split("\\.")[0];
                                                int d1 = Integer.parseInt(a);
                                                String cqty = attendance_list.get(position).get("caseqty");
                                                int c = Integer.parseInt(cqty.replaceAll("\\s", ""));
                                                holder.list_d7.setText("" + d1*c);
                                            }
                                            else {
                                                a = num.split("\\.")[0];
                                                int d1 = Integer.parseInt(a);
                                                b = num.split("\\.")[1];
                                                int d2 = Integer.parseInt(b);
                                                String cqty = attendance_list.get(position).get("caseqty");
                                                int c = Integer.parseInt(cqty.replaceAll("\\s", ""));
                                                d1 = -(d1);
                                                int btl_qty = c * d1;
                                                btl_qty = btl_qty + d2;
                                                holder.list_d7.setText("" + btl_qty*(-1));
                                            }
                                        }catch (Exception e){
                                            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            holder.list_d5.setText("");
                            holder.list_d7.setText("");
                        }
                    }
                    if(data_list.size()>0 && attendance_list.size()>0) {
                        Log.d("aaa", data_list.toString());
                        dmap = new HashMap<String, String>();
                        dmap.put("item_code", attendance_list.get(position).get("item_code"));
                        dmap.put("brnd_desc", attendance_list.get(position).get("brnd_desc"));
                        dmap.put("size_desc", attendance_list.get(position).get("size_desc"));
                        dmap.put("computer_stock", attendance_list.get(position).get("computer_stock"));
                        dmap.put("actual_stock", holder.list_d4.getText().toString());
                        //dmap.put("diff", holder.list_d5.getText().toString());
                        dmap.put("diff", holder.list_d7.getText().toString());
                        dmap.put("to_loct_stock", attendance_list.get(position).get("to_loct_stock"));
                        dmap.put("case_qty", attendance_list.get(position).get("case_qty"));
                        dmap.put("SALE_RATE", attendance_list.get(position).get("SALE_RATE"));
                        dmap.put("CASHMEMO_PRICE", attendance_list.get(position).get("CASHMEMO_PRICE"));
                        dmap.put("MRP", attendance_list.get(position).get("MRP"));
                        // data_list.remove(position);
                        // data_list.add(dmap);
                        data_list.set(position, dmap);
                        menu_card_arryList.add(map);
                        attendance_recyclerAdapter.notifyDataSetChanged();
                        //attendance_list.remove(position);
                        swap_arryList.remove(position);
                        swap_recyclerAdapter.notifyDataSetChanged();
                    }
                }
            });*/
        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }
        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d6,list_d7, list_amt;
            EditText list_d3,list_d4;
            ImageView list_d5;
            LinearLayout lin;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1= (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d6= (TextView) itemView.findViewById(R.id.list_d6);
                this.list_d7= (TextView) itemView.findViewById(R.id.list_d7);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (EditText) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (EditText) itemView.findViewById(R.id.list_d4);
               // this.list_d5 = (ImageView) itemView.findViewById(R.id.list_d5);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(Transfer_Note.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Alert");
        builder.setIcon(R.drawable.warn);
        builder.setMessage("Are You Sure,You Want To Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
