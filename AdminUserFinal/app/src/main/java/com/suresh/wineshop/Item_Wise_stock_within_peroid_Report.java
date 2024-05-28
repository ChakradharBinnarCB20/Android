package com.suresh.wineshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Item_Wise_stock_within_peroid_Report extends AppCompatActivity {
    String check_id="";
    String m_pricetype,cdate,Query_date,brnd_chk,chksummary,stock_wise_radioButton,stock_in_radioButton;
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,m_purdate,m_fdate,m_tdate;
    TextView txt_total,txt_sale_total;
    Double ottl=0.0,rttl=0.0,sttl=0.0,cttl=0.0;
    Double sale_total=0.0;
    String qry="",IMEINumber,systemDate,db;
    Connection con;
    PreparedStatement ps1;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    Toolbar toolbar;
    int m_TAB_CODE;
    TextView txt_total1,txt_total2,txt_total3,txt_total4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_wise_stock_within_peroid);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
       // lin_summary_hide=(LinearLayout)findViewById(R.id.lin_summary_hide);
       // lin_heading_hide=(LinearLayout)findViewById(R.id.lin_heading_hide);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        systemDate = df.format(c);

        Bundle bb = getIntent().getExtras();
        try {
            check_id = bb.getString("checklist");
            //str_price_type_title=bb.getString("str_price_type_title");
            stock_in_radioButton=bb.getString("stock_in_radioButton");//Bottles//Cases
            m_pricetype=bb.getString("m_pricetype");

            Query_date=bb.getString("Query_date");
            m_purdate=bb.getString("m_purdate");
            m_fdate=bb.getString("fdate");
            m_tdate=bb.getString("tdate");
            stock_wise_radioButton=bb.getString("stock_wise_radioButton");//Group//Trade//Brand
            brnd_chk=bb.getString("brnd_chk");

            Log.d("rrrr",stock_wise_radioButton);
            Log.d("rrrr",check_id);
            Log.d("rrrr",brnd_chk);

        } catch (Exception e) { }

        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText(""+str_compdesc+""+"\n"+"ItemWise Stock Within Period :  ("+stock_in_radioButton+")");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(m_fdate);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(m_tdate);
        toolbar_to_date.setTextColor(0xFFFFFFFF);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        pbbar = (ProgressBar)findViewById(R.id.pgb);
        SharedPreferences sp1 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
         m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getApplicationContext(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        txt_total1=(TextView)findViewById(R.id.txt_total1);
        txt_total2=(TextView)findViewById(R.id.txt_total2);
        txt_total3=(TextView)findViewById(R.id.txt_total3);
        txt_total4=(TextView)findViewById(R.id.txt_total4);


                         connectionClass = new Config();
                         try {
                             pbbar.setVisibility(View.VISIBLE);
                             con = connectionClass.CONN(con_ipaddress, portnumber,db);
                             if (con == null) {
                                 Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
                             }
                             else {
                                 try {
                                     if(stock_wise_radioButton.equals("Group")) {
                                         if (brnd_chk.equals("1")) {
                                             if (stock_in_radioButton.equals("Bottles")) {
                                                 //select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,op_stok,tot_purchase+dr_amt as purqty,tot_sale,cl_balance from tabreportparameters where ip_address = " + m_TAB_CODE + " and liqr_code = " & Val(Flxtax.TextMatrix(r_count, 0)) & " order by seq_no,brnd_desc
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,convert(varchar(10),amount_1) as op_stok,convert(varchar(10),amount_2+amount_3) as purqty,convert(varchar(10),tot_sale) as tot_sale,convert(varchar(10),amount) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "  and liqr_code in(" + check_id + ") order by brnd_desc,seq_no";

                                             } else {
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,gl_desc as op_stok,convert(varchar(10),doc_no) as purqty,convert(varchar(10),doc_type) as tot_sale,convert(varchar(10),crdr_cd) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + " and liqr_code in(" + check_id + ") order by brnd_desc, seq_no";

                                             }
                                         } else if (brnd_chk.equals("0")) {
                                             if (stock_in_radioButton.equals("Bottles")) {
                                                 //select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,op_stok,tot_purchase+dr_amt as purqty,tot_sale,cl_balance from tabreportparameters where ip_address = " + m_TAB_CODE + " and liqr_code = " & Val(Flxtax.TextMatrix(r_count, 0)) & " order by seq_no,brnd_desc
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,convert(varchar(10),amount_1) as op_stok,convert(varchar(10),amount_2+amount_3) as purqty,convert(varchar(10),tot_sale) as tot_sale,convert(varchar(10),amount) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "  and liqr_code in(" + check_id + ") order by seq_no,brnd_desc";

                                             } else {
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,gl_desc as op_stok,convert(varchar(10),doc_no) as purqty,convert(varchar(10),doc_type) as tot_sale,convert(varchar(10),crdr_cd) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + " and liqr_code in(" + check_id + ") order by seq_no,brnd_desc";

                                             }
                                         }
                                     }else if(stock_wise_radioButton.equals("Trade")){

                                         if (brnd_chk.equals("1")) {
                                             if (stock_in_radioButton.equals("Bottles")) {
                                                 //select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,op_stok,tot_purchase+dr_amt as purqty,tot_sale,cl_balance from tabreportparameters where ip_address = " + m_TAB_CODE + " and liqr_code = " & Val(Flxtax.TextMatrix(r_count, 0)) & " order by seq_no,brnd_desc
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,convert(varchar(10),amount_1) as op_stok,convert(varchar(10),amount_2+amount_3) as purqty,convert(varchar(10),tot_sale) as tot_sale,convert(varchar(10),amount) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "  and ac_head_id in(" + check_id + ") order by brnd_desc,seq_no";

                                             } else {
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,gl_desc as op_stok,convert(varchar(10),doc_no) as purqty,convert(varchar(10),doc_type) as tot_sale,convert(varchar(10),crdr_cd) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + " and ac_head_id in(" + check_id + ") order by brnd_desc, seq_no";

                                             }
                                         } else if (brnd_chk.equals("0")) {
                                             if (stock_in_radioButton.equals("Bottles")) {
                                                 //select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,op_stok,tot_purchase+dr_amt as purqty,tot_sale,cl_balance from tabreportparameters where ip_address = " + m_TAB_CODE + " and liqr_code = " & Val(Flxtax.TextMatrix(r_count, 0)) & " order by seq_no,brnd_desc
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,convert(varchar(10),amount_1) as op_stok,convert(varchar(10),amount_2+amount_3) as purqty,convert(varchar(10),tot_sale) as tot_sale,convert(varchar(10),amount) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "  and ac_head_id in(" + check_id + ") order by seq_no,brnd_desc";

                                             } else {
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,gl_desc as op_stok,convert(varchar(10),doc_no) as purqty,convert(varchar(10),doc_type) as tot_sale,convert(varchar(10),crdr_cd) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + " and ac_head_id in(" + check_id + ") order by seq_no,brnd_desc";

                                             }
                                         }
                                     }else{
                                         if (brnd_chk.equals("1")) {
                                             if (stock_in_radioButton.equals("Bottles")) {
                                                 //select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,op_stok,tot_purchase+dr_amt as purqty,tot_sale,cl_balance from tabreportparameters where ip_address = " + m_TAB_CODE + " and liqr_code = " & Val(Flxtax.TextMatrix(r_count, 0)) & " order by seq_no,brnd_desc
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,convert(varchar(10),amount_1) as op_stok,convert(varchar(10),amount_2+amount_3) as purqty,convert(varchar(10),tot_sale) as tot_sale,convert(varchar(10),amount) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "  and brnd_code in(" + check_id + ") order by brnd_desc,seq_no";

                                             } else {
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,gl_desc as op_stok,convert(varchar(10),doc_no) as purqty,convert(varchar(10),doc_type) as tot_sale,convert(varchar(10),crdr_cd) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + " and brnd_code in(" + check_id + ") order by brnd_desc, seq_no";

                                             }
                                         } else if (brnd_chk.equals("0")) {
                                             if (stock_in_radioButton.equals("Bottles")) {
                                                 //select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,op_stok,tot_purchase+dr_amt as purqty,tot_sale,cl_balance from tabreportparameters where ip_address = " + m_TAB_CODE + " and liqr_code = " & Val(Flxtax.TextMatrix(r_count, 0)) & " order by seq_no,brnd_desc
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,convert(varchar(10),amount_1) as op_stok,convert(varchar(10),amount_2+amount_3) as purqty,convert(varchar(10),tot_sale) as tot_sale,convert(varchar(10),amount) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "  and brnd_code in(" + check_id + ") order by seq_no,brnd_desc";

                                             } else {
                                                 qry = "select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,gl_desc as op_stok,convert(varchar(10),doc_no) as purqty,convert(varchar(10),doc_type) as tot_sale,convert(varchar(10),crdr_cd) as cl_balance from tabreportparameters where TAB_CODE =" + m_TAB_CODE + " and brnd_code in(" + check_id + ") order by seq_no,brnd_desc";

                                             }
                                         }
                                     }
//

                                   //  qry="select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,convert(varchar(10),amount_1) as op_stok,convert(varchar(10),amount_2+amount_3) as purqty,convert(varchar(10),tot_sale) as tot_sale,convert(varchar(10),amount) as cl_balance from tabreportparameters where TAB_CODE ="+m_TAB_CODE+"  and liqr_code in("+check_id+") order by seq_no,brnd_desc";
                                 } catch (Exception e) {
                                 }

                                 PreparedStatement ps = con.prepareStatement(qry);
                                 ResultSet rs = ps.executeQuery();
                                 ottl=0.0;rttl=0.0;sttl=0.0;cttl=0.0;
                                 HashMap<String, String> map2 = new HashMap<String, String>();
                                 while (rs.next()) {
                                     HashMap<String, String> map = new HashMap<String, String>();
                                     map.put("brnd_desc", rs.getString("brnd_desc"));
                                     map.put("size_desc", rs.getString("size_desc"));
                                     //map.put("tot_sale", rs.getString("tot_sale"));
                                     //NumberFormat nn =new DecimalFormat(".00");
                                     map.put("op_stok", rs.getString("op_stok"));
                                    // map.put("AMOUNT_1", rs.getString("amount_1"));
                                     map.put("purqty", rs.getString("purqty"));
                                     map.put("tot_sale", rs.getString("tot_sale"));
                                     map.put("cl_balance", rs.getString("cl_balance"));
                                     map.put("sqno", rs.getString("seq_no"));

                                     //if (rs.getString("seq_no").equals("2")) {
                                     ottl = ottl + Double.parseDouble(rs.getString("op_stok"));
                                     rttl = rttl + Double.parseDouble(rs.getString("purqty"));
                                     sttl = sttl + Double.parseDouble(rs.getString("tot_sale"));
                                     cttl = cttl + Double.parseDouble(rs.getString("cl_balance"));
                                     //}
                                     menu_card_arryList.add(map);
                                     NumberFormat n = new DecimalFormat(".0");
                                    txt_total1.setText("" + n.format(ottl));
                                    txt_total2.setText("" + n.format(rttl));
                                    txt_total3.setText("" + n.format(sttl));
                                    txt_total4.setText("" + n.format(cttl));

                                 }
                             }
                             pbbar.setVisibility(View.GONE);


                             if (attendance_recyclerAdapter != null) {
                                 attendance_recyclerAdapter.notifyDataSetChanged();
                                 System.out.println("Adapter " + attendance_recyclerAdapter.toString());
                             }
                             con.close();
                         } catch (Exception e) {
                             Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                         }
                       //--------------------------------------------
                    }

                    //Toast.makeText(getApplicationContext(), "check_id: " + check_id, Toast.LENGTH_SHORT).show();


    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
    }

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wise_stock__within_prdlist_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_item.setText(attendance_list.get(position).get("brnd_desc"));
            holder.list_size.setText(attendance_list.get(position).get("size_desc"));
            holder.list_sale.setText(attendance_list.get(position).get("op_stok"));
            holder.list_rate.setText(attendance_list.get(position).get("purqty"));
            holder.list_value.setText(attendance_list.get(position).get("tot_sale"));
            holder.list_cl.setText(attendance_list.get(position).get("cl_balance"));


           /* if(attendance_list.get(position).get("seq_no").equals("2"))
            {
                holder.list_item.setTextColor(Color.RED);
                holder.list_sale.setTextColor(Color.RED);
                holder.list_value.setTextColor(Color.RED);
            }
            else
            {
                holder.list_item.setTextColor(Color.BLACK);
                holder.list_sale.setTextColor(Color.BLACK);
                holder.list_value.setTextColor(Color.BLACK);
            }

            if(attendance_list.get(position).get("seq_no ").equals("0"))
            {
                holder.list_item.setTextColor(Color.BLUE);

            }
*/
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_item, list_size, list_sale, list_rate, list_value,list_cl;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_item = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_size = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_sale = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_rate = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_value = (TextView) itemView.findViewById(R.id.list_d5);
                this.list_cl = (TextView) itemView.findViewById(R.id.list_d6);
            }
        }
    }

}
