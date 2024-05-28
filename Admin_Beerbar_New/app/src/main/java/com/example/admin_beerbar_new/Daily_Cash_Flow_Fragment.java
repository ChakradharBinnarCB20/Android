package com.example.admin_beerbar_new;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.admin_beerbar_new.Class.TransparentProgressDialog;

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
    String formattedDate,Temp_date,db;
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
    int ch=0;
    String TEMPSTOCK_LIQR;
    String TEMPSTOCK_OTHR;
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

                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run()
                    {
                        m_purdate = "doc_dt";

                        insert_data(Temp_date);
                        Intent i=new Intent(getActivity(),Daily_Cash_Flow_Report.class);
                        i.putExtra("date",edt_as_on_date.getText().toString());
                        startActivity(i);
                        pd.dismiss();
                    }
                }, 1000);

            }
        });
    }

    public void insert_data(String date)
    {
        //===========ALL==============================
        try {
            con = CONN(con_ipaddress, portnumber, db);
            TEMPSTOCK_LIQR = "TEMPSTOCK_LIQR" + m_TAB_CODE;
            TEMPSTOCK_OTHR = "TEMPSTOCK_OTHR" + m_TAB_CODE;

            ps1 = con.prepareStatement("IF OBJECT_ID('" + TEMPSTOCK_LIQR + "') IS NOT NULL BEGIN DROP TABLE " + TEMPSTOCK_LIQR + " END");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("CREATE TABLE " + TEMPSTOCK_LIQR + "(ITEM_CODE NVARCHAR(100) NOT NULL DEFAULT ' ', CL_BALANCE MONEY NOT NULL DEFAULT 0, LIQR_CODE FLOAT NOT NULL DEFAULT 0,  LIQR_DESC NVARCHAR(100) NOT NULL DEFAULT ' ', BASIC_AMT MONEY NOT NULL DEFAULT 0)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("IF OBJECT_ID('" + TEMPSTOCK_OTHR + "') IS NOT NULL BEGIN DROP TABLE " + TEMPSTOCK_OTHR + " END");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("CREATE TABLE " + TEMPSTOCK_OTHR + "(ITEM_CODE NVARCHAR(100) NOT NULL DEFAULT ' ', CL_BALANCE MONEY NOT NULL DEFAULT 0, LIQR_CODE FLOAT NOT NULL DEFAULT 0,  LIQR_DESC NVARCHAR(100) NOT NULL DEFAULT ' ', BASIC_AMT MONEY NOT NULL DEFAULT 0)");
            ps1.executeUpdate();

            ps1 = con.prepareStatement("select closing_cash_yn from profile");
            ResultSet rs = ps1.executeQuery();
            int m_rowcount;
            while (rs.next()) {
                m_closingcashyn = rs.getInt("closing_cash_yn");
            }

            m_computernetcash = 0;
            m_shortextracash = 0;
          //---------------------checkbox start---------------------
            if(chk.isChecked()){
            //===============Stock Updation liqr=====================
            ps1 = con.prepareStatement("delete from " + TEMPSTOCK_LIQR + "");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("select LOCT_CODE from LOCTmast where LOCT_CODE >0 and STOCK_CHECK_YN = 1 AND loct_code in(select loct_code from onlnstok where item_type =1)");
            ResultSet rss = ps1.executeQuery();

            while (rss.next()) {
                //Sealed Stock
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code = " + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,op_balance," + m_TAB_CODE + " from onlnstok where op_balance>0 and comp_code=" + m_compcode + " and loct_code='" + rss.getString("loct_code") + "' and item_type = 1");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,sum(bottle_qty+free_qty)," + m_TAB_CODE + " from puritem where pur_type = 1 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,sum(bottle_qty)," + m_TAB_CODE + " from chalanitem where doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,sum(bottle_qty)," + m_TAB_CODE + " from transfernote where item_type = 1 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and to_loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,-sum(qty)," + m_TAB_CODE + " from countersaleitem where maintain_stock=1 and item_type = 1 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,-sum(qty)," + m_TAB_CODE + " from saleitem where maintain_stock=1 and item_type = 1 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,-sum(bottle_qty)," + m_TAB_CODE + " from transfernote where item_type = 1 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and from_loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code,doc_no)select item_code,sum(gl_opbal)," + m_TAB_CODE + ",1 from tabreportparameters where tab_code = " + m_TAB_CODE + " group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where ((gl_opbal = 0) or doc_no = 0) and tab_code = " + m_TAB_CODE + "");
                ps1.executeUpdate();

                //Loose Stock
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select tabreportparameters.item_code,convert(int,right(str(gl_opbal,12,4),4))+(FLOOR(gl_opbal))*convert(Float(9), Left(Replace(size_desc, Space(1), Space(10)), 4))," + m_TAB_CODE + " from tabreportparameters,itemmast,sizemast where tabreportparameters.item_code = itemmast.item_code and itemmast.size_code = sizemast.size_code and tabreportparameters.tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where doc_no = 1 and  tab_code = " + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select from_item_code,-sum(qty*convert(float(9),left(replace(size_desc,space(01),space(10)),4)))," + m_TAB_CODE + " from countersaleitem,sizemast,itemmast where maintain_stock=1 and item_type = 2 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and countersaleitem.item_code = itemmast.item_code and itemmast.size_code = sizemast.size_code and loct_code='" + rss.getString("loct_code") + "' group by from_item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select from_item_code,-sum(qty*convert(float(9),left(replace(size_desc,space(01),space(10)),4)))," + m_TAB_CODE + " from saleitem,sizemast,itemmast where maintain_stock=1 and item_type = 2 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and saleitem.item_code = itemmast.item_code and itemmast.size_code = sizemast.size_code and loct_code='" + rss.getString("loct_code") + "' group by from_item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select from_item_code,-sum(qty*convert(float(9),left(replace(size_desc,space(01),space(10)),4)))," + m_TAB_CODE + " from liqrrecipetran,sizemast,itemmast where doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and liqrrecipetran.item_code = itemmast.item_code and itemmast.size_code = sizemast.size_code and loct_code='" + rss.getString("loct_code") + "' group by from_item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code,doc_no)select item_code,sum(gl_opbal)," + m_TAB_CODE + ",1 from tabreportparameters where tab_code = " + m_TAB_CODE + " group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set ac_head_id=isnull((select convert(float(9),left(replace(size_desc,space(01),space(10)),4)) from sizemast,itemmast where tabreportparameters.item_code = itemmast.item_code and sizemast.size_code = itemmast.size_code),0) where tab_code = " + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount=isnull((select purchase_price from itemmast where item_code = tabreportparameters.item_code),0) where tab_code = " + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount=isnull((select top 1 round((basic_amt/bottle_qty),2) from puritem where item_code=tabreportparameters.item_code and pur_type = 1 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + "  and doc_dt in(select max(doc_dt) from puritem a where a.item_code=tabreportparameters.item_code and a.pur_type = 1 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + ")),amount) where tab_code = " + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1=round(amount/ac_head_id,2) where tab_code = " + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_2=round(amount*convert(int,gl_opbal/ac_head_id),2) where tab_code = " + m_TAB_CODE + " and amount <> 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_2=amount_2 + round(amount_1*(gl_opbal-convert(int,gl_opbal/ac_head_id,0)*ac_head_id),2) where tab_code = " + m_TAB_CODE + " and amount <> 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("INSERT INTO " + TEMPSTOCK_LIQR + "(ITEM_CODE,BASIC_AMT) SELECT ITEM_CODE,amount_2 FROM tabreportparameters WHERE tab_code =" + m_TAB_CODE + " and gl_opbal <> 0 and doc_no = 1");
                ps1.executeUpdate();
            }
            ps1 = con.prepareStatement("update " + TEMPSTOCK_LIQR + " set liqr_code = (select liqr_code from itemmast where itemmast.item_code = " + TEMPSTOCK_LIQR + ".item_code)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update " + TEMPSTOCK_LIQR + " set liqr_code = 1002, liqr_desc = 'BEER-STRONG' where liqr_code = 2 ");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update " + TEMPSTOCK_LIQR + " set liqr_code = 1003, liqr_desc = 'BEER-MILD' where liqr_code = 1");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update " + TEMPSTOCK_LIQR + " set liqr_code = 1004, liqr_desc = 'WINE' where liqr_code in(14,15) ");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update " + TEMPSTOCK_LIQR + " set liqr_code = 1001, liqr_desc = 'FOREIGN' where liqr_code > 0 and liqr_code < 1000");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where TAB_CODE =" + m_TAB_CODE + " ");
            ps1.executeUpdate();

            //===============Stock Updation Other=====================
            ps1 = con.prepareStatement("delete from " + TEMPSTOCK_OTHR + "");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("select LOCT_CODE from LOCTmast where LOCT_CODE >0 and STOCK_CHECK_YN = 1 AND loct_code in(select loct_code from onlnstok where item_type =2)");
            rss = ps1.executeQuery();

            while (rss.next()) {
                //Sealed Stock
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code = " + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,op_qty," + m_TAB_CODE + " from opitstok where comp_code=" + m_compcode + " and loct_code='" + rss.getString("loct_code") + "' and item_type = 2");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,sum(bottle_qty+free_qty)," + m_TAB_CODE + " from puritem where pur_type = 2 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,sum(bottle_qty)," + m_TAB_CODE + " from transfernote where item_type = 2 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and to_loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,-sum(qty)," + m_TAB_CODE + " from countersaleitem where maintain_stock=1 and item_type = 3 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,-sum(qty)," + m_TAB_CODE + " from saleitem where maintain_stock=1 and item_type = 3 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,gl_opbal,tab_code)select item_code,-sum(bottle_qty)," + m_TAB_CODE + " from transfernote where item_type = 2 and doc_dt < ='" + Temp_date + "' and comp_code=" + m_compcode + " and from_loct_code='" + rss.getString("loct_code") + "' group by item_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("INSERT INTO " + TEMPSTOCK_OTHR + "(ITEM_CODE,CL_BALANCE) SELECT ITEM_CODE,SUM(GL_OPBAL) FROM tabreportparameters WHERE tab_code =" + m_TAB_CODE + " GROUP BY ITEM_CODE ");
                ps1.executeUpdate();
            }

            ps1 = con.prepareStatement("delete from " + TEMPSTOCK_OTHR + " where cl_balance = 0 ");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update " + TEMPSTOCK_OTHR + " set BASIC_AMT = (select purchase_price from menucarditemmast where menuitem_code=" + TEMPSTOCK_OTHR + ".item_code)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update " + TEMPSTOCK_OTHR + " SET BASIC_AMT = isnull((select top 1 max(basic_rate) from puritem where item_code=" + TEMPSTOCK_OTHR + ".item_code and pur_type = 2 and doc_dt in(select max(doc_dt) from puritem a where a.pur_type = 2 and a.item_code = " + TEMPSTOCK_OTHR + ".item_code and a.doc_dt<='" + Temp_date + "')),BASIC_AMT)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update " + TEMPSTOCK_OTHR + " SET BASIC_AMT = round(cl_balance*BASIC_AMT,2)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update " + TEMPSTOCK_OTHR + " set liqr_desc = (select menuitem_desc from menucarditemmast where menuitem_code = " + TEMPSTOCK_OTHR + ".item_code) ");
            ps1.executeUpdate();
        }
           //------------------------checkbox end------------------------------------------------
            ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where TAB_CODE =" + m_TAB_CODE + " ");
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
                MskCashOpDate=rs.getString("cash_op_date");
                o_cashopbal=rs.getDouble("cash_op_balance");
            }

            //====String to date=====

            Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(Temp_date);
            Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(MskCashOpDate);
            //-----------''''New Addition On 14/12/2018-----
            ps1 = con.prepareStatement("UPDATE sales SET AMOUNT = ISNULL((SELECT SUM(QTY*RATE) FROM saleitem WHERE saleitem.DOC_NO=sales.DOC_NO AND saleitem.DOC_DT=sales.DOC_DT),AMOUNT) + CGST_AMT + SGST_AMT where doc_dt between '" +Temp_date+ "' and ' " +MskCashOpDate+ "' and comp_code= " + m_compcode + "");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("UPDATE sales SET NET_AMOUNT = round(AMOUNT + FOOD_SERVICE_TAX_AMT - DIS_AMOUNT + LIQ_VAT_AMT,0) where doc_dt between '" +Temp_date+ "' and ' " +MskCashOpDate+ "' and comp_code= " + m_compcode + "");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("UPDATE sales SET PAID_AMOUNT = NET_AMOUNT WHERE doc_dt between '" +Temp_date+ "' and ' " +MskCashOpDate+ "' and comp_code= " + m_compcode + " and BAL_AMOUNT = 0");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("UPDATE sales SET BAL_AMOUNT = NET_AMOUNT-PAID_AMOUNT WHERE doc_dt between '" +Temp_date+ "' and ' " +MskCashOpDate+ "' and comp_code= " + m_compcode + "");
            ps1.executeUpdate();
            //-----------''''New Addition On 14/12/2018-----
            if(date1.getTime()<=date2.getTime())
            {
                o_cashopbal = 0;
            }
            m_cashopbal = 0;
            try {
                if (o_cashopbal != 0) {
                    m_cashopbal = o_cashopbal;
                        ps1 = con.prepareStatement("select isnull(sum(paid_amount),0) as amt from sales where pmt_mode = 1 and doc_dt > '" + MskCashOpDate + "' and doc_dt < '" + Temp_date + "' and comp_code= " + m_compcode + " ");
                    rs = ps1.executeQuery();
                    while (rs.next()) {
                        m_cashopbal = m_cashopbal + rs.getDouble("amt");
                    }
                    ps1 = con.prepareStatement("select isnull(sum(by_cash_pmt),0) as amt from sales where doc_dt > '" + MskCashOpDate + "' and doc_dt < '" + Temp_date + "' and comp_code= " + m_compcode + " ");
                    rs = ps1.executeQuery();
                    while (rs.next()) {
                        m_cashopbal = m_cashopbal + rs.getDouble("amt");
                    }
                    ps1 = con.prepareStatement("select isnull(sum(amount),0) as amt from dailyrcp where crdt_code = 0 and doc_dt > '" + MskCashOpDate + "' and doc_dt < '" + Temp_date + "' and comp_code= " + m_compcode + " ");
                    rs = ps1.executeQuery();
                    while (rs.next()) {
                        m_cashopbal = m_cashopbal + rs.getDouble("amt");
                    }
                    ps1 = con.prepareStatement("select isnull(sum(amount),0) as amt from dailyexp where doc_dt > '" + MskCashOpDate + "' and doc_dt < '" + Temp_date + "' and comp_code= " + m_compcode + " ");
                    rs = ps1.executeQuery();
                    while (rs.next()) {
                        m_cashopbal = m_cashopbal - rs.getDouble("amt");
                    }
                }
            }catch (Exception e)
            {
                Toast.makeText(getActivity(), "350"+e, Toast.LENGTH_SHORT).show();
            }

            //=============''''''' opening cash balance ''''''''
            //''''''''''New Addition On 27/04/2017
            ps1 = con.prepareStatement("select withdrawal_amount,amount,card_amount from DATEWISEOPCASH where doc_dt='" + Temp_date + "' and comp_code=" + m_compcode + " and withdrawal_amount+amount > 0");
            rs = ps1.executeQuery();
            while (rs.next()) {
                m_withdrawalcash = rs.getDouble("withdrawal_amount");
                m_nextdayopcash = rs.getDouble("amount");
                m_cardamount = rs.getDouble("card_amount");
            }

            if (o_cashopbal == 0) {
                if (m_closingcashyn==0){
                  //  ps1 = con.prepareStatement("select isnull(sum(amount),0) as amount from dailyrcp where crdt_code = 0 and doc_dt = '" +Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id = 0 and amount <> 0 ");
                    ps1 = con.prepareStatement("select isnull(sum(amount),0) as amount from dailyrcp where crdt_code = 0 and doc_dt = '" +Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id = 0 and amount <> 0 ");
                }
                else
                    {
                    ps1 = con.prepareStatement("select amount as amount from datewiseopcash where doc_dt in(select max(doc_dt) from datewiseopcash a where a.comp_code="+ m_compcode + " and a.doc_dt < '" + Temp_date + "')");
                    }
            } else
                {
                ps1 = con.prepareStatement("select " + m_cashopbal + " as amount");
            }
            rs = ps1.executeQuery();
            while (rs.next()) {
                m_openingcash = m_openingcash + rs.getDouble("amount");
            }
            //-----------------------------------------------------------------------------

            ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where TAB_CODE =" + m_TAB_CODE + " and crdr_cd <> 'A'");
            ps1.executeUpdate();
            m_entryseqno = 0;
            ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Opening Balance'," + m_openingcash + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
            ps1.executeUpdate();
            m_entryseqno = m_entryseqno + 1;
            ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Add Sales'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
            ps1.executeUpdate();
            m_entryseqno = m_entryseqno + 1;
            ps1 = con.prepareStatement("select isnull(sum(item_value),0) as amount from saleitem where doc_dt = '" +Temp_date + "' and comp_code = " + m_compcode + " and item_type = 3 and item_code in(select menuitem_code from menucarditemmast where menu_code in(select menu_code from menumast where bargroup_yn = 0))");
            rs = ps1.executeQuery();
            while (rs.next()) {
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Food Amount'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
                m_grntot = m_grntot + rs.getDouble("amount");
            }

            if (m_closingcashyn==1){
                ps1 = con.prepareStatement("select menu_desc,isnull(sum(item_value),0) as amount from saleitem,menucarditemmast,menumast where saleitem.item_code=menucarditemmast.menuitem_code and menucarditemmast.menu_code = menumast.menu_code and bargroup_yn = 0 and show_seperate_yn = 1 and doc_dt = '" +Temp_date + "' and comp_code = " + m_compcode + " and item_type = 3 group by menu_desc order by menu_desc");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount_1,tab_code,doc_no) values('" + rs.getString("menu_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }
            }

            ps1 = con.prepareStatement("select isnull(sum(item_value),0) as amount from saleitem where doc_dt = '" +Temp_date + "' and comp_code = " + m_compcode + " and ((item_type <> 3) or (item_type = 3 and item_code in(select menuitem_code from menucarditemmast where menu_code in(select menu_code from menumast where bargroup_yn = 1))))");
            rs = ps1.executeQuery();
            while (rs.next()) {
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Bar Amount'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
                m_grntot = m_grntot + rs.getDouble("amount");
            }

            if (m_closingcashyn==1){
                ps1 = con.prepareStatement("select 'SALE-COUNTER' AS MENU_DESC,isnull(sum(item_value),0) as amount from saleitem,sales where saleitem.doc_no=sales.doc_no and saleitem.doc_dt=sales.doc_dt and sales.doc_dt = '" +Temp_date + "' and sales.comp_code = " + m_compcode + " and ((item_type <> 3) or (item_type = 3 and item_code in(select menuitem_code from menucarditemmast where menu_code in(select menu_code from menumast where bargroup_yn = 1)))) and sales.tbno_code not in(select scanner_tbno_code from profile) union select 'SALE-PARCEL' AS MENU_DESC,isnull(sum(item_value),0) as amount from saleitem,sales where saleitem.doc_no=sales.doc_no and saleitem.doc_dt=sales.doc_dt and sales.doc_dt = '" +Temp_date + "' and sales.comp_code = " + m_compcode + " and ((item_type <> 3) or (item_type = 3 and item_code in(select menuitem_code from menucarditemmast where menu_code in(select menu_code from menumast where bargroup_yn = 1)))) and sales.tbno_code in(select scanner_tbno_code from profile) ");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount_1,tab_code,doc_no) values('" + rs.getString("menu_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }
            }

            ps1 = con.prepareStatement("select isnull(sum(DIS_AMOUNT),0) as amount from sales where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
            rs = ps1.executeQuery();
            while (rs.next()) {
                if (rs.getDouble("amount") > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Less Discount'," + rs.getDouble("amount") + " ," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot - rs.getDouble("amount");
                }
            }

            ps1 = con.prepareStatement("select isnull(sum(CGST_AMT),0) as amount from sales where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
            rs = ps1.executeQuery();
            while (rs.next()) {
                if (rs.getDouble("amount") > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('CGST Amount'," + rs.getDouble("amount") + " ," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("amount");
                }
            }

            ps1 = con.prepareStatement("select isnull(sum(SGST_AMT),0) as amount from sales where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
            rs = ps1.executeQuery();
            while (rs.next()) {
                if (rs.getDouble("amount") > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('SGST Amount'," + rs.getDouble("amount") + " ," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("amount");
                }
            }

            ps1 = con.prepareStatement("select isnull(sum(liq_vat_amt),0) as amount from sales where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
            rs = ps1.executeQuery();
            while (rs.next()) {
                if (rs.getDouble("amount") > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('VAT Amount'," + rs.getDouble("amount") + " ," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("amount");
                }
            }

            ps1 = con.prepareStatement("select isnull(sum(food_service_tax_amt),0) as amount from sales where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
            rs = ps1.executeQuery();
            while (rs.next()) {
                if (rs.getDouble("amount") > 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Service Charge Amount'," + rs.getDouble("amount") + " ," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("amount");
                }
            }

            ps1 = con.prepareStatement("select isnull(sum(BAL_AMOUNT),0) as amount from sales where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
            rs = ps1.executeQuery();
            while (rs.next()) {
                if (rs.getDouble("amount") > 0) {
                    m_grntot = m_grntot - rs.getDouble("amount");
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Less Customer Credit Sale'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }
            }

            ps1 = con.prepareStatement("select (select gl_desc from glmast where ac_head_id=sales.ac_head_id) as gl_desc,isnull(sum(BAL_AMOUNT),0) as amount from sales where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' and bal_amount <> 0 group by ac_head_id order by gl_desc");
            rs = ps1.executeQuery();
            while (rs.next()) {
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount_1,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
            }

            ps1 = con.prepareStatement("select isnull(sum(paid_AMOUNT-by_cash_pmt),0) as amount from sales where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' and pmt_mode in(2,3)");
            rs = ps1.executeQuery();
            while (rs.next()) {
                if (rs.getDouble("amount") > 0) {
                    m_grntot = m_grntot - rs.getDouble("amount");
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Less Card Sale'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }
            }

            ps1 = con.prepareStatement("select (select crdt_desc from crdtmast where crdt_code=sales.crdt_code) as gl_desc,isnull(sum(paid_AMOUNT-by_cash_pmt),0) as amount from sales where comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' and pmt_mode in(2,3) group by crdt_code order by gl_desc");
            rs = ps1.executeQuery();
            while (rs.next()) {
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount_1,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
            }

            if(m_closingcashyn ==1){
                ps1 = con.prepareStatement("select isnull(sum(amount),0) as amount from sales where stock_adjust_yn=1 and comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    if (rs.getDouble("amount") > 0) {
                        m_grntot = m_grntot - rs.getDouble("amount");
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Less Bar Amount (Stk Adj)'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                    }
                }
            }

            if(m_closingcashyn ==1){
                ps1 = con.prepareStatement("select isnull(sum(amount),0) as amount from sales where stock_adjust_yn=2 and comp_code=" + m_compcode + " and doc_dt='" + Temp_date + "' ");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    if (rs.getDouble("amount") > 0) {
                        m_grntot = m_grntot - rs.getDouble("amount");
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Less Other Amount (Stk Adj)'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno = m_entryseqno + 1;
                    }
                }
            }

            ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Net Cash From Sale'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
            ps1.executeUpdate();
            m_entryseqno = m_entryseqno + 1;

            m_clbal = 0;

            ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyrcp.ac_head_id) as gl_desc,sum(amount) as amount from dailyrcp where doc_dt='" + Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id order by gl_desc");
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

            ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,sum(amount) as amount from dailyrcp where doc_dt='" + Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select pending_bill_ac_head_id from profile) group by narr order by gl_desc");
            rs = ps1.executeQuery();
            m_entryseqno = m_entryseqno + 1;
            m_rowcount = 0;
            while (rs.next()) {
                if (m_rowcount == 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Add Receipts (Other Customers)'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
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
            m_entryseqno = m_entryseqno + 1;
            m_rowcount = 0;
            ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,sum(amount) as amount from dailyrcp where doc_dt='" + Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id not in(select pending_bill_ac_head_id from profile) and ac_head_id not in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by narr order by narr");
            rs = ps1.executeQuery();
            while (rs.next()) {
                if (m_rowcount == 0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Add Other Receipts'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                }
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
                m_grntot = m_grntot + rs.getDouble("amount");
                m_rowcount++;
                m_clbal = m_clbal + rs.getDouble("amount");
            }
            if (m_rowcount > 0)
            {
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Total'," + m_clbal + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
            }

            if (o_cashopbal != 0) {
                m_clbal = 0;
                m_entryseqno = m_entryseqno + 1;
                m_rowcount = 0;
                ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,sum(amount) as amount from dailyrcp where doc_dt='" + Temp_date + "' and comp_code = " + m_compcode + " and ac_head_id = 0 group by narr order by narr");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    if (m_rowcount == 0) {
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Add Other Receipts'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                    }
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE + "," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("amount");
                    m_rowcount++;
                    m_clbal = m_clbal + rs.getDouble("amount");
                }
                if (m_rowcount > 0)
                {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Total'," + m_clbal + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }
            }

            m_grntot = m_grntot + m_openingcash;

            m_clbal = 0;

            ps1 = con.prepareStatement("select (select crdt_desc From crdtmast Where crdt_code=dailyrcp.crdt_code) as gl_desc,sum(amount) as amount from dailyrcp where crdt_code <> 0 and doc_dt='"+Temp_date+"' and comp_code = "+m_compcode+" group by crdt_code order by gl_desc");
            rs = ps1.executeQuery();
            m_entryseqno=m_entryseqno+1;
            m_rowcount=0;
            while (rs.next()) {
                if (m_rowcount==0){
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Less Credit Card Receipts'," + m_TAB_CODE +"," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno=m_entryseqno+1;
                }
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount_1,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE +"," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno=m_entryseqno+1;
                m_grntot = m_grntot - rs.getDouble("amount");
                m_clbal = m_clbal + rs.getDouble("amount");
                m_rowcount++;
            }
            if (m_rowcount>0) {
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Total'," + m_clbal + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
            }

            m_clbal = 0;

            ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyexp.ac_head_id) as gl_desc,sum(amount) as amount from dailyexp where doc_dt='"+Temp_date+"' and comp_code = "+m_compcode+" group by ac_head_id order by gl_desc");
            rs = ps1.executeQuery();
            m_entryseqno=m_entryseqno+1;
            while (rs.next()) {
                if (m_rowcount==0){
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Less Expenses'," + m_TAB_CODE +"," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno=m_entryseqno+1;
                }
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "'," + rs.getDouble("amount") + "," + m_TAB_CODE +"," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno=m_entryseqno+1;
                m_grntot = m_grntot - rs.getDouble("amount");
                m_clbal = m_clbal + rs.getDouble("amount");
                m_rowcount++;
            }
            if (m_rowcount>0) {
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Total'," + m_clbal + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
            }

            ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Computer Net Cash',"+ m_grntot +"," + m_TAB_CODE +"," + m_entryseqno + ")");
            ps1.executeUpdate();
            m_entryseqno=m_entryseqno+1;

            m_computernetcash = m_grntot;

            if (m_closingcashyn==1){

                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('WithDrawal Cash',"+ m_withdrawalcash +"," + m_TAB_CODE +"," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno=m_entryseqno+1;
                m_grntot = m_grntot - m_withdrawalcash;

                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Card Swapping',"+ m_cardamount +"," + m_TAB_CODE +"," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno=m_entryseqno+1;
                m_grntot = m_grntot - m_cardamount;

                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no) values('Next Day Opening Cash',"+ m_nextdayopcash +"," + m_TAB_CODE +"," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno=m_entryseqno+1;
                m_grntot = m_grntot - m_nextdayopcash;

                m_shortextracash = m_grntot;

                if (m_grntot < 0){
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Extra Cash',"+ -m_grntot +"," + m_TAB_CODE +"," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno=m_entryseqno+1;
                }
                else if (m_grntot >= 0){
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Short Cash',"+ m_grntot +"," + m_TAB_CODE +"," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno=m_entryseqno+1;
                }

            }
                m_grntot = 0;
                ps1 = con.prepareStatement("select (select gl_desc from glmast where ac_head_id=purchase.ac_head_id) as gl_desc,invoice_no,net_amount from purchase where doc_dt = '" + Temp_date + "' and comp_code = " + m_compcode + " and pur_type = 1 order by gl_desc");
                rs = ps1.executeQuery();
                m_entryseqno=m_entryseqno+1;
                m_rowcount=0;
                while (rs.next()) {
                    if (m_rowcount==0){
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('T.P.s From Traders'," + m_TAB_CODE +"," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno=m_entryseqno+1;
                    }
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,gl_desc,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "', '" + rs.getString("invoice_no") + "'," + rs.getDouble("net_amount") + "," + m_TAB_CODE +"," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno=m_entryseqno+1;
                    m_grntot=m_grntot+rs.getDouble("net_amount");
                    m_rowcount++;
                }
                if (m_rowcount>0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Invoice Total'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }

                m_grntot = 0;

                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(doc_no,ac_head_id,amount,crdr_cd,tab_code) select doc_no,ac_head_id,sum(basic_amt),'C',"+m_TAB_CODE+" from chalanitem where doc_dt = '" + Temp_date + "' and comp_code = " + m_compcode + " group by doc_no,ac_head_id");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update TABREPORTPARAMETERS set dis_amount = (select sum(add_amount) from chalanitem where chalanitem.doc_no=tabreportparameters.doc_no and chalanitem.ac_head_id=tabreportparameters.ac_head_id and chalanitem.doc_srno = 1 and chalanitem.doc_dt = '" + Temp_date+ "' and comp_code=" + m_compcode +") where tab_code = "+m_TAB_CODE+" and crdr_cd='C'");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update TABREPORTPARAMETERS set tot_amount = amount + dis_amount where tab_code = "+m_TAB_CODE+" and crdr_cd='C'");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("select glmast.gl_desc,tot_amount as net_amount,convert(int,doc_no) as doc_no from glmast,tabreportparameters where glmast.ac_head_id=tabreportparameters.ac_head_id and tab_code = "+m_TAB_CODE+" and crdr_cd='C' order by glmast.gl_desc,doc_no");
                rs = ps1.executeQuery();
                m_rowcount=0;
                while (rs.next()) {
                    if (m_rowcount==0){
                        ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Challans'," + m_TAB_CODE +"," + m_entryseqno + ",1)");
                        ps1.executeUpdate();
                        m_entryseqno=m_entryseqno+1;
                    }
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,gl_desc,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "', " + rs.getInt("doc_no") + "," + rs.getDouble("net_amount") + "," + m_TAB_CODE +"," + m_entryseqno + ")");
                    ps1.executeUpdate();
                    m_entryseqno=m_entryseqno+1;
                    m_grntot=m_grntot+rs.getDouble("net_amount");
                    m_rowcount++;
                }
                if (m_rowcount>0) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Challan Total'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                }

            m_grntot = 0;
            ps1 = con.prepareStatement("select (select gl_desc from glmast where ac_head_id=purchase.ac_head_id) as gl_desc,invoice_no,net_amount from purchase where doc_dt = '" + Temp_date + "' and comp_code = " + m_compcode + " and pur_type = 2 order by gl_desc");
            rs = ps1.executeQuery();
            m_entryseqno=m_entryseqno+1;
            m_rowcount=0;
            while (rs.next()) {
                if (m_rowcount==0){
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Other Purchase'," + m_TAB_CODE +"," + m_entryseqno + ",1)");
                    ps1.executeUpdate();
                    m_entryseqno=m_entryseqno+1;
                }
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,gl_desc,amount,tab_code,doc_no) values('" + rs.getString("gl_desc") + "', '" + rs.getString("invoice_no") + "'," + rs.getDouble("net_amount") + "," + m_TAB_CODE +"," + m_entryseqno + ")");
                ps1.executeUpdate();
                m_entryseqno=m_entryseqno+1;
                m_grntot=m_grntot+rs.getDouble("net_amount");
                m_rowcount++;
            }
            if (m_rowcount>0) {
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Invoice Total'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;
            }


            if (chk.isChecked()) {
                ps1 = con.prepareStatement("select liqr_desc as gl_desc,sum(basic_amt) as net_amount from " + TEMPSTOCK_LIQR + " group by liqr_code,liqr_desc order by liqr_code");
                rs = ps1.executeQuery();
                m_grntot = 0;
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale) values('Cl.Stock Valuation-Liquor'," + m_TAB_CODE + "," + m_entryseqno + ",1)");
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


                ps1 = con.prepareStatement("select liqr_desc as gl_desc,basic_amt as net_amount,ltrim(str(cl_balance)) as clqty from " + TEMPSTOCK_OTHR + " order by liqr_desc");
                rs = ps1.executeQuery();
                m_grntot = 0;
                m_entryseqno = m_entryseqno + 1;
                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,tab_code,doc_no,tot_sale,gl_desc) values('Cl.Stock Valuation-Other'," + m_TAB_CODE + "," + m_entryseqno + ",1,'Cl.Qty')");
                ps1.executeUpdate();
                m_entryseqno = m_entryseqno + 1;

                while (rs.next()) {
                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,gl_desc) values('" + rs.getString("gl_desc") + "', " + rs.getDouble("net_amount") + "," + m_TAB_CODE + "," + m_entryseqno + ",'" + rs.getString("clqty") + "')");
                    ps1.executeUpdate();
                    m_entryseqno = m_entryseqno + 1;
                    m_grntot = m_grntot + rs.getDouble("net_amount");
                }

                ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS(item_code,amount,tab_code,doc_no,tot_sale) values('Stock Value Total'," + m_grntot + "," + m_TAB_CODE + "," + m_entryseqno + ",1)");
                ps1.executeUpdate();
            }

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
