package com.suresh.wineshop;

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
import android.widget.DatePicker;
import android.widget.ProgressBar;
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
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profit_Loss_Statement_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date,txt_total,txt_cash_with_total,txt_card_payment_total,txt_purchase_total,txt_challan_total,txt_sale_total;
    ProgressBar pgb;
    PreparedStatement ps1;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "WINESHOP";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="",m_item_code;
    TransparentProgressDialog pd;
    double OP_VALUE=0.00;
    double PUR_VALUE=0.00;
    double CHALAN_VALUE=0.00;
    double CL_VALUE =0.00;
    double SALE_VALUE =0.00;

    double GP=0.00;
    double exp_ttl=0.00;
    double recpt_ttl=0.00;
    double RGP=0.00;
    int m_compcode,m_TAB_CODE;
    Button btn_report;
    String TEMPSTOCK;
    String IMEINumber,con_ipaddress,portnumber,db;
    TextView txt_expamt,txt_recpt_ttl;
  static  int seq_no=0;
  static  int dseq_no=0;
    //================Recyclerview 1======================
//    ArrayList<HashMap<String, String>> menu_card_arryList;
//    private RecyclerView.LayoutManager layoutManager_pe;
//    Profit_Loss_Statement_Fragment.atnds_recyclerAdapter attendance_recyclerAdapter;
//    private RecyclerView recycler_medal_offline_rpt_list;
    //================Recyclerview 2======================
//    ArrayList<HashMap<String, String>> menu_card_arryList2;
//    private RecyclerView.LayoutManager layoutManager_pe2;
//    Profit_Loss_Statement_Fragment.atnds_recyclerAdapter2 attendance_recyclerAdapter2;
//    private RecyclerView recycler_medal_offline_rpt_list2;
    public Profit_Loss_Statement_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pft_ls_fgrgmnt, container, false);
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

        pgb=(ProgressBar)view.findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        btn_report=(Button)view.findViewById(R.id.btn_report);
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

        SimpleDateFormat out = new SimpleDateFormat("M/d/yyyy");
        Temp_frm_date=out.format(d);

        Date dd = Calendar.getInstance().getTime();
        SimpleDateFormat ot = new SimpleDateFormat("M/d/yyyy");
        Temp_to_date=ot.format(dd);

        edt_frm_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                txt_opbal.setText("");
//                txt_sale.setText("");
//                txt_purchase.setText("");
//                txt_wine.setText("");

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                //-----------------
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
//                txt_opbal.setText("");
//                txt_sale.setText("");
//                txt_purchase.setText("");
//                txt_wine.setText("");

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

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                txt_opbal.setText("");
//                txt_sale.setText("");
//                txt_purchase.setText("");
//                txt_wine.setText("");

                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run()
                    {
                        grid_data(Temp_frm_date,Temp_to_date);
                      //  new insert_op().execute();
                        Intent i=new Intent(getActivity(),Profit_Loss_Report.class);
                        i.putExtra("from_date",Temp_frm_date);
                        i.putExtra("to_date",Temp_to_date);
                        startActivity(i);
                        pd.dismiss();
                    }
                }, 8000);
            }
        });
    }

    public void grid_data(String temp_frm_date,String temp_to_date) {
        seq_no=0;
        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

//                //--------pur_date------------------------
//                String q="select PURCHASE_STOCK_AS_PER from profile";
//                PreparedStatement ps = con.prepareStatement(q);
//                ResultSet prs = ps.executeQuery();
//                while(prs.next())
//                {
//                    m_purdate=prs.getString("PURCHASE_STOCK_AS_PER");
//
//                }
               // --------OPENING STOCK VALUE--------------------------
                TEMPSTOCK = "TEMPSTOCK" + m_TAB_CODE;
                ps1 = con.prepareStatement("IF OBJECT_ID('" + TEMPSTOCK + "') IS NOT NULL BEGIN DROP TABLE " + TEMPSTOCK + " END");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("CREATE TABLE " + TEMPSTOCK + " (ITEM_CODE NVARCHAR(100) NOT NULL DEFAULT ' ', CL_BALANCE FLOAT NOT NULL DEFAULT 0, LIQR_CODE FLOAT NOT NULL DEFAULT 0, BRND_CODE FLOAT NOT NULL DEFAULT 0, LIQR_DESC NVARCHAR(100) NOT NULL DEFAULT ' ', RATE MONEY NOT NULL DEFAULT 0, AMOUNT MONEY NOT NULL DEFAULT 0)");
                ps1.executeUpdate();

                //===============Stock Updation=====================
                ps1 = con.prepareStatement("delete from " + TEMPSTOCK + " ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("INSERT INTO " + TEMPSTOCK + "(ITEM_CODE,RATE) SELECT ITEM_CODE,PURCHASE_PRICE FROM ITEMMAST");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = isnull((select sum(op_balance+op_balance_b) from onlnstok where comp_code=" + m_compcode + " and onlnstok.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance+isnull((select sum(bottle_qty+free_qty) from puritem where doc_dt <'" + temp_frm_date + "' and comp_code=" + m_compcode + " and puritem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance+isnull((select sum(bottle_qty) from chalanitem where  doc_dt<'" + temp_frm_date + "' and comp_code=" + m_compcode + " and chalanitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance+isnull((select sum(qty) from countersalereturnitem where doc_dt<'" + temp_frm_date + "' and comp_code=" + m_compcode + " and countersalereturnitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance-isnull((select sum(qty+breakage_qty) from countersaleitem where doc_dt<'" + temp_frm_date + "' and comp_code=" + m_compcode + " and countersaleitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance-isnull((select sum(qty+breakage_qty) from provisionalsaleitem where doc_dt<'" + temp_frm_date + "' and comp_code=" + m_compcode + " and provisionalsaleitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from " + TEMPSTOCK + " where cl_balance = 0");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update "+ TEMPSTOCK + " set RATE = isnull((select top 1 round(basic_amt/bottle_qty,2) from puritem where bottle_qty > 0 and basic_amt > 0 and puritem.item_code = " + TEMPSTOCK + ".item_code and doc_dt<='" + temp_frm_date +"' and puritem.comp_code=" +m_compcode+" and doc_dt in(select max(doc_dt) from puritem a where a.item_code = " + TEMPSTOCK + ".item_code and a.bottle_qty > 0 and a.basic_amt > 0 and a.doc_dt<='" + temp_frm_date +"')),RATE)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " +TEMPSTOCK+" set amount = round(cl_balance*RATE,2)");
                ps1.executeUpdate();
                ResultSet   rs;
                ps1 = con.prepareStatement("select sum(amount) as net_amount from "+TEMPSTOCK+"");
                 rs = ps1.executeQuery();
                while (rs.next()) {
                    OP_VALUE = rs.getDouble("net_amount");
                }
                Log.d("VVVV-OP_VALUE",""+OP_VALUE);


                //--------PURCHASE STOCK VALUE -----------------------
                ps1 = con.prepareStatement("select isnull(sum(AMOUNT),0) as net_amount  from PURCHASE where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+"");
                 rs = ps1.executeQuery();
                while (rs.next()) {
                    PUR_VALUE = rs.getDouble("net_amount");
                }
                Log.d("VVVV-PUR_VALUE",""+PUR_VALUE);


               // --------CHALLAN STOCK VALUE--------------------------
                ps1 = con.prepareStatement("select isnull(sum(basic_amt),0)as net_amount from chalanitem where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+"");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    CHALAN_VALUE = rs.getDouble("net_amount");
                }
                Log.d("VVVV-CHALAN_VALUE",""+CHALAN_VALUE);
              //---------------CLOSING STOCK VALUE--------------------
                ps1 = con.prepareStatement("delete from " + TEMPSTOCK + " ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("INSERT INTO " + TEMPSTOCK + "(ITEM_CODE,RATE) SELECT ITEM_CODE,PURCHASE_PRICE FROM ITEMMAST");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = isnull((select sum(op_balance+op_balance_b) from onlnstok where comp_code=" + m_compcode + " and onlnstok.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance+isnull((select sum(bottle_qty+free_qty) from puritem where doc_dt<='" + temp_to_date + "' and comp_code=" + m_compcode + " and puritem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance+isnull((select sum(bottle_qty) from chalanitem where  doc_dt<='" + temp_to_date + "' and comp_code=" + m_compcode + " and chalanitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance+isnull((select sum(qty) from countersalereturnitem where doc_dt<='" + temp_to_date + "' and comp_code=" + m_compcode + " and countersalereturnitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance-isnull((select sum(qty+breakage_qty) from countersaleitem where doc_dt<='" + temp_to_date + "' and comp_code=" + m_compcode + " and countersaleitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " + TEMPSTOCK + " set cl_balance = cl_balance-isnull((select sum(qty+breakage_qty) from provisionalsaleitem where doc_dt<='" + temp_to_date + "' and comp_code=" + m_compcode + " and provisionalsaleitem.item_code=" + TEMPSTOCK + ".item_code),0)");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("delete from " + TEMPSTOCK + " where cl_balance = 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update "+ TEMPSTOCK + " set RATE = isnull((select top 1 round(basic_amt/bottle_qty,2) from puritem where bottle_qty > 0 and basic_amt > 0 and puritem.item_code = " + TEMPSTOCK + ".item_code and doc_dt<='" + temp_to_date +"' and puritem.comp_code=" +m_compcode+" and doc_dt in(select max(doc_dt) from puritem a where a.item_code = " + TEMPSTOCK + ".item_code and a.bottle_qty > 0 and a.basic_amt > 0 and a.doc_dt<='" + temp_to_date +"')),RATE)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update " +TEMPSTOCK+" set amount = round(cl_balance*RATE,2)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("select sum(amount) as net_amount from "+TEMPSTOCK+"");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    CL_VALUE  = rs.getDouble("net_amount");
                }
                Log.d("VVVV-CL_VALUE ",""+CL_VALUE );
                //----------------SAle VALUE-----------------------------------------------
                ps1 = con.prepareStatement("select isnull(sum((qty+breakage_qty)*RATE),0) AS net_amount  from COUNTERSALEITEM where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+"");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    SALE_VALUE  = rs.getDouble("net_amount");
                }

                ps1 = con.prepareStatement("select isnull(sum(qty*RATE),0) AS net_amount  from COUNTERSALERETURNITEM where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+"");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    SALE_VALUE  =SALE_VALUE- rs.getDouble("net_amount");
                }
                Log.d("VVVV-SAlE VALUE ",""+SALE_VALUE );
               //---------Tab Reports------------------------------------
                ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "");
                ps1.executeUpdate();
                seq_no++;
                ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,DOC_TYPE,AMOUNT_2,TAB_CODE,AMOUNT_1)values('To Opening Stock',"+OP_VALUE+",'By Sale',"+SALE_VALUE+"," + m_TAB_CODE + ","+seq_no+")");
                ps1.executeUpdate();
                //------------SALE DETAIL--------------------------------
                ps1 = con.prepareStatement("delete from " + TEMPSTOCK + " ");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 1,'IMFL',isnull(sum((qty+breakage_qty)*RATE),0) AS net_amount from COUNTERSALEITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE IN (1,2) AND ITEMMAST.LIQR_CODE NOT IN(17,56) ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement(" INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 1,'IMFL',isnull (sum((qty)*RATE),0)*-1 AS net_amount from COUNTERSALERETURNITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALERETURNITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE IN (1,2) AND ITEMMAST.LIQR_CODE NOT IN(17,56)");
                ps1.executeUpdate();

                //WINE
                ps1 = con.prepareStatement("INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 2,'WINE',isnull(sum((qty+breakage_qty)*RATE),0) AS net_amount from COUNTERSALEITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE IN (3,4) AND ITEMMAST.LIQR_CODE NOT IN(56) ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 2,'WINE',isnull(sum((qty)*RATE),0)*-1 AS net_amount from COUNTERSALERETURNITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALERETURNITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE IN (3,4) AND ITEMMAST.LIQR_CODE NOT IN(56) ");
                ps1.executeUpdate();

                //STRONG BEER
                ps1 = con.prepareStatement("INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 3,'STRONG-BEER',isnull(sum((qty+breakage_qty)*RATE),0) AS net_amount from COUNTERSALEITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE IN (5) AND ITEMMAST.LIQR_CODE NOT IN(56) ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("  INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 3,'STRONG-BEER',isnull(sum((qty)*RATE),0)*-1 AS net_amount from COUNTERSALERETURNITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALERETURNITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE IN (5) AND ITEMMAST.LIQR_CODE NOT IN(56)");
                ps1.executeUpdate();

                //MILD BEER
                ps1 = con.prepareStatement(" INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 4,'MILD-BEER',isnull(sum((qty+breakage_qty)*RATE),0) AS net_amount from COUNTERSALEITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE IN (6) AND ITEMMAST.LIQR_CODE NOT IN(56) ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement(" INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 4,'MILD-BEER',isnull(sum((qty)*RATE),0)*-1 AS net_amount from COUNTERSALERETURNITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALERETURNITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE IN (6) AND ITEMMAST.LIQR_CODE NOT IN(56) ");
                ps1.executeUpdate();

                //COUNTRY
                ps1 = con.prepareStatement(" INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 5,'COUNTRY',isnull(sum((qty+breakage_qty)*RATE),0) AS net_amount from COUNTERSALEITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND ITEMMAST.LIQR_CODE IN (17) AND ITEMMAST.LIQR_CODE NOT IN(56) ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 5,'COUNTRY',isnull(sum((qty)*RATE),0)*-1 AS net_amount from COUNTERSALERETURNITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+"  AND COUNTERSALERETURNITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND ITEMMAST.LIQR_CODE IN (17) AND ITEMMAST.LIQR_CODE NOT IN(56) ");
                ps1.executeUpdate();


                //COLD-DRINKS
                ps1 = con.prepareStatement("  INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 6,'COLD DRINKS',isnull(sum((qty+breakage_qty)*RATE),0) AS net_amount from COUNTERSALEITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+" AND COUNTERSALEITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND ITEMMAST.LIQR_CODE IN (56)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement(" INSERT INTO " +TEMPSTOCK+ "(RATE,ITEM_CODE,AMOUNT) select 6,'COLD DRINKS',isnull(sum((qty)*RATE),0)*-1 AS net_amount from COUNTERSALERETURNITEM,ITEMMAST,BRNDMAST,MAINLIQUORHEADMAST where doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code="  +m_compcode+"  AND COUNTERSALERETURNITEM.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND BRNDMAST.MAINLIQRHEAD_CODE=MAINLIQUORHEADMAST.MAINLIQRHEAD_CODE AND ITEMMAST.LIQR_CODE IN (56)");
                ps1.executeUpdate();

              //----------------------------------------------------------------------------------------------
               // -------Fill Grid---------------------------
                ps1 = con.prepareStatement("SELECT left(ltrim(ITEM_CODE)+space(18),18)+STR(SUM(AMOUNT),12,2) AS name FROM " +TEMPSTOCK+" GROUP BY ITEM_CODE,RATE ORDER BY RATE");
                rs = ps1.executeQuery();
                while (rs.next()) {
                   // SALE_VALUE  = rs.getInt("name");
                   // ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,DOC_TYPE,TAB_CODE)values('To Purchase ',"+PUR_VALUE+",'"+rs.getString("name")+"'," + m_TAB_CODE + ")");
                   // ps1.executeUpdate();
                    Log.d("VVV",rs.getString("name"));
                    if(rs.getString("name").startsWith("IMFL"))
                    {
                        seq_no++;
                        ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,DOC_TYPE,TAB_CODE,AMOUNT_1)values('To Purchase ',"+PUR_VALUE+",'"+rs.getString("name")+"'," + m_TAB_CODE + ","+seq_no+")");
                        ps1.executeUpdate();
                    }
                    if(rs.getString("name").startsWith("WINE"))
                    {

                        seq_no++;
                        ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,DOC_TYPE,TAB_CODE,AMOUNT_1)values('To Other ',"+CHALAN_VALUE+",'"+rs.getString("name")+"'," + m_TAB_CODE + ","+seq_no+")");
                        ps1.executeUpdate();
                    }
                    if(rs.getString("name").startsWith("STRONG-BEER"))
                    {

                        seq_no++;
                        ps1 = con.prepareStatement("insert into tabreportparameters (DOC_TYPE,TAB_CODE,AMOUNT_1)values('"+rs.getString("name")+"'," + m_TAB_CODE + ","+seq_no+")");
                        ps1.executeUpdate();
                    }
                    if(rs.getString("name").startsWith("MILD-BEER"))
                    {

                        seq_no++;
                        ps1 = con.prepareStatement("insert into tabreportparameters (DOC_TYPE,TAB_CODE,AMOUNT_1)values('"+rs.getString("name")+"'," + m_TAB_CODE + ","+seq_no+")");
                        ps1.executeUpdate();
                    }
                    if(rs.getString("name").startsWith("COUNTRY"))
                    {
                        seq_no++;
                        ps1 = con.prepareStatement("insert into tabreportparameters (DOC_TYPE,TAB_CODE,AMOUNT_1)values('"+rs.getString("name")+"'," + m_TAB_CODE + ","+seq_no+")");
                        ps1.executeUpdate();
                    }
                    if(rs.getString("name").startsWith("COLD DRINKS"))
                    {

                        seq_no++;
                        ps1 = con.prepareStatement("insert into tabreportparameters (DOC_TYPE,TAB_CODE,AMOUNT_1)values('"+rs.getString("name")+"'," + m_TAB_CODE + ","+seq_no+")");
                        ps1.executeUpdate();
                    }
                }
                //-----------------------------------

                seq_no++;
                ps1 = con.prepareStatement("insert into tabreportparameters (DOC_TYPE,AMOUNT_2,TAB_CODE,AMOUNT_1)values('By Closing Stock ',"+CL_VALUE+"," + m_TAB_CODE + ","+seq_no+")");
                ps1.executeUpdate();
                GP = (OP_VALUE + PUR_VALUE + CHALAN_VALUE) - (SALE_VALUE) - (CL_VALUE);
                //seq_no++;
               // ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,TAB_CODE,AMOUNT_1)values('Gross Profit',"+GP+"," + m_TAB_CODE + ","+seq_no+")");
               // ps1.executeUpdate();
                GP = GP * -1;
                if(GP<0){

                  double tt=SALE_VALUE + CL_VALUE+GP;
                    seq_no++;
                    ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,TAB_CODE,AMOUNT_1)values('Gross Profit',"+GP+"," + m_TAB_CODE + ","+seq_no+")");
                    ps1.executeUpdate();
                    seq_no++;
                    ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,DOC_TYPE,AMOUNT_2,TAB_CODE,AMOUNT_1)values('Total',"+tt+",'Total',"+tt+"," + m_TAB_CODE + ","+seq_no+")");
                    ps1.executeUpdate();
                }else{

                    double tt1=OP_VALUE + PUR_VALUE + CHALAN_VALUE+GP;

                    seq_no++;
                    ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,TAB_CODE,AMOUNT_1)values('Gross Profit',"+GP+"," + m_TAB_CODE + ","+seq_no+")");
                    ps1.executeUpdate();
                    seq_no++;
                    ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,DOC_TYPE,AMOUNT_2,TAB_CODE,AMOUNT_1)values('Total',"+tt1+",'Total',"+tt1+"," + m_TAB_CODE + ","+seq_no+")");
                    ps1.executeUpdate();
                }
                seq_no++;
                ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,TAB_CODE,AMOUNT_1)values('Expenses'," + m_TAB_CODE + ","+seq_no+")");
                ps1.executeUpdate();

                try {
                    pgb.setVisibility(View.VISIBLE);
                    // sp_data  = new ArrayList<Map<String, String>>();
                    ps1 = con.prepareStatement("delete from " + TEMPSTOCK + " ");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("INSERT INTO "+TEMPSTOCK+"(ITEM_CODE,RATE,AMOUNT) select 'TO '+GL_DESC AS GL_DESC ,SUM(AMOUNT) AS AMOUNT,dailyrcp.AC_HEAD_ID from dailyrcp,GLMAST where group_code in (select group_code from fagroupparameters where group_type in ('OTHER-RECEIPTS')) and doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code= 1 AND dailyrcp.AC_HEAD_ID=GLMAST.AC_HEAD_ID GROUP BY dailyrcp.AC_HEAD_ID,GL_DESC UNION ALL select 'TO '+GL_DESC AS GL_DESC,SUM(AMOUNT) AS AMOUNT,JVTRAN.AC_HEAD_ID from JVTRAN,GLMAST where group_code in (select group_code from fagroupparameters where group_type in ('OTHER-RECEIPTS')) and CRDR_CD='D' AND doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code= 1 AND JVTRAN.AC_HEAD_ID=GLMAST.AC_HEAD_ID GROUP BY JVTRAN.AC_HEAD_ID,GL_DESC UNION ALL select 'TO '+GL_DESC AS GL_DESC,SUM(AMOUNT) AS AMOUNT,BPTRAN.AC_HEAD_ID from BPTRAN,GLMAST where group_code in (select group_code from fagroupparameters where group_type in ('OTHER-RECEIPTS')) and CRDR_CD='D' AND doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code= 1 AND BPTRAN.AC_HEAD_ID=GLMAST.AC_HEAD_ID GROUP BY BPTRAN.AC_HEAD_ID,GL_DESC ");

                    ps1.executeUpdate();
                    exp_ttl=0;
                    dseq_no=seq_no++;

                    ps1 = con.prepareStatement("SELECT item_code,rtrim(ltrim(STR(SUM(rate),12,2))) as rate FROM " +TEMPSTOCK+" group by amount,item_code ORDER BY item_code");
                    rs = ps1.executeQuery();

                        while (rs.next()) {
                            Log.d("ssss",""+rs.getString("item_code"));
                            Log.d("ssss",""+rs.getString("rate"));
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("item_code", rs.getString("item_code"));
                            map.put("rate", rs.getString("rate"));
                            seq_no++;
                            ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,TAB_CODE,AMOUNT_1)values('"+rs.getString("item_code")+"',"+rs.getString("rate")+"," + m_TAB_CODE + ","+seq_no+")");
                            ps1.executeUpdate();
                           // exp_ttl=exp_ttl+Double.parseDouble(rs.getString("rate"));
                           // txt_expamt.setText(""+exp_ttl);

                        }
                       // GP = GP * -1;
                        Log.d("gggg",""+GP);
                    pgb.setVisibility(View.GONE);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
                }
              //----Daily Receipts

            }

           // dseq_no++;
            ps1 = con.prepareStatement("update tabreportparameters set DOC_TYPE='Gross Profit',AMOUNT_2="+GP+" where TAB_CODE=" + m_TAB_CODE + "and AMOUNT_1="+dseq_no+"");
            ps1.executeUpdate();
            pgb.setVisibility(View.GONE);
            try {
                pgb.setVisibility(View.VISIBLE);
                // sp_data  = new ArrayList<Map<String, String>>();
                ps1 = con.prepareStatement("delete from " + TEMPSTOCK + " ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("INSERT INTO "+TEMPSTOCK+"(ITEM_CODE,RATE,AMOUNT) select 'TO '+GL_DESC AS GL_DESC ,SUM(AMOUNT) AS AMOUNT,dailyexp.AC_HEAD_ID from dailyexp,GLMAST where group_code in (select group_code from fagroupparameters where group_type in ('BANK COMMISSION','SHOP EXPENSES','OTHER-RECEIPTS')) and doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code= 1 AND dailyexp.AC_HEAD_ID=GLMAST.AC_HEAD_ID GROUP BY dailyexp.AC_HEAD_ID,GL_DESC UNION ALL select 'TO '+GL_DESC AS GL_DESC,SUM(AMOUNT) AS AMOUNT,JVTRAN.AC_HEAD_ID from JVTRAN,GLMAST where group_code in (select group_code from fagroupparameters where group_type in ('BANK COMMISSION','SHOP EXPENSES','OTHER-RECEIPTS')) and CRDR_CD='D' AND doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code= 1 AND JVTRAN.AC_HEAD_ID=GLMAST.AC_HEAD_ID GROUP BY JVTRAN.AC_HEAD_ID,GL_DESC UNION ALL select 'TO '+GL_DESC AS GL_DESC,SUM(AMOUNT) AS AMOUNT,BPTRAN.AC_HEAD_ID from BPTRAN,GLMAST where group_code in (select group_code from fagroupparameters where group_type in ('BANK COMMISSION','SHOP EXPENSES','OTHER-RECEIPTS')) and CRDR_CD='D' AND doc_dt between '" +temp_frm_date+ "' and '" +temp_to_date+ "' and comp_code= 1 AND BPTRAN.AC_HEAD_ID=GLMAST.AC_HEAD_ID GROUP BY BPTRAN.AC_HEAD_ID,GL_DESC ");

                ps1.executeUpdate();
               // ps1.executeUpdate();
                ResultSet   rs;
                ps1 = con.prepareStatement("SELECT item_code,rtrim(ltrim(STR(SUM(rate),12,2))) as rate FROM " +TEMPSTOCK+" group by amount,item_code ORDER BY item_code");
                rs = ps1.executeQuery();

                recpt_ttl=0;
                dseq_no++;
                while (rs.next()) {
                    Log.d("dddd",""+rs.getString("item_code"));
                    Log.d("dddd",""+rs.getString("rate"));
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("item_code", rs.getString("item_code"));
                    map.put("rate", rs.getString("rate"));
                    dseq_no++;
                    ps1 = con.prepareStatement("update tabreportparameters set DOC_TYPE='"+rs.getString("item_code")+"',AMOUNT_2="+ rs.getString("rate")+" where TAB_CODE=" + m_TAB_CODE + "and AMOUNT_1="+dseq_no+"");
                    ps1.executeUpdate();
                    recpt_ttl=recpt_ttl+Double.parseDouble(rs.getString("rate"));
                   // txt_recpt_ttl.setText(""+recpt_ttl);
                    RGP=recpt_ttl+GP;

                    double tnet=RGP-exp_ttl;

                   // menu_card_arryList2.add(map);

                    seq_no++;
                    ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,TAB_CODE,AMOUNT_1)values('Total Expences',"+exp_ttl+"," + m_TAB_CODE + ","+seq_no+")");
                    ps1.executeUpdate();
                    seq_no++;
                    ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,TAB_CODE,AMOUNT_1)values('Net Profit',"+tnet+"," + m_TAB_CODE + ","+seq_no+")");
                    ps1.executeUpdate();
                    seq_no++;
                    ps1 = con.prepareStatement("insert into tabreportparameters (GL_DESC,AMOUNT,DOC_TYPE,AMOUNT_2,TAB_CODE,AMOUNT_1)values('Total ',"+RGP+",'Total ',"+RGP+"," + m_TAB_CODE + ","+seq_no+")");
                    ps1.executeUpdate();
                }

                pgb.setVisibility(View.GONE);
               // Log.d("Attendance_End_Data", "" + menu_card_arryList2.toString());

//                if (attendance_recyclerAdapter2 != null) {
//                    attendance_recyclerAdapter2.notifyDataSetChanged();
//                    System.out.println("Adapter " + attendance_recyclerAdapter2.toString());
//                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
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
