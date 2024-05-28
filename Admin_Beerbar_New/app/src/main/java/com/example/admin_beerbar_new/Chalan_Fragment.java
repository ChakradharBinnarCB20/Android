package com.example.admin_beerbar_new;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.admin_beerbar_new.Class.TransparentProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chalan_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    SearchView searchView;
    String SubCodeStr;
    AlertDialog dialog;
    ProgressBar pgb;
    PreparedStatement ps1;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="",db;
    TransparentProgressDialog pd;
    DecimalFormat df2;
    DecimalFormat formatter ;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    int m_compcode,m_TAB_CODE;
    double m_clbal,cbal;
    String IMEINumber,con_ipaddress,portnumber,str_ac_head_id;
    String Temp_date,Query_date;
    // ConnectionClass connectionClass;
    ProgressBar pbbar;
    Toolbar toolbar;
    TextView txt_save,txt_cancel,txt_enquery;
    String query="";
    TextView edt_date;
    int lastpos=0;
    double basic_amt=0.00;
    double basic_rate=0.00;
    ImageView btn_cancel;
    Button btn_save,btn_yes,btn_no;
    ArrayList<HashMap<String, String>> data_list;
    HashMap<String, String> map;
    HashMap<String, String> dmap;
    //================Recyclerview swap======================
    ArrayList<HashMap<String, String>> swap_arryList;
    private RecyclerView.LayoutManager layoutManager_swap;
    tswap_recyclerAdapter swap_recyclerAdapter;
    private RecyclerView recycler_swap_list;
    HashMap<String, String> smap;
    EditText edt_net_sale,edt_add_amt;
    ArrayList<HashMap<String, String>> mBackupData;
    String user, cqty="",str_customer,m_customer_code;
    int c,btl_qty;
    ProgressDialog progressDoalog;
    String qry="";
    int dd1=0;
    Spinner sp_customer;
    SharedPreferences spf;
    String doc_date="";
    Double doc_no=0.0;
    Double tot_amt=0.0;
    Double add_amt = 0.00;
    double amt=0.00;
    int doc_srno=0;
    Config connectionClass;
    String num,systemDate;
    PreparedStatement ps;
    String a,item_code;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    EditText edit_qty, edit_value;
    EditText txt_bar_code, txt_pur_case, txt_size, txt_brand, txt_mrp,txt_bottale;
    String str_bar_code, str_pur_case, str_size, str_brand, str_mrp,str_bottale;
    String str_barcode,str_item_nm,str_case_qty,str_qty,str_value,str_btl_qty;
    //==================================
    public Chalan_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chalan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      //  String m_androidId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
      //  Log.d("m_androidId",m_androidId);
        formatter = new DecimalFormat("0.00");
        SharedPreferences ss = getActivity().getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

        edt_net_sale=(EditText) view.findViewById(R.id.edt_net_sale);
        edt_add_amt=(EditText) view.findViewById(R.id.edt_add_amt);
        sp_customer=(Spinner) view.findViewById(R.id.sp_customer);
        pgb=(ProgressBar)view.findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        mBackupData = new ArrayList<>();
        txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);
        txt_save = (TextView) view.findViewById(R.id.txt_save);
        txt_enquery = (TextView) view.findViewById(R.id.txt_enquery);
        pbbar = (ProgressBar) view.findViewById(R.id.pgb);
        connectionClass = new Config();

        //---------------------Recyclerview 1-----------------------------------------
        data_list = new ArrayList<HashMap<String, String>>();
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) view.findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getActivity(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        //---------------------Recyclerview swap-----------------------------------------
        swap_arryList = new ArrayList<HashMap<String, String>>();
        recycler_swap_list = (RecyclerView)view.findViewById(R.id.recycler_swap_list);
        layoutManager_swap = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_swap_list.setLayoutManager(layoutManager_swap);
        swap_recyclerAdapter=new tswap_recyclerAdapter(getActivity(),swap_arryList);
        recycler_swap_list.setAdapter(swap_recyclerAdapter);
        //-------------------------------------------------
        txt_enquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),Chalan_Enquery.class);
                startActivity(i);
            }
        });

    //  con = connectionClass.CONN(con_ipaddress, portnumber,db);
        con = CONN(con_ipaddress,portnumber,db);
        if (con == null) {
            Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
        }
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
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

        searchView=(SearchView)view.findViewById(R.id.report_searchView);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 0) {

                    SubCodeStr = newText;
                    // SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();
                    //subcodestr = subcodestr.replaceAll("\\s+", "% ").toLowerCase();
                    Log.d("ssss", SubCodeStr);

                    //new FetchSearchResult().execute();
                    sale_report(SubCodeStr);
                } else if (TextUtils.isEmpty(newText)) {
                    // lin_grid_visible.setVisibility(View.INVISIBLE);
                    // menu_card_arryList.clear();
                    // menu_search("");
                } else {
                    sale_report("");
                }
                return false;
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
        edt_date=(TextView)view.findViewById(R.id.edt_date);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),R.style.AppCompatAlertDialogStyle,
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
        //==========ADD AMOUNT===============================
        edt_add_amt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                add_amt = 0.00;
                 amt=0.00;
                amt=tot_amt;
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edt_add_amt.length() != 0 && edt_net_sale.length() != 0 ) {
                    add_amt = Double.parseDouble(edt_add_amt.getText().toString());
                }
                amt = amt + add_amt;
                edt_net_sale.setText("" + amt);
               if(edt_add_amt.length()==0)
               {
                   edt_net_sale.setText("" + tot_amt);
               }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        new load_customer().execute();
        sale_report("");
        
    }
    public class load_customer extends AsyncTask<String, String, String> {
        List<Map<String, String>> sp_data = new ArrayList<Map<String, String>>();
        @Override
        protected void onPreExecute() {
            progressDoalog = new ProgressDialog(getActivity());
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                } else {


                    String query = "select glmast.ac_head_id,gl_desc from glmast,fatree,voucherdebitcredit where  glmast.group_code = fatree.group_code and glmast.group_code = voucherdebitcredit.group_code and (fatree.sub_groupcode in(select group_code from fagroupparameters where group_type ='SUPPLIERS')) and voucherdebitcredit.crdr_cd = 'C' and voucherdebitcredit.doc_id in (select doc_id from favoucher where doc_desc = 'PURCHASE VOUCHER-TRADING MATERIAL-LIQUOR') order by glmast.gl_desc";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));
                        sp_data.add(data);
                    }
                }
            } catch (Exception e) {
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            progressDoalog.dismiss();
            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};
            final SimpleAdapter spnr_data = new SimpleAdapter(getActivity(), sp_data, R.layout.spin, from, views);
            sp_customer.setAdapter(spnr_data);
            sp_customer.setSelection(0);
            sp_customer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    str_customer = (String) obj.get("A");
                    m_customer_code = (String) obj.get("B");
                    //  Toast.makeText(Reports.this, "loct_code: "+m_loct_code, Toast.LENGTH_SHORT).show();

                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            super.onPostExecute(s);
        }
    }
    void save_data() {

        doc_no = 0.0;
        doc_srno = 0;

        int m_alert = 0;

        if (swap_arryList.isEmpty()) {
            m_alert = 1;
        } else {
            // Save Record in Countersale and CountersaleReturn Table

            try {
                ps1 = con.prepareStatement("UPDATE DOC_NO SET CHALAN_DOCNO=CHALAN_DOCNO+1 where '" + Query_date + "' BETWEEN from_year and to_year");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("select isnull(max(doc_srno),0) as doc_srno from countersaleitem where doc_dt='" + Query_date + "'");
                ResultSet rr = ps1.executeQuery();
                while (rr.next()) {
                    doc_srno = rr.getInt(1);
                }

            } catch (Exception e) {
            }
            df.setRoundingMode(RoundingMode.UP);
            for (int k = 0; k < swap_arryList.size(); k++) {
                dmap = (HashMap) swap_arryList.get(k);

                try {

                    doc_srno = doc_srno + 1;
                    ps1 = con.prepareStatement("SELECT CHALAN_DOCNO FROM DOC_NO where '" + Query_date + "' BETWEEN from_year and to_year");
                    ResultSet s = ps1.executeQuery();
                    while (s.next()) {
                        //  ps1 = con.prepareStatement("INSERT INTO COUNTERSALEITEM (DOC_NO,DOC_DT,ITEM_CODE,QTY,RATE,ITEM_VALUE,DOC_SRNO,LOCT_CODE,STOCK_METHOD,COMP_CODE,MRP,CASHMEMO_PRICE) VALUES(" + s.getString("COUNTER_SALE_DOCNO") + ",'" + Query_date + "','" + map.get("item_code") + "'," + map.get("diff") + "," + map.get("SALE_RATE") + "," + map.get("diff") + " * " + map.get("SALE_RATE") + " , " + m_counter_sale_doc_srno + "," + m_frm_loct_code + ",1,1," + map.get("MRP") + "," + map.get("CASHMEMO_PRICE") + ")");
                        // qry="INSERT INTO CHALANITEM (DOC_NO,DOC_DT,DOC_SRNO,AC_HEAD_ID,ITEM_CODE,QUANTITY,BOTTLE_QTY,CASE_QTY,CHANGE_CASE_QTY,LOCT_CODE,STOCK_METHOD,COMP_CODE,MRP,BASIC_RATE,BASIC_AMT,ADD_AMOUNT,USER_CODE) VALUES(" + s.getString("CHALAN_DOCNO") + ",'" + Query_date + "',"+doc_srno+",'" + m_customer_code + "'," + dmap.get("item_code") + ",1," + dmap.get("btl_qty") + "," + dmap.get("qty") + " ,0 ,1,1,1 ," + dmap.get("mrp") + "," + dmap.get("basic_rate") + "," + dmap.get("basic_amt") + "," +add_amt + "," + m_TAB_CODE + ")";
                        qry="INSERT INTO CHALANITEM (DOC_NO,DOC_DT,DOC_SRNO,AC_HEAD_ID,ITEM_CODE,QUANTITY,BOTTLE_QTY,CASE_QTY,CHANGE_CASE_QTY,LOCT_CODE,STOCK_METHOD,COMP_CODE,MRP,BASIC_RATE,BASIC_AMT,ADD_AMOUNT,USER_CODE) VALUES(" + s.getString("CHALAN_DOCNO") + ",'" + Query_date + "',"+doc_srno+",'" + m_customer_code + "'," + dmap.get("item_code") + "," + dmap.get("qty") + "," + dmap.get("btl_qty") + "," + dmap.get("cqty") + " ,0 ,1,1,1 ," + dmap.get("mrp") + "," + df.format(Double.parseDouble(dmap.get("basic_rate"))) + "," + dmap.get("basic_amt") + "," +add_amt + "," + m_TAB_CODE + ")";
                        Log.d("qry",qry);
                        ps1 = con.prepareStatement(qry);
                        ps1.executeUpdate();
                        //=====Procedure Call ==========12/10/2021
                        // ps1 = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS " + s.getString("COUNTER_SALE_DOCNO") + ",'" + Query_date + "','COUNTERSALEITEM',1,'0',''");
                        // ps1.executeUpdate();
                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
                }
            }

        }
        if (m_alert == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
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
                    reload_group();
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    public  void reload_group()
    {
       // m_changeyn = 1;
        //data_list.clear();
       // menu_card_arryList.clear();
      //  attendance_recyclerAdapter.notifyDataSetChanged();
        dmap.clear();
        swap_arryList.clear();
        swap_recyclerAdapter.notifyDataSetChanged();
        edt_net_sale.setText("");
        edt_add_amt.setText("");

    }

    public void sale_report(String search_key) {
        try {
            pbbar.setVisibility(View.VISIBLE);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            } else {

               // query="select item_code,mrp,brnd_desc,size_desc,case_qty from itemmast,brndmast,sizemast where brndmast.brnd_code=itemmast.brnd_code and sizemast.size_code=itemmast.size_code and itemmast.live_yn = 1 and full_bottle = 1 order by brnd_desc,seq_no";
                query="select item_code,ltrim(str(mrp,12,2))as MRP ,brnd_desc,size_desc,case_qty from itemmast,brndmast,sizemast where brndmast.brnd_code=itemmast.brnd_code and sizemast.size_code=itemmast.size_code and itemmast.live_yn = 1 and full_bottle = 1 and brnd_desc like '"+search_key+"%' order by  brnd_desc,seq_no";
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
                    map.put("case_qty", rs.getString("case_qty"));
                    map.put("MRP", rs.getString("MRP"));
                    menu_card_arryList.add(map);
                   // data_list.add(map);
                }
            }
            pbbar.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());
            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
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
            holder.list_d3.setText(attendance_list.get(position).get("case_qty"));
            holder.list_d4.setText(attendance_list.get(position).get("item_code"));
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
               str_barcode =attendance_list.get(position).get("item_code");
               str_item_nm=attendance_list.get(position).get("brnd_desc");
               str_size=attendance_list.get(position).get("size_desc");
               str_case_qty=attendance_list.get(position).get("case_qty");
               str_mrp=attendance_list.get(position).get("MRP");
               Log.d("ssss",str_barcode);
               Log.d("ssss",str_item_nm);
               Log.d("ssss",str_size);
               Log.d("ssss",str_case_qty);
               Log.d("ssss",str_mrp);
                    chalan_popup_form(str_barcode,str_size,str_item_nm,str_mrp,str_case_qty);
                }
            });
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
               // this.list_d6= (TextView) itemView.findViewById(R.id.list_d6);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (EditText) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (EditText) itemView.findViewById(R.id.list_d4);
               // this.list_d5 = (ImageView) itemView.findViewById(R.id.list_d5);
            }
        }
    }

    public void chalan_popup_form(String b,String s,String inm,String mrp,String qty) {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_chalan, null);
        txt_bar_code = (EditText) alertLayout.findViewById(R.id.txt_bar_code);
        txt_bar_code.setText(b);
        txt_size = (EditText) alertLayout.findViewById(R.id.txt_size);
        txt_size.setText(s);
        txt_brand = (EditText) alertLayout.findViewById(R.id.txt_brand);
        txt_brand.setText(inm);
        txt_mrp = (EditText) alertLayout.findViewById(R.id.txt_mrp);

       // DecimalFormat formatter = new DecimalFormat("0.00");
       // txt_mrp.setText(formatter.format(mrp));
        txt_mrp.setText(mrp);
        edit_value = (EditText) alertLayout.findViewById(R.id.edit_value);

        edit_qty = (EditText) alertLayout.findViewById(R.id.edit_qty);
        edit_qty.setText(qty);
        txt_bottale = (EditText) alertLayout.findViewById(R.id.txt_bottale);
        txt_pur_case = (EditText) alertLayout.findViewById(R.id.txt_pur_case);
        //  txt_pur_case.setText(type);
        txt_pur_case.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txt_pur_case.length() != 0) {
                    String sp = txt_pur_case.getText().toString();
                    // Double sp=Double.parseDouble(sale_pr);
                    double edt = Double.parseDouble(edit_qty.getText().toString());
                    //========CASE TO BOTTALE=============================
                    //--------------------------
                    num = String.valueOf(sp);
                    Log.d("nnnn", num);
                    if (!num.contains(".")&& num.length()>=0) {
                        cqty = edit_qty.getText().toString().replace("0", "");
                        cqty = cqty.replace(".", "");
                        c = Integer.parseInt(cqty);
                        int value = Integer.parseInt(sp);
                        dd1=c*value;
                       // txt_bottale.setText("" + dd1);
                    }
                    // NumberFormat n = new DecimalFormat("00.00");
                    //  num = n.format(s);
                    else {
                        num = num.replace("0", "");
                        Log.d("nnnn", num);
                        try {

                            if (num.equals(".")) {
                            } else if (num.startsWith(".")) {
                                a = num.split("\\.")[1];
                                dd1 = Integer.parseInt(a);
                                if (dd1 >= edt) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                                    builder.setTitle("Warning");
                                    builder.setIcon(R.drawable.fail);
                                    builder.setMessage("Loose Bottle Quantity" + dd1 + "Should Be Less Than Case Quantity " + edt);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            txt_pur_case.setText("");
                                            txt_bottale.setText("");
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.dismiss();
                                            txt_pur_case.setText("");
                                            txt_bottale.setText("");
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                }
                                Log.d("nnnn", num);
                                // holder.list_d6.setText("" + dd1);
                            } else if (!num.startsWith(".") && num.length() == 2) {
                                a = num.split("\\.")[0];
                                dd1 = Integer.parseInt(a);

                                if (dd1 >= edt) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                                    builder.setTitle("Warning");
                                    builder.setIcon(R.drawable.fail);
                                    builder.setMessage("Loose Bottle Quantity " + dd1 + "Should Be Less Than Case Quantity " + edt);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            txt_pur_case.setText("");
                                            txt_bottale.setText("");
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.dismiss();
                                            txt_pur_case.setText("");
                                            txt_bottale.setText("");
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                }
                                Log.d("nnnn", "" + dd1);
                                cqty = edit_qty.getText().toString().replace("0", "");
                                cqty = cqty.replace(".", "");
                                c = Integer.parseInt(cqty);
                                Log.d("nnnn", "" + c);
                                // holder.list_d6.setText("" + dd1*c);
                                dd1 = dd1 * c;
                            } else {
                                String a = num.split("\\.")[0];
                                dd1 = Integer.parseInt(a);
                                String b = num.split("\\.")[1];
                                int dd2 = Integer.parseInt(b);

                                if (dd2 >= edt) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                                    builder.setTitle("Warning");
                                    builder.setIcon(R.drawable.fail);
                                    builder.setMessage("Loose Bottle Quantity " + dd2 + "Should Be Less Than Case Quantity " + edt);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            txt_pur_case.setText("");
                                            txt_bottale.setText("");
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.dismiss();
                                            txt_pur_case.setText("");
                                            txt_bottale.setText("");
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                }
                                cqty = edit_qty.getText().toString().replace("0", "");
                                cqty = cqty.replace(".", "");
                                c = Integer.parseInt(cqty);
                                btl_qty = c * dd1;
                                btl_qty = btl_qty + dd2;
                                Log.d("nnnn", "" + btl_qty);
                                // holder.list_d6.setText("" + btl_qty);
                                dd1 = btl_qty;
                                Log.d("nnnn", "" + dd1);
                                //=====================================================
                                // txt_bottale.setText("" + sp * edt);
                            }
                        } catch (Exception e) {
                            //Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                    txt_bottale.setText("" + dd1);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txt_bottale.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txt_bottale.length() != 0) {
                    try {
                        Double sp = Double.parseDouble(txt_bottale.getText().toString());
                        // Double sp=Double.parseDouble(sale_pr);
                        Double edt = Double.parseDouble(txt_mrp.getText().toString());
                        edit_value.setText("" + sp * edt);
                    } catch (Exception e) {
                        //Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    edit_value.setText("00");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        btn_save = (Button) alertLayout.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basic_amt=0.00;
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        str_case_qty = edit_qty.getText().toString();
                        str_bar_code = txt_bar_code.getText().toString();
                        str_pur_case = txt_pur_case.getText().toString();
                        str_size = txt_size.getText().toString();
                        str_brand = txt_brand.getText().toString();
                        str_mrp = txt_mrp.getText().toString();
                        str_bottale = txt_bottale.getText().toString();

                       /* //--------------------------------------------------
                        String str = basic_rate+"";
                        Log.d("basic_rate",str);
                        String a = str.split("\\.")[0];
                        int  dd1 = Integer.parseInt(a);
                        Log.d("basic_rate","a "+dd1);
                        String b = str.split("\\.")[1].substring(0,3);
                        Log.d("basic_rate","b "+b);
                        double dd2 = Math.ceil(Integer.parseInt(b));
                        Log.d("basic_rate","r "+dd2);
                        str=dd1+"."+dd2;
                        Log.d("basic_rate",str);
                        //--------------------------------------------------*/

                        if (txt_pur_case.getText().toString().equals("") || txt_pur_case.getText().toString().equals("0")|| edit_value.getText().toString().equals("")|| edit_value.getText().toString().equals("0")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                            builder.setTitle("Alert");
                            builder.setIcon(R.drawable.warn);
                            builder.setMessage("Quantity/Value Can Not Be Null Or Zero.");
                            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();
                                    pd.dismiss();
                                }
                            });
                            builder.setCancelable(false);
                            builder.show();
                            // Toast.makeText(getApplicationContext(), "Quantity Can Not Be Null Or Zero", Toast.LENGTH_SHORT).show();

                        } else {

                            basic_amt=Double.parseDouble(edit_value.getText().toString());
                            basic_rate=basic_amt/Double.parseDouble(str_bottale);
                            if (txt_pur_case.getText().toString().equals("")) {
                                str_qty = "0";
                            } else {
                                str_qty = txt_pur_case.getText().toString();
                            }
                            if (txt_bottale.getText().toString().equals("")) {
                                str_btl_qty = "0";
                            } else {
                                str_btl_qty = txt_bottale.getText().toString();
                            }
                            if (edit_value.getText().toString().equals("")) {
                                str_value = "0.00";
                            } else {
                                str_value = edit_value.getText().toString();
                            }
                            Log.d("ffff", str_bar_code);
                            Log.d("ffff", str_size);
                            Log.d("ffff", str_brand);
                            Log.d("ffff", str_mrp);
                            Log.d("ffff", str_qty);
                            Log.d("ffff", str_value);
                            Log.d("ffff", m_customer_code);
                            Log.d("ffff", str_btl_qty);
                            smap = new HashMap<String, String>();
                          //  smap.put("doc_no", "" + doc_no);
                            smap.put("ac_head_id", "" + m_customer_code);
                            smap.put("item_code", str_bar_code);
                            smap.put("brnd_desc", str_brand);
                            smap.put("size_desc", str_size);
                            smap.put("mrp", str_mrp);
                            smap.put("value", str_value);
                            smap.put("cqty", str_case_qty);
                            smap.put("qty", str_qty);
                            smap.put("btl_qty", str_btl_qty);
                            smap.put("basic_amt", ""+basic_amt);
                            smap.put("basic_rate", ""+basic_rate);
                            //smap.put("time", time);
                            swap_arryList.add(smap);
                            swap_recyclerAdapter.notifyDataSetChanged();
                            tot_amt=tot_amt+Double.parseDouble(str_mrp);
                            edt_net_sale.setText(""+formatter.format(tot_amt));
                            pd.dismiss();
                           // ttl_value();
                          //  discount();
                        }
                        //save_data();
                    }
                }, 1000);
                dialog.dismiss();
            }
        });

        btn_cancel = (ImageView) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setView(alertLayout);
        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
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
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {
            holder.list_d1.setText(attendance_list.get(position).get("brnd_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("size_desc"));
            holder.list_d3.setText(attendance_list.get(position).get("qty"));
            holder.list_d4.setText(attendance_list.get(position).get("btl_qty"));
            holder.list_d5.setText(attendance_list.get(position).get("mrp"));
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.warn);
                    builder.setMessage("Are You Sures ? Do You Want To Delete The Record.");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                            swap_arryList.remove(position);
                            swap_recyclerAdapter.notifyDataSetChanged();
                            if(swap_arryList.isEmpty())
                            {
                                edt_net_sale.setText("0");
                            }
                            else {
                                tot_amt = 0.00;
                                for (int k = 0; k < swap_arryList.size(); k++) {
                                    map = (HashMap) swap_arryList.get(k);
                                    {
                                        tot_amt = tot_amt + Double.parseDouble(map.get("mrp"));
                                        edt_net_sale.setText("" + formatter.format(tot_amt));

                                    }
                                }
                            }

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
            });

        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }
        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2,list_d3,list_d4, list_d6,list_d5, list_amt;

            LinearLayout lin;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1= (TextView) itemView.findViewById(R.id.list_d1);
              //  this.list_d6= (TextView) itemView.findViewById(R.id.list_d6);
                this.list_d5= (TextView) itemView.findViewById(R.id.list_d5);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);


            }
        }
    }
    //============Delete=========================

    public Connection CONN(String ip, String port,String db) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll().build();

        StrictMode.setThreadPolicy(policy);

        con = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";;
            con = DriverManager.getConnection(ConnURL);

        } catch (SQLException se) {

            Log.e("ERRO", se.getMessage());

        } catch (ClassNotFoundException e) {

            Log.e("ERRO", e.getMessage());

        } catch (Exception e) {

            Log.e("ERRO", e.getMessage());

        }

        return con;

    }
}
