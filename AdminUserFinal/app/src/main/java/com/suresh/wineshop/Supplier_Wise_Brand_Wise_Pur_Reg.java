package com.suresh.wineshop;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Supplier_Wise_Brand_Wise_Pur_Reg extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date;
    int m_TAB_CODE;
    String con_ipaddress ,portnumber, str_month="",str_day="";
    ProgressBar pbbar;
    ProgressDialog progressDoalog;
    TransparentProgressDialog pd;
    Button btn_proceed,btn_report,btn_check_all,btn_uncheck_all;
    String IMEINumber,m_ac_head_id,m_sup_name;
    int m_compcode;
    Date date;
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
    Spinner sp_sup;
    //--------------------------
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    // String db = "WINESHOP";
    // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    ProgressBar progressBar;
    PreparedStatement ps1;
    String Temp_frm_date,Temp_to_date,formattedDate,db;
    Date d;
    RadioGroup radioGroup,radiowiseGroup;

    LinearLayout lin_radio_hide;
   //static int doc_no=0;
   int seq_no = 0;
    public Supplier_Wise_Brand_Wise_Pur_Reg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.supp_wise_brnd_wise_pur_reg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences ss = getActivity().getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        pbbar = (ProgressBar) view.findViewById(R.id.pgb);
        lin_radio_hide = (LinearLayout) view.findViewById(R.id.lin_radio_hide);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar_cyclic);


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
                btn_check_all.setVisibility(View.INVISIBLE);
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                lin_radio_hide.setVisibility(View.INVISIBLE);

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
                btn_check_all.setVisibility(View.INVISIBLE);
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                lin_radio_hide.setVisibility(View.INVISIBLE);

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

        //---------------------Recyclerview 2-----------------------------------------
        bill_arryList = new ArrayList<HashMap<String, String>>();
        recycler_bill_list = (RecyclerView) view.findViewById(R.id.recycler_bill_list);
        layoutManager_bill = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_bill_list.setLayoutManager(layoutManager_bill);
        bill_recyclerAdapter = new tbill_recyclerAdapter(getActivity(), bill_arryList);
        recycler_bill_list.setAdapter(bill_recyclerAdapter);

        // txt_tbl_bill = (TextView) findViewById(R.id.txt_tbl_bill);
        //------------------------------------------------------------------------------------------
        //---------------------Recyclerview swap-----------------------------------------
        swap_arryList = new ArrayList<HashMap<String, String>>();
        recycler_swap_list = (RecyclerView)view. findViewById(R.id.recycler_swap_list);
        layoutManager_swap = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_swap_list.setLayoutManager(layoutManager_swap);
        swap_recyclerAdapter=new tswap_recyclerAdapter(getActivity(),swap_arryList);
        recycler_swap_list.setAdapter(swap_recyclerAdapter);
     //----------Supplier--------------------------------------------------------
      //  sp_sup=(Spinner)view.findViewById(R.id.sp_sup);

        btn_proceed=(Button)view.findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_check_all.setVisibility(View.VISIBLE);
                    lin_radio_hide.setVisibility(View.VISIBLE);
                    btn_uncheck_all.setVisibility(View.INVISIBLE);

                    bill_arryList.clear();
                    bill_recyclerAdapter.notifyDataSetChanged();
                    swap_arryList.clear();
                    swap_recyclerAdapter.notifyDataSetChanged();
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                      //  insert_data(Temp_frm_date, Temp_to_date);
                       // new load_spinner_data().execute();
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
                            // btn_uncheck_all.setVisibility(View.VISIBLE);
                            btn_check_all.setVisibility(View.VISIBLE);
                            // progressBar.setVisibility(View.INVISIBLE);
                        pd.dismiss();
                    }
                }, 5000);

            }
        });

        //------------------------------------------------------------------------------------------
        btn_uncheck_all=(Button)view.findViewById(R.id.btn_uncheck_all);
        btn_check_all=(Button)view.findViewById(R.id.btn_check_all);
        btn_check_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_uncheck_all.setVisibility(View.VISIBLE);
                btn_check_all.setVisibility(View.INVISIBLE);

                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();
                try {
                    con = CONN(con_ipaddress, portnumber,db);
                    PreparedStatement ps = con.prepareStatement("select ac_head_id,gl_desc , plac_desc from glmast,placmast where placmast.plac_code=glmast.plac_code and ac_head_id in(select ac_head_id from purchase where invoice_date between  '"+Temp_frm_date+ "' and '"+Temp_to_date+"' and comp_code=1  and tran_type='ONLINE' and pur_type_caSh_credit='CREDIT' and pur_type=1)  order by gl_desc");
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        smap = new HashMap<String, String>();
                        smap.put("ac_head_id", rs.getString("ac_head_id"));
                        smap.put("plac_desc", rs.getString("plac_desc"));
                        smap.put("gl_desc", rs.getString("gl_desc"));

                        //-------------------------------------------
                        swap_arryList.add(smap);

                    }
                }
                catch (NullPointerException e)
                {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();
                }
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
                    con = CONN(con_ipaddress, portnumber,db);
                    PreparedStatement ps = con.prepareStatement("select ac_head_id,gl_desc , plac_desc from glmast,placmast where placmast.plac_code=glmast.plac_code and ac_head_id in(select ac_head_id from purchase where invoice_date between  '"+Temp_frm_date+ "' and '"+Temp_to_date+"' and comp_code=1  and tran_type='ONLINE' and pur_type_caSh_credit='CREDIT' and pur_type=1)  order by gl_desc");
                    ResultSet rs = ps.executeQuery();

                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        map = new HashMap<String, String>();
                        map.put("ac_head_id", rs.getString("ac_head_id"));
                        map.put("plac_desc", rs.getString("plac_desc"));
                        map.put("gl_desc", rs.getString("gl_desc"));

                        //-------------------------------------------
                        bill_arryList.add(map);

                    }

                }
                catch (NullPointerException e) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){ Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();}
            }
        });
        //-----------------------------------------------------------------------------------
        btn_report=(Button)view.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // progressBar.setVisibility(View.VISIBLE);
                try
                {
                    con = CONN(con_ipaddress,portnumber,db);
                     ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE ="+m_TAB_CODE+"");
                     ps1.executeUpdate();

                    con.close();
                }
                catch(NullPointerException e)
                {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e)
                {
                    Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
                }
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if(swap_arryList.size()==0)
                        {
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
                        }
                        else {
                            String check_id = "", M_liqr_code = "";

                            for (HashMap<String, String> map1 : swap_arryList)
                                for (Map.Entry<String, String> mapEntry : map1.entrySet()) {
                                    if (mapEntry.getKey().equals("ac_head_id")) {
                                        check_id = mapEntry.getValue();
                                        seq_no= seq_no+1;
                                        insert_data(Temp_frm_date,Temp_to_date,check_id,seq_no);
                                        seq_no= seq_no+1;
//
                                    }

                                }
                            Intent i = new Intent(getActivity(), Sup_Brnd_Pur_Report.class);
                            i.putExtra("checklist", M_liqr_code);
                            i.putExtra("ac_head_id", m_ac_head_id);
                            i.putExtra("from_date", edt_frm_date.getText().toString());
                            i.putExtra("qfrom_date", Temp_frm_date);
                            i.putExtra("to_date", edt_to_date.getText().toString());
                            i.putExtra("qto_date", Temp_to_date);

                            startActivity(i);
                        }
                        pd.dismiss();
                        //  progressBar.setVisibility(View.INVISIBLE);
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
            progressDoalog.dismiss();
            pbbar.setVisibility(View.VISIBLE);
            bill_arryList.clear();
            con = CONN(con_ipaddress,portnumber,db);

            String q="select ac_head_id,gl_desc , plac_desc from glmast,placmast where placmast.plac_code=glmast.plac_code and ac_head_id in(select ac_head_id from purchase where invoice_date between  '"+Temp_frm_date+ "' and '"+Temp_to_date+"' and comp_code=1  and tran_type='ONLINE' and pur_type_caSh_credit='CREDIT' and pur_type=1)  order by gl_desc";
           // String q="select liqr_code,liqr_desc from liqrmast where liqr_code in(select distinct liqr_code from tabreportparameters where item_code <> '' and TAB_CODE="+m_TAB_CODE+") order by liqr_desc";
            Log.d("final query_____",q);
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            //ArrayList data1 = new ArrayList();
            while (rs.next()) {
                map= new HashMap<String, String>();
                map.put("ac_head_id", rs.getString("ac_head_id"));
                map.put("gl_desc", rs.getString("gl_desc"));
                map.put("plac_desc", rs.getString("plac_desc"));

                //-------------------------------------------
                bill_arryList.add(map);
            }

            pbbar.setVisibility(View.GONE);
            Log.d("bill_arryList_Data", "" + bill_arryList.toString());
            if (bill_recyclerAdapter != null) {
                bill_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + bill_recyclerAdapter.toString());
            }

        }
        catch (NullPointerException e)
        {

            Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e) {
            Toast.makeText(getActivity(), "308" + e, Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sup_swap_table, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            // holder.contact_list_id.setText(attendance_list.get(position).get("A"));
            holder.name.setText(attendance_list.get(position).get("gl_desc"));
            holder.bname.setText(attendance_list.get(position).get("plac_desc"));
            // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    smap= new HashMap<String, String>();
                    smap.put("gl_desc",attendance_list.get(position).get("gl_desc"));
                    smap.put("plac_desc",attendance_list.get(position).get("plac_desc"));
                    smap.put("ac_head_id",attendance_list.get(position).get("ac_head_id"));

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

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name,bname;
            ImageView action;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.bname = (TextView) itemView.findViewById(R.id.list_d2);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sup_swap_table_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.name.setText(attendance_list.get(position).get("gl_desc"));
            holder.bname.setText(attendance_list.get(position).get("plac_desc"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    map= new HashMap<String, String>();
                    map.put("gl_desc",attendance_list.get(position).get("gl_desc"));
                    map.put("plac_desc",attendance_list.get(position).get("plac_desc"));
                    map.put("ac_head_id",attendance_list.get(position).get("ac_head_id"));

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
            TextView name, bname;
            ImageView action;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.bname = (TextView) itemView.findViewById(R.id.list_d2);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.action = (ImageView) itemView.findViewById(R.id.imp_swap);

            }
        }
    }

    public void insert_data(String frm_date,String to_date,String check_id,int seq_no)
    {
        //===========ALL==============================
        try
        {
            con = CONN(con_ipaddress,portnumber,db);

            ps1 = con.prepareStatement("insert into tabreportparameters(ac_head_id,tab_code,doc_no) values("+check_id+","+m_TAB_CODE+","+seq_no+")");
            ps1.executeUpdate();
            seq_no++;
            ps1 = con.prepareStatement("insert into tabreportparameters(ac_head_id,item_code,gl_opbal,gl_clbal,tab_code,doc_no)select "+check_id+",item_code,isnull(sum(bottle_qty),0),isnull(sum(quantity),0),"+m_TAB_CODE+","+seq_no+" from puritem where doc_dt between '"+frm_date+"' and '"+to_date+"' and ac_head_id ="+check_id+" group by item_code");
            ps1.executeUpdate();

            con.close();
        }
        catch(NullPointerException e)
        {
            Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
        }
    }
//    public class load_spinner_data extends AsyncTask<String, String, String> {
//        List<Map<String, String>> sp_data = new ArrayList<Map<String, String>>();
//
//        @Override
//        protected void onPreExecute() {
//            progressDoalog = new ProgressDialog(getActivity());
//            progressDoalog.setMessage("Loading....");
//            progressDoalog.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//            try {
//                con = CONN(con_ipaddress,portnumber,db);
//                if (con == null) {
//                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    String query = "select ac_head_id,gl_desc + ', ' + plac_desc from glmast,placmast where placmast.plac_code=glmast.plac_code and ac_head_id in(select ac_head_id from purchase where invoice_date between  '"+Temp_frm_date+ "' and '"+Temp_to_date+"' and comp_code=1  and tran_type='ONLINE' and pur_type_caSh_credit='CREDIT' and pur_type=1)  order by gl_desc";
//                  //  String query = "select loct_code,loct_desc from loctmast where loct_code in(select loct_code from onlnstok)";
//                    PreparedStatement ps = con.prepareStatement(query);
//                    ResultSet rs = ps.executeQuery();
//
//                    while (rs.next()) {
//                        Map<String, String> data = new HashMap<String, String>();
//                        data.put("B", rs.getString(1));
//                        data.put("A", rs.getString(2));
//                        sp_data.add(data);
//                    }
//                }
//                //con.close();
//            } catch (Exception e) {
//
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            progressDoalog.dismiss();
//            String[] from = {"A", "B"};
//            int[] views = {R.id.list_d1};
//
//            final SimpleAdapter spnr_data = new SimpleAdapter(getActivity(), sp_data, R.layout.spin, from, views);
//            sp_sup.setAdapter(spnr_data);
//            sp_sup.setSelection(0);
//            sp_sup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
//                    m_sup_name = (String) obj.get("A");
//                    m_ac_head_id = (String) obj.get("B");
//
//
//                    btn_check_all.setVisibility(View.VISIBLE);
//                    lin_radio_hide.setVisibility(View.VISIBLE);
//                    btn_uncheck_all.setVisibility(View.INVISIBLE);
//
//
//                    bill_arryList.clear();
//                    bill_recyclerAdapter.notifyDataSetChanged();
//                    swap_arryList.clear();
//                    swap_recyclerAdapter.notifyDataSetChanged();
//                    pd.show();
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
//
//                            //  insert_data(Temp_frm_date, Temp_to_date);
//                            load_data(m_ac_head_id);
//                            //load_data();
//                            if(bill_arryList.size()==0)
//                            {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
//                                builder.setTitle("Alert");
//                                builder.setIcon(R.drawable.warn);
//                                builder.setMessage("Record Not Found");
//                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNegativeButton("Cancel", null);
//                                builder.show();
//                            }
//                            // btn_uncheck_all.setVisibility(View.VISIBLE);
//                            btn_check_all.setVisibility(View.VISIBLE);
//                            // progressBar.setVisibility(View.INVISIBLE);
//                            pd.dismiss();
//                        }
//                    }, 5000);
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//            });
//            super.onPostExecute(s);
//        }
//    }
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
