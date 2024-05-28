package com.suresh.wineshop;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.suresh.wineshop.Class.TransparentProgressDialog;

import org.json.JSONObject;

import java.sql.Connection;

public class Stock_Value_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,from_date,to_date,dfrom_date,dto_date;
    ProgressDialog progressDoalog;
    Connection con;
    Toolbar toolbar;
    String IMEINumber;
    double m_TAB_CODE;
    double m_clbal;
    double debit_total=0.00;
    double m_slval=0.00;
    TextView txt_debit_total,txt_credit_total;
    String mclbal,forname,db;
    ProgressBar progressBar;
    TransparentProgressDialog pd;
    //================Recyclerview 1======================
    String url="";
    TextView list_d1, list_d2, list_d3,list_d4,list_d5,list_d6,list_d7,list_d8,list_d9,list_d10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_value_reciepts_list_);
        connectionClass = new Config();

        Bundle bd = getIntent().getExtras();
        try {
            dfrom_date = bd.getString("dfrom_date");
            dto_date = bd.getString("dto_date");
            from_date = bd.getString("from_date");
            to_date = bd.getString("to_date");

        } catch (Exception e) {
        }
        pd = new TransparentProgressDialog(Stock_Value_Report.this, R.drawable.hourglass);
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");

        SharedPreferences sp =getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Stock Value Op+Pur+Sl+Cl");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(dto_date);
        toolbar_to_date.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(dfrom_date);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        SharedPreferences sp1 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

        url="http://pigateway.com/api/StockOpPurSlCl/StockValueOpPurSlCl?i="+db+"&m_TAB_CODE="+m_TAB_CODE+"&from_date="+from_date+"&to_date="+to_date+"";
        Log.d("uuuu",url);
       // url="http://pigateway.com/api/StockOpPurSlCl/StockValueOpPurSlCl?i=WINES_KAKA_NAVAPUR&m_TAB_CODE=899&from_date=02/01/2023&to_date=02/02/2023";
        progressBar=(ProgressBar)findViewById(R.id.pg);
        txt_debit_total=(TextView)findViewById(R.id.txt_debit_total);
        txt_credit_total=(TextView)findViewById(R.id.txt_credit_total);
        list_d1 = (TextView) findViewById(R.id.list_d1);
        list_d2 = (TextView) findViewById(R.id.list_d2);
        list_d3 = (TextView) findViewById(R.id.list_d3);
        list_d4 = (TextView) findViewById(R.id.list_d4);
        list_d5 = (TextView) findViewById(R.id.list_d5);
        list_d6 = (TextView) findViewById(R.id.list_d6);
        list_d7 = (TextView) findViewById(R.id.list_d7);
        list_d8 = (TextView) findViewById(R.id.list_d8);
        list_d9 = (TextView) findViewById(R.id.list_d9);
        list_d10 = (TextView) findViewById(R.id.list_d10);

         report();

    }

    //==========================================
//    public class report extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//           /* progressDoalog = new ProgressDialog(Stock_Value_Report.this);
//            progressDoalog.setMessage("Loading....");
//            progressDoalog.show();*/
//            pd.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... strings)  {
//
//            try {
//                con = connectionClass.CONN(con_ipaddress, portnumber,db);
//                if (con == null) {
//                    Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    //==================Opening Value======================================
//                    PreparedStatement ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("INSERT INTO TABREPORTPARAMETERS(ITEM_CODE,AMOUNT_1,TAB_CODE) SELECT ITEM_CODE,PURCHASE_PRICE,"+m_TAB_CODE+" FROM ITEMMAST WHERE LIQR_CODE <> 56 ");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = isnull((select sum(op_balance+op_balance_b) from onlnstok where comp_code=1 and onlnstok.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL+isnull((select sum(bottle_qty+free_qty) from puritem where doc_dt <'" +from_date+ "' and comp_code=1 and puritem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL+isnull((select sum(bottle_qty) from chalanitem where doc_dt<'"+from_date+"' and comp_code=1 and chalanitem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL+isnull((select sum(qty) from countersalereturnitem where doc_dt<'" +from_date+ "' and comp_code=1 and countersalereturnitem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL-isnull((select sum(qty+breakage_qty) from countersaleitem where doc_dt<'" +from_date+"' and comp_code=1 and countersaleitem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL-isnull((select sum(qty+breakage_qty) from provisionalsaleitem where doc_dt<'"+from_date+"' and comp_code=1 and provisionalsaleitem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where GL_CLBAL = 0 and TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set AMOUNT_1 = isnull((select top 1 round(basic_amt/bottle_qty,2) from puritem where bottle_qty > 0 and basic_amt > 0 and puritem.item_code = TABREPORTPARAMETERS.item_code and doc_dt <'" +from_date+"' and comp_code=1 and doc_dt in(select max(doc_dt) from puritem a with(index(puritem_doc_dt_desc)) where a.bottle_qty > 0 and a.basic_amt > 0 and a.doc_dt<'" + from_date +"')),AMOUNT_1) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set amount = round(GL_CLBAL*AMOUNT_1,2) where  TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("select isnull(sum(amount),0) as amount from TABREPORTPARAMETERS  where  TAB_CODE ="+m_TAB_CODE+"");
//                    ResultSet  rs = ps1.executeQuery();
//                    while (rs.next()) {
//                        NumberFormat nff = new DecimalFormat(".00");
//                        list_d1.setText(""+nff.format(rs.getDouble("amount")));
//                    }
//
//                    //==================Purchase  Value======================================
//                    ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where  TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("INSERT INTO TABREPORTPARAMETERS(ITEM_CODE,AMOUNT_1,TAB_CODE) SELECT ITEM_CODE,PURCHASE_PRICE,"+m_TAB_CODE+" FROM ITEMMAST WHERE LIQR_CODE <> 56");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL+isnull((select sum(bottle_qty+free_qty) from puritem where DOC_DT between '" +from_date+ "' and '"+to_date+ "' and comp_code=1 and puritem.item_code=TABREPORTPARAMETERS.item_code),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL+isnull((select sum(bottle_qty) from chalanitem where doc_dt between '" +from_date+ "' and '"+to_date+ "' and comp_code=1 and chalanitem.item_code=TABREPORTPARAMETERS.item_code),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where GL_CLBAL = 0 and TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set AMOUNT_1 = isnull((select top 1 round(basic_amt/bottle_qty,2) from puritem where bottle_qty > 0 and basic_amt > 0 and puritem.item_code = TABREPORTPARAMETERS.item_code and DOC_DT<='" +to_date+ "' and comp_code=1 and doc_dt in(select max(doc_dt) from puritem a with(index(puritem_doc_dt_desc)) where a.bottle_qty > 0 and a.basic_amt > 0 and doc_dt<='" + to_date +"')),AMOUNT_1)where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set amount = round(GL_CLBAL*AMOUNT_1,2) where TAB_CODE ="+m_TAB_CODE+" ");
//                    ps1.executeUpdate();
//
//                    ps1 = con.prepareStatement("select isnull(sum(amount),0) as amount from TABREPORTPARAMETERS  where  TAB_CODE ="+m_TAB_CODE+"");
//                    rs = ps1.executeQuery();
//                    while (rs.next()) {
//                        NumberFormat nff = new DecimalFormat(".00");
//                        list_d2.setText(""+nff.format(rs.getDouble("amount")));
//                    }
//
//                    //==================Total  Value======================================
//                    double opval=Double.parseDouble(list_d1.getText().toString());
//                    double purval=Double.parseDouble(list_d2.getText().toString());
//                    double ttl_val=opval+purval;
//                    NumberFormat nff = new DecimalFormat(".00");
//                    list_d3.setText(""+nff.format(ttl_val));
//                    //--------------------------------------------------------------------
//                    //==================Sale  Value======================================
//                    ps1 = con.prepareStatement("select isnull(sum((qty+breakage_qty)*rate),0) as amount from countersaleitem where doc_dt between '" +from_date+ "' and '"+to_date+"' and comp_code=1");
//                    rs = ps1.executeQuery();
//                    m_slval=0;
//                    while (rs.next()) {
//                        m_slval=  rs.getDouble("amount");
//                    }
//                    ps1 = con.prepareStatement("select isnull(sum((qty+breakage_qty)*rate),0)  as amount from provisionalsaleitem where doc_dt between '" +from_date+"' and '" +to_date+ "' and comp_code=1");
//                    rs = ps1.executeQuery();
//                    while (rs.next()) {
//                        m_slval= m_slval+ rs.getDouble("amount");
//                    }
//                    ps1 = con.prepareStatement("select isnull(sum(qty*rate),0)  as amount from countersalereturnitem where doc_dt between '" +from_date+"' and '" +to_date+ "' and comp_code=1");
//                    rs = ps1.executeQuery();
//                    while (rs.next()) {
//                        m_slval= m_slval- rs.getDouble("amount");
//                    }
//                    NumberFormat nf = new DecimalFormat(".00");
//                    list_d4.setText(""+nf.format(m_slval));
//                    //----------------------------------------------------------------
//                    //==================Closing   Value======================================
//                    ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("INSERT INTO TABREPORTPARAMETERS(ITEM_CODE,AMOUNT_1,TAB_CODE) SELECT ITEM_CODE,PURCHASE_PRICE,"+m_TAB_CODE+" FROM ITEMMAST WHERE LIQR_CODE <> 56 ");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = isnull((select sum(op_balance+op_balance_b) from onlnstok where comp_code=1 and onlnstok.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL+isnull((select sum(bottle_qty+free_qty) from puritem where doc_dt <='" +to_date+ "' and comp_code=1 and puritem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL+isnull((select sum(bottle_qty) from chalanitem where doc_dt<='"+to_date+"' and comp_code=1 and chalanitem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL+isnull((select sum(qty) from countersalereturnitem where doc_dt<='" +to_date+ "' and comp_code=1 and countersalereturnitem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL-isnull((select sum(qty+breakage_qty) from countersaleitem where doc_dt<='" +to_date+"' and comp_code=1 and countersaleitem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set GL_CLBAL = GL_CLBAL-isnull((select sum(qty+breakage_qty) from provisionalsaleitem where doc_dt<='"+to_date+"' and comp_code=1 and provisionalsaleitem.item_code=TABREPORTPARAMETERS.item_code ),0) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where GL_CLBAL = 0 and TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set AMOUNT_1 = isnull((select top 1 round(basic_amt/bottle_qty,2) from puritem where bottle_qty > 0 and basic_amt > 0 and puritem.item_code = TABREPORTPARAMETERS.item_code and doc_dt <='" +to_date+"' and comp_code=1  and doc_dt in(select max(doc_dt) from puritem a with(index(puritem_doc_dt_desc)) where a.bottle_qty > 0 and a.basic_amt > 0 and doc_dt<='" + to_date +"')),AMOUNT_1) where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("update TABREPORTPARAMETERS set amount = round(GL_CLBAL*AMOUNT_1,2) where  TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("select isnull(sum(amount),0) as amount from TABREPORTPARAMETERS  where  TAB_CODE ="+m_TAB_CODE+"");
//                    rs = ps1.executeQuery();
//                    while (rs.next()) {
//                        NumberFormat nfff = new DecimalFormat(".00");
//                        list_d5.setText(""+nfff.format(rs.getDouble("amount")));
//                    }
//
//                    //==================Margin    Value======================================
//                    double op_val=Double.parseDouble(list_d1.getText().toString());
//                    double pur_val=Double.parseDouble(list_d2.getText().toString());
//                    double cls_val=Double.parseDouble(list_d5.getText().toString());
//                    double ttlval=op_val+pur_val;
//                    double mar_val=ttlval-m_slval-cls_val;
//                    NumberFormat n = new DecimalFormat(".00");
//                    list_d6.setText(""+n.format(mar_val));
//
//                    //==================CashWithdrawals======================================
//                    ps1 = con.prepareStatement("SELECT isnull(SUM(withdrawal_amount),0) as amount from datewiseopcash where doc_dt between '" +from_date+ "' and '"+to_date+"' ");
//                    rs = ps1.executeQuery();
//                    while (rs.next()) {
//                        NumberFormat nfff = new DecimalFormat(".00");
//                        list_d7.setText(""+nfff.format(rs.getDouble("amount")));
//                    }
//
//                    //================== New Customers ======================================
//                    ps1 = con.prepareStatement("delete from TABREPORTPARAMETERS where TAB_CODE ="+m_TAB_CODE+"");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS (ac_head_id,amount,TAB_CODE)select ac_head_id,opening_bal,"+m_TAB_CODE+" from custwiseopeningbal where comp_code=1");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS (ac_head_id,amount,TAB_CODE)select ac_head_id,isnull(sum(net_amount),0),"+m_TAB_CODE+" from countersaleitem where pmt_mode=1 and comp_code=1 and ac_head_id <>0 AND DOC_SRNO IN(SELECT MIN(DOC_SRNO) FROM COUNTERSALEITEM A WHERE A.DOC_DT = COUNTERSALEITEM.DOC_DT AND A.WHOLESALE_DOC_NO = COUNTERSALEITEM.WHOLESALE_DOC_NO) group by ac_head_id");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS (ac_head_id,amount,TAB_CODE)select ac_head_id,isnull(sum(net_amount),0),"+m_TAB_CODE+" from provisionalsaleitem where pmt_mode=1 and comp_code=1 and ac_head_id <>0 AND DOC_SRNO IN(SELECT MIN(DOC_SRNO) FROM provisionalsaleitem A WHERE A.DOC_DT = provisionalsaleitem.DOC_DT AND A.WHOLESALE_DOC_NO = provisionalsaleitem.WHOLESALE_DOC_NO) group by ac_head_id");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS (ac_head_id,amount,TAB_CODE)select ac_head_id,-isnull(sum(basic_amt),0),"+m_TAB_CODE+" from chalanitem where comp_code=1 and ac_head_id in(select ac_head_id from glmast where group_code in(select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS (ac_head_id,amount,TAB_CODE)select ac_head_id,-isnull(sum(amount),0),"+m_TAB_CODE+" from jvtran where comp_code=1 and ac_head_id in(select ac_head_id from glmast where group_code in(select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) and crdr_cd='C' group by ac_head_id");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS (ac_head_id,amount,TAB_CODE)select ac_head_id,isnull(sum(amount),0),"+m_TAB_CODE+" from jvtran where comp_code=1 and ac_head_id in(select ac_head_id from glmast where group_code in(select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) and crdr_cd='D' group by ac_head_id");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS (ac_head_id,amount,TAB_CODE)select ac_head_id,-isnull(sum(add_amount-less_amount),0),"+m_TAB_CODE+" from chalanitem where comp_code=1 and DOC_SRNO IN(SELECT MIN(DOC_SRNO) FROM chalanitem A WHERE A.DOC_DT = chalanitem.DOC_DT) and ac_head_id in(select ac_head_id from glmast where group_code in(select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS (ac_head_id,amount,TAB_CODE)select ac_head_id,isnull(sum(amount),0),"+m_TAB_CODE+" from dailyexp where comp_code=1 and ac_head_id <> 0 and ac_head_id in(select ac_head_id from glmast where group_code in (select fatree.group_code from  fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id");
//                    ps1.executeUpdate();
//                    ps1 = con.prepareStatement("insert into TABREPORTPARAMETERS (ac_head_id,amount,TAB_CODE)select ac_head_id,-1*isnull(sum(amount),0),"+m_TAB_CODE+" from dailyrcp where comp_code=1 and ac_head_id <> 0 and ac_head_id in(select ac_head_id from glmast where group_code in (select fatree.group_code from  fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id");
//                    ps1.executeUpdate();
//
//                    ps1 = con.prepareStatement("select ac_head_id,(select lr_yn from glmast where glmast.ac_head_id=TABREPORTPARAMETERS.ac_head_id)as gl_desc,sum(amount) as m_clbal from TABREPORTPARAMETERS  where TAB_CODE = "+m_TAB_CODE+" group by ac_head_id,gl_desc having sum(amount) >0 ");
//                    rs = ps1.executeQuery();
//                    double m_calbal=0.00;
//                    double o_calbal=0.00;
//                    while (rs.next()) {
//                        String gl_desc=rs.getString("gl_desc");
//                        if(gl_desc.equals("0.0")){
//                            NumberFormat nfff = new DecimalFormat(".00");
//                            m_calbal=m_calbal+rs.getDouble("m_clbal");
//                            list_d8.setText(""+nfff.format(m_calbal));
//                        }
//                        else if(gl_desc.equals("1.0")){
//                            NumberFormat nfff = new DecimalFormat(".00");
//                            o_calbal=o_calbal+rs.getDouble("m_clbal");
//                            list_d9.setText(""+nfff.format(o_calbal));
//                        }
//                    }
//                    //===================TotOutstandings====================
//                    double nbal=0.00;
//                    double obal=0.00;
//                    if(!list_d8.getText().toString().equals(""))
//                    {
//                        nbal=Double.parseDouble(list_d8.getText().toString());
//                    }
//                    else {
//                        nbal=0.00;
//                        list_d8.setText(".00");
//                    }
//                    if(!list_d9.getText().toString().equals(""))
//                    {
//                        obal=Double.parseDouble(list_d9.getText().toString());
//                    }
//                    else {
//                        obal=0.00;
//                        list_d9.setText(".00");
//                    }
//
//                    // double obal=Double.parseDouble(list_d9.getText().toString());
//                    double ttl=nbal+obal;
//                    NumberFormat nn = new DecimalFormat(".00");
//                    list_d10.setText(""+nn.format(ttl));
//                }
//                con.close();
//               // progressDoalog.dismiss();
//            } catch (Exception e) {
//
//            }
//            return null;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//
//
//        pd.dismiss();
//     super.onPostExecute(s);
//    }
//}
//==========================================
   //=================================================
public void report(){
    progressDoalog = new ProgressDialog(Stock_Value_Report.this);
    progressDoalog.setMessage("Loading....");
    progressDoalog.show();
    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            progressBar.setVisibility(View.VISIBLE);
            if(response!=null)
            {
                progressBar.setVisibility(View.INVISIBLE);
                try{
                    JSONObject jsonObject = new JSONObject(response.toString());
                            String op = jsonObject.getString("opening");
                            String pur = jsonObject.getString("purchase");
                            String tot = jsonObject.getString("total_value");
                            String sale = jsonObject.getString("sale");
                            String closing = jsonObject.getString("closing");
                            String mar = jsonObject.getString("margin");
                            String cash_withdraw = jsonObject.getString("cash_withdraw");
                            String new_cust = jsonObject.getString("new_cust");
                            String old_cust = jsonObject.getString("old_cust");
                            String tot_outstanding = jsonObject.getString("tot_outstanding");
                            list_d1.setText(op);
                            list_d2.setText(pur);
                            list_d3.setText(tot);
                            list_d4.setText(sale);
                            list_d5.setText(closing);
                            list_d6.setText(mar);
                            list_d7.setText(cash_withdraw);
                            list_d8.setText(new_cust);
                            list_d9.setText(old_cust);
                            list_d10.setText(tot_outstanding);

                        progressDoalog.dismiss();

                }catch (Exception e){
                    Toast.makeText(Stock_Value_Report.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
               DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    MySingleton.getInstance(Stock_Value_Report.this).addToRequestque(jsonObjectRequest);
}
}
