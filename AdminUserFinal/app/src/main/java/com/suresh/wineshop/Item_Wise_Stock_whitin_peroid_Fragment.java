package com.suresh.wineshop;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suresh.wineshop.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Item_Wise_Stock_whitin_peroid_Fragment extends Fragment {

    //---------------------------------------------------------
    int mYear, mMonth, mDay;
    String con_ipaddress ,portnumber,db;
    ProgressBar pbbar;
    ProgressDialog progressDoalog;
    TransparentProgressDialog pd;
    Button btn_proceed,btn_report,btn_check_all,btn_uncheck_all;
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
    HashMap<String, String> map;
    HashMap<String, String> smap;
    //--------------------------
    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "WINESHOP";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String IMEINumber = "";
    private ProgressBar progressBar;
    //---------------------------------------------------------
    CheckBox chk_stk_all_loc,chk_rpt_as_per_main,chk_summary;
    String str_stk_all_loc,str_all,str_summary="0",str_brnd_chk="0";
    //EditText edt_data;

    Spinner sp_counter_godown,sp_cost_evaluation;
    String formattedDate,Temp_frm_date,Temp_to_date, str_month="",str_day,systemDate;
    RadioGroup radioGroup,radiowiseGroup;

    String m_loct_desc,m_loct_code="0",Temp_date,Query_date,M_ratetype_code,M_ratetype_desc;
    LinearLayout lin_sp_hide;
    String str_cost_evaluation,str_price_type_title;
    RadioButton rb,radio_bottle,radio_cases,radio_group_wise_stock,radio_trade_wise_stock,radio_summary_wise,radio_selected_brnd;
    String q="";
    String radio_val="";
    TextView edt_frm_date,edt_to_date;
    int m_TAB_CODE;
    int m_compcode;
    int txt_per=0;
    PreparedStatement ps1;
    LinearLayout lin_hide;
    String check_id="",M_liqr_code="";
    public Item_Wise_Stock_whitin_peroid_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_wise_stock_, container, false);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        pbbar = (ProgressBar) view.findViewById(R.id.pgb);
        lin_sp_hide = (LinearLayout) view.findViewById(R.id.lin_sp_hide);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar_cyclic);

        //---------------------Recyclerview 2-----------------------------------------
        bill_arryList = new ArrayList<HashMap<String, String>>();
        recycler_bill_list = (RecyclerView) view.findViewById(R.id.recycler_bill_list);
        layoutManager_bill = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_bill_list.setLayoutManager(layoutManager_bill);
        bill_recyclerAdapter = new tbill_recyclerAdapter(getActivity(), bill_arryList);
        recycler_bill_list.setAdapter(bill_recyclerAdapter);
        //---------------------Recyclerview swap-----------------------------------------
        swap_arryList = new ArrayList<HashMap<String, String>>();
        recycler_swap_list = (RecyclerView)view. findViewById(R.id.recycler_swap_list);
        layoutManager_swap = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_swap_list.setLayoutManager(layoutManager_swap);
        swap_recyclerAdapter=new tswap_recyclerAdapter(getActivity(),swap_arryList);
        recycler_swap_list.setAdapter(swap_recyclerAdapter);
        //-------------------------------------------------
       // chk_rpt_as_per_main=(CheckBox)view.findViewById(R.id.chk_rpt_as_per_main);
        chk_summary=(CheckBox)view.findViewById(R.id.chk_summary);

        chk_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_summary.isChecked())
                {
                    str_brnd_chk="1";
                }
                else
                {    str_brnd_chk="0";

                }
            }
        });
        chk_stk_all_loc=(CheckBox)view.findViewById(R.id.chk_stk_all_loc);
        chk_stk_all_loc.setChecked(true);
        chk_stk_all_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_stk_all_loc.isChecked())
                {
                    lin_sp_hide.setVisibility(View.GONE);
                }
                else
                {    str_stk_all_loc="0";
                    lin_sp_hide.setVisibility(View.VISIBLE);
                }
            }
        });
        lin_hide=(LinearLayout) view.findViewById(R.id.lin_hide);
       // chk_all=(CheckBox)view.findViewById(R.id.chk_all);
        //chk_all.setChecked(true);

        radio_group_wise_stock=(RadioButton) view.findViewById(R.id.radio_group_wise);
        radio_summary_wise=(RadioButton) view.findViewById(R.id.radio_summary_wise);
        radio_trade_wise_stock=(RadioButton) view.findViewById(R.id.radio_trade_wise);
        radio_selected_brnd=(RadioButton) view.findViewById(R.id.radio_selected_brnd);
        radio_bottle=(RadioButton) view.findViewById(R.id.radio_bottle);
        radio_cases=(RadioButton)view.findViewById(R.id.radio_cases);
        radio_bottle.setChecked(true);
        radio_group_wise_stock.setChecked(true);
        radioGroup=(RadioGroup)view.findViewById(R.id.radioGroup);
        radiowiseGroup=(RadioGroup)view.findViewById(R.id.radiowiseGroup);
        radiowiseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                btn_check_all.setVisibility(View.VISIBLE);
                  rb=(RadioButton)view.findViewById(checkedId);
               // progressBar.setVisibility(View.VISIBLE);
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        try {
                            Log.d("llllll",rb.getText().toString());
                            if (rb.getText().toString().startsWith("Group")) {
                                bill_arryList.clear();
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="Group_stock";
                                q = "select distinct liqr_code,(select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+"";
                            } else if (rb.getText().toString().startsWith("Trader")) {
                                bill_arryList.clear();
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="Trader_stock";
                                q = "select ac_head_id,case when ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end from glmast where ac_head_id in(select ac_head_id from tabreportparameters where TAB_CODE="+m_TAB_CODE+") order by gl_desc";
                            }
                            else if (rb.getText().toString().startsWith("selected")) {
                                bill_arryList.clear();
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="Brand";
                                q = "select distinct brnd_code,(select brnd_desc from brndmast where brnd_code=tabreportparameters.brnd_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+" order by type";
                            }
                            else if (rb.getText().toString().startsWith("Size")) {
                                bill_arryList.clear();
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="Size";
                                //q = "select distinct brnd_code,(select brnd_desc from brndmast where brnd_code=tabreportparameters.brnd_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+" order by type";
                            }
                            else {
                                bill_arryList.clear();
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="";
                               // q = "select ac_head_id,case when ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end from glmast where ac_head_id in(select ac_head_id from tabreportparameters where TAB_CODE="+m_TAB_CODE+") order by gl_desc";
                                q = "select distinct liqr_code,(select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+"";
                            }
                        }catch (Exception e)
                        {
                            q="select distinct liqr_code,(select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+"";

                        }

                       load_data();

                        pd.dismiss();
                       // progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 3000);

            }
        });
        edt_frm_date=(TextView) view.findViewById(R.id.edt_frm_date);
        edt_to_date=(TextView) view.findViewById(R.id.edt_to_date);
        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        edt_frm_date=(TextView) view.findViewById(R.id.edt_frm_date);
        edt_to_date=(TextView) view.findViewById(R.id.edt_to_date);
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
               // chk_all.setEnabled(true);
                btn_check_all.setVisibility(View.INVISIBLE);
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                lin_hide.setVisibility(View.INVISIBLE);
                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        edt_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // chk_all.setEnabled(true);
                btn_check_all.setVisibility(View.INVISIBLE);
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                lin_hide.setVisibility(View.INVISIBLE);
                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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

        btn_proceed=(Button)view.findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_check_all.setVisibility(View.VISIBLE);
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                lin_hide.setVisibility(View.VISIBLE);
                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();

                      //  chk_all.setEnabled(false);
                        q="";
                        radio_group_wise_stock.setChecked(true);
                        radio_summary_wise.setChecked(false);
                        radio_trade_wise_stock.setChecked(false);
                        radio_selected_brnd.setChecked(false);
                        // Actions to do after 10 seconds
                        lin_hide.setVisibility(View.VISIBLE);
                        bill_arryList.clear();
                        bill_arryList.clear();
                        bill_recyclerAdapter.notifyDataSetChanged();
                        swap_arryList.clear();
                        swap_recyclerAdapter.notifyDataSetChanged();
                        //-------------------Checkbox-----------------------------------------------
                        String msg="";
                        if(chk_stk_all_loc.isChecked()) {
                            msg = msg + "stok_for_all_location ";
                            str_stk_all_loc = "1";
                        }
                        else
                        {
                            str_stk_all_loc = "0";
                        }
                        //if(chk_all.isChecked())
                         //   msg = msg + "all ";
                        str_all="1";

                        new insert_op().execute();
                           load_data();
                        btn_check_all.setVisibility(View.VISIBLE);


            }
        });

        btn_report=(Button)view.findViewById(R.id.btn_report);
        //----------Stream--------------------------------------------------------
        sp_counter_godown=(Spinner)view.findViewById(R.id.sp_counter_godown);
        new load_spinner_data().execute();

        //----------Stream--------------------------------------------------------


        btn_uncheck_all=(Button)view.findViewById(R.id.btn_uncheck_all);
        btn_check_all=(Button)view.findViewById(R.id.btn_check_all);
        btn_check_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //m_fullswapyn=1;
                btn_uncheck_all.setVisibility(View.VISIBLE);
                btn_check_all.setVisibility(View.INVISIBLE);
                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();
                try {
                    String qry="select distinct liqr_code,(select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+"";
                    con = CONN(con_ipaddress, portnumber,db);
                    if (con == null) {
                        Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                    } else {
                        if(radio_val.equals("Trader_stock"))
                        {
                            qry="select ac_head_id,case when ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end from glmast where ac_head_id in(select ac_head_id from tabreportparameters where TAB_CODE="+m_TAB_CODE+") order by gl_desc";
                        }
                        else  if(radio_val.equals("Group_stock"))
                        {
                            qry="select distinct liqr_code,(select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+"";
                        }
                        else  if(radio_val.equals("Brand"))
                        {
                            qry="select distinct brnd_code,(select brnd_desc from brndmast where brnd_code=tabreportparameters.brnd_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+" order by type";
                        }
                        PreparedStatement ps = con.prepareStatement(qry);
                        ResultSet rs = ps.executeQuery();

                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {
                            smap = new HashMap<String, String>();
                            smap.put("liqr_code", rs.getString(1));
                            smap.put("liqr_desc", rs.getString(2));
                            //-------------------------------------------
                            swap_arryList.add(smap);
                        }

                    }
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();}

            }
        });
        //-----------------------------------------------------------------------------------
        btn_uncheck_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                btn_check_all.setVisibility(View.VISIBLE);

                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();
                try {
                    String qry="select distinct liqr_code,(select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+"";
                    con = CONN(con_ipaddress, portnumber,db);
                    if (con == null) {
                        Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                    } else {
                        if(radio_val.equals("Trader_stock"))
                        {
                            qry="select ac_head_id,case when ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end from glmast where ac_head_id in(select ac_head_id from tabreportparameters where TAB_CODE="+m_TAB_CODE+") order by gl_desc";
                        }
                        else  if(radio_val.equals("Group_stock"))
                        {
                            qry="select distinct liqr_code,(select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+"";
                        }
                        else  if(radio_val.equals("Brand"))
                        {
                            qry="select distinct brnd_code,(select brnd_desc from brndmast where brnd_code=tabreportparameters.brnd_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+" order by type";
                        }
                        PreparedStatement ps = con.prepareStatement(qry);
                        ResultSet rs = ps.executeQuery();

                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {
                            map = new HashMap<String, String>();
                            map.put("liqr_code", rs.getString(1));
                            map.put("liqr_desc", rs.getString(2));
                            //-------------------------------------------
                            bill_arryList.add(map);

                        }

                    }
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();}

            }
        });
        //-----------------------------------------------------------------------------------
        btn_report=(Button)view.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();

                //progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        if (radio_summary_wise.isChecked() == true) {
                            Intent i = new Intent(getActivity(), Item_Wise_Stock_Summary_Report.class);
                            i.putExtra("cdate", Query_date);
                            i.putExtra("fdate", Temp_frm_date);
                            i.putExtra("tdate", Temp_to_date);
                            startActivity(i);
                        }
                       else {
                            if (swap_arryList.size() == 0) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                                builder.setTitle("Alert");
                                builder.setIcon(R.drawable.warn);
                                builder.setMessage("Please Click At Least One Checkbox");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                builder.show();
                            } else {
                                //-------------------Checkbox-------------------
                                String msg = "";
                                if (chk_stk_all_loc.isChecked()) {
                                    msg = msg + "stok_for_all_location ";
                                    str_stk_all_loc = "1";
                                } else {
                                    str_stk_all_loc = "0";
                                }
                                //if (chk_all.isChecked())
                                 //   msg = msg + "all ";
                                str_all = "1";

                                //=================Liqr_Code======================
                                M_liqr_code = "";
                                for (HashMap<String, String> map1 : swap_arryList)
                                    for (Map.Entry<String, String> mapEntry : map1.entrySet()) {
                                        if (mapEntry.getKey().equals("liqr_code")) {
                                            check_id = mapEntry.getValue();
                                            if (M_liqr_code.length() == 0) {
                                                M_liqr_code = check_id;
                                            } else {
                                                M_liqr_code = M_liqr_code + ',' + check_id;
                                            }
                                        }
                                    }
                                //=================================================
                                {
                                    if (radio_group_wise_stock.isChecked()) {
                                        Report_Group_Wise_Stock();
                                    } else if (radio_trade_wise_stock.isChecked()) {
                                        Report_Trader_Wise_Stock();
                                    }else{
                                        Report_Selected_Brand();
                                    }

                                }
                            }
                        }
                     pd.dismiss();
                        //   progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 5000);

            }
        });
    }

    public void load_data() {
        progressDoalog = new ProgressDialog(getActivity());
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        try {

            pbbar.setVisibility(View.VISIBLE);
            bill_arryList.clear();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                if(isNullOrEmpty(q))
                {

                    q = "select distinct liqr_code,(select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+"";
                }
                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("liqr_code", rs.getString(1));
                    map.put("liqr_desc", rs.getString(2));
                    //-------------------------------------------
                    bill_arryList.add(map);
                }
            }
            pbbar.setVisibility(View.GONE);
            progressDoalog.dismiss();

            if (bill_recyclerAdapter != null) {
                bill_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + bill_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "609" + e, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
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
            holder.name.setText(attendance_list.get(position).get("liqr_desc"));
            // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    smap= new HashMap<String, String>();
                    smap.put("liqr_code",attendance_list.get(position).get("liqr_code"));
                    smap.put("liqr_desc",attendance_list.get(position).get("liqr_desc"));

                    swap_arryList.add(smap);
                    swap_recyclerAdapter.notifyDataSetChanged();

                    // attendance_list.remove(position);
                    bill_arryList.remove(position);
                    bill_recyclerAdapter.notifyDataSetChanged();

                }
            });
        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }
        //MotionEvent myCopy = MotionEvent.obtain(event);
        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView action;
            LinearLayout lin;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
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

            holder.name.setText(attendance_list.get(position).get("liqr_desc"));
            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    map= new HashMap<String, String>();
                    map.put("liqr_code",attendance_list.get(position).get("liqr_code"));
                    map.put("liqr_desc",attendance_list.get(position).get("liqr_desc"));

                    bill_arryList.add(map);
                    bill_recyclerAdapter.notifyDataSetChanged();

                    //attendance_list.remove(position);
                    swap_arryList.remove(position);
                    swap_recyclerAdapter.notifyDataSetChanged();
                }
            });
        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView action;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.action = (ImageView) itemView.findViewById(R.id.imp_swap);

            }
        }
    }

    //==========================================
    public class insert_op extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
           /* progressDoalog = new ProgressDialog(getActivity());
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();*/
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try
            {
                con = CONN(con_ipaddress,portnumber,db);
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    //=======================CheckBox Condition===========================
                    if (m_loct_code.equals("0")) {
                        ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE = " + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("insert into tabreportparameters(item_code,tab_code) select distinct item_code," + m_TAB_CODE + " from opitstok where comp_code=1 union select distinct item_code," + m_TAB_CODE + " from puritem where doc_dt <= '" + Temp_to_date + "' and comp_code=1 union select distinct item_code," + m_TAB_CODE + " from countersaleitem where doc_dt<= '" + Temp_to_date + "' and comp_code=1 union select distinct item_code," + m_TAB_CODE + " from countersalereturnitem where doc_dt <= '" + Temp_to_date + "' and comp_code=1   union select distinct item_code," + m_TAB_CODE + " from chalanitem where doc_dt<= '" + Temp_to_date + "' and comp_code=1 union select distinct item_code," + m_TAB_CODE + " from opitstok_b where comp_code=1 union select distinct item_code," + m_TAB_CODE + " from provisionalsaleitem where doc_dt <= '" + Temp_to_date + "' and comp_code=1");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = (select isnull(sum(op_qty),0) from opitstok where comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + (select isnull(sum(op_qty),0) from opitstok_b where comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + (select isnull(sum(bottle_qty)+sum(free_qty),0) from puritem where doc_dt< '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 - (select isnull(sum(qty+breakage_qty),0) from countersaleitem where doc_dt< '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 - (select isnull(sum(qty+breakage_qty),0) from provisionalsaleitem where doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + (select isnull(sum(qty),0) from countersalereturnitem where doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + (select isnull(sum(bottle_qty),0) from chalanitem where doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        //''''''''''Transactions Within From-To Dates
                        ps1 = con.prepareStatement("update tabreportparameters set amount_2 = (select isnull(sum(bottle_qty)+sum(free_qty),0) from puritem where doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set tot_sale = (select isnull(sum(qty+breakage_qty),0) from countersaleitem where doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale+(select isnull(sum(qty+breakage_qty),0) from provisionalsaleitem where doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale - (select isnull(sum(qty),0) from countersalereturnitem where doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_3 = (select isnull(sum(bottle_qty),0) from chalanitem where doc_dt  between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount = amount_1+amount_2-tot_sale+amount_3 where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE =" + m_TAB_CODE + " and (amount_1+amount_2+tot_sale+amount_3) = 0 ");
                        ps1.executeUpdate();
                    } else {
                        //''''''''''Opening Stock For From Date
                        ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("insert into tabreportparameters(item_code,TAB_CODE) select distinct item_code," + m_TAB_CODE + " from opitstok where loct_code = " + m_loct_code + " and comp_code=1 union select distinct item_code," + m_TAB_CODE + " from puritem where doc_dt <= '" + Temp_to_date + "' and loct_code = " + m_loct_code + " and comp_code=1 union select distinct item_code," + m_TAB_CODE + " from countersaleitem where doc_dt<= '" + Temp_to_date + "' and loct_code = " + m_loct_code + " and comp_code=1 union select distinct item_code," + m_TAB_CODE + " from countersalereturnitem where doc_dt<= '" + Temp_to_date + "' and loct_code = " + m_loct_code + " and comp_code=1  union select distinct item_code," + m_TAB_CODE + " from chalanitem where doc_dt<= '" + Temp_to_date + "' and loct_code = " + m_loct_code + " and comp_code=1 union select distinct item_code," + m_TAB_CODE + " from opitstok_b where loct_code = " + m_loct_code + " and comp_code=1  union select distinct item_code," + m_TAB_CODE + " from transfernote where doc_dt <= '" + Temp_to_date + "' and from_loct_code = " + m_loct_code + " and comp_code=1 union select distinct item_code," + m_TAB_CODE + " from transfernote where to_loct_code = " + m_loct_code + " and comp_code=1 union select distinct item_code," + m_TAB_CODE + " from provisionalsaleitem where doc_dt <= '" + Temp_to_date + "' and loct_code = " + m_loct_code + " and comp_code=1 ");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = (select isnull(sum(op_qty),0) from opitstok where loct_code = " + m_loct_code + " and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + (select isnull(sum(op_qty),0) from opitstok_b where loct_code = " + m_loct_code + " and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + (select isnull(sum(bottle_qty)+sum(free_qty),0) from puritem where loct_code = " + m_loct_code + " and doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 - (select isnull(sum(qty+breakage_qty),0) from countersaleitem where loct_code = " + m_loct_code + " and doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 - (select isnull(sum(qty+breakage_qty),0) from provisionalsaleitem where loct_code = " + m_loct_code + " and doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + (select isnull(sum(qty),0) from countersalereturnitem where loct_code = " + m_loct_code + " and doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + (select isnull(sum(bottle_qty),0) from chalanitem where loct_code = " + m_loct_code + " and doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 + (select isnull(sum(bottle_qty),0) from transfernote where to_loct_code = " + m_loct_code + " and doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1 - (select isnull(sum(bottle_qty),0) from transfernote where from_loct_code = " + m_loct_code + " and doc_dt < '" + Temp_frm_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        //''''''''''Transactions Within From-To Dates
                        ps1 = con.prepareStatement("update tabreportparameters set amount_2 = (select isnull(sum(bottle_qty)+sum(free_qty),0) from puritem where loct_code = " + m_loct_code + " and doc_dt  between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_2 = amount_2 + (select isnull(sum(bottle_qty),0) from transfernote where to_loct_code = " + m_loct_code + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set tot_sale = (select isnull(sum(qty+breakage_qty),0) from countersaleitem where loct_code = " + m_loct_code + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale+(select isnull(sum(qty+breakage_qty),0) from provisionalsaleitem where loct_code = " + m_loct_code + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale + (select isnull(sum(bottle_qty),0) from transfernote where from_loct_code = " + m_loct_code + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale - (select isnull(sum(qty),0) from countersalereturnitem where loct_code = " + m_loct_code + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount_3 = (select isnull(sum(bottle_qty),0) from chalanitem where loct_code = " + m_loct_code + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code=1 and item_code=tabreportparameters.item_code) where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set amount = amount_1+amount_2-tot_sale+amount_3 where TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE =" + m_TAB_CODE + " and (amount_1+amount_2+tot_sale+amount_3) = 0 ");
                        ps1.executeUpdate();

                    }
                    if(radio_group_wise_stock.isChecked()){

                        ps1 = con.prepareStatement("update tabreportparameters set liqr_code = (select liqr_code from itemmast where item_code=tabreportparameters.item_code) where TAB_CODE="+m_TAB_CODE+"");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set brnd_code = (select brnd_code from itemmast where item_code = tabreportparameters.item_code) where TAB_CODE="+m_TAB_CODE+"");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set ac_head_id = (select trader_ac_head_id from itemmast,brndmast where itemmast.item_code = tabreportparameters.item_code and itemmast.brnd_code = brndmast.brnd_code) where TAB_CODE = " + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        q="select distinct liqr_code,(select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code) as Type,'' from tabreportparameters where TAB_CODE="+m_TAB_CODE+"";

                     }
                    else{

                        ps1 = con.prepareStatement("update tabreportparameters set brnd_code = (select brnd_code from itemmast where item_code = tabreportparameters.item_code) where TAB_CODE="+m_TAB_CODE+"");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set liqr_code = (select mainliqrhead_code from brndmast where brnd_code = tabreportparameters.brnd_code) where TAB_CODE="+m_TAB_CODE+"");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set main_liqr_desc =  'IMFL',liqr_code=1 where TAB_CODE="+m_TAB_CODE+" and liqr_code in(1,2)");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set main_liqr_desc =  'WINE' where TAB_CODE="+m_TAB_CODE+" and liqr_code in(3,4)");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set main_liqr_desc =  'STRONG BEER' where TAB_CODE="+m_TAB_CODE+" and liqr_code in(5)");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set main_liqr_desc =  'MILD BEER' where TAB_CODE="+m_TAB_CODE+" and liqr_code in(6)");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set main_liqr_desc =  'COLD DRINKS & OTHERS' where TAB_CODE="+m_TAB_CODE+" and liqr_code in(7)");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("update tabreportparameters set liqr_code=17, main_liqr_desc =  'COUNTRY' where TAB_CODE="+m_TAB_CODE+" and item_code in(select item_code from itemmast where liqr_code = 17)");
                        ps1.executeUpdate();
                        q="select distinct liqr_code,main_liqr_desc from tabreportparameters where TAB_CODE="+m_TAB_CODE+" order by liqr_code";

                    }
                    //''''''''''''New Addition On 20/10/2020
                    ps1 = con.prepareStatement("update tabreportparameters set gl_desc = (select LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,(amount_1/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))) + '.' + LTRIM(STR(amount_1-((SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE))* CONVERT(INT,(CONVERT(FLOAT,(amount_1/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))))) where TAB_CODE="+m_TAB_CODE+"");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set doc_no = (select LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,((amount_2+amount_3)/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))) + '.' + LTRIM(STR((amount_2+amount_3)-((SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE))* CONVERT(INT,(CONVERT(FLOAT,((amount_2+amount_3)/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))))) where TAB_CODE="+m_TAB_CODE+"");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set doc_type = (select LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,(tot_sale/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))) + '.' + LTRIM(STR(tot_sale-((SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE))* CONVERT(INT,(CONVERT(FLOAT,(tot_sale/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))))) where TAB_CODE="+m_TAB_CODE+"");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set crdr_cd = (select LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,(amount/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))) + '.' + LTRIM(STR(amount-((SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE))* CONVERT(INT,(CONVERT(FLOAT,(amount/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))))) where TAB_CODE="+m_TAB_CODE+" and amount >=0");
                    ps1.executeUpdate();

                    // Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }
                con.close();

            }catch(Exception e)
            {
                Log.d("eeee",""+e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
           pd.dismiss();
            load_data();
            if(bill_arryList.size()==0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.warn);
                builder.setMessage("Record Not Found");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }

            btn_check_all.setVisibility(View.VISIBLE);
            super.onPostExecute(s);
        }
    }
    //==========================================
    public void Report_Group_Wise_Stock()
    {
        try {
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(getActivity(), Item_Wise_stock_within_peroid_Report.class);
                i.putExtra("checklist", M_liqr_code);
                i.putExtra("str_price_type_title", str_price_type_title);
                i.putExtra("m_pricetype", str_cost_evaluation);
                i.putExtra("Query_date", Query_date);
                i.putExtra("brnd_chk", str_brnd_chk);
                i.putExtra("fdate", Temp_frm_date);
                i.putExtra("tdate", Temp_to_date);
                i.putExtra("stock_wise_radioButton", "Group");
                   try {
                      if (radio_bottle.isChecked() == true) {
                          i.putExtra("stock_in_radioButton", "Bottles");
                          } else {
                          i.putExtra("stock_in_radioButton", "Cases");
                                 }
                         } catch (Exception e) {  }
                          startActivity(i);
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "973"+e, Toast.LENGTH_SHORT).show();
        }
    }
    public void Report_Trader_Wise_Stock()
    {
        try {

            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {

                Intent i = new Intent(getActivity(), Item_Wise_stock_within_peroid_Report.class);
                i.putExtra("checklist", M_liqr_code);
                i.putExtra("str_price_type_title", str_price_type_title);
                i.putExtra("m_pricetype", str_cost_evaluation);
                i.putExtra("Query_date", Query_date);
                i.putExtra("brnd_chk", str_brnd_chk);
                i.putExtra("fdate", Temp_frm_date);
                i.putExtra("tdate", Temp_to_date);
                i.putExtra("stock_wise_radioButton", "Trade");
                try {
                    if (radio_bottle.isChecked() == true) {
                        i.putExtra("stock_in_radioButton", "Bottles");
                    } else {
                        i.putExtra("stock_in_radioButton", "Cases");
                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "1001"+e, Toast.LENGTH_SHORT).show();
                }
                startActivity(i);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "1005"+e, Toast.LENGTH_SHORT).show();
        }
    }

    public void  Report_Selected_Brand()
    {
        try {

            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {

                Intent i = new Intent(getActivity(), Item_Wise_stock_within_peroid_Report.class);
                i.putExtra("checklist", M_liqr_code);
                i.putExtra("str_price_type_title", str_price_type_title);
                i.putExtra("m_pricetype", str_cost_evaluation);
                i.putExtra("Query_date", Query_date);
                i.putExtra("brnd_chk", str_brnd_chk);
                i.putExtra("stock_wise_radioButton", "Brand");
                try {
                    if (radio_bottle.isChecked() == true) {
                        i.putExtra("stock_in_radioButton", "Bottles");
                    } else {
                        i.putExtra("stock_in_radioButton", "Cases");
                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "1035"+e, Toast.LENGTH_SHORT).show();
                }
                startActivity(i);

            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "1042"+e, Toast.LENGTH_SHORT).show();
        }
    }
    //=====================================
    public class load_spinner_data extends AsyncTask<String, String, String> {
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
                con = CONN(con_ipaddress,portnumber,db);
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    String query = "select LOCT_CODE,LOCT_DESC from LOCTmast where LOCT_CODE >0 and loct_code in(select loct_code from onlnstok) union select 0 as loct_code,'TOTAL LOCATIONS' as loct_desc order by LOCT_DESC ";
                   // String query = "select loct_code,loct_desc from loctmast where loct_code in(select loct_code from onlnstok)";
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
            sp_counter_godown.setAdapter(spnr_data);
            sp_counter_godown.setSelection(0);
            sp_counter_godown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    m_loct_desc = (String) obj.get("A");
                    m_loct_code = (String) obj.get("B");
                    bill_arryList.clear();
                    bill_recyclerAdapter.notifyDataSetChanged();
                    swap_arryList.clear();
                    swap_recyclerAdapter.notifyDataSetChanged();
                    btn_uncheck_all.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
        }
    }

    public Connection CONN(String ip,String port,String db) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll().build();

        StrictMode.setThreadPolicy(policy);

        con = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";;
            con = DriverManager.getConnection(ConnURL);

        } catch (Exception se) {

            Log.e("ERRO", se.getMessage());

        }

        return con;

    }
}