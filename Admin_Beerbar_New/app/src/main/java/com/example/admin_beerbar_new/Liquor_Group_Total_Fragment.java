package com.example.admin_beerbar_new;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin_beerbar_new.Class.TransparentProgressDialog;

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
public class Liquor_Group_Total_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date;
    int m_TAB_CODE;
    String con_ipaddress ,portnumber, str_month="",str_day="";
    ProgressBar pbbar;
    ProgressDialog progressDoalog;
    TransparentProgressDialog pd;
    Button btn_proceed,btn_report,btn_check_all,btn_uncheck_all;
    String IMEINumber,db;
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

    //--------------------------
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    //String db = "WINESHOP";
    // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    ProgressBar progressBar;
    PreparedStatement ps1;
    String Temp_frm_date,Temp_to_date,formattedDate;
    int flag=0;
    String check="";
    String check_id = "", M_liqr_code = "";
    String fcheck_id = "", M_food_code = "";
    LinearLayout lin_radio_hide;
    public Liquor_Group_Total_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liquor_group_total, container, false);
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
                }, 3000);

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

                            PreparedStatement ps = con.prepareStatement("select distinct ltrim(str(menumast.MENU_CODE))+'F' as menu_code,MENU_DESC,0 AS SEQNO from menumast,MENUCARDITEMMAST,SALEITEM WHERE bargroup_yn=1 and menumast.MENU_CODE=MENUCARDITEMMAST.MENU_CODE and MENUITEM_CODE=SALEITEM.ITEM_CODE AND ITEM_TYPE =3 and doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' UNION selecT distinct ltrim(str(LIQRMAST.LIQR_CODE))+'L',LIQR_DESC,1 AS SEQNO from LIQRmast,ITEMMAST,SALEITEM WHERE LIQRmast.LIQR_CODE=ITEMMAST.LIQR_CODE and SALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE <>3 and doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' order by SEQNO,MENU_DESC");
                            ResultSet rs = ps.executeQuery();

                            while (rs.next()) {
                                smap = new HashMap<String, String>();
                                smap.put("menu_code", rs.getString("menu_code"));
                                smap.put("menu_desc", rs.getString("menu_desc"));

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
                    PreparedStatement ps = con.prepareStatement("select distinct ltrim(str(menumast.MENU_CODE))+'F' as menu_code,MENU_DESC,0 AS SEQNO from menumast,MENUCARDITEMMAST,SALEITEM WHERE bargroup_yn=1 and menumast.MENU_CODE=MENUCARDITEMMAST.MENU_CODE and MENUITEM_CODE=SALEITEM.ITEM_CODE AND ITEM_TYPE =3 and doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' UNION selecT distinct ltrim(str(LIQRMAST.LIQR_CODE))+'L',LIQR_DESC,1 AS SEQNO from LIQRmast,ITEMMAST,SALEITEM WHERE LIQRmast.LIQR_CODE=ITEMMAST.LIQR_CODE and SALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE <>3 and doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' order by SEQNO,MENU_DESC");
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        smap = new HashMap<String, String>();
                        smap.put("menu_code", rs.getString("menu_code"));
                        smap.put("menu_desc", rs.getString("menu_desc"));
                                            //-------------------------------------------
                        bill_arryList.add(smap);
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
        btn_report=(Button)view.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // progressBar.setVisibility(View.VISIBLE);
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
                            check="";
                            check_id = "";
                            M_liqr_code = "";
                            fcheck_id = "";
                            M_food_code = "";
                            for (HashMap<String, String> map1 : swap_arryList)
                                for (Map.Entry<String, String> mapEntry : map1.entrySet()) {
                                    if (mapEntry.getKey().equals("menu_code")) {

                                        check_id = mapEntry.getValue();
                                        if(check_id.contains("F")) {
                                            check_id= check_id.replace("F","");
                                            if (M_food_code.length() == 0) {
                                                M_food_code = check_id;
                                            } else {
                                                M_food_code = M_food_code + ',' + check_id;
                                            }
                                        }
                                        else
                                        {  check_id= check_id.replace("L","");
                                            if (M_liqr_code.length() == 0) {
                                                M_liqr_code = check_id;
                                            } else {
                                                M_liqr_code = M_liqr_code + ',' + check_id;
                                            }
                                        }
                                    }
                                }
                            insert_data(Temp_frm_date, Temp_to_date);
                            Intent i = new Intent(getActivity(), Liquor_Group_Total_Report.class);
                            i.putExtra("checklist", M_liqr_code);
                            i.putExtra("from_date", edt_frm_date.getText().toString());
                            i.putExtra("to_date", edt_to_date.getText().toString());

                            startActivity(i);
                        }
                        pd.dismiss();
                      //  progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 2000);

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

                String q="select distinct ltrim(str(menumast.MENU_CODE))+'F' as menu_code,MENU_DESC,0 AS SEQNO from menumast,MENUCARDITEMMAST,SALEITEM WHERE bargroup_yn=1 and menumast.MENU_CODE=MENUCARDITEMMAST.MENU_CODE and MENUITEM_CODE=SALEITEM.ITEM_CODE AND ITEM_TYPE =3 and doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' UNION selecT distinct ltrim(str(LIQRMAST.LIQR_CODE))+'L',LIQR_DESC,1 AS SEQNO from LIQRmast,ITEMMAST,SALEITEM WHERE LIQRmast.LIQR_CODE=ITEMMAST.LIQR_CODE and SALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE <>3 and doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' order by SEQNO,MENU_DESC";
                Log.d("final query_____",q);
                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("menu_code", rs.getString("menu_code"));
                    map.put("menu_desc", rs.getString("menu_desc"));
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

                Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swap_table, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {
            // holder.contact_list_id.setText(attendance_list.get(position).get("A"));
            holder.name.setText(attendance_list.get(position).get("menu_desc"));
           // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    smap= new HashMap<String, String>();
                    smap.put("menu_code",attendance_list.get(position).get("menu_code"));
                    smap.put("menu_desc",attendance_list.get(position).get("menu_desc"));
                    smap.put("SEQNO",attendance_list.get(position).get("SEQNO"));

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
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.name.setText(attendance_list.get(position).get("menu_desc"));
           // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    map= new HashMap<String, String>();
                    map.put("menu_code",attendance_list.get(position).get("menu_code"));
                    map.put("menu_desc",attendance_list.get(position).get("menu_desc"));
                    map.put("SEQNO",attendance_list.get(position).get("SEQNO"));

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
            TextView name, qty,  lsize,time;
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
    public void insert_data(String frm_date,String to_date)
    {
        //===========ALL==============================
        try
        {
             con = CONN(con_ipaddress,portnumber,db);
            ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();
            if(M_food_code.length() > 0) {
                ps1 = con.prepareStatement("INSERT INTO TABREPORTPARAMETERS(ITEM_CODE,AMOUNT,TAB_CODE,AC_HEAD_ID,AMOUNT_1,AMOUNT_2) SELECT ITEM_CODE,SUM(ITEM_VALUE)," + m_TAB_CODE + ",MENU_CODE,1,SUM(QTY) from SALEITEM,MENUCARDITEMMAST WHERE ITEM_TYPE = 3 AND SALEITEM.ITEM_CODE=MENUCARDITEMMAST.MENUITEM_CODE AND MENU_CODE IN(" + M_food_code + ") and doc_dt between '" + frm_date + "' and '" + to_date + "' GROUP BY MENU_CODE,ITEM_CODE");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET GL_DESC = (SELECT MENUITEM_DESC FROM MENUCARDITEMMAST WHERE MENUCARDITEMMAST.MENUITEM_CODE=TABREPORTPARAMETERS.ITEM_CODE AND TAB_CODE=" + m_TAB_CODE + ") WHERE TAB_CODE=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("INSERT INTO TABREPORTPARAMETERS(AMOUNT,TAB_CODE,AC_HEAD_ID,AMOUNT_1) SELECT SUM(AMOUNT)," + m_TAB_CODE + ",AC_HEAD_ID,1 FROM TABREPORTPARAMETERS WHERE TAB_CODE=" + m_TAB_CODE + " GROUP BY AC_HEAD_ID");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET ITEM_CODE = (SELECT MENU_DESC FROM MENUMAST WHERE MENUMAST.MENU_CODE=TABREPORTPARAMETERS.AC_HEAD_ID AND TAB_CODE=" + m_TAB_CODE + ") WHERE TAB_CODE=" + m_TAB_CODE + "");
                ps1.executeUpdate();
            }

            ps1 = con.prepareStatement("INSERT INTO TABREPORTPARAMETERS(ITEM_CODE,AMOUNT,TAB_CODE,AC_HEAD_ID,AMOUNT_1,AMOUNT_2) SELECT SALEITEM.ITEM_CODE,SUM(ITEM_VALUE),"+m_TAB_CODE+",LIQR_CODE,2,SUM(QTY) from SALEITEM,ITEMMAST WHERE ITEM_TYPE <> 3 AND SALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND LIQR_CODE IN("+M_liqr_code+") and doc_dt between '"+frm_date+"' and '"+to_date+"' GROUP BY LIQR_CODE,SALEITEM.ITEM_CODE");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET GL_DESC = (SELECT BRND_DESC FROM ITEMMAST,BRNDMAST WHERE ITEMMAST.BRND_CODE = BRNDMAST.BRND_CODE AND ITEMMAST.ITEM_CODE=TABREPORTPARAMETERS.ITEM_CODE AND TAB_CODE="+m_TAB_CODE+" AND AMOUNT_1=2) WHERE TAB_CODE="+m_TAB_CODE+" AND AMOUNT_1=2");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET DOC_TYPE = (SELECT SIZE_DESC FROM ITEMMAST,SIZEMAST WHERE ITEMMAST.SIZE_CODE = SIZEMAST.SIZE_CODE AND ITEMMAST.ITEM_CODE=TABREPORTPARAMETERS.ITEM_CODE AND TAB_CODE="+m_TAB_CODE+" AND AMOUNT_1=2) WHERE TAB_CODE="+m_TAB_CODE+" AND AMOUNT_1=2");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET DOC_NO = (SELECT SEQ_NO FROM ITEMMAST,SIZEMAST WHERE ITEMMAST.SIZE_CODE = SIZEMAST.SIZE_CODE AND ITEMMAST.ITEM_CODE=TABREPORTPARAMETERS.ITEM_CODE AND TAB_CODE="+m_TAB_CODE+" AND AMOUNT_1=2) WHERE TAB_CODE="+m_TAB_CODE+" AND AMOUNT_1=2");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("INSERT INTO TABREPORTPARAMETERS(AMOUNT,TAB_CODE,AC_HEAD_ID,AMOUNT_1) SELECT SUM(AMOUNT),"+m_TAB_CODE+",AC_HEAD_ID,2 FROM TABREPORTPARAMETERS WHERE TAB_CODE="+m_TAB_CODE+" AND AMOUNT_1=2 GROUP BY AC_HEAD_ID");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET ITEM_CODE = (SELECT LIQR_DESC FROM LIQRMAST WHERE LIQRMAST.LIQR_CODE=TABREPORTPARAMETERS.AC_HEAD_ID AND TAB_CODE="+m_TAB_CODE+" AND AMOUNT_1=2) WHERE AMOUNT_1=2 AND TAB_CODE="+m_TAB_CODE+"");
            ps1.executeUpdate();

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
