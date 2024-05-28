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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class Daily_Cash_Flow_Fragment extends Fragment {
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
    String con_ipaddress,portnumber,IMEINumber,str_month="",str_day="",m_purdate,MskCashOpDate;
    DatePickerDialog  datePickerDialog;
    PreparedStatement ps1;
    double m_clbal,m_grntot,m_openingcash,o_cashopbal,m_cashopbal,m_retailcash,m_stockadjamt,m_computernetcash,m_shortextracash,m_withdrawalcash,m_nextdayopcash,m_cardamount;
    int m_closingcashyn;
    int m_entryseqno;
    CheckBox chk;
    String db;
    String TEMPSTOCK;
    String WHOLESALE;
    public Daily_Cash_Flow_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.daily_cash_expence, container, false);
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
        chk=(CheckBox)view.findViewById(R.id.id_chk);
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

                        try {
                        con = CONN(con_ipaddress,portnumber,db);
                            if (con == null) {
                                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                            } else {
                                String q="select PURCHASE_STOCK_AS_PER from profile";
                                PreparedStatement ps = con.prepareStatement(q);
                                ResultSet rs = ps.executeQuery();

                                while(rs.next())
                                {
                                    m_purdate=rs.getString("PURCHASE_STOCK_AS_PER");
                                    Log.d("m_purdate",m_purdate);
                                }
                              //  Intent i=new Intent(getActivity(),Daily_Cash_Flow_Report.class);
                               // i.putExtra("date",edt_as_on_date.getText().toString());
                                //startActivity(i);
                            }
                            con.close();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "175" + e, Toast.LENGTH_SHORT).show();
                        }
                       // insert_data(Temp_date);
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
                con = CONN(con_ipaddress, portnumber, db);
                TEMPSTOCK = "TEMPSTOCK" + m_TAB_CODE;
                WHOLESALE = "WHOLESALE" + m_TAB_CODE;
                ps1 = con.prepareStatement("IF OBJECT_ID('" + TEMPSTOCK + "') IS NOT NULL BEGIN DROP TABLE " + TEMPSTOCK + " END");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("IF OBJECT_ID('" + WHOLESALE + "') IS NOT NULL BEGIN DROP TABLE " + WHOLESALE + " END");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("CREATE TABLE " + TEMPSTOCK + "(ITEM_CODE NVARCHAR(100) NOT NULL DEFAULT ' ', CL_BALANCE FLOAT NOT NULL DEFAULT 0, LIQR_CODE FLOAT NOT NULL DEFAULT 0, BRND_CODE FLOAT NOT NULL DEFAULT 0, LIQR_DESC NVARCHAR(100) NOT NULL DEFAULT ' ', RATE MONEY NOT NULL DEFAULT 0, AMOUNT MONEY NOT NULL DEFAULT 0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("select closing_cash_yn from profile");
                ResultSet rs = ps1.executeQuery();
                int m_rowcount;
                while (rs.next()) {
                    m_closingcashyn = rs.getInt("closing_cash_yn");
                }

                m_computernetcash = 0;
                m_shortextracash = 0;
                if (chk.isChecked()) {
                    Log.d("sssss","STOCK CHECKED");
                    //===============Stock Updation=====================
                    ps1 = con.prepareStatement("delete from " + TEMPSTOCK + " ");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("INSERT INTO " + TEMPSTOCK + "(ITEM_CODE,RATE) SELECT ITEM_CODE,PURCHASE_PRICE FROM ITEMMAST WHERE LIQR_CODE <> 56");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = isnull((select sum(op_balance+op_balance_b) from onlnstok where comp_code=" + m_compcode + " and onlnstok.item_code=" + TEMPSTOCK + ".item_code),0)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance+isnull((select sum(bottle_qty+free_qty) from puritem where " + m_purdate + "<='" + Temp_date + "' and comp_code=" + m_compcode + " and puritem.item_code=" + TEMPSTOCK + ".item_code),0)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance+isnull((select sum(bottle_qty) from chalanitem where  doc_dt<='" + Temp_date + "' and comp_code=" + m_compcode + " and chalanitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance+isnull((select sum(qty) from countersalereturnitem where doc_dt<='" + Temp_date + "' and comp_code=" + m_compcode + " and countersalereturnitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance-isnull((select sum(qty+breakage_qty) from countersaleitem where doc_dt<='" + Temp_date + "' and comp_code=" + m_compcode + " and countersaleitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance-isnull((select sum(qty+breakage_qty) from provisionalsaleitem where doc_dt<='" + Temp_date + "' and comp_code=" + m_compcode + " and provisionalsaleitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("delete from " + TEMPSTOCK + " where cl_balance = 0");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set liqr_code = (select case when liqr_code = 17 then 999 else liqr_code end from itemmast where itemmast.item_code = " + TEMPSTOCK + ".item_code)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set brnd_code = (select mainliqrhead_code from itemmast,brndmast where itemmast.item_code = " + TEMPSTOCK + ".item_code and itemmast.brnd_code = brndmast.brnd_code)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set brnd_code = 2 where liqr_code = 57");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set liqr_desc = (select mainliqrhead_desc from mainliquorheadmast where mainliqrhead_code = " + TEMPSTOCK + ".brnd_code)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set liqr_desc = 'COUNTRY', brnd_code = 999 where liqr_code=999");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set rate = isnull((select top 1 round(basic_amt/bottle_qty,2) from puritem where bottle_qty > 0 and basic_amt > 0 and puritem.item_code = " + TEMPSTOCK + ".item_code and " + m_purdate + " <='" + Temp_date + "' and comp_code=" + m_compcode + " and "+m_purdate +" in(select max("+m_purdate +") from puritem a where a.item_code = " + TEMPSTOCK + ".item_code and a.bottle_qty > 0 and a.basic_amt > 0 and a."+m_purdate +" <='"+Temp_date+"')),rate)");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update " + TEMPSTOCK + " set amount = round(cl_balance*rate,2)");
                    ps1.executeUpdate();
                }
                //======================Stock Updation===============================

                ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,tab_code,crdr_cd) select distinct item_code," + m_TAB_CODE + ",'A' from countersaleitem where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' union select distinct item_code," + m_TAB_CODE + ",'A' from provisionalsaleitem where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' union select distinct item_code," + m_TAB_CODE + ",'A' from countersalereturnitem where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = isnull((select sum(item_value) from countersaleitem where countersaleitem.item_code = tabreportparameters.item_code and comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "'),0) where tab_code = " + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount+isnull((select sum(item_value) from provisionalsaleitem where provisionalsaleitem.item_code = tabreportparameters.item_code and comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "'),0) where tab_code = " + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount-isnull((select sum(item_value) from countersalereturnitem where countersalereturnitem.item_code = tabreportparameters.item_code and comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "'),0) where tab_code = " + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where amount = 0 and  TAB_CODE =" + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set liqr_code = (select liqr_code from itemmast where item_code = tabreportparameters.item_code) where  TAB_CODE =" + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set liqr_code = 1002, gl_desc = 'BEER-STRONG' where liqr_code = 2 and TAB_CODE =" + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set liqr_code = 1003, gl_desc = 'BEER-MILD' where liqr_code = 1 and TAB_CODE =" + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set liqr_code = 1004, gl_desc = 'WINE' where liqr_code in(14,15,50) and TAB_CODE =" + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set liqr_code = 1005, gl_desc = 'COUNTRY' where liqr_code = 17 and TAB_CODE =" + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set liqr_code = 1006, gl_desc = 'COLD DRINKS' where liqr_code in(56) and TAB_CODE =" + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set liqr_code = 1001, gl_desc = 'FOREIGN' where liqr_code > 0 and liqr_code < 1000 and TAB_CODE =" + m_TAB_CODE + " and crdr_cd = 'A' ");
                ps1.executeUpdate();

                m_grntot = 0;
                m_clbal = 0;
                m_openingcash = 0;
                m_stockadjamt = 0;
                o_cashopbal = 0;

                //=====================New Addition On 27/04/2017===================

                ps1 = con.prepareStatement("select convert(varchar(10),cash_op_date,101) as cash_op_date,cash_op_balance from profile");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    MskCashOpDate = rs.getString("cash_op_date");
                    o_cashopbal = rs.getDouble("cash_op_balance");
                }

                //====String to date=====

                Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(Temp_date);
                Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(MskCashOpDate);
                if (date1.getTime() <= date2.getTime()) {
                    o_cashopbal = 0;
                }
                m_cashopbal = 0;
                try {
                    if (o_cashopbal != 0) {
                        m_cashopbal = o_cashopbal;
                        ps1 = con.prepareStatement("select isnull(sum(amount),0) as amt from dailyrcp where doc_dt > '" + MskCashOpDate + "' and doc_dt < '" + Temp_date + "' and comp_code= " + m_compcode + " and ac_head_id in(select ac_head_id from glmast where licn_code = 0)");
                        rs = ps1.executeQuery();
                        while (rs.next()) {
                            m_cashopbal = m_cashopbal + rs.getDouble("amt");
                        }

                        ps1 = con.prepareStatement("select isnull(sum(amount),0) as amt from dailyexp where doc_dt > '" + MskCashOpDate + "' and doc_dt < '" + Temp_date + "' and comp_code= " + m_compcode + " and ac_head_id in(select ac_head_id from glmast where licn_code = 0)");
                        rs = ps1.executeQuery();
                        while (rs.next()) {
                            m_cashopbal = m_cashopbal - rs.getDouble("amt");
                        }
                        //----------8/27/2022=====================================================
                        ps1 = con.prepareStatement("select isnull(sum(item_value),0) as amt from countersaleitem where doc_dt > '" +MskCashOpDate+ "' and doc_dt < '" + Temp_date + "' and comp_code= " + m_compcode + " and ac_head_id=0");
                        rs = ps1.executeQuery();
                        while (rs.next()) {
                            m_cashopbal = m_cashopbal + rs.getDouble("amt");
                        }

                        ps1 = con.prepareStatement("select isnull(sum(item_value),0) as amt from countersalereturnitem where doc_dt > '" +MskCashOpDate+ "' and doc_dt < '" + Temp_date + "' and comp_code= " + m_compcode + "");
                        rs = ps1.executeQuery();
                        while (rs.next()) {
                            m_cashopbal = m_cashopbal - rs.getDouble("amt");
                        }

                        ps1 = con.prepareStatement("select ISNULL(SUM(DELIVERY_AMT),0)as amt from HOMEDELIVERYSALEITEMFINAL WHERE modi_date > '" +MskCashOpDate+ "' and modi_date < '" + Temp_date + "' and comp_code= " + m_compcode + "  AND DOC_SRNO IN(SELECT MAX(DOC_SRNO) FROM HOMEDELIVERYSALEITEMFINAL A WHERE CONVERT(VARCHAR(10),A.DOC_DT,101) = CONVERT(VARCHAR(10),HOMEDELIVERYSALEITEMFINAL.DOC_DT,101) AND A.MOBILE_NO = HOMEDELIVERYSALEITEMFINAL.MOBILE_NO AND A.CUST_TRAN_ID=HOMEDELIVERYSALEITEMFINAL.CUST_TRAN_ID AND cast(convert(varchar(10),A.modi_date,101)as datetime) > '" +MskCashOpDate+"' and cast(convert(varchar(10),A.modi_date,101)as datetime) < '" + Temp_date + "' and A.comp_code= " + m_compcode + ")");
                        rs = ps1.executeQuery();
                        while (rs.next()) {
                            m_cashopbal = m_cashopbal + rs.getDouble("amt");
                        }


                        ps1 = con.prepareStatement("SELECT distinct wholesale_doc_no,doc_dt,net_amount FROM COUNTERSALEITEM where doc_dt > '" +MskCashOpDate+ "' and doc_dt < '" + Temp_date + "' and comp_code= " + m_compcode + " and ac_head_id<>0 and pmt_mode = 0");
                        rs = ps1.executeQuery();
                        while (rs.next()) {
                            m_cashopbal = m_cashopbal + rs.getDouble("net_amount");
                        }
                        //-------------------------------------------------------------------------
                    }
                }catch(Exception e){
                    Toast.makeText(getActivity(), "330"+e, Toast.LENGTH_SHORT).show();
                }
                //=============''''''' opening cash balance ''''''''
                ps1 = con.prepareStatement("select withdrawal_amount,amount,card_amount from DATEWISEOPCASH where doc_dt='" + Temp_date + "' and comp_code=" + m_compcode + " and withdrawal_amount+amount > 0");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_withdrawalcash = rs.getDouble("withdrawal_amount");
                    m_nextdayopcash = rs.getDouble("amount");
                    m_cardamount = rs.getDouble("card_amount");
                }

                if (o_cashopbal == 0) {
                    if (m_closingcashyn == 0) {
                        ps1 = con.prepareStatement("select isnull(sum(amount),0) as amount from dailyrcp where doc_dt = '" + Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id = 0 and amount <> 0 ");
                    } else {
                        ps1 = con.prepareStatement("select amount as amount from datewiseopcash where doc_dt in(select max(doc_dt) from datewiseopcash a where a.comp_code=" + m_compcode + " and a.doc_dt < '" + Temp_date + "')");
                    }
                } else {
                    ps1 = con.prepareStatement("select " + m_cashopbal + " as amount");
                }
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_openingcash = m_openingcash + rs.getDouble("amount");
                }
                //------------------

                ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where TAB_CODE =" + m_TAB_CODE + " and crdr_cd <> 'A'");
                ps1.executeUpdate();
                m_entryseqno = 0;
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Opening Balance'," + m_openingcash + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Add Sales'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
                ps1 = con.prepareStatement("select gl_desc,sum(amount) as amount,liqr_code from tabreportparameters where TAB_CODE =" + m_TAB_CODE + " and crdr_cd='A' group by gl_desc,liqr_code order by liqr_code");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("amount");
                }

                ps1 = con.prepareStatement("SELECT WHOLESALE_DOC_NO,DISCOUNT_AMOUNT,AC_HEAD_ID,NET_AMOUNT,PMT_MODE,ADD_AMOUNT INTO " + WHOLESALE + " FROM COUNTERSALEITEM WHERE doc_dt='" + Temp_date + "' and comp_code=" + m_compcode + " AND WHOLESALE_DOC_NO <> 0 GROUP BY WHOLESALE_DOC_NO,DISCOUNT_AMOUNT,AC_HEAD_ID,NET_AMOUNT,PMT_MODE,ADD_AMOUNT");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("INSERT INTO " + WHOLESALE + " SELECT WHOLESALE_DOC_NO,DISCOUNT_AMOUNT,AC_HEAD_ID,NET_AMOUNT,PMT_MODE,0 FROM PROVISIONALSALEITEM WHERE doc_dt='" + Temp_date + "' and comp_code=" + m_compcode + " AND WHOLESALE_DOC_NO <> 0 GROUP BY WHOLESALE_DOC_NO,DISCOUNT_AMOUNT,AC_HEAD_ID,NET_AMOUNT,PMT_MODE");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("select isnull(sum(DISCOUNT_AMOUNT),0) as amount from " + WHOLESALE + "");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    if (rs.getDouble("amount") > 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Less Wholesale Discount'," + rs.getDouble("amount") + " ," + m_TAB_CODE + "," + m_entryseqno + ")");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                        m_grntot = m_grntot - rs.getDouble("amount");
                    }
                }
                ps1 = con.prepareStatement("select isnull(sum(DISCOUNT_AMOUNT),0) as amount from countersaleitem where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' AND WHOLESALE_DOC_NO = 0");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    if (rs.getDouble("amount") > 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Less Retail Discount'," + rs.getDouble("amount") + " ," + m_TAB_CODE + "," + m_entryseqno + ")");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                        m_grntot = m_grntot - rs.getDouble("amount");
                    }
                }

                ps1 = con.prepareStatement("select isnull(sum(ADD_AMOUNT),0)as amount from " + WHOLESALE + " ");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    if (rs.getDouble("amount") > 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Add Amount'," + rs.getDouble("amount") + " ," + m_TAB_CODE + "," + m_entryseqno + ")");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                        m_grntot = m_grntot + rs.getDouble("amount");
                    }
                }

                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Net Sale'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
                ps1 = con.prepareStatement("select isnull(sum(NET_AMOUNT),0) as amount from " + WHOLESALE + " where pmt_mode = 1");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    if (rs.getDouble("amount") > 0) {
                        m_grntot = m_grntot - rs.getDouble("amount");
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Less Customer Credit Sale'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                    }
                }

                ps1 = con.prepareStatement("select (select gl_desc from glmast where ac_head_id=" + WHOLESALE + ".ac_head_id) as gl_desc,isnull(sum(NET_AMOUNT),0) as amount from " + WHOLESALE + " where pmt_mode = 1 group by ac_head_id order by gl_desc");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount_1,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }

                ps1 = con.prepareStatement("select isnull(sum(item_value),0) as amount from countersaleitem where stock_adj_yn=1 and comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_stockadjamt = m_stockadjamt + rs.getDouble("amount");
                }

                ps1 = con.prepareStatement("select isnull(sum(item_value),0) as amount from countersalereturnitem where stock_adj_yn=1 and comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_stockadjamt = m_stockadjamt - rs.getDouble("amount");
                }
                if (m_stockadjamt != 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount_1,tab_code,doc_no) values('Less Stock Adjustment'," + m_stockadjamt + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot - m_stockadjamt;
                }
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Net Cash From Sale'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;

                ps1 = con.prepareStatement("select isnull(sum(item_value-discount_amount),0) as amount from countersaleitem where stock_adj_yn = 0 and comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' and WHOLESALE_DOC_NO = 0");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_retailcash = rs.getDouble("amount");
                }

                ps1 = con.prepareStatement("select isnull(sum(item_value),0) as amount from countersalereturnitem where stock_adj_yn = 0 and comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_retailcash = m_retailcash - rs.getDouble("amount");
                }

                ps1 = con.prepareStatement("select isnull(sum(NET_AMOUNT),0) as amount from " + WHOLESALE + " where pmt_mode = 0");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount_1,tab_code,doc_no) values('Retail Sale Cash'," + m_retailcash + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount_1,tab_code,doc_no) values('Whole Sale Cash'," + m_grntot + " - " + m_retailcash + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }

                m_clbal = 0;

                ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyrcp.ac_head_id) as gl_desc,sum(amount) as amount from dailyrcp where doc_dt='" + Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) and ac_head_id in(select ac_head_id from glmast where licn_code = 0) group by ac_head_id order by gl_desc");
                rs = ps1.executeQuery();
                m_entryseqno = m_entryseqno + 1;
                m_rowcount = 0;
                while (rs.next()) {
                    if (m_rowcount == 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Add Receipts (Regular Customers)'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                    }
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("amount");
                    m_clbal = m_clbal + rs.getDouble("amount");
                    m_rowcount++;
                }

                if (m_rowcount > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Total'," + m_clbal + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }

                m_clbal = 0;

                ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyrcp.ac_head_id) as gl_desc,sum(amount) as amount from dailyrcp where doc_dt='" + Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id not in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) and ac_head_id in(select ac_head_id from glmast where licn_code = 0) group by ac_head_id order by gl_desc");
                rs = ps1.executeQuery();
                m_entryseqno = m_entryseqno + 1;
                m_rowcount = 0;
                while (rs.next()) {
                    if (m_rowcount == 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Add Other Receipts'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                    }
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("amount");
                    m_clbal = m_clbal + rs.getDouble("amount");
                    m_rowcount++;
                }
                if (m_rowcount > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Total'," + m_clbal + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }

                m_clbal = 0;

                if (o_cashopbal != 0) {
                    m_clbal = 0;
                    ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,sum(amount) as amount from dailyrcp where doc_dt='" + Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id = 0 group by narr order by narr");
                    rs = ps1.executeQuery();
                    while (rs.next()) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                        m_grntot = m_grntot + rs.getDouble("amount");
                        m_clbal = m_clbal + rs.getDouble("amount");
                    }
                    if (m_clbal > 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Total'," + m_clbal + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                    }
                }

                m_grntot = m_grntot + m_openingcash;

                m_clbal = 0;

                ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyexp.ac_head_id) as gl_desc,sum(amount) as amount from dailyexp where doc_dt='" + Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id in(select ac_head_id from glmast where licn_code = 0) group by ac_head_id order by gl_desc");
                rs = ps1.executeQuery();
                m_entryseqno = m_entryseqno + 1;
                m_rowcount = 0;
                while (rs.next()) {
                    if (m_rowcount == 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Less Expenses'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                    }
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot - rs.getDouble("amount");
                    m_clbal = m_clbal + rs.getDouble("amount");
                    m_rowcount++;
                }
                if (m_rowcount > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Total'," + m_clbal + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Computer Net Cash'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;

                m_computernetcash = m_grntot;

                if (m_closingcashyn == 1) {

                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('WithDrawal Cash'," + m_withdrawalcash + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot - m_withdrawalcash;

                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Card Swapping'," + m_cardamount + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot - m_cardamount;

                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Next Day Opening Cash'," + m_nextdayopcash + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot - m_nextdayopcash;

                    m_shortextracash = m_grntot;

                    if (m_grntot < 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Extra Cash'," + -m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                    } else if (m_grntot >= 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Short Cash'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                    }
                }
                m_grntot = 0;
                ps1 = con.prepareStatement("select (select gl_desc from glmast where ac_head_id=purchase.ac_head_id) as gl_desc,invoice_no,net_amount from purchase where doc_dt = '" + Temp_date + "' and comp_code = " + m_compcode + " order by gl_desc");
                rs = ps1.executeQuery();
                m_entryseqno = m_entryseqno + 1;
                m_rowcount = 0;
                while (rs.next()) {
                    if (m_rowcount == 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('T.P.s From Traders'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                    }
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,gl_desc,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "', '" + rs.getString("invoice_no") + "'," + rs.getDouble("net_amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("net_amount");
                    m_rowcount++;
                }
                if (m_rowcount > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Invoice Total'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }
                m_grntot = 0;

                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(doc_no,ac_head_id,amount,crdr_cd,tab_code) select doc_no,ac_head_id,sum(basic_amt),'C'," + m_TAB_CODE + " from chalanitem where doc_dt = '" + Temp_date + "' and comp_code = " + m_compcode + " group by doc_no,ac_head_id");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update TABREPORTPARAMETERS set dis_amount = (select sum(add_amount) from chalanitem where chalanitem.doc_no=tabreportparameters.doc_no and chalanitem.ac_head_id=tabreportparameters.ac_head_id and chalanitem.doc_srno = 1 and chalanitem.doc_dt = '" + Temp_date + "' and comp_code=" + m_compcode + ") where tab_code = " + m_TAB_CODE + " and crdr_cd='C'");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update TABREPORTPARAMETERS set tot_amount = amount + dis_amount where tab_code = " + m_TAB_CODE + " and crdr_cd='C'");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("select glmast.gl_desc,tot_amount as net_amount,convert(int,doc_no) as doc_no from glmast,tabreportparameters where glmast.ac_head_id=tabreportparameters.ac_head_id and tab_code = " + m_TAB_CODE + " and crdr_cd='C' order by glmast.gl_desc,doc_no");
                rs = ps1.executeQuery();
                m_rowcount = 0;
                while (rs.next()) {
                    if (m_rowcount == 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Challans'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                    }
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,gl_desc,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "', " + rs.getInt("doc_no") + "," + rs.getDouble("net_amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("net_amount");
                    m_rowcount++;
                }
                if (m_rowcount > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Challan Total'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }
                if(chk.isChecked())
                {
                    Log.d("sssss","STOCK CHECKED2");
                    ps1 = con.prepareStatement("select liqr_desc as gl_desc,sum(amount) as net_amount,brnd_code from " + TEMPSTOCK + " group by liqr_desc,brnd_code order by brnd_code");
                    rs = ps1.executeQuery();
                    m_grntot = 0;
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Closing Stock Valuation'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;

                    while (rs.next()) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "', " + rs.getDouble("net_amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                        m_grntot = m_grntot + rs.getDouble("net_amount");
                    }

                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Stock Value Total'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                }

        }catch(Exception e)
        {
            Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
        }
            return null;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        pd.dismiss();
        Intent i=new Intent(getActivity(),Daily_Cash_Flow_Report.class);
        i.putExtra("date",edt_as_on_date.getText().toString());
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
