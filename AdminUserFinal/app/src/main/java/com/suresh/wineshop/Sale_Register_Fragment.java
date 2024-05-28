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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.suresh.wineshop.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class Sale_Register_Fragment extends Fragment {

    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "WINESHOP";
    // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    ProgressBar progressBar;
    PreparedStatement ps1;
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date;

    DatePickerDialog datePickerDialog;
    Button btn_report;
    RadioGroup radioGroup;
    String IMEINumber;
    int m_compcode;
    int m_TAB_CODE;
    int m_from_code;
    int m_percent;
    TransparentProgressDialog pd;
    RadioButton rb,radio_mrp,radio_counter_price,radio_cash_memo;
    String str_radio_mrp,str_radio_counter_price,str_radio_cash_memo,db;
    CheckBox chk_all,chk_monthly_summary,chk_adj_sdk;
    TextView txt_chk_adj_sdk;
    String str_chk_all,str_chk_monthly_summary,str_chk_adj_sdk,m_tempfile;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="",con_ipaddress ,portnumber;
    public Sale_Register_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sale_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
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
        txt_chk_adj_sdk=(TextView)view.findViewById(R.id.txt_chk_adj_sdk);


        chk_monthly_summary=(CheckBox)view.findViewById(R.id.chk_monthly_summary);
        chk_adj_sdk=(CheckBox)view.findViewById(R.id.chk_adj_sdk);
        chk_all=(CheckBox)view.findViewById(R.id.chk_all);
        chk_all.setChecked(true);
        radio_mrp=(RadioButton)view.findViewById(R.id.radio_mrp);
        radio_mrp.setChecked(true);
        radio_counter_price=(RadioButton)view.findViewById(R.id.radio_counter_price);
        radio_cash_memo=(RadioButton)view.findViewById(R.id.radio_cash_memo);
        radioGroup=(RadioGroup)view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb=(RadioButton)view.findViewById(checkedId);
            }
        });
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

        try {

            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                String q="SELECT FORM_CODE FROM USERRIGHTS WHERE FORM_CODE=241";

                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                int cnt=0;
                while(rs.next())
                {cnt++;
                    m_from_code=rs.getInt("FORM_CODE");
                    Log.d("m_from_code",""+m_from_code);
                }
                if(cnt>0)
                {
                    chk_adj_sdk.setVisibility(View.VISIBLE);
                    txt_chk_adj_sdk.setVisibility(View.VISIBLE);
                }
                else
                {
                    chk_adj_sdk.setVisibility(View.INVISIBLE);
                    txt_chk_adj_sdk.setVisibility(View.INVISIBLE);
                }

            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
        }
        btn_report=(Button)view.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        if (chk_all.isChecked()) {
                            str_chk_all = "1";

                        } else {
                            str_chk_all = "0";
                        }
                        if (chk_monthly_summary.isChecked()) {
                            str_chk_monthly_summary = "1";
                        } else {
                            str_chk_monthly_summary = "0";
                        }
                        if (chk_adj_sdk.isChecked()) {
                            str_chk_adj_sdk = "1";
                        } else {
                                    str_chk_adj_sdk = "0";
                        }
                        try {
                            if (radio_mrp.isChecked() == true) {
                                str_radio_mrp="1";
                            } else {
                                str_radio_mrp="0";
                            }

                            if (radio_cash_memo.isChecked() == true) {
                                str_radio_cash_memo="1";
                            } else {
                                str_radio_cash_memo="0";
                            }

                            if (radio_counter_price.isChecked() == true) {
                                str_radio_counter_price="1";

                            } else {
                                str_radio_counter_price="0";
                            }
                            new insert_op().execute();


                        } catch (Exception e) {
                        }

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
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    m_tempfile="TEMP"+m_TAB_CODE;
                    ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("INSERT INTO TABREPORTPARAMETERS(DOC_DT,TAB_CODE) SELECT DISTINCT DOC_DT," + m_TAB_CODE + " FROM countersaleitem where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' union SELECT DISTINCT DOC_DT," + m_TAB_CODE + " FROM PROVISIONALSALEITEM where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "'");
                    ps1.executeUpdate();

                    if (str_chk_adj_sdk.equals("1")) { //m_percent=0;

                        ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_1 = ISNULL((SELECT SUM(QTY*RATE) FROM countersaleitem,itemmast where stock_adj_yn = 1 and comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_2 = ISNULL((SELECT SUM(QTY*RATE) FROM countersaleitem,itemmast where stock_adj_yn = 1 and comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_3 = ISNULL((SELECT SUM(QTY*RATE) FROM countersaleitem,itemmast where stock_adj_yn = 1 and comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_4 = ISNULL((SELECT SUM(QTY*RATE) FROM countersaleitem,itemmast where stock_adj_yn = 1 and comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_5 = ISNULL((SELECT SUM(QTY*RATE) FROM countersaleitem,itemmast where stock_adj_yn = 1 and comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_6 = ISNULL((SELECT SUM(QTY*RATE) FROM countersaleitem,itemmast where stock_adj_yn = 1 and comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                    } else {
                        if (str_radio_cash_memo.equals("0")) {
                            if (str_chk_all.equals("1")) {
                                if (str_radio_mrp.equals("1")) {
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_1 = ISNULL((SELECT SUM(QTY*countersaleitem.MRP) FROM countersaleitem,itemmast where comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.MRP) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_2 = ISNULL((SELECT SUM(QTY*countersaleitem.MRP) FROM countersaleitem,itemmast where comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.MRP) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_3 = ISNULL((SELECT SUM(QTY*countersaleitem.MRP) FROM countersaleitem,itemmast where comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.MRP) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_4 = ISNULL((SELECT SUM(QTY*countersaleitem.MRP) FROM countersaleitem,itemmast where comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.MRP) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_5 = ISNULL((SELECT SUM(QTY*countersaleitem.MRP) FROM countersaleitem,itemmast where comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.MRP) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_6 = ISNULL((SELECT SUM(QTY*countersaleitem.MRP) FROM countersaleitem,itemmast where comp_code=1 and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.MRP) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();

                                    ps1 = con.prepareStatement("IF OBJECT_ID('" + m_tempfile + "') IS NOT NULL BEGIN DROP TABLE " + m_tempfile + " END");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("select item_code,doc_dt,isnull(sum(qty)*(select MRP from countersaleitem where item_code = countersalereturnitem.item_code and doc_dt = countersalereturnitem.doc_dt and doc_no in(select max(doc_no) from countersaleitem where item_code = countersalereturnitem.item_code and doc_dt = countersalereturnitem.doc_dt)),0) as mrp_value into " + m_tempfile + " from countersalereturnitem where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' group by doc_dt,item_code");
                                    ps1.executeUpdate();

                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_1 = AMOUNT_1-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_2 = AMOUNT_2-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_3 = AMOUNT_3-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_4 = AMOUNT_4-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_5 = AMOUNT_5-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_6 = AMOUNT_6-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();

                                } else//OptMrp
                                {
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_1 = ISNULL((SELECT SUM(item_value) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) + ISNULL((SELECT SUM(item_value) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_2 = ISNULL((SELECT SUM(item_value) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) + ISNULL((SELECT SUM(item_value) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_3 = ISNULL((SELECT SUM(item_value) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) + ISNULL((SELECT SUM(item_value) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_4 = ISNULL((SELECT SUM(item_value) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) + ISNULL((SELECT SUM(item_value) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_5 = ISNULL((SELECT SUM(item_value) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) + ISNULL((SELECT SUM(item_value) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_6 = ISNULL((SELECT SUM(item_value) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) + ISNULL((SELECT SUM(item_value) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();

                                    ps1 = con.prepareStatement("IF OBJECT_ID('" + m_tempfile + "') IS NOT NULL BEGIN DROP TABLE " + m_tempfile + " END");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("select item_code,doc_dt,isnull(sum(item_value),0) as mrp_value into " + m_tempfile + " from countersalereturnitem where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' group by doc_dt,item_code");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("delete from " + m_tempfile + " where mrp_value=0");
                                    ps1.executeUpdate();

                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_1 = AMOUNT_1-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_2 = AMOUNT_2-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_3 = AMOUNT_3-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_4 = AMOUNT_4-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_5 = AMOUNT_5-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();
                                    ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_6 = AMOUNT_6-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                    ps1.executeUpdate();

                                }
                            } else//ChkAll
                            {
                                String m_pricetype;
                                if (str_radio_cash_memo.equals("1")) {
                                    m_pricetype = "CASHMEMO_PRICE";
                                }
                                else if (str_radio_mrp.equals("1")){
                                    m_pricetype = "MRP";
                                }
                                else{
                                    m_pricetype = "RATE";
                                }
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_1 = ISNULL((SELECT SUM(QTY*countersaleitem."+m_pricetype+") FROM countersaleitem,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM."+m_pricetype+") FROM PROVISIONALSALEITEM,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_2 = ISNULL((SELECT SUM(QTY*countersaleitem."+m_pricetype+") FROM countersaleitem,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM."+m_pricetype+") FROM PROVISIONALSALEITEM,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_3 = ISNULL((SELECT SUM(QTY*countersaleitem."+m_pricetype+") FROM countersaleitem,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM."+m_pricetype+") FROM PROVISIONALSALEITEM,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_4 = ISNULL((SELECT SUM(QTY*countersaleitem."+m_pricetype+") FROM countersaleitem,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM."+m_pricetype+") FROM PROVISIONALSALEITEM,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_5 = ISNULL((SELECT SUM(QTY*countersaleitem."+m_pricetype+") FROM countersaleitem,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM."+m_pricetype+") FROM PROVISIONALSALEITEM,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_6 = ISNULL((SELECT SUM(QTY*countersaleitem."+m_pricetype+") FROM countersaleitem,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM."+m_pricetype+") FROM PROVISIONALSALEITEM,itemmast where sale_type = 0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();


                                ps1 = con.prepareStatement("IF OBJECT_ID('" + m_tempfile + "') IS NOT NULL BEGIN DROP TABLE " + m_tempfile + " END");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("select item_code,doc_dt,isnull(sum(qty)*(select "+m_pricetype+" from countersaleitem where sale_type=0 and item_code = countersalereturnitem.item_code and doc_dt = countersalereturnitem.doc_dt and doc_no in(select max(doc_no) from countersaleitem where sale_type=0 and item_code = countersalereturnitem.item_code and doc_dt = countersalereturnitem.doc_dt)),0) as mrp_value into " + m_tempfile + " from countersalereturnitem where sale_type=0 and comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' group by doc_dt,item_code");
                                ps1.executeUpdate();

                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_1 = AMOUNT_1-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_2 = AMOUNT_2-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_3 = AMOUNT_3-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_4 = AMOUNT_4-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_5 = AMOUNT_5-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();
                                ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_6 = AMOUNT_6-ISNULL((SELECT SUM(MRP_VALUE) FROM " + m_tempfile + ",itemmast where " + m_tempfile + ".item_code = itemmast.item_code and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                                ps1.executeUpdate();

                            }

                        }
                        else{
                            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_1 = ISNULL((SELECT SUM(QTY*countersaleitem.CASHMEMO_PRICE) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.CASHMEMO_PRICE) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code not in(1,2,14,15,16,17,50,56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                            ps1.executeUpdate();
                            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_2 = ISNULL((SELECT SUM(QTY*countersaleitem.CASHMEMO_PRICE) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.CASHMEMO_PRICE) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(17)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                            ps1.executeUpdate();
                            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_3 = ISNULL((SELECT SUM(QTY*countersaleitem.CASHMEMO_PRICE) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.CASHMEMO_PRICE) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(2)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                            ps1.executeUpdate();
                            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_4 = ISNULL((SELECT SUM(QTY*countersaleitem.CASHMEMO_PRICE) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.CASHMEMO_PRICE) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(1)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                            ps1.executeUpdate();
                            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_5 = ISNULL((SELECT SUM(QTY*countersaleitem.CASHMEMO_PRICE) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.CASHMEMO_PRICE) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(14,15,16,50)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                            ps1.executeUpdate();
                            ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET AMOUNT_6 = ISNULL((SELECT SUM(QTY*countersaleitem.CASHMEMO_PRICE) FROM countersaleitem,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and countersaleitem.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) + ISNULL((SELECT SUM(QTY*PROVISIONALSALEITEM.CASHMEMO_PRICE) FROM PROVISIONALSALEITEM,itemmast where comp_code=" + m_compcode + " and doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and PROVISIONALSALEITEM.item_code=itemmast.item_code and comp_code=" + m_compcode + " and doc_dt=TABREPORTPARAMETERS.DOC_DT and liqr_code in(56)),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                            ps1.executeUpdate();
                        }
                        ps1 = con.prepareStatement("UPDATE TABREPORTPARAMETERS SET DIS_AMOUNT = isnull((select sum(DISCOUNT_AMOUNT) from countersaleitem where DOC_DT = TABREPORTPARAMETERS.DOC_DT and WHOLESALE_DOC_NO = 0),0) WHERE TAB_CODE=" + m_TAB_CODE + "");
                        ps1.executeUpdate();
                    }

                }
                con.close();

            }catch(Exception e)
            {
                Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            pd.dismiss();

            Intent i=new Intent(getActivity(),Sale_Register_Report.class);
            i.putExtra("from_date", edt_frm_date.getText().toString());
            i.putExtra("to_date", edt_to_date.getText().toString());
            i.putExtra("str_chk_monthly_summary", str_chk_monthly_summary);
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
