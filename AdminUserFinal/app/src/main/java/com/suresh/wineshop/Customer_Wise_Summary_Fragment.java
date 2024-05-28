package com.suresh.wineshop;


import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.suresh.wineshop.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Customer_Wise_Summary_Fragment extends Fragment {
    TextView edt_as_on_date;
    Button btn_report;
    int mYear, mMonth, mDay;
    //--------------------------
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    //String db = "WINESHOP";
    // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String formattedDate,Temp_date;
    TransparentProgressDialog pd;
    int m_TAB_CODE;
    int m_compcode;
    Spinner sp_type;
    String str_type,type="";

    String con_ipaddress,portnumber,IMEINumber,str_month="",str_day="",db;
    DatePickerDialog  datePickerDialog;
    public Customer_Wise_Summary_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_wise_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        SharedPreferences ss = getActivity().getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);
        sp_type=(Spinner)view.findViewById(R.id.sp_type);
        List<String> list = new ArrayList<String>();
        list.add("New");
        list.add("Old");
        list.add("New+Old");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        sp_type.setAdapter(dataAdapter);

        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                str_type = adapterView.getItemAtPosition(position).toString();
                // Toast.makeText(getApplicationContext(), "Selected: " + chk_cat_type, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        formattedDate = df.format(c);
        System.out.println("Today Date => " + formattedDate);

        edt_as_on_date=(TextView) view.findViewById(R.id.edt_as_on_date);
        edt_as_on_date.setText(formattedDate);

        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Temp_date=out.format(d);
        btn_report=(Button) view.findViewById(R.id.btn_report);

        //img_as_on_date=(ImageView)view.findViewById(R.id.img_as_on_date);
        edt_as_on_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth)
                            {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edt_as_on_date.setText(""+str_day + "/" + str_month + "/" + year);

                                Temp_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        if(str_type.equals("New"))
                        { type="0";}
                        else if(str_type.equals("Old"))
                        { type="1";}
                        else{type="2";}
                       new insert_op().execute();
            }
        });
    }

    //==========================================
    public class insert_op extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {

            try
            {

                con = CONN(con_ipaddress,portnumber,db);
                PreparedStatement ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE ="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,TAB_CODE) select ac_head_id,"+m_TAB_CODE+" from glmast where ac_head_id in(select ac_head_id from custwiseopeningbal union select ac_head_id from countersaleitem where pmt_mode=1 and doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and ac_head_id <>0 union select ac_head_id from provisionalsaleitem where pmt_mode=1 and doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and ac_head_id <>0 union select ac_head_id from chalanitem where doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and ac_head_id <>0 and glmast.group_code in(select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code) union select ac_head_id from dailyexp where doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and ac_head_id <>0 and glmast.group_code in(select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code) union select ac_head_id from dailyrcp where doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and ac_head_id <>0 and glmast.group_code in(select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code))");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = isnull((select opening_bal from custwiseopeningbal where custwiseopeningbal.ac_head_id = tabreportparameters.ac_head_id and comp_code="+m_compcode+"),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount + isnull((select sum(net_amount) from countersaleitem where countersaleitem.ac_head_id = tabreportparameters.ac_head_id and pmt_mode=1 and doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and ac_head_id <>0 AND DOC_SRNO IN(SELECT MIN(DOC_SRNO) FROM COUNTERSALEITEM A WHERE A.DOC_DT = COUNTERSALEITEM.DOC_DT AND A.WHOLESALE_DOC_NO = COUNTERSALEITEM.WHOLESALE_DOC_NO)),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount + isnull((select sum(net_amount) from provisionalsaleitem where provisionalsaleitem.ac_head_id = tabreportparameters.ac_head_id and pmt_mode=1 and doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and ac_head_id <>0 AND DOC_SRNO IN(SELECT MIN(DOC_SRNO) FROM provisionalsaleitem A WHERE A.DOC_DT = provisionalsaleitem.DOC_DT AND A.WHOLESALE_DOC_NO = provisionalsaleitem.WHOLESALE_DOC_NO)),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount - isnull((select sum(basic_amt) from chalanitem where chalanitem.ac_head_id = tabreportparameters.ac_head_id and doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+"),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount - isnull((select sum(add_amount) from chalanitem where chalanitem.ac_head_id = tabreportparameters.ac_head_id and doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and DOC_SRNO IN(SELECT MIN(DOC_SRNO) FROM chalanitem A WHERE A.DOC_DT = chalanitem.DOC_DT)),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount + isnull((select sum(amount) from dailyexp where dailyexp.ac_head_id = tabreportparameters.ac_head_id and doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and ac_head_id <> 0),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount - isnull((select sum(amount) from dailyrcp where dailyrcp.ac_head_id = tabreportparameters.ac_head_id and doc_dt <='"+ Temp_date +"' and comp_code="+m_compcode+" and ac_head_id <> 0),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where amount = 0 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();

                if(type.equals("0"))
                {
                    ps1 = con.prepareStatement("delete from tabreportparameters  where ac_head_id in(select ac_head_id from glmast where lr_yn = 1) and tab_code="+m_TAB_CODE+"");
                    ps1.executeUpdate();
                }
                else if(type.equals("1"))
                {
                    ps1 = con.prepareStatement("delete from tabreportparameters  where ac_head_id in(select ac_head_id from glmast where lr_yn = 0) and tab_code="+m_TAB_CODE+"");
                    ps1.executeUpdate();
                }
                con.close();

            }catch(Exception e)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            pd.dismiss();
            Intent i=new Intent(getActivity(),Customer_Wise_Summary_Report.class);
            i.putExtra("date",edt_as_on_date.getText().toString());
            i.putExtra("type",str_type);
            startActivity(i);
            super.onPostExecute(s);
        }
    }
    //==========================================

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

        } catch (Exception se) {

            Log.e("ERRO", se.getMessage());

        }

        return con;

    }
}
