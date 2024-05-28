package com.example.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cancel_Kot_Activity extends AppCompatActivity {
    //================Recyclerview 2======================
    ArrayList<HashMap<String, String>> bill_arryList;
    private RecyclerView.LayoutManager layoutManager_bill;
    tbill_recyclerAdapter bill_recyclerAdapter;
    private RecyclerView recycler_bill_list;
    Toolbar toolbar;
    ProgressDialog progressDoalog;
    TextView txt_tbl_bill;
    IMEI_Activity m_com;
    int rowCount = 0;
    IMEI_Activity connectionClass;
    String con_ipaddress ,portnumber,doc_no,TBNO_DESC,str_compcode,str_compdesc;
    ProgressBar pbbar;
    float m_compcode;
    int m_EVERY_ENTRY_KOT_YN=0;
    HashMap<String, String> map;
    float TBNO_CODE,m_loctcode;
    String doc_dt,m_lastdocsrno,flag,query,doc_dt_display,menu_option, m_cncl_desc,m_cncl_code;;
    String m_print_kot_yn;
    String m_print_bill_yn;
    String m_itemcode,m_itemname,m_qty,tab_user_code,str_loctcode,m_ipaddress;
    Button btn_qty, btn_contact_operation_cancle, btn_update;
    AlertDialog dialog;
    EditText edt_cancel_order;
    TextView item_name,edt_order;
    Spinner sp_kot_cancel;
    int m_clbalance, m_clbalance_b;
    int m_ordqty=0;
    int m_canqty=0;
    int mqty=0;
    int mtype=0;
    int mitemcode=0;
    String m_tempfile="TEMPCNLKOT";
    Float m_tbusercode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kot);
        SharedPreferences i = getSharedPreferences("IPADDRESS", MODE_PRIVATE);
        m_ipaddress = i.getString("IPADDRESS", "");
        Log.d("IPADDRESS", m_ipaddress);

        SharedPreferences sp2 = getSharedPreferences("Profile_data", MODE_PRIVATE);
        str_loctcode = sp2.getString("m_loctcode", "");
        m_loctcode = Float.parseFloat(str_loctcode);

        SharedPreferences s = getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        tab_user_code = s.getString("tab_user_code", "");
        m_tbusercode=Float.parseFloat(s.getString("tab_user_code", ""));
        Log.d("tab_user_code",tab_user_code);
        m_tempfile = m_tempfile + Math.round(m_tbusercode);

        SharedPreferences sp11 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp11.getString("ipaddress", "");
        portnumber = sp11.getString("portnumber", "");
        SharedPreferences ss = getSharedPreferences("COMP_CODE", MODE_PRIVATE);
        str_compcode = ss.getString("COMP_CODE", "");
        str_compdesc = ss.getString("COMP_DESC", "");
        m_compcode = Float.parseFloat(str_compcode);

        SharedPreferences spp2 = getSharedPreferences("Profile_data", MODE_PRIVATE);
        m_EVERY_ENTRY_KOT_YN = spp2.getInt("m_EVERY_ENTRY_KOT_YN", 0);
        Log.d("pppp", "m_EVERY_ENTRY_KOT_YN "+m_EVERY_ENTRY_KOT_YN);

        SharedPreferences sp = getSharedPreferences("HOME_DATA", MODE_PRIVATE);
        TBNO_CODE = Float.parseFloat(sp.getString("TBNO_CODE", ""));
        TBNO_DESC = sp.getString("TBNO_DESC", "");
        Bundle b=getIntent().getExtras();
        try
        {
            doc_dt=b.getString("doc_dt");
            m_lastdocsrno=b.getString("m_lastdocsrno");
            doc_dt_display=b.getString("doc_dt_display");
            menu_option=b.getString("menu_option");
            flag=b.getString("flag");
        }catch(Exception e){}
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TextView tbl_bill = (TextView) toolbar.findViewById(R.id.txt_total);//title
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);//date
        TextView txt_option = (TextView) toolbar.findViewById(R.id.txt_option);//menu

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Table : "+TBNO_DESC);
        toolbar_date.setText("Date : "+doc_dt_display);
        txt_option.setText(menu_option);
        TextView txt_hotel_name = (TextView) toolbar.findViewById(R.id.txt_hotel_name);
        txt_hotel_name.setText(str_compdesc);
        txt_hotel_name.setTextColor(0xFFFFFFFF);
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_date.setTextColor(0xFFFFFFFF);
        txt_option.setTextColor(0xFFFFFFFF);
        pbbar = (ProgressBar) findViewById(R.id.pgb);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

            //---------------------Recyclerview 2-----------------------------------------
            bill_arryList = new ArrayList<HashMap<String, String>>();
            recycler_bill_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
            layoutManager_bill = new LinearLayoutManager(Cancel_Kot_Activity.this, RecyclerView.VERTICAL, false);
            recycler_bill_list.setLayoutManager(layoutManager_bill);
            bill_recyclerAdapter = new tbill_recyclerAdapter(Cancel_Kot_Activity.this, bill_arryList);
            recycler_bill_list.setAdapter(bill_recyclerAdapter);

           // txt_tbl_bill = (TextView) findViewById(R.id.txt_tbl_bill);
            //------------------------------------------------------------------------------------------
        }

        //flags 1-Latest order  2-Total order  3-Total order(summary)
        //parameter to be pass doc_dt,tbno_code,doc_srno

            query="select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,doc_srno,LEFT(CONVERT(VARCHAR(10),tran_date,8),5) as entrytime,item_code from countersaleitem where doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+TBNO_CODE+" and print_kot_yn = 1 and FROM_TAB_YN = 1 AND bill_no = 0 order by doc_srno";


        load_kot_data();

    }

    public void load_kot_data() {
        // ttl=0;
        // bill_arryList.clear();
        progressDoalog = new ProgressDialog(Cancel_Kot_Activity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        connectionClass = new IMEI_Activity();
        try {
            progressDoalog.dismiss();
            pbbar.setVisibility(View.VISIBLE);
            //bill_arryList.clear();
            Connection con = connectionClass.CONN(con_ipaddress,portnumber);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                //String query = "select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc+' ['+(select liqr_desc from liqrmast where liqr_code=itemmast.liqr_code)+']' from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,ltrim(str(rate,9,2)) as rate,ltrim(str(item_value,12,2)) as item_value,doc_srno,print_kot_yn,print_yn from countersaleitem where doc_dt = '12/21/2019' and comp_code = 1 and tbno_code <> 0 and tbno_code=20 order by doc_srno";
                //String query = "\n" +
                //       "select * from SURESH_TESTING where TBL_NO='"+TBNO_DESC+"'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("ITEM", rs.getString("item_name"));
                    map.put("LSIZE", rs.getString("size_desc"));
                    map.put("QTY", rs.getString("qty"));
                   // map.put("RATE", rs.getString("rate"));
                   // map.put("VALUE", rs.getString("item_value"));
                    map.put("DOC_SRNO", rs.getString("doc_srno"));
                    map.put("entrytime", rs.getString("entrytime"));
                    map.put("ITEM_CODE", rs.getString("ITEM_CODE"));

                    bill_arryList.add(map);
                }
            }
            pbbar.setVisibility(View.GONE);
            Log.d("bill_arryList_Data", "" + bill_arryList.toString());

            if (bill_recyclerAdapter != null) {
                bill_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + bill_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error.." + e, Toast.LENGTH_SHORT).show();
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
        public tbill_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kot_list, parent, false);
            tbill_recyclerAdapter.Pex_ViewHolder viewHolder = new tbill_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final tbill_recyclerAdapter.Pex_ViewHolder holder, final int position) {
            // holder.contact_list_id.setText(attendance_list.get(position).get("A"));
           // m_print_kot_yn=attendance_list.get(position).get("PRINT_KOT_YN");
           // m_print_bill_yn=attendance_list.get(position).get("PRINT_YN");

            holder.name.setText(attendance_list.get(position).get("ITEM"));
            holder.lsize.setText(attendance_list.get(position).get("LSIZE"));
            holder.qty.setText(attendance_list.get(position).get("QTY"));
           // holder.rate.setText(attendance_list.get(position).get("RATE"));
           // holder.value.setText(attendance_list.get(position).get("VALUE"));
            holder.time.setText(attendance_list.get(position).get("entrytime"));
            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doc_no = attendance_list.get(position).get("DOC_SRNO");
                    m_itemcode = attendance_list.get(position).get("ITEM_CODE");
                    m_itemname = attendance_list.get(position).get("ITEM");
                    if (attendance_list.get(position).get("LSIZE").length()>2) {
                        m_itemname = m_itemname +", "+ attendance_list.get(position).get("LSIZE");
                    }

                    m_qty = attendance_list.get(position).get("QTY");
                    kot_cancel_popup_form();
                }
            });

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name, qty, rate, value, lsize,time;
            ImageView img_delete;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.lsize = (TextView) itemView.findViewById(R.id.list_d2);
                this.qty = (TextView) itemView.findViewById(R.id.list_d3);
                //this.rate = (TextView) itemView.findViewById(R.id.list_d4);
               // this.value = (TextView) itemView.findViewById(R.id.list_d5);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.img_delete = (ImageView) itemView.findViewById(R.id.img_delete);
                this.time = (TextView) itemView.findViewById(R.id.list_d6);

            }
        }
    }
    public void kot_cancel_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.cancel_kot_popup_form, null);
        item_name = (TextView) alertLayout.findViewById(R.id.item_name);
        item_name.setText(m_itemname);
        edt_cancel_order = (EditText) alertLayout.findViewById(R.id.edt_cancel_order);
       // edt_cancel_order.setText("");
        edt_order = (TextView) alertLayout.findViewById(R.id.edt_order);
        edt_order.setText(m_qty);
        sp_kot_cancel = (Spinner) alertLayout.findViewById(R.id.sp_kot_cancel);
        new  load_spinner_kot_cancel().execute();
        // sp_size=(Spinner) alertLayout.findViewById(R.id.sp_size);
        // qty=edt_order.getText().toString();
        btn_contact_operation_cancle = (Button) alertLayout.findViewById(R.id.btn_contact_operation_cancle);
        btn_contact_operation_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_qty = (Button) alertLayout.findViewById(R.id.btn_qty);
        btn_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_cancel_order.getText().toString().length()==0){
                    edt_cancel_order.setError("Quantity Should Not Be Zero");
                    edt_cancel_order.requestFocus();
                    return;
                }
                else {
                    m_ordqty = Integer.parseInt(edt_order.getText().toString());
                    m_canqty = Integer.parseInt(edt_cancel_order.getText().toString());
                    if (m_canqty == 0 || m_canqty > m_ordqty) {
                        edt_cancel_order.setError("Quantity Should Not Be Zero OR Cancelled Quantity Should Not Be Greater Than Ordered Quantity");
                        edt_cancel_order.requestFocus();
                        return;
                    }
                    String query="";
                    if (m_canqty == m_ordqty) {
                        Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                        if (con == null) {
                            Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                PreparedStatement ps = con.prepareStatement("update countersaleitem set cncl_qty=cncl_qty+" + m_canqty + ", cncl_code = " + m_cncl_code + " where doc_srno = " + doc_no + " and doc_dt = '"+doc_dt+"'");
                                ps.executeUpdate();
                                ps = con.prepareStatement("insert into countersaleitemcancel select * from countersaleitem where doc_srno = " + doc_no + " and doc_dt = '"+doc_dt+"'");
                                ps.executeUpdate();
                                ps = con.prepareStatement("delete from countersaleitem where doc_srno = " + doc_no + " and doc_dt = '"+doc_dt+"' ");
                                ps.executeUpdate();
                                ps = con.prepareStatement("delete from liqrrecipetran where doc_no = " + doc_no + " and doc_dt = '"+doc_dt+"'");
                                ps.executeUpdate();
                                ps = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','COUNTERSALEITEM',1,'0',''");
                                ps.executeUpdate();
                                ps = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','LIQRRECIPETRAN',1,'0',''");
                                ps.executeUpdate();
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        try {
                        Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                        //------#TEMP FILE----------------------------
                         PreparedStatement ps = con.prepareStatement("if object_id('"+m_tempfile+"') is not null begin drop table " + m_tempfile + " end");
                         ps.executeUpdate();
                        //--------------------------------------------
                        ps = con.prepareStatement("select * into " + m_tempfile + " from countersaleitem where doc_srno = " + doc_no + " and doc_dt = '"+doc_dt+"' ");
                        ps.executeUpdate();
                        ps = con.prepareStatement("delete from countersaleitem where doc_srno = " + doc_no + " and doc_dt = '"+doc_dt+"' ");
                        ps.executeUpdate();
                        ps = con.prepareStatement("delete from liqrrecipetran where doc_no = " + doc_no + " and doc_dt = '"+doc_dt+"'");
                        ps.executeUpdate();
                        ps = con.prepareStatement("update " + m_tempfile + " set qty=qty-" + m_canqty + ", cncl_qty=cncl_qty+" + m_canqty + ", item_value = rate*(qty-" + m_canqty + "), cncl_code = " + m_cncl_code + "");
                        ps.executeUpdate();
                        ps = con.prepareStatement("insert into countersaleitem select * from " + m_tempfile + "");
                        ps.executeUpdate();
                        ps = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','COUNTERSALEITEM',1,'0',''");
                        ps.executeUpdate();
                        ps = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','LIQRRECIPETRAN',1,'0',''");
                        ps.executeUpdate();
                        ps = con.prepareStatement("select ltrim(rtrim(str(qty))) as qty,ltrim(rtrim(str(item_type))) as item_type,item_code from " + m_tempfile + "");
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            mqty = Integer.parseInt(rs.getString("qty"));
                            mtype = Integer.parseInt(rs.getString("item_type"));
                            mitemcode = Integer.parseInt(rs.getString("item_code"));
                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "378"+e, Toast.LENGTH_SHORT).show();
                        }

                        if (mtype == 3) {
                            try {
                                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                                if (con == null) {
                                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();
                                } else {
                                   // String qry = "SELECT MENUITEM_CODE,RAWITEM_CODE,QUANTITY*" + edt_order.getText().toString() + " as qty,(QUANTITY*" + edt_order.getText().toString() + ")*CONVERT(MONEY,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10)) as mlqty,LIQR_CODE,BRND_CODE,full_bottle,loose_bottle FROM ITEMDEFINATION,ITEMMAST,SIZEMAST WHERE ITEM_TYPE = 1 AND SIZEMAST.SIZE_CODE=ITEMMAST.SIZE_CODE AND ITEM_CODE=RAWITEM_CODE AND MENUITEM_CODE=" + item_code + "";
                                    String qry = "SELECT MENUITEM_CODE,RAWITEM_CODE,QUANTITY*" + mqty + " as qty,(QUANTITY*" + mqty + ")*CONVERT(MONEY,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10)) as mlqty,LIQR_CODE,BRND_CODE,full_bottle,loose_bottle FROM ITEMDEFINATION,ITEMMAST,SIZEMAST WHERE ITEM_TYPE = 1 AND SIZEMAST.SIZE_CODE=ITEMMAST.SIZE_CODE AND ITEM_CODE=RAWITEM_CODE AND MENUITEM_CODE=" + mitemcode + "";
                                    PreparedStatement ps = con.prepareStatement(qry);
                                    ResultSet rs = ps.executeQuery();
                                    rowCount = 0;
                                    //ArrayList data1 = new ArrayList();
                                    while (rs.next()) {
                                        rowCount++;
                                        if (rowCount > 0) {
                                            //=========================================
                                            try {
                                                String qrystk = "";
                                                connectionClass = new IMEI_Activity();
                                                Connection conn = connectionClass.CONN(con_ipaddress, portnumber);
                                                if (conn == null) {
                                                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    if (rs.getInt("loose_bottle") == 1) {
                                                        qrystk = "SELECT ONLNSTOK.ITEM_CODE,CL_BALANCE-CL_BALANCE_B as CL_BALANCE,CL_BALANCE_B,itemmast.mrp FROM ONLNSTOK,ITEMMAST WHERE ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE = 1 AND LIQR_CODE=" + rs.getInt("liqr_code") + " AND BRND_CODE=" + rs.getInt("brnd_code") + " AND LOCT_CODE=" + m_loctcode + " and cl_balance >=" + rs.getInt("mlqty") + " order by (select seq_no from sizemast where size_code=itemmast.size_code) ";
                                                    } else if (rs.getInt("full_bottle") == 1) {
                                                        qrystk = "SELECT ONLNSTOK.ITEM_CODE,CL_BALANCE-CL_BALANCE_B as CL_BALANCE,CL_BALANCE_B,itemmast.mrp FROM ONLNSTOK,ITEMMAST WHERE ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=" + rs.getString("RAWITEM_CODE") + " AND LOCT_CODE=" + m_loctcode + " and cl_balance >=" + rs.getInt("mlqty") + " ";
                                                    }
                                                    PreparedStatement pss = con.prepareStatement(qrystk);
                                                    ResultSet rst = pss.executeQuery();
                                                    int cnt = 0;
                                                    //ArrayList data1 = new ArrayList();
                                                    while (rst.next()) {
                                                        cnt++;
                                                        m_clbalance = rst.getInt("cl_balance");
                                                        m_clbalance_b = rst.getInt("cl_balance_b");
                                                        Log.d("innner cnt", "" + cnt);
                                                        if (m_clbalance_b >= rs.getInt("mlqty")) {
                                                            try {
                                                                Connection cn = connectionClass.CONN(con_ipaddress, portnumber);
                                                                if (cn == null) {
                                                                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                                                                } else {
                                                                    int m_exit_yn=0;
                                                                    String q = "insert into liqrrecipetran(doc_no,doc_dt,food_item_code,food_qty,qty,sale_type,item_code,from_item_code,comp_code,mrp,loct_code,ip_address,tbno_code)values(" + doc_no + ",'" + doc_dt + "'," + mitemcode + "," + mqty + "," + rs.getString("qty") + ",1,'" + rs.getString("rawitem_code") + "','" + rst.getString("item_code") + "'," + m_compcode + "," + rst.getString("mrp") + "," + m_loctcode + ",'" + m_ipaddress + "'," + TBNO_CODE + ")";
                                                                    while (true) {
                                                                        try {
                                                                            PreparedStatement p = cn.prepareStatement(q);
                                                                            p.executeUpdate();
                                                                            p = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','LIQRRECIPETRAN',1,'0',''");
                                                                            p.executeUpdate();
                                                                            m_exit_yn=1;
                                                                        }
                                                                        catch (Exception e){
                                                                            Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        if(m_exit_yn==1){
                                                                            break;
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                            } catch (Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
                                                            }

                                                        }else if (m_clbalance >= rs.getInt("mlqty")) {
                                                            try {
                                                                Connection cn = connectionClass.CONN(con_ipaddress, portnumber);
                                                                if (cn == null) {
                                                                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                                                                } else {
                                                                    int m_exit_yn=0;
                                                                    String q = "insert into liqrrecipetran(doc_no,doc_dt,food_item_code,food_qty,qty,sale_type,item_code,from_item_code,comp_code,mrp,loct_code,ip_address,tbno_code)values(" + doc_no + ",'" + doc_dt + "'," + mitemcode + "," + mqty + "," + rs.getString("qty") + ",0,'" + rs.getString("rawitem_code") + "','" + rst.getString("item_code") + "'," + m_compcode + "," + rst.getString("mrp") + "," + m_loctcode + ",'" + m_ipaddress + "'," + TBNO_CODE + ")";
                                                                    while (true) {
                                                                        try {
                                                                            PreparedStatement p = cn.prepareStatement(q);
                                                                            p.executeUpdate();
                                                                            p = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','LIQRRECIPETRAN',1,'0',''");
                                                                            p.executeUpdate();
                                                                            m_exit_yn=1;
                                                                        }
                                                                        catch (Exception e){
                                                                            Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        if(m_exit_yn==1){
                                                                            break;
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                            } catch (Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }

                                                }
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    int m_exit_yn=0;
                    query = "INSERT INTO TBNOFROMTABFORKOT(TBNO_CODE,ACTION_TYPE,DOC_SRNO_UPTO,CNCL_QTY,TABUSER_CODE) VALUES("+TBNO_CODE+",2,"+doc_no+","+m_canqty+","+tab_user_code+")";
                    m_exit_yn=0;
                    while (true) {
                        try {
                            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                            if (con == null) {
                                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
                            } else {
                                PreparedStatement ps = con.prepareStatement(query);
                                ps.executeUpdate();
                                m_exit_yn=1;
                            }
                        } catch (Exception e) {
                        }
                        if (m_exit_yn==1){
                            break;
                        }
                    }
                    finish();
                }
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(Cancel_Kot_Activity.this);
        alert.setView(alertLayout);
        dialog = alert.create();
        dialog.show();

    }

    public class load_spinner_kot_cancel extends AsyncTask<String, String, String> {
        List<Map<String, String>> sp_data = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

                } else {
                    //String query="select size_code,size_desc from sizemast";
                    String query = "select cncl_code,cncl_desc from cnclmast union select 0 as cncl_code,'' as cncl_desc order by cncl_desc";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));

                        sp_data.add(data);

                    }

                }  //z = "Success";


            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getApplicationContext(), sp_data, R.layout.spin, from, views);
            sp_kot_cancel.setAdapter(spnr_data);
            sp_kot_cancel.setSelection(0);
            sp_kot_cancel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, android.view.View view, int i, long l) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    m_cncl_desc = (String) obj.get("A");
                    m_cncl_code = (String) obj.get("B");

                    //   String text = sp_food_test.getSelectedItem().toString();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
        }
    }
}
