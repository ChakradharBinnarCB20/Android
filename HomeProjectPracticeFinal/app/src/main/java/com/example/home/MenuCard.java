package com.example.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuCard extends AppCompatActivity {

    IMEI_Activity m_com;
    IMEI_Activity connectionClass;
    //ConnectionClass connectionClass;
    Connection con;
    ProgressBar pbbar;
    LinearLayout lin_lst_odr, lin_fnl_entry_odr, lin_fnl_smry_odr, lin_print, lin_cancel_print, lin_tbl_swap,lin_liquor_stock,lin_other_stock;
    SearchView searchView;
    String SubCodeStr, m_Remark = "-";
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    Button btn_qty, btn_contact_operation_cancle, btn_update;
    AlertDialog dialog;
    TextView item_name, txt_tbl_bill, update_item_name;
    EditText edt_order, edt_update_order, edt_update_rate, edt_update_value;
    //  final com.example.home.Class.m_com m_com = new m_com(getApplicationContext());
    Spinner sp_food_test, sp_liqour_two, sp_cmb_size, sp_cmb_brand,sp_group_filter;
    int qty = 0;
    int qty1 = 0;
    float item_value = 0;
    float rate = 0;
    String m_testcode = "";
    float m_WATRCODE = 0;
    int m_BRANDCLUBYN = 0;
    int m_SWAP_TABLE_YN =0;
    int m_CANCEL_KOT_YN =0;
    int print_kot_yn=0;
    String q,m_group_name,m_group_code;
    String DATA, NM, SIZE, query, str_food_test_text, str_size, str_size_text, item_code, str_item_type, str_liqour_two, menu_item_code, str_item, str_menu_type;
    Toolbar toolbar;
    float m_mrp;
    int size, ICode, item_type;
    int m_mrprateyn, m_saletype, m_clubbrandcode;
    int m_clbalance, m_clbalance_b,m_swap;
    int cmb_list_cnt, cmb_frm_size_cnt;
    LinearLayout lin_grid_visible, lin_sp_hide, lin_sp_hide_l_s, lin_sp_brand;
    TransparentProgressDialog pd;
    String str_compcode,str_compdesc, str_loctcode, str_maintainstockyn, str_gstreverseyn, m_orderbyseq, m_fromsizecode, m_fromitemcode, m_LOOSEFROMSIZEALLYN, str_brand, str_brand_name;
    Float m_compcode, m_loctcode, m_maintainstockyn, m_gstreverseyn;
    int cmb_size, cmb_food_test_size, m_liqrcode, m_brndcode, cmb_bsize;
    ArrayList<String> food_test_arryList;
    ArrayList<String> liquor_one_arryList;
    ArrayList<String> liquor_two_arryList;
    String TBNO_DESC, m_ratetype, STR_SERVICE_TAX_PER, STR_CGST_PER, STR_SGST_PER;
    float TBNO_CODE;
    Double SERVICE_TAX_PER, CGST_PER, SGST_PER;
    String sp_size_qry = "";
    String sp_brand_qry = "";
    List<Map<String, String>> bsize_data;
    List<Map<String, String>> size_data;
    String str_waiter, doc_dt, doc_dt_display, m_ipaddress;
    int rowCount = 0;
    int uptd_val, uptd_qty, uptd_rt;
    String str_uptd_val, str_uptd_qty, str_uptd_rt;
    HashMap<String, String> map;
    int m_index = 0;
    int n_index = 0;
    int m_no_of_person = 0;
    float doc_no = 0;
    float m_sealedddamt = 0;
    int m_EVERY_ENTRY_KOT_YN=0;
    String con_ipaddress, portnumber;
    String m_lastdocsrno = "";
    String m_liqrsearch="";
    String m_foodsearch="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_card);
        m_com = new IMEI_Activity();
        pd = new TransparentProgressDialog(MenuCard.this, R.drawable.busy);
        SharedPreferences sp11 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp11.getString("ipaddress", "");
        portnumber = sp11.getString("portnumber", "");
        //Today's date
        try {
            doc_dt = m_com.M_get_string("select isnull(convert(varchar(10),max(doc_dt),101),convert(varchar(10),getdate(),101)) from countersaleitem", con_ipaddress, portnumber);
            doc_dt_display = m_com.M_get_string("select isnull(convert(varchar(10),max(doc_dt),103),convert(varchar(10),getdate(),103)) from countersaleitem", con_ipaddress, portnumber);
//            if (doc_dt.length() > 0) {
//
//            } else {
//
//            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
        }

        try {
            connectionClass = new IMEI_Activity();
            Connection con = connectionClass.CONN(con_ipaddress, portnumber);

            if (con == null) {
                Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

            } else {
                //String query="select size_code,size_desc from sizemast";

                String query = "select convert(int,mrp_yn) as mrp_yn from weekday where week_day = upper(DATENAME(dw,'" + doc_dt + "')) ";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    m_mrprateyn = rs.getInt("mrp_yn");
                }

            }


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
        }

        food_test_arryList = new ArrayList<String>();
        liquor_one_arryList = new ArrayList<String>();
        liquor_two_arryList = new ArrayList<String>();
        lin_grid_visible = (LinearLayout) findViewById(R.id.lin_grid_visible);

        SharedPreferences i = getSharedPreferences("IPADDRESS", MODE_PRIVATE);
        m_ipaddress = i.getString("IPADDRESS", "");
        Log.d("IPADDRESS", m_ipaddress);

        SharedPreferences s = getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        str_waiter = s.getString("tab_user_name", "");

        SharedPreferences ss = getSharedPreferences("COMP_CODE", MODE_PRIVATE);
        str_compcode = ss.getString("COMP_CODE", "");
        str_compdesc = ss.getString("COMP_DESC", "");
        m_compcode = Float.parseFloat(str_compcode);
        Log.d("pppp", str_compcode);
        Log.d("pppp", str_compdesc);

        //----------------------------------------------------------------------------------
        SharedPreferences sp2 = getSharedPreferences("Profile_data", MODE_PRIVATE);
        print_kot_yn = sp2.getInt("print_kot_yn", 0);
        str_loctcode = sp2.getString("m_loctcode", "");
        m_loctcode = Float.parseFloat(str_loctcode);
        Log.d("pppp", str_loctcode);

        str_maintainstockyn = sp2.getString("m_maintainstockyn", "");
        m_maintainstockyn = Float.parseFloat(str_maintainstockyn);
        Log.d("pppp", str_maintainstockyn);

        str_gstreverseyn = sp2.getString("m_gstreverseyn", "");
        m_gstreverseyn = Float.parseFloat(str_gstreverseyn);
        Log.d("pppp", str_gstreverseyn);

        m_orderbyseq = sp2.getString("m_orderbyseq", "");
        Log.d("pppp", m_orderbyseq);

        m_EVERY_ENTRY_KOT_YN = sp2.getInt("m_EVERY_ENTRY_KOT_YN", 0);
        Log.d("pppp", "m_EVERY_ENTRY_KOT_YN "+m_EVERY_ENTRY_KOT_YN);

        m_LOOSEFROMSIZEALLYN = sp2.getString("m_LOOSEFROMSIZEALLYN", "");
        Log.d("pppp", m_LOOSEFROMSIZEALLYN);
        SharedPreferences snp = getSharedPreferences("SPIN_DATA", MODE_PRIVATE);
        // TBNO_CODE = Float.parseFloat(snp.getString("m_waiter_code", ""));
         m_WATRCODE = Float.parseFloat(snp.getString("m_waiter_code", ""));
         Log.d("wwww",""+m_WATRCODE);

        SharedPreferences sp = getSharedPreferences("HOME_DATA", MODE_PRIVATE);
        TBNO_CODE = Float.parseFloat(sp.getString("TBNO_CODE", ""));
        TBNO_DESC = sp.getString("TBNO_DESC", "");
        m_ratetype = sp.getString("RATETYPE_DESC", "");
        STR_SERVICE_TAX_PER = sp.getString("SERVICE_TAX_PER", "");
       // m_WATRCODE = sp.getInt("m_WATRCODE", 0);
        m_BRANDCLUBYN = sp.getInt("m_BRANDCLUBYN", 0);
        m_swap = sp.getInt("m_swap", 0);
        m_SWAP_TABLE_YN = sp.getInt(" m_SWAP_TABLE_YN", 0);
        m_CANCEL_KOT_YN = sp.getInt(" m_CANCEL_KOT_YN", 0);
        Log.d(" m_CANCEL_KOT_YN",""+ m_CANCEL_KOT_YN);
        Log.d("m_SWAP_TABLE_YN",""+m_SWAP_TABLE_YN);

        SERVICE_TAX_PER = Double.parseDouble(STR_SERVICE_TAX_PER);
        STR_CGST_PER = sp.getString("CGST_PER", "");
        CGST_PER = Double.parseDouble(STR_CGST_PER);
        STR_SGST_PER = sp.getString("SGST_PER", "");
        SGST_PER = Double.parseDouble(STR_SGST_PER);

        try {
            m_lastdocsrno = m_com.M_get_string("select isnull(ltrim(rtrim(str(max(doc_srno)))),'0') from countersaleitem where FROM_TAB_YN = 1 and tbno_code = " + TBNO_CODE + " ", con_ipaddress, portnumber);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
        }

        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TextView tbl_bill = (TextView) toolbar.findViewById(R.id.txt_total);//title
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);//date
        ImageView option = (ImageView) toolbar.findViewById(R.id.option);//date
        TextView txt_hotel_name = (TextView) toolbar.findViewById(R.id.txt_hotel_name);//date
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Table : " + TBNO_DESC);
        toolbar_date.setText("Date : " + doc_dt_display);
        txt_hotel_name.setText(str_compdesc);
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_date.setTextColor(0xFFFFFFFF);
        txt_hotel_name.setTextColor(0xFFFFFFFF);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu_option_popup();

            }

        });


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        searchView = (SearchView) findViewById(R.id.report_searchView);
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_exp_list);
        layoutManager_pe = new LinearLayoutManager(MenuCard.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(MenuCard.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);

        //------------------------------------------------------------------------------------------

        sp_group_filter=(Spinner)findViewById(R.id.sp_group_filter);
        new load_spinner_group().execute();

        menu_search("");
        //------------------------------------------------------------------------------------------
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 0) {
                    lin_grid_visible.setVisibility(View.VISIBLE);
                    SubCodeStr = newText;
                    SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();
                    Log.d("ssss", SubCodeStr);

                    //new FetchSearchResult().execute();
                    menu_search(SubCodeStr);
                } else if (TextUtils.isEmpty(newText)) {
                    menu_search("");
                } else {
                    menu_search("");
                }
                return false;
            }
        });
    }
    public void menu_search(String SubCodeStr) {
        connectionClass = new IMEI_Activity();
        try {
            pbbar.setVisibility(View.VISIBLE);
            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

            } else {
                if (m_maintainstockyn == 1.0) {
                    if (m_gstreverseyn == 0.0) {
                        query = "select brnd_desc,size_desc,liqr_desc as menu_Type,item_code,0 as MENUITEMCODE,ltrim(str(" + m_ratetype + " ,6,0)), ltrim(str(MRP,12,2)),selection_item_code, 1 as itemtype,1 as qty,seq_no from brndmast,sizemast,Itemmast,liqrmast where "+m_liqrsearch+" sizemast.size_code=itemmast.size_code and brndmast.brnd_code=itemmast.brnd_code and liqrmast.liqr_code=itemmast.liqr_code and sizemast.size_code > 0 and itemmast.live_yn = 1 and brndmast.brnd_code > 0 and full_bottle=1 and loose_bottle=0 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and convert(int,(cl_balance)/convert(float(9),left(replace(size_desc,space(01),space(10)),4))) >= 1) and (brnd_desc like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "') "
                                + " union select brnd_desc,size_desc,liqr_desc as menu_Type,item_code,0 as MENUITEMCODE,ltrim(str(" + m_ratetype + ",6,0)),ltrim(str(MRP,12,2)),selection_item_code,2 as itemtype,1 as qty,seq_no from brndmast,sizemast,Itemmast,liqrmast where "+m_liqrsearch+" sizemast.size_code=itemmast.size_code and brndmast.brnd_code=itemmast.brnd_code and liqrmast.liqr_code=itemmast.liqr_code and sizemast.size_code > 0 and full_bottle=0 and loose_bottle=1 and itemmast.live_yn = 1 and brndmast.brnd_code > 0 and ltrim(str(itemmast.liqr_code))+'-'+ltrim(str(itemmast.brnd_code)) in(select ltrim(str(liqr_code))+'-'+ltrim(str(brnd_code)) from itemmast where loose_peg_yn = 1 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and convert(int,(cl_balance)/convert(float(9),left(replace(size_desc,space(01),space(10)),4)))>0)) "
                                + " and (brnd_desc like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "' or item_code LIKE '" + SubCodeStr + "%') "
                                + " union select brnd_desc,size_desc,liqr_desc as menu_Type,item_code,0 as MENUITEMCODE,ltrim(str(" + m_ratetype + ",6,0)),ltrim(str(MRP,12,2)),selection_item_code,0 as itemtype,1 as qty,seq_no from brndmast,sizemast,Itemmast,liqrmast where "+m_liqrsearch+" sizemast.size_code=itemmast.size_code and brndmast.brnd_code=itemmast.brnd_code and liqrmast.liqr_code=itemmast.liqr_code and sizemast.size_code > 0 and itemmast.live_yn = 1 and brndmast.brnd_code > 0 and loose_bottle=1 and full_bottle=1 and "
                                + " ltrim(str(itemmast.liqr_code))+'-'+ltrim(str(itemmast.brnd_code)) in(select ltrim(str(liqr_code))+'-'+ltrim(str(brnd_code)) from itemmast where item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and convert(int,(cl_balance)/convert(float(9),left(replace(size_desc,space(01),space(10)),4)))>0)) and (brnd_desc like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "' or item_code LIKE '" + SubCodeStr + "%') "
                                + " union select MENUITEM_DESC as brnd_desc,'',menu_desc as menu_Type,LTRIM(STR(MENUITEM_CODE)),MENUITEM_CODE ,CASE WHEN " + m_ratetype + " -FLOOR(" + m_ratetype + ") = 0 THEN LTRIM(STR(" + m_ratetype + ")) ELSE LTRIM(STR(" + m_ratetype + ",6,2)) END as nonac_price,'',selection_item_code,3 as itemtype,1 as qty,99999 as seq_no from menucarditemmast,menumast where "+m_foodsearch+" menumast.menu_code=menucarditemmast.MENU_code and MENUITEM_CODE > 0 and (MENUITEM_DESC like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "') and maintain_stock=0 "
                                + " union select MENUITEM_DESC as brnd_desc,'',menu_desc as menu_Type,LTRIM(STR(MENUITEM_CODE)),MENUITEM_CODE ,CASE WHEN " + m_ratetype + " -FLOOR(" + m_ratetype + ") = 0 THEN LTRIM(STR(" + m_ratetype + ")) ELSE LTRIM(STR(" + m_ratetype + ",6,2)) END as nonac_price,'',selection_item_code,3 as itemtype,1 as qty,99999 as seq_no from menucarditemmast,menumast where "+m_foodsearch+" menumast.menu_code=menucarditemmast.MENU_code and MENUITEM_CODE > 0 and (MENUITEM_DESC like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "') and maintain_stock=1 and menuitem_code in(select item_code from onlnstok where item_type = 2 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and cl_balance > 0)order by " + m_orderbyseq + "";
                        Log.d("query......", query);
                        longLog(query);

                    } else {
                        query = "select brnd_desc,size_desc,liqr_desc as menu_Type,item_code,0 as MENUITEMCODE,ltrim(str(" + m_ratetype + " ,6,0)), ltrim(str(MRP,12,2)),selection_item_code, 1 as itemtype,1 as qty,seq_no from brndmast,sizemast,Itemmast,liqrmast where "+m_liqrsearch+" sizemast.size_code=itemmast.size_code and brndmast.brnd_code=itemmast.brnd_code and liqrmast.liqr_code=itemmast.liqr_code and sizemast.size_code > 0 and itemmast.live_yn = 1 and brndmast.brnd_code > 0 and full_bottle=1 and loose_bottle=0 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and convert(int,(cl_balance)/convert(float(9),left(replace(size_desc,space(01),space(10)),4))) >= 1) and (brnd_desc like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "')"
                                + "  union select brnd_desc,size_desc,liqr_desc as menu_Type,item_code,0 as MENUITEMCODE,ltrim(str(" + m_ratetype + ",6,0)),ltrim(str(MRP,12,2)),selection_item_code,2 as itemtype,1 as qty,seq_no from brndmast,sizemast,Itemmast,liqrmast where "+m_liqrsearch+" sizemast.size_code=itemmast.size_code and brndmast.brnd_code=itemmast.brnd_code and liqrmast.liqr_code=itemmast.liqr_code and sizemast.size_code > 0 and full_bottle=0 and loose_bottle=1 and itemmast.live_yn = 1 and brndmast.brnd_code > 0 and ltrim(str(itemmast.liqr_code))+'-'+ltrim(str(itemmast.brnd_code)) in(select ltrim(str(liqr_code))+'-'+ltrim(str(brnd_code)) from itemmast where loose_peg_yn = 1 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and convert(int,(cl_balance)/convert(float(9),left(replace(size_desc,space(01),space(10)),4)))>0))"
                                + "  and (brnd_desc like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "' or item_code LIKE '" + SubCodeStr + "')"
                                + "  union select brnd_desc,size_desc,liqr_desc as menu_Type,item_code,0 as MENUITEMCODE,ltrim(str(" + m_ratetype + ",6,0)),ltrim(str(MRP,12,2)),selection_item_code,0 as itemtype,1 as qty,seq_no from brndmast,sizemast,Itemmast,liqrmast where "+m_liqrsearch+" sizemast.size_code=itemmast.size_code and brndmast.brnd_code=itemmast.brnd_code and liqrmast.liqr_code=itemmast.liqr_code and sizemast.size_code > 0 and itemmast.live_yn = 1 and brndmast.brnd_code > 0 and loose_bottle=1 and full_bottle=1 and "
                                + " ltrim(str(itemmast.liqr_code))+'-'+ltrim(str(itemmast.brnd_code)) in(select ltrim(str(liqr_code))+'-'+ltrim(str(brnd_code)) from itemmast where item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and convert(int,(cl_balance)/convert(float(9),left(replace(size_desc,space(01),space(10)),4)))>0)) and (brnd_desc like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "' or item_code LIKE '" + SubCodeStr + "%')"
                                + " union select MENUITEM_DESC as brnd_desc,'',menu_desc as menu_Type,LTRIM(STR(MENUITEM_CODE)),MENUITEM_CODE ,ltrim(str(round(" + m_ratetype + "/(100+" + CGST_PER + " + " + SGST_PER + ")*100,2),6,2)) as nonac_price,'',selection_item_code,3 as itemtype,1 as qty,99999 as seq_no from menucarditemmast,menumast where "+m_foodsearch+" menumast.menu_code=menucarditemmast.MENU_code and MENUITEM_CODE > 0 and (MENUITEM_DESC like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "') and maintain_stock=0 "
                                + " union select MENUITEM_DESC as brnd_desc,'',menu_desc as menu_Type,LTRIM(STR(MENUITEM_CODE)),MENUITEM_CODE ,ltrim(str(round(" + m_ratetype + "/(100+" + CGST_PER + " + " + SGST_PER + ")*100,2),6,2)) as nonac_price,'',selection_item_code,3 as itemtype,1 as qty,99999 as seq_no from menucarditemmast,menumast where "+m_foodsearch+" menumast.menu_code=menucarditemmast.MENU_code and MENUITEM_CODE > 0 and (MENUITEM_DESC like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "') and maintain_stock=1 and menuitem_code in(select item_code from onlnstok where item_type = 2 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and cl_balance > 0) order by " + m_orderbyseq + "";
                        Log.d("query......", query);
                        longLog(query);
                    }
                } else {
                    if (m_gstreverseyn == 0.0) {
                        query = "select brnd_desc,size_desc,liqr_desc as menu_Type,item_code,0 as MENUITEMCODE,CASE WHEN (" + m_ratetype + ")-FLOOR(" + m_ratetype + ") = 0 THEN LTRIM(STR(" + m_ratetype + ")) ELSE LTRIM(STR(" + m_ratetype + ",6,2)) END as nonac_price, ltrim(str(MRP,12,2)),selection_item_code, 1 as itemtype,1 as qty,seq_no,1,1,'',1 from brndmast,sizemast,Itemmast,liqrmast where "+m_liqrsearch+" sizemast.size_code=itemmast.size_code and brndmast.brnd_code=itemmast.brnd_code and liqrmast.liqr_code=itemmast.liqr_code and sizemast.size_code > 0 and itemmast.live_yn = 1 and brndmast.brnd_code > 0 and (brnd_desc like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "')"
                                + " union select MENUITEM_DESC as brnd_desc,'',menu_desc as menu_Type,LTRIM(STR(MENUITEM_CODE)),MENUITEM_CODE ,CASE WHEN (" + m_ratetype + ")-FLOOR(" + m_ratetype + ") = 0 THEN LTRIM(STR(" + m_ratetype + ")) ELSE LTRIM(STR(" + m_ratetype + ",6,2)) END as nonac_price,'',selection_item_code,3 as itemtype,1 as qty,99999 as seq_no,bargroup_yn,maintain_stock,'',print_kot_yn from menucarditemmast,menumast where "+m_foodsearch+" menumast.menu_code=menucarditemmast.MENU_code and MENUITEM_CODE > 0 and (MENUITEM_DESC like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "')order by " + m_orderbyseq + "";
                    } else {
                        query = " select brnd_desc,size_desc,liqr_desc as menu_Type,item_code,0 as MENUITEMCODE,ltrim(str(round(" + m_ratetype + "/(100+" + CGST_PER + "+" + SGST_PER + ")*100,2),6,2)) as nonac_price, ltrim(str(MRP,12,2)),selection_item_code, 1 as itemtype,1 as qty,seq_no from brndmast,sizemast,Itemmast,liqrmast where "+m_liqrsearch+" sizemast.size_code=itemmast.size_code and brndmast.brnd_code=itemmast.brnd_code and liqrmast.liqr_code=itemmast.liqr_code and sizemast.size_code > 0 and itemmast.live_yn = 1 and brndmast.brnd_code > 0 and (brnd_desc like ' " + SubCodeStr + "%' or short_name = '" + SubCodeStr + "' )"
                                + " union select MENUITEM_DESC as brnd_desc,'',menu_desc as menu_Type,LTRIM(STR(MENUITEM_CODE)),MENUITEM_CODE ,ltrim(str(round(" + m_ratetype + "/(100+" + CGST_PER + " +" + SGST_PER + ")*100,2),6,2)) as nonac_price,'',selection_item_code,3 as itemtype,1 as qty,99999 as seq_no from menucarditemmast,menumast where "+m_foodsearch+" menumast.menu_code=menucarditemmast.MENU_code and MENUITEM_CODE > 0 and (MENUITEM_DESC like '" + SubCodeStr + "%' or short_name = '" + SubCodeStr + "')order by " + m_orderbyseq + "";
                    }

                }

                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                while (rs.next()) {
                    //item,size,menu_type,item_code,menu_item_code,no_column_nme_1,no_column_nme_2,selection_item_code,item_type,qty,seq_no,no_column_nme_3,no_column_nme_4,no_column_nme_5,no_column_nme_6
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("item", rs.getString(1));
                    map.put("size", rs.getString(2));
                    map.put("menu_type", rs.getString(3));
                    map.put("item_code", rs.getString(4));
                    map.put("menu_item_code", rs.getString(5));
                    map.put("no_column_nme_1", rs.getString(6));
                    map.put("no_column_nme_2", rs.getString(7));
                    map.put("selection_item_code", rs.getString(8));
                    map.put("item_type", rs.getString(9));
                    map.put("qty", rs.getString(10));
                    menu_card_arryList.add(map);
                }
            }
            pbbar.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
        }
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rep_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.list_item.setText(attendance_list.get(position).get("item"));
            holder.list_size.setText(attendance_list.get(position).get("size"));
            holder.list_menu_type.setText(attendance_list.get(position).get("menu_type"));
            holder.list_rate.setText(attendance_list.get(position).get("no_column_nme_1"));
            holder.list_item_type.setText(attendance_list.get(position).get("item_type"));

            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Initilization
                    m_fromitemcode = "";
                    m_fromsizecode = "";
                    m_liqrcode = 0;
                    m_brndcode = 0;
                    m_mrp = 0;
                    m_sealedddamt = 0;
                    m_clubbrandcode = 0;
                    cmb_bsize = 0;
                    cmb_size =0;
                    m_testcode = "";
                    ICode = Integer.parseInt((attendance_list.get(position).get("item_type")));
                    rate = Float.parseFloat((attendance_list.get(position).get("no_column_nme_1")));//7th column
                    if (ICode != 3) {
                        m_mrp = Float.parseFloat((attendance_list.get(position).get("no_column_nme_2")));
                    }//7th column
                    if (ICode != 3 && m_mrprateyn == 1 && m_mrp != 0) {
                        rate = m_mrp;
                    }
                    menu_item_code = (attendance_list.get(position).get("menu_item_code"));//5th column
                    item_code = (attendance_list.get(position).get("item_code"));//4th column
                    str_menu_type = (attendance_list.get(position).get("menu_type"));//3rd column
                    str_item = (attendance_list.get(position).get("item"));//1st column
                    str_size = (attendance_list.get(position).get("size"));//2nd column
                    if (str_size.equals("")) {

                    } else {
                        Log.d("mmm", "Actual String " + str_size);
                        if(str_size.contains("TETRA PACK"))
                        {
                            String[] split = str_size.split("TETRA PACK");
                            String firstSubString = split[0];
                            Log.d("mmm", "After Split String " + firstSubString);
                            String a = firstSubString.replaceAll("\\s", "");
                            size = Integer.parseInt(a);
                            Log.d("mmm", "No Space String " + firstSubString);
                            Log.d("mmm", "size" + size);
                        }else {
                            String[] split = str_size.split("ML");
                            String firstSubString = split[0];
                            Log.d("mmm", "After Split String " + firstSubString);
                            String a = firstSubString.replaceAll("\\s", "");
                            size = Integer.parseInt(a);
                            Log.d("mmm", "No Space String " + firstSubString);
                            Log.d("mmm", "size" + size);
                        }
                    }
                    if (ICode == 0) {
                        try {
                            String r = m_com.M_get_string("select count(*) from onlnstok where item_code like '" + item_code + "' and item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and (cl_balance)>= " + size + "", con_ipaddress, portnumber);
                            Log.d("rrrrr", r);
                            if (r.equals("0")) {
                                ICode = 2;
                            } else {
                                try {
                                    connectionClass = new IMEI_Activity();
                                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                                    if (con == null) {
                                        Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();
                                    } else {

                                        String query = "select liqr_code,brnd_code from itemmast where item_code like " + item_code + "";
                                        PreparedStatement ps = con.prepareStatement(query);
                                        ResultSet rs = ps.executeQuery();
                                        //ArrayList data1 = new ArrayList();
                                        while (rs.next()) {
                                            m_liqrcode = rs.getInt("liqr_code");
                                            m_brndcode = rs.getInt("brnd_code");
                                            Log.d("m_liqrcode", "" + m_liqrcode);
                                            Log.d("m_brndcode", "" + m_brndcode);
                                        }
                                        query = "select size_code,size_desc from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) > " + size + " and size_code in (select size_code from itemmast where LOOSE_PEG_YN = 1 and liqr_code = " + m_liqrcode + " and brnd_code = " + m_brndcode + " and full_bottle=1 and live_yn = 1 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and (cl_balance) >=" + size + ")) order by seq_no";
                                        PreparedStatement ss = con.prepareStatement(query);
                                        ResultSet SS = ss.executeQuery();
                                        m_index = 0;
                                        while (SS.next()) {
                                            m_index++;
                                        }
                                        if (m_index == 0) {
                                            ICode = 1;
                                        }
                                    }

                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error..579" + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                        //FillCombo CmbFromSize
                        sp_size_qry = "select size_code,size_desc from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) > " + size + " and size_code in (select size_code from itemmast where LOOSE_PEG_YN = 1 and liqr_code = " + m_liqrcode + " and brnd_code = " + m_brndcode + " and full_bottle=1 and live_yn = 1 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and (cl_balance) >=" + size + ")) order by seq_no";

                    }
                    //-------------------------------------------------------------------------
                    if (ICode == 2) {
                        try {
                            connectionClass = new IMEI_Activity();
                            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                            if (con == null) {
                                Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                            } else {

                                String query = "select convert(int,liqr_code) as liqr_code,convert(int,brnd_code) as brnd_code from itemmast where item_code like " + item_code + "";
                                PreparedStatement ps = con.prepareStatement(query);
                                ResultSet rs = ps.executeQuery();

                                //ArrayList data1 = new ArrayList();
                                while (rs.next()) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    m_liqrcode = rs.getInt("liqr_code");
                                    // str_brndcode= rs.getString("brnd_code");
                                    m_brndcode = rs.getInt("brnd_code");

                                }
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                        }
                        sp_brand_qry = "select convert(int,brnd_code) as brnd_code,brnd_desc from brndmast where brnd_code=" + m_brndcode + " or brnd_code in(select club_brnd_code from brandclub where brnd_code=" + m_brndcode + ") and brnd_code in(select brnd_code from itemmast where live_yn=1 and size_code in(select size_code from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) = " + size + ")) order by brnd_desc";

                        //========================================================================
                        try {
                            connectionClass = new IMEI_Activity();
                            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                            if (con == null) {
                                Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                            } else {

                                //String query = "select liqr_code,brnd_code from itemmast where item_code like " item_code ";
                                String query = "select convert(int,liqr_code) as liqr_code,convert(int,brnd_code) as brnd_code from itemmast where item_code like " + item_code + "";
                                PreparedStatement ps = con.prepareStatement(query);
                                ResultSet rs = ps.executeQuery();

                                //ArrayList data1 = new ArrayList();
                                while (rs.next()) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    m_liqrcode = rs.getInt("liqr_code");
                                    // str_brndcode= rs.getString("brnd_code");
                                    m_liqrcode = rs.getInt("liqr_code");

                                }
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                        }
                        sp_size_qry = "select size_code,size_desc from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) > " + size + " and size_code in (select size_code from itemmast where LOOSE_PEG_YN = 1 and liqr_code = " + m_liqrcode + " and brnd_code = " + m_brndcode + " and full_bottle=1 and live_yn = 1 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and (cl_balance) >=" + size + ")) order by seq_no";

                    }

                    SIZE = attendance_list.get(position).get("size");
                    if (ICode == 3) {
                        NM = attendance_list.get(position).get("item");
                    } else {
                        NM = attendance_list.get(position).get("item") + ", " + SIZE;
                    }

                    DATA = attendance_list.get(position).get("no_column_nme_1");


                    str_item_type = String.valueOf(item_type);

                    if (ICode == 3) {
                        food_popup_form(); //food taste popup//
                    } else if (ICode == 2 || ICode == 0) {
                        new load_spinner_cmbsize().execute();
                        new load_spinner_cmbbrand().execute();
                        loose_liquor_popup_form();
                    } else if (ICode == 1) {

                        non_food_popup_form();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_item, list_size, list_menu_type, list_rate, list_item_type;
            TableLayout layout_gird_position;
            ImageView img_1, img_2, img_3;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_item = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_size = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_menu_type = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_rate = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_item_type = (TextView) itemView.findViewById(R.id.list_d5);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);

            }
        }
    }
    public void non_food_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.non_food_popup_form, null);
        item_name = (TextView) alertLayout.findViewById(R.id.item_name);
        item_name.setText(NM);
        edt_order = (EditText) alertLayout.findViewById(R.id.edt_order);
        edt_order.setText("1");

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

                if (edt_order.getText().toString().equals("0") || edt_order.getText().toString().equals("")) {
                    edt_order.setError("Quantity Should Not Be Zero");
                    edt_order.requestFocus();
                    return;
                } else {
                    String query = "CREATE TABLE LIQRSTOCK(DOC_NO FLOAT DEFAULT 0)";
                    int m_exit_yn = 0;
                    int m_force_exit_yn = 0;
                    while (true) {
                        m_force_exit_yn++;
                        try {
                            connectionClass = new IMEI_Activity();
                            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                            if (con == null) {
                                Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();
                            } else {
                                PreparedStatement ps = con.prepareStatement(query);
                                ps.executeUpdate();
                                m_exit_yn = 1;
                            }  //z = "Success";

                        } catch (Exception e) {
                        }
                        if (m_exit_yn == 1||m_force_exit_yn>50) {
                            break;
                        }
                    }
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run()
                        {
                           final_save_data();
                            pd.dismiss();
                        }
                    }, 1000);

                }
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(MenuCard.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }
    public void food_popup_form() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.food_popup_form, null);
        item_name = (TextView) alertLayout.findViewById(R.id.item_name);
        item_name.setText(NM);
        edt_order = (EditText) alertLayout.findViewById(R.id.edt_order);
        edt_order.setText("1");
        sp_food_test = (Spinner) alertLayout.findViewById(R.id.sp_food_test);
        // sp_size=(Spinner) alertLayout.findViewById(R.id.sp_size);
        new load_spinner_cmbtest().execute();
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
                if (edt_order.getText().toString().equals("0") || edt_order.getText().toString().equals("")) {
                    edt_order.setError("Quantity Should Not Be Zero");
                    edt_order.requestFocus();
                    return;
                } else {

                    int m_exit_yn = 0;
                    int m_force_exit_yn = 0;
                    String query = "CREATE TABLE FOODSTOCK(DOC_NO FLOAT DEFAULT 0)";
                    while (true) {
                    m_force_exit_yn++;
                        try {
                            connectionClass = new IMEI_Activity();
                            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                            if (con == null) {
                                Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();
                            } else {
                                PreparedStatement ps = con.prepareStatement(query);
                                ps.executeUpdate();
                                m_exit_yn = 1;
                            }  //z = "Success";

                        } catch (Exception e) {
                        }
                        if (m_exit_yn == 1||m_force_exit_yn>50) {
                            break;
                        }
                    }

                    stock_check();
                }
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(MenuCard.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }
    public void loose_liquor_popup_form() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.loose_liquor_popup_form, null);
        lin_sp_hide_l_s = (LinearLayout) alertLayout.findViewById(R.id.lin_sp_hide_l_s);
        lin_sp_hide = (LinearLayout) alertLayout.findViewById(R.id.lin_sp_hide);
        lin_sp_brand = (LinearLayout) alertLayout.findViewById(R.id.lin_sp_brand);
        item_name = (TextView) alertLayout.findViewById(R.id.item_name);
        item_name.setText(NM);
        edt_order = (EditText) alertLayout.findViewById(R.id.edt_order);
        edt_order.setText("1");
        sp_cmb_brand = (Spinner) alertLayout.findViewById(R.id.sp_cmb_brand);
        sp_cmb_size = (Spinner) alertLayout.findViewById(R.id.sp_cmb_size);
        sp_liqour_two = (Spinner) alertLayout.findViewById(R.id.sp_liqour_two);
        //=====================liqour one spinner data---------------------------

        //-----------------------------------------------------------------------
        // qty=edt_order.getText().toString();
        if (ICode == 2) {
            size_data = new ArrayList<Map<String, String>>();
            try {
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                } else {
                   // sp_size_qry = "select size_code,size_desc from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) > " + size + " and size_code in (select size_code from itemmast where LOOSE_PEG_YN = 1 and liqr_code = " + m_liqrcode + " and brnd_code = " + m_brndcode + " and full_bottle=1 and live_yn = 1 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and (cl_balance) >=" + size + ")) order by seq_no";
                    String q = sp_size_qry;
                    System.out.println("......" + q);
                    //String q="select size_code,size_desc from sizemast";
                    // String query = "select test_code,test_desc from testmast union select 0,'' order by test_desc";
                    PreparedStatement ps = con.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();

                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));

                        size_data.add(data);
                        cmb_size = size_data.size();
                        System.out.println("....." + cmb_size);
                        Log.d("cmb_frm_size_cnt", "" + cmb_frm_size_cnt);
                    }
                }  //z = "Success";

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
            if (cmb_size > 0) {

                if (cmb_size == 1 || (cmb_size > 1 && m_LOOSEFROMSIZEALLYN.equals("0.0"))) {
                    lin_sp_hide.setVisibility(View.VISIBLE);
                    lin_sp_brand.setVisibility(View.INVISIBLE);
                    sp_cmb_size.setEnabled(false);
                }
                lin_sp_hide.setVisibility(View.VISIBLE);
                //sp_brand_qry = "select brnd_code,brnd_desc from brndmast where brnd_code=" + m_brndcode + " or brnd_code in(select club_brnd_code from brandclub where brnd_code=" + m_brndcode + ") and brnd_code in(select brnd_code from itemmast where live_yn=1 and size_code in(select size_code from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) = " + size + ")) order by brnd_desc";
                bsize_data = new ArrayList<Map<String, String>>();
                try {
                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                    if (con == null) {
                        Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                    } else {
                        //String query=sp_brand_qry;
                        String query = "select brnd_code,brnd_desc from brndmast where brnd_code=" + m_brndcode + " or brnd_code in(select club_brnd_code from brandclub where brnd_code=" + m_brndcode + ") and brnd_code in(select brnd_code from itemmast where live_yn=1 and size_code in(select size_code from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) = " + size + ")) order by brnd_desc";
                        // String query = "select size_code,size_desc from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) > " +size+ " and size_code in (select size_code from itemmast where LOOSE_PEG_YN = 1 and liqr_code = " + m_liqrcode + " and brnd_code = " + m_brndcode +" and full_bottle=1 and live_yn = 1 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode +" and (cl_balance) >=" +size+")) order by seq_no";
                        PreparedStatement ps = con.prepareStatement(query);
                        ResultSet rs = ps.executeQuery();

                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {
                            Map<String, String> data = new HashMap<String, String>();
                            data.put("B", rs.getString(1));
                            data.put("A", rs.getString(2));

                            bsize_data.add(data);
                            cmb_bsize = bsize_data.size();
                            Log.d("cmb_bsize", "....." + cmb_bsize);
                            System.out.println("....." + cmb_bsize);
                            if (cmb_bsize == 1 || m_BRANDCLUBYN == 0) {
                                Log.d("cmb_bsize", "==1: " + cmb_bsize);
                                lin_sp_brand.setVisibility(View.INVISIBLE);
                            } else {
                                Log.d("cmb_bsize", "!=1: " + cmb_bsize);
                                lin_sp_brand.setVisibility(View.VISIBLE);
                            }

                        }

                    }  //z = "Success";

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
                }

            } else {

            }
        } else {
            lin_sp_hide_l_s.setVisibility(View.VISIBLE);
            lin_sp_hide.setVisibility(View.VISIBLE);
            if (m_LOOSEFROMSIZEALLYN.equals("0.0")) {
                sp_cmb_size.setEnabled(false);
            }
        }

        //=====================liqour two spinner data---------------------------
        List<String> list = new ArrayList<String>();
        list.add("SEALED");
        list.add("LOOSE");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MenuCard.this, android.R.layout.simple_spinner_item, list);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        sp_liqour_two.setAdapter(dataAdapter);
        sp_liqour_two.setSelection(0);
        sp_liqour_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                str_liqour_two = adapterView.getItemAtPosition(position).toString();
                if (str_liqour_two.equals("LOOSE")) {
                    ICode = 2;
                    rate = rate - m_sealedddamt;

                    lin_sp_hide.setVisibility(View.VISIBLE);
                } else {
                    ICode = 1;

                    lin_sp_hide.setVisibility(View.INVISIBLE);

                    try {
                        connectionClass = new IMEI_Activity();
                        Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                        if (con == null) {
                            Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                        } else {

                            String query = "select sealed_add_amt from itemmast where item_code like '" + item_code + "'";
                            PreparedStatement ps = con.prepareStatement(query);
                            Log.d("error....", "" + query);

                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                m_sealedddamt = rs.getFloat("sealed_add_amt");
                                rate = rate + m_sealedddamt;
                            }

                        }

                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                        Log.d("error....", "" + e);

                    }


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //-------------------------------------------------------------------------
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
                if (edt_order.getText().toString().equals("0") || edt_order.getText().toString().equals("")) {
                    edt_order.setError("Quantity Should Not Be Zero");
                    edt_order.requestFocus();
                    return;
                } else {
                    int m_exit_yn = 0;
                    int m_force_exit_yn = 0;
                    while (true) {
                        m_force_exit_yn++;
                        try {
                            connectionClass = new IMEI_Activity();
                            Connection con = connectionClass.CONN(con_ipaddress, portnumber);

                            if (con == null) {
                                Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                            } else {

                                String query = "CREATE TABLE LIQRSTOCK(DOC_NO FLOAT DEFAULT 0)";
                                PreparedStatement ps = con.prepareStatement(query);
                                ps.executeUpdate();
                                m_exit_yn = 1;
                            }  //z = "Success";


                        } catch (Exception e) {
                        }
                        if (m_exit_yn == 1||m_force_exit_yn>50) {
                            break;
                        }
                    }
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run()
                        {
                            final_save_data();
                            pd.dismiss();
                        }
                    }, 1000);

                   /* pd.show();
                    final_save_data();
                    pd.dismiss();*/
                }
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(MenuCard.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }
    public void update_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.update_grid_popup_form, null);
        edt_update_value = (EditText) alertLayout.findViewById(R.id.edt_update_value);
        edt_update_value.setText("" + uptd_val);
        update_item_name = (TextView) alertLayout.findViewById(R.id.update_item_name);
        update_item_name.setText(NM);
        edt_update_rate = (EditText) alertLayout.findViewById(R.id.edt_update_rate);
        edt_update_rate.setText("" + uptd_rt);

        edt_update_rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edt_update_rate.getText().toString().equals("")) {
                    uptd_rt = 0;
                } else {
                    uptd_rt = Integer.parseInt(edt_update_rate.getText().toString());
                }

                if (edt_update_order.getText().toString().equals("")) {
                    uptd_qty = 0;
                } else {
                    uptd_qty = Integer.parseInt(edt_update_order.getText().toString());
                }

                uptd_val = uptd_qty * uptd_rt;
                edt_update_value.setText("" + uptd_val);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edt_update_order = (EditText) alertLayout.findViewById(R.id.edt_update_order);
        edt_update_order.setText("" + uptd_qty);
        edt_update_order.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edt_update_rate.getText().toString().equals("")) {
                    uptd_rt = 0;
                } else {
                    uptd_rt = Integer.parseInt(edt_update_rate.getText().toString());
                }

                if (edt_update_order.getText().toString().equals("")) {
                    uptd_qty = 0;
                } else {
                    uptd_qty = Integer.parseInt(edt_update_order.getText().toString());
                }
                uptd_val = uptd_qty * uptd_rt;
                edt_update_value.setText("" + uptd_val);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_contact_operation_cancle = (Button) alertLayout.findViewById(R.id.btn_contact_operation_cancle);
        btn_contact_operation_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_update = (Button) alertLayout.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_uptd_qty = Integer.toString(uptd_qty);
                str_uptd_rt = Integer.toString(uptd_rt);
                str_uptd_val = Integer.toString(uptd_val);

                // holder.qty.setText(""+uptd_qty);
                // holder.rate.setText(""+uptd_rt);
                // holder.value.setText(""+uptd_val);
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(MenuCard.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();

    }
    public static void longLog(String str) {
        if (str.length() > 4000) {
            Log.d("QUERY", str.substring(0, 4000));
            longLog(str.substring(4000));
        } else
            Log.d("QUERY", str);
    }
    public class load_spinner_cmbtest extends AsyncTask<String, String, String> {
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
                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                } else {
                    //String query="select size_code,size_desc from sizemast";
                    String query = "select test_code,test_desc from testmast union select 0,'' order by test_desc";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));

                        sp_data.add(data);
                        cmb_food_test_size = sp_data.size();

                    }

                }  //z = "Success";


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getApplicationContext(), sp_data, R.layout.spin, from, views);
            sp_food_test.setAdapter(spnr_data);
            sp_food_test.setSelection(0);
            sp_food_test.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    cmb_list_cnt = i;

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    str_food_test_text = (String) obj.get("A");
                    m_testcode = (String) obj.get("B");

                    //   String text = sp_food_test.getSelectedItem().toString();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
        }
    }
    public class load_spinner_cmbsize extends AsyncTask<String, String, String> {
        List<Map<String, String>> size_data = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                } else {
                    // size_data.clear();
                    //sp_size_qry = "select size_code,size_desc from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) > " + size + " and size_code in (select size_code from itemmast where LOOSE_PEG_YN = 1 and liqr_code = " + m_liqrcode + " and brnd_code = " + m_brndcode + " and full_bottle=1 and live_yn = 1 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode + " and (cl_balance) >=" + size + ")) order by seq_no";
                    String q = sp_size_qry;
                    System.out.println("......" + q);
                    //String q="select size_code,size_desc from sizemast";
                    // String query = "select test_code,test_desc from testmast union select 0,'' order by test_desc";
                    PreparedStatement ps = con.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();

                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));

                        size_data.add(data);
                        cmb_size = size_data.size();
                        System.out.println("....." + cmb_size);
                    }
                }  //z = "Success";

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getApplicationContext(), size_data, R.layout.spin, from, views);
            sp_cmb_size.setAdapter(spnr_data);
            sp_cmb_size.setSelection(0);
            sp_cmb_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    cmb_frm_size_cnt = i;
                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    str_size_text = (String) obj.get("A");
                    str_size = (String) obj.get("B");

                   /* FormMain.m_com.MainRs.Open "select item_code from itemmast a where (select brnd_desc+' ['+(select liqr_desc from liqrmast where liqr_code=itemmast.liqr_code)+']' from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and full_bottle=1 and itemmast.live_yn = 1 and itemmast.item_code=a.item_code) = '" & FlxItem.TextMatrix(FlxItem.RowSel, 0) & " [" & (FlxItem.TextMatrix(FlxItem.RowSel, 2)) & "]" & "' and size_code= " & Val(CmbFromSize.ItemData(CmbFromSize.ListIndex)) & " ", FormMain.m_com.mainDb, 3, 3
                    If FormMain.m_com.MainRs.RecordCount > 0 Then m_fromitemcode = Trim(FormMain.m_com.MainRs.Fields("item_code"))
                    FormMain.m_com.MainRs.Close*/

                    try {
                        connectionClass = new IMEI_Activity();
                        Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                        if (con == null) {
                            Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                        } else {

                            //  String query = "select item_code from itemmast a where (select brnd_desc+' ['+(select liqr_desc from liqrmast where liqr_code=itemmast.liqr_code)+']' from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and full_bottle=1 and itemmast.live_yn = 1 and itemmast.item_code=a.item_code) = '" & FlxItem.TextMatrix(FlxItem.RowSel, 0) & " [\" & (FlxItem.TextMatrix(FlxItem.RowSel, 2)) & \"]\" & \"' and size_code= \" & Val(CmbFromSize.ItemData(CmbFromSize.ListIndex)) & \" ";
                            // String query = "select item_code from itemmast a where (select brnd_desc+' ['+(select liqr_desc from liqrmast where liqr_code=itemmast.liqr_code)+']' from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and full_bottle=1 and itemmast.live_yn = 1 and itemmast.item_code=a.item_code) = 'BLENDERS PRIDE[WHISKY (PREMIUM)]' and size_code= 24.0";

                            String query = "select item_code from itemmast a where (select brnd_desc+' ['+(select liqr_desc from liqrmast where liqr_code=itemmast.liqr_code)+']' from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and full_bottle=1 and itemmast.live_yn = 1 and itemmast.item_code=a.item_code) = '" + str_item + " [" + str_menu_type + "]" + "'and size_code= " + str_size + "";
                            Log.d("mfromitemcode", query);
                            PreparedStatement ps = con.prepareStatement(query);
                            ResultSet rs = ps.executeQuery();

                            //ArrayList data1 = new ArrayList();
                            while (rs.next()) {
                                m_fromitemcode = rs.getString("item_code");
                                Log.d("m_fromitemcode", m_fromitemcode);

                            }
                        }


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                    }

                    //   String text = sp_food_test.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
        }

    }
    public class load_spinner_cmbbrand extends AsyncTask<String, String, String> {
        List<Map<String, String>> bsize_data = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                } else {
                    sp_brand_qry = "select convert(int,brnd_code) as brnd_code,brnd_desc from brndmast where brnd_code=" + m_brndcode + " or brnd_code in(select club_brnd_code from brandclub where brnd_code=" + m_brndcode + ") and brnd_code in(select brnd_code from itemmast where live_yn=1 and size_code in(select size_code from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) = " + size + ")) order by brnd_desc";
                    String query = sp_brand_qry;
                    // String query="select brnd_code,brnd_desc from brndmast where brnd_code=" + m_brndcode + " or brnd_code in(select club_brnd_code from brandclub where brnd_code=" + m_brndcode + ") and brnd_code in(select brnd_code from itemmast where live_yn=1 and size_code in(select size_code from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) = " + size + ")) order by brnd_desc";
                    // String query = "select size_code,size_desc from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) > " +size+ " and size_code in (select size_code from itemmast where LOOSE_PEG_YN = 1 and liqr_code = " + m_liqrcode + " and brnd_code = " + m_brndcode +" and full_bottle=1 and live_yn = 1 and item_code in(select item_code from onlnstok where item_type = 1 and comp_code = " + m_compcode + " and loct_code= " + m_loctcode +" and (cl_balance) >=" +size+")) order by seq_no";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    //ArrayList data1 = new ArrayList();
                    m_index = 0;
                    n_index = 0;
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));
                        if (str_item.equals(rs.getString(2))) {
                            m_index = n_index;
                        }
                        n_index++;

                        bsize_data.add(data);
                        cmb_bsize = bsize_data.size();
                        Log.d("cmb_bsize", "....." + cmb_bsize);
                        System.out.println("....." + cmb_bsize);

                    }

                }  //z = "Success";

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getApplicationContext(), bsize_data, R.layout.spin, from, views);
            sp_cmb_brand.setAdapter(spnr_data);
            sp_cmb_brand.setSelection(m_index);
            sp_cmb_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    str_brand_name = (String) obj.get("A");
                    str_brand = (String) obj.get("B");
                    //   String text = sp_food_test.getSelectedItem().toString();


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            super.onPostExecute(s);
        }
    }
    public void stock_check() {
        qty = Integer.parseInt(edt_order.getText().toString());
        int m_addyn = 1;
        if (ICode == 3) {
            try {
                connectionClass = new IMEI_Activity();
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                } else {

                    String query = "SELECT MENUITEM_CODE,RAWITEM_CODE,QUANTITY*" + edt_order.getText().toString() + " as qty,(QUANTITY*" + edt_order.getText().toString() + ")*CONVERT(MONEY,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10)) as mlqty,LIQR_CODE,BRND_CODE,full_bottle,loose_bottle FROM ITEMDEFINATION,ITEMMAST,SIZEMAST WHERE ITEM_TYPE = 1 AND SIZEMAST.SIZE_CODE=ITEMMAST.SIZE_CODE AND ITEM_CODE=RAWITEM_CODE AND MENUITEM_CODE=" + item_code + "";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    longLog(query);
                    rowCount = 0;
                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        rowCount++;
                        if (rowCount > 0) {
                            //=========================================
                            try {
                                String qrystk="";
                                connectionClass = new IMEI_Activity();
                                Connection conn = connectionClass.CONN(con_ipaddress, portnumber);
                                if (conn == null) {
                                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                                } else {
                                    if (rs.getInt("loose_bottle") == 1)
                                    {
                                        qrystk = "SELECT ONLNSTOK.ITEM_CODE,CL_BALANCE-CL_BALANCE_B as CL_BALANCE,CL_BALANCE_B,itemmast.mrp FROM ONLNSTOK,ITEMMAST WHERE ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE = 1 AND LIQR_CODE=" + rs.getInt("liqr_code") + " AND BRND_CODE=" + rs.getInt("brnd_code") + " AND LOCT_CODE=" + m_loctcode + " and cl_balance >=" + rs.getInt("mlqty") + " order by (select seq_no from sizemast where size_code=itemmast.size_code) ";
                                    }
                                    else if (rs.getInt("full_bottle") == 1)
                                    {
                                        qrystk = "SELECT ONLNSTOK.ITEM_CODE,CL_BALANCE-CL_BALANCE_B as CL_BALANCE,CL_BALANCE_B,itemmast.mrp FROM ONLNSTOK,ITEMMAST WHERE ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=" + rs.getString("RAWITEM_CODE") + " AND LOCT_CODE=" + m_loctcode + " and cl_balance >=" + rs.getInt("mlqty") + " ";
                                    }
                                    PreparedStatement pss = con.prepareStatement(qrystk);
                                    ResultSet rst = pss.executeQuery();
                                    longLog(qrystk);
                                    int cnt = 0;
                                    //ArrayList data1 = new ArrayList();
                                    while (rst.next()) {
                                        cnt++;
                                        Log.d("innner cnt", "" + cnt);
                                    }
                                    Log.d("outer cnt", "" + cnt);
                                    if (cnt == 0) {
                                        m_addyn = 0;
                                        pd.dismiss();
                                        int m_exit_yn = 0;
                                        String q = "IF OBJECT_ID('FOODSTOCK') IS NOT NULL BEGIN DROP TABLE FOODSTOCK END";
                                        while (true) {
                                            try {
                                                connectionClass = new IMEI_Activity();
                                                Connection con1 = connectionClass.CONN(con_ipaddress, portnumber);
                                                PreparedStatement p = con1.prepareStatement(q);
                                                p.executeUpdate();
                                                m_exit_yn = 1;
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
                                            }
                                            if (m_exit_yn == 1) {
                                                break;
                                            }
                                        }

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(MenuCard.this, R.style.AppCompatAlertDialogStyle);
                                        builder.setTitle("Alert");
                                        builder.setIcon(R.drawable.warning);
                                        builder.setMessage("Liquor Stock For Making This Recipe Is Not Present");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", null);
                                        builder.show();

                                        break;
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
        if (m_addyn == 1) {
            pd.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run()
                {
                    final_save_data();
                    pd.dismiss();
                }
            }, 1000);
           /* pd.show();
            final_save_data();
            pd.dismiss();*/
        }
    }
    public void final_save_data() {

        qty = Integer.parseInt(edt_order.getText().toString());

        //==========================ADDINTOGRID==================================

        if (ICode != 3 && m_maintainstockyn == 1.0) {
            try {
                connectionClass = new IMEI_Activity();
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();
                } else {
                    if (ICode == 1) {
                        q = "select item_code FROM itemmast WHERE item_code like '" + item_code + "' and ITEM_CODE IN(SELECT ITEM_CODE FROM ONLNSTOK,sizemast where item_type = 1 and loct_code= " + m_loctcode + " and comp_code = " + m_compcode + " and sizemast.size_code = itemmast.size_code and itemmast.item_code = onlnstok.item_code and "
                                + " convert(int,(cl_balance)/convert(float(9),left(replace(size_desc,space(01),space(10)),4))) >= " + qty + ")";

                        Log.d("ICode==1", q);
                    } else if (ICode == 2) {
                        q = "select item_code from onlnstok where item_type = 1 and item_code like '" + m_fromitemcode + "' and loct_code= " + m_loctcode + " and (cl_balance)>=(SELECT(" + edt_order.getText().toString() + "*CONVERT(FLOAT(9),LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),4))) FROM ITEMMAST,SIZEMAST WHERE ITEM_CODE = '" + item_code + "' and ITEMMAST.SIZE_CODE = SIZEMAST.SIZE_CODE)";

                        Log.d("ICode==2", q);
                    }
                    PreparedStatement ps = con.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();
                    longLog(q);
                    //ArrayList data1 = new ArrayList();
                    int cnt = 0;
                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        cnt++;
                    }
                    if (cnt == 0) {

                        int m_exit_yn = 0;
                        String q = "IF OBJECT_ID('LIQRSTOCK') IS NOT NULL BEGIN DROP TABLE LIQRSTOCK END";
                        while (true) {
                            try {
                                connectionClass = new IMEI_Activity();
                                Connection con1 = connectionClass.CONN(con_ipaddress, portnumber);
                                PreparedStatement p = con1.prepareStatement(q);
                                p.executeUpdate();
                                m_exit_yn = 1;
                                pd.dismiss();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
                            }
                            if (m_exit_yn == 1) {
                                break;
                            }
                        }

                        ICode = 0;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MenuCard.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Alert");
                        builder.setIcon(R.drawable.warning);
                        builder.setMessage("Stock Not Available");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Cancel", null);
                        builder.show();

                        //Toast.makeText(MenuCard.this, "Liquor Stock For Making This Recipe Is Not Present", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
            }
        }
        if (ICode == 3 && m_maintainstockyn == 1.0) {
            try {
                connectionClass = new IMEI_Activity();
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();
                } else {

                    String query = "select menuitem_code from menucarditemmast where menuitem_code like '" + item_code + "' and maintain_stock = 1 ";
                    Log.d("ICode == 3if", query);
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    longLog(query);
                    int rowCount = 0;
                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        rowCount++;
                    }

                    if (rowCount > 0) {
                        //=========================================
                        try {
                            connectionClass = new IMEI_Activity();
                            Connection conn = connectionClass.CONN(con_ipaddress, portnumber);
                            if (conn == null) {
                                Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                            } else {
                                String qry = "select item_code from onlnstok where item_type = 2 and item_code like '" + item_code + "' and loct_code= " + m_loctcode + " and cl_balance >= " + edt_order.getText().toString() + " ";
                                Log.d("ICode == 3else", query);
                                //String qry = "SELECT ONLNSTOK.ITEM_CODE,CL_BALANCE-CL_BALANCE_B as CL_BALANCE,CL_BALANCE_B,itemmast.mrp FROM ONLNSTOK,ITEMMAST WHERE ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE = 1 AND LIQR_CODE=" + rs.getInt("liqr_code") + " AND BRND_CODE=" + rs.getInt("brnd_code") + " AND LOCT_CODE=" + m_loctcode + " and cl_balance >=" +rs.getInt("mlqty")+ " ";
                                PreparedStatement pss = con.prepareStatement(qry);
                                ResultSet rst = pss.executeQuery();
                                longLog(qry);

                                int cnt = 0;
                                //ArrayList data1 = new ArrayList();
                                while (rst.next()) {
                                    cnt++;
                                    Log.d("innner cnt", "" + cnt);
                                }
                                Log.d("outer cnt", "" + cnt);
                                if (cnt == 0) {
                                    pd.dismiss();

                                    int m_exit_yn = 0;
                                    String q = "IF OBJECT_ID('FOODSTOCK') IS NOT NULL BEGIN DROP TABLE FOODSTOCK END";
                                    while (true) {
                                        try {
                                            connectionClass = new IMEI_Activity();
                                            Connection con1 = connectionClass.CONN(con_ipaddress, portnumber);
                                            PreparedStatement p = con1.prepareStatement(q);
                                            p.executeUpdate();
                                            m_exit_yn = 1;
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_SHORT).show();
                                        }
                                        if (m_exit_yn == 1) {
                                            break;
                                        }
                                    }
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(MenuCard.this, R.style.AppCompatAlertDialogStyle);
                                    builder.setTitle("Alert");
                                    builder.setIcon(R.drawable.warning);
                                    builder.setMessage("Stock Not Available");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", null);
                                    builder.show();
                                    // Log.d("stock", "Liquor Stock For Making This Recipe Is Not Present");
                                    //Toast.makeText(MenuCard.this, "Liquor Stock For Making This Recipe Is Not Present", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
            }
        }

        if (cmb_bsize > 1 && !str_brand_name.equals(str_item)) {
            m_clubbrandcode = Integer.parseInt(str_brand);
            try {
                connectionClass = new IMEI_Activity();
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(this, "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                } else {
                    String query = "select " + m_ratetype + " from itemmast where brnd_code= " + m_clubbrandcode + " and size_code in(select size_code from sizemast where convert(float(9),left(replace(size_desc,space(01),space(10)),4)) = " + size + ") and live_yn=1";
                    PreparedStatement ps = con.prepareStatement(query);
                    Log.d("qqqqq", query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        rate = Float.parseFloat(rs.getString("NONAC_PRICE"));
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
        } else {
            m_clubbrandcode = 0;
        }
        //=====''''''''' New Addition On 18/09/2013========================
        if (m_maintainstockyn == 1.0) {
            String query;
            if (ICode != 3) {
                if (ICode == 1) {
                    query = "select convert(int,floor((cl_balance-cl_balance_b)/" + size + ")) as cl_balance,convert(int,floor(cl_balance_b/" + size + ")) as cl_balance_b FROM onlnstok WHERE item_code like '" + item_code + "' and item_type = 1 and loct_code=" + m_loctcode + " and comp_code =" + m_compcode + " ";
                } else {
                    query = "select convert(int,cl_balance-cl_balance_b) as cl_balance,convert(int,cl_balance_b) as cl_balance_b FROM onlnstok WHERE item_code like '" + m_fromitemcode + "' and item_type = 1 and loct_code= " + m_loctcode + " and comp_code = " + m_compcode + " ";
                }
                try {
                    connectionClass = new IMEI_Activity();
                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                    if (con == null) {
                        Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                    } else {

                        Log.d("cl_balance.......", query);
                        // String query = "select (cl_balance-cl_balance_b)/\" & Val(FlxItem.TextMatrix(FlxItem.RowSel, 1)) & \" as cl_balance,cl_balance_b/\" & Val(FlxItem.TextMatrix(FlxItem.RowSel, 1)) & \" as cl_balance_b FROM onlnstok WHERE item_code like '\" & Trim(FlxItem.TextMatrix(FlxItem.RowSel, 3)) & \"' and item_type = 1 and loct_code= \" & Val(TxtDecreaseStock.Text) & \" and comp_code = \" & FormMain.m_com.m_compcode & \" \"";
                        PreparedStatement ps = con.prepareStatement(query);
                        ResultSet rs = ps.executeQuery();
                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {
                            m_clbalance = rs.getInt("cl_balance");
                            m_clbalance_b = rs.getInt("cl_balance_b");
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Error.." + e, Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (m_clbalance < 0) {
            m_clbalance = 0;
        }
        if (m_clbalance_b < 0) {
            m_clbalance_b = 0;
        }

        //==========================================================================================
//----------------------------------finalcheck------------------------------------------------------------
        if ((ICode == 1 && m_clbalance_b >= qty) || (ICode == 2 && m_clbalance_b >= qty * (size))) {
            m_saletype = 1;
            try {
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(this, "SQL Server Connection Error", Toast.LENGTH_SHORT).show();
                } else {
                    fill_temperory_data();
                    if (ICode != 2) {
                        m_fromitemcode = "";
                    }
                    item_value = qty * rate;
                    addcountersaleitem();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
        }
        //''''''''New Addittion On 09/07/2016
        else if ((ICode != 3 && m_clbalance_b == 0) || ICode == 3) {
            m_saletype = 0;
            try {
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(this, "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                } else {
                    fill_temperory_data();
                    if (ICode != 2) {
                        m_fromitemcode = "";
                    }
                    item_value = qty * rate;
                    addcountersaleitem();
                    try {
                        connectionClass = new IMEI_Activity();
                        Connection conn = connectionClass.CONN(con_ipaddress, portnumber);
                        if (conn == null) {
                            Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                        } else {
                            String qr = "SELECT MENUITEM_CODE,RAWITEM_CODE,QUANTITY*" + qty + " as qty,convert(int,(QUANTITY*" + qty + ")*CONVERT(MONEY,LEFT(REPLACE(SIZE_DESC,SPACE(01),SPACE(10)),10))) as mlqty,LIQR_CODE,BRND_CODE,full_bottle,loose_bottle FROM ITEMDEFINATION,ITEMMAST,SIZEMAST WHERE ITEM_TYPE = 1 AND SIZEMAST.SIZE_CODE=ITEMMAST.SIZE_CODE AND ITEM_CODE=RAWITEM_CODE AND MENUITEM_CODE=" + item_code + "";
                            PreparedStatement pst = conn.prepareStatement(qr);
                            ResultSet rs = pst.executeQuery();
                            longLog(qr);
                            //ArrayList data1 = new ArrayList();
                            while (rs.next()) {

                                try {
                                    String qry="";
                                    connectionClass = new IMEI_Activity();
                                    Connection conn1 = connectionClass.CONN(con_ipaddress, portnumber);
                                    if (conn1 == null) {
                                        Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                                    } else {
                                        if (rs.getInt("loose_bottle") == 1)
                                        {
                                            qry = "SELECT ONLNSTOK.ITEM_CODE,CL_BALANCE-CL_BALANCE_B as CL_BALANCE,CL_BALANCE_B,itemmast.mrp FROM ONLNSTOK,ITEMMAST WHERE ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE = 1 AND LIQR_CODE=" + rs.getInt("liqr_code") + " AND BRND_CODE=" + rs.getInt("brnd_code") + " AND LOCT_CODE=" + m_loctcode + " and cl_balance >=" + rs.getInt("mlqty") + " order by (select seq_no from sizemast where size_code=itemmast.size_code) ";
                                        }
                                        else if (rs.getInt("full_bottle") == 1)
                                        {
                                            qry = "SELECT ONLNSTOK.ITEM_CODE,CL_BALANCE-CL_BALANCE_B as CL_BALANCE,CL_BALANCE_B,itemmast.mrp FROM ONLNSTOK,ITEMMAST WHERE ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=" + rs.getString("RAWITEM_CODE") + " AND LOCT_CODE=" + m_loctcode + " and cl_balance >=" + rs.getInt("mlqty") + " ";
                                        }
                                        //  String qry = "SELECT ONLNSTOK.ITEM_CODE,convert(int,CL_BALANCE-CL_BALANCE_B) as CL_BALANCE,convert(int,CL_BALANCE_B) as CL_BALANCE_B,itemmast.mrp FROM ONLNSTOK,ITEMMAST WHERE ITEM_TYPE = 1 AND ONLNSTOK.ITEM_CODE=ITEMMAST.ITEM_CODE AND ITEM_TYPE = 1 AND LIQR_CODE=" + rs.getInt("liqr_code") + " AND BRND_CODE=" + rs.getInt("brnd_code") + " AND LOCT_CODE=" + m_loctcode + " and cl_balance >=" + rs.getInt("mlqty") + "order by (select seq_no from sizemast where size_code=itemmast.size_code) ";
                                        PreparedStatement pss = conn1.prepareStatement(qry);
                                        ResultSet rst = pss.executeQuery();
                                        longLog(qry);
                                        int cnt = 0;
                                        //ArrayList data1 = new ArrayList();
                                        while (rst.next()) {
                                            m_clbalance = rst.getInt("cl_balance");
                                            m_clbalance_b = rst.getInt("cl_balance_b");
                                            cnt = rs.getInt("mlqty");
                                            if (m_clbalance_b >= rs.getInt("mlqty")) {
                                                try {
                                                    Connection cn = connectionClass.CONN(con_ipaddress, portnumber);
                                                    if (cn == null) {
                                                        Toast.makeText(this, "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        int m_exit_yn=0;
                                                        String q = "insert into liqrrecipetran(doc_no,doc_dt,food_item_code,food_qty,qty,sale_type,item_code,from_item_code,comp_code,mrp,loct_code,ip_address,tbno_code)values(" + doc_no + ",'" + doc_dt + "'," + item_code + "," + qty + "," + rs.getString("qty") + ",1,'" + rs.getString("rawitem_code") + "','" + rst.getString("item_code") + "'," + m_compcode + "," + rst.getString("mrp") + "," + m_loctcode + ",'" + m_ipaddress + "'," + TBNO_CODE + ")";
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
                                            } else if (m_clbalance >= Integer.parseInt(rs.getString("mlqty"))) {
                                                try {
                                                    Connection cn = connectionClass.CONN(con_ipaddress, portnumber);
                                                    if (cn == null) {
                                                        Toast.makeText(this, "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        int m_exit_yn=0;
                                                        String q = "insert into liqrrecipetran(doc_no,doc_dt,food_item_code,food_qty,qty,sale_type,item_code,from_item_code,comp_code,mrp,loct_code,ip_address,tbno_code)values(" + doc_no + ",'" + doc_dt + "'," + item_code + "," + qty + "," + rs.getString("qty") + ",0,'" + rs.getString("rawitem_code") + "','" + rst.getString("item_code") + "'," + m_compcode + "," + rst.getString("mrp") + "," + m_loctcode + ",'" + m_ipaddress + "'," + TBNO_CODE + ")";
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

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }

        }
        //''''''''New Addittion On 22/11/2017
        else if (ICode == 1 && m_clbalance_b < qty) {
            m_saletype = 1;
            qty1 = qty;
            qty = m_clbalance_b;
            item_value = rate * qty;
            fill_temperory_data();
            try {
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(this, "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                } else {
                    //fill_temperory_data();
                    item_value = qty * rate;
                    addcountersaleitem();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
            m_saletype = 0;
            qty = qty1 - m_clbalance_b;
            item_value = rate * qty;
            fill_temperory_data();
            try {
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(this, "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                } else {
                    //fill_temperory_data();
                    item_value = qty * rate;
                    addcountersaleitem();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }

            //  ''''''''New Addittion On 09/07/2016
        } else if (ICode == 2 && m_clbalance_b < qty * size) {
            m_saletype = 1;
            m_clbalance_b = m_clbalance_b / size;
            qty1 = qty;
            if (m_clbalance_b != 0) {
                qty = m_clbalance_b;
                item_value = rate * qty;
                fill_temperory_data();
                try {
                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                    if (con == null) {
                        Toast.makeText(this, "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                    } else {
                        //fill_temperory_data();
                        item_value = qty * rate;
                        addcountersaleitem();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception" + e, Toast.LENGTH_SHORT).show();
                }
            }
                m_saletype = 0;
                qty = qty1 - m_clbalance_b;
                item_value = rate * qty;
                fill_temperory_data();
                try {
                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                    if (con == null) {
                        Toast.makeText(this, "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                    } else {
                        //fill_temperory_data();
                        item_value = qty * rate;
                        addcountersaleitem();

                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
                }
            }

        if (ICode == 3) {
            q = "IF OBJECT_ID('FOODSTOCK') IS NOT NULL BEGIN DROP TABLE FOODSTOCK END";
        } else {
            q = "IF OBJECT_ID('LIQRSTOCK') IS NOT NULL BEGIN DROP TABLE LIQRSTOCK END";
        }
        int m_exit_yn = 0;
        while (true) {
            try {
                connectionClass = new IMEI_Activity();
                Connection con1 = connectionClass.CONN(con_ipaddress, portnumber);
                PreparedStatement p = con1.prepareStatement(q);
                p.executeUpdate();
                m_exit_yn = 1;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
            if (m_exit_yn == 1) {
                break;
            }
        }

        searchView.setQuery("", false);
    }
    public void fill_temperory_data() {
        int n_exit_yn = 0;
        int m_exit_yn = 0;
        while (true) {
            try {
                PreparedStatement ps;
                connectionClass = new IMEI_Activity();
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                // Disable auto commit
                con.setAutoCommit(false);
                // Do SQL updates...
                //query = "update doc_no set COUNTER_SALE_DOCNO = COUNTER_SALE_DOCNO + 1";
                if (n_exit_yn == 0) {
                    query = "update doc_no set COUNTER_SALE_DOCNO = COUNTER_SALE_DOCNO + 1 where '" + doc_dt + "' between from_year and to_year";
                    ps = con.prepareStatement(query);
                    ps.executeUpdate();
                    con.commit();
                    n_exit_yn = 1;
                }

                if (n_exit_yn == 1) {
                    query = "select COUNTER_SALE_DOCNO from doc_no where '" + doc_dt + "' between from_year and to_year";
                    ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    m_exit_yn = 1;
                    while (rs.next()) {
                        doc_no = rs.getFloat("COUNTER_SALE_DOCNO");
                    }
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
            if (m_exit_yn == 1) {
                break;
            }
        }

        //==============================================================================
    }
    public void menu_option_popup() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.menu_option_popup_form, null);

        lin_lst_odr = (LinearLayout) alertLayout.findViewById(R.id.lin_lst_odr);
        lin_lst_odr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), ReportActivity.class);
               // i.putExtra("doc_dt", doc_dt);
               // i.putExtra("m_lastdocsrno", m_lastdocsrno);
               // i.putExtra("doc_dt_display", doc_dt_display);
               // i.putExtra("m_compcode", m_compcode);
               // i.putExtra("menu_option", "Last Order");
               // i.putExtra("flag", "1");
                startActivity(i);

                SharedPreferences pref = getSharedPreferences("MENU_DATA", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("doc_dt",doc_dt);
                editor.putString("m_lastdocsrno",m_lastdocsrno);
                editor.putString("doc_dt_display",doc_dt_display);
                editor.putString("m_compcode",""+m_compcode);
                editor.putString("menu_option","Last Order");
                editor.putString("flag","1");
                editor.commit();

                dialog.dismiss();

            }
        });
        lin_fnl_entry_odr = (LinearLayout) alertLayout.findViewById(R.id.lin_fnl_entry_odr);
        lin_fnl_entry_odr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ReportActivity.class);
                //i.putExtra("doc_dt", doc_dt);
               // i.putExtra("m_lastdocsrno", m_lastdocsrno);
               // i.putExtra("doc_dt_display", doc_dt_display);
                //i.putExtra("m_compcode", m_compcode);
               // i.putExtra("menu_option", "Total Order (Entry By Entry)");
               // i.putExtra("flag", "2");
                startActivity(i);

                SharedPreferences pref = getSharedPreferences("MENU_DATA", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("doc_dt",doc_dt);
                editor.putString("m_lastdocsrno",m_lastdocsrno);
                editor.putString("doc_dt_display",doc_dt_display);
                editor.putString("m_compcode",""+m_compcode);
                editor.putString("menu_option","Total Order (Entry By Entry)");
                editor.putString("flag","2");
                editor.commit();
                dialog.dismiss();

            }
        });
        lin_fnl_smry_odr = (LinearLayout) alertLayout.findViewById(R.id.lin_fnl_smry_odr);
        lin_fnl_smry_odr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ReportActivity.class);
                i.putExtra("doc_dt", doc_dt);
                i.putExtra("m_lastdocsrno", m_lastdocsrno);
                i.putExtra("doc_dt_display", doc_dt_display);
                i.putExtra("m_compcode", m_compcode);
                i.putExtra("menu_option", "Total Order (Summary)");
                i.putExtra("flag", "3");
                startActivity(i);

                SharedPreferences pref = getSharedPreferences("MENU_DATA", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("doc_dt",doc_dt);
                editor.putString("m_lastdocsrno",m_lastdocsrno);
                editor.putString("doc_dt_display",doc_dt_display);
                editor.putString("m_compcode",""+m_compcode);
                editor.putString("menu_option","Total Order (Summary)");
                editor.putString("flag","3");
                editor.commit();
                dialog.dismiss();
            }
        });
        lin_tbl_swap = (LinearLayout) alertLayout.findViewById(R.id.lin_tbl_swap);
        if(m_swap==1||m_SWAP_TABLE_YN==0)
        {
            lin_tbl_swap.setVisibility(View.GONE);
        }
        else
        {
            lin_tbl_swap.setVisibility(View.VISIBLE);
        }
        lin_tbl_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Swap_Table_Activity.class);
                i.putExtra("doc_dt", doc_dt);
                i.putExtra("m_lastdocsrno", m_lastdocsrno);
                i.putExtra("doc_dt_display", doc_dt_display);
                i.putExtra("m_compcode", m_compcode);
                i.putExtra("menu_option", "Swap Table");
                i.putExtra("flag", "4");
                startActivity(i);
                dialog.dismiss();
            }
        });
        lin_liquor_stock = (LinearLayout) alertLayout.findViewById(R.id.lin_liquor_stock);
        lin_liquor_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Liquor_Stock_ctivity.class);
                startActivity(i);
                dialog.dismiss();
            }
        });
        lin_other_stock = (LinearLayout) alertLayout.findViewById(R.id.lin_other_stock);
        lin_other_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Other_Stock_ctivity.class);
                startActivity(i);
                dialog.dismiss();
            }
        });
        lin_print = (LinearLayout) alertLayout.findViewById(R.id.lin_print);
        lin_cancel_print = (LinearLayout) alertLayout.findViewById(R.id.lin_cancel_print);
        if (print_kot_yn==0){
            lin_print.setVisibility(View.GONE);
            lin_cancel_print.setVisibility(View.GONE);
        }
        else if(m_CANCEL_KOT_YN==0) {
            lin_cancel_print.setVisibility(View.GONE);
        }
        else
        {
            lin_print.setVisibility(View.VISIBLE);
            lin_cancel_print.setVisibility(View.VISIBLE);
        }
        lin_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Print_Kot_Activity.class);
                i.putExtra("doc_dt", doc_dt);
                i.putExtra("m_lastdocsrno", m_lastdocsrno);
                i.putExtra("doc_dt_display", doc_dt_display);
                i.putExtra("m_compcode", m_compcode);
                i.putExtra("menu_option", "List Of Items For KOT Print");
                startActivity(i);
                dialog.dismiss();
            }
        });

        lin_cancel_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Cancel_Kot_Activity.class);
                i.putExtra("doc_dt", doc_dt);
                i.putExtra("m_lastdocsrno", m_lastdocsrno);
                i.putExtra("doc_dt_display", doc_dt_display);
                i.putExtra("m_compcode", m_compcode);
                i.putExtra("menu_option", "List Of Items For KOT Cancellation");
                startActivity(i);
                dialog.dismiss();
            }
        });
        btn_contact_operation_cancle = (Button) alertLayout.findViewById(R.id.btn_contact_operation_cancle);
        btn_contact_operation_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        AlertDialog.Builder alert = new AlertDialog.Builder(MenuCard.this);
        alert.setView(alertLayout);
        dialog = alert.create();
        dialog.show();
    }
    public void addcountersaleitem() {
        int m_exit_yn = 0;
        String query = "insert into countersaleitem(doc_dt,item_code,item_type,qty,rate,item_value,doc_srno,loct_code,comp_code,ip_address,mrp,tbno_code,watr_code,from_item_code,from_tbno_code,print_kot_yn,maintain_stock,brnd_code_as,sale_type,no_of_persons,test_code,cncl_qty,cncl_code,remark,FROM_TAB_YN)values('" + doc_dt + "','" + item_code + "', " + ICode + " ," + qty + "," + rate + "," + item_value + "," + doc_no + "," + m_loctcode + "," + m_compcode + ",'" + m_ipaddress + "'," + m_mrp + "," + TBNO_CODE + "," + m_WATRCODE + ",'"
                + m_fromitemcode + "',''," + 0 + ", " + m_maintainstockyn + "," + m_clubbrandcode + "," + m_saletype + ","
                + m_no_of_person + ",'" + m_testcode + "', " + 0.0 + "," + 0.0 + ",'" + m_Remark + "',1)";
        while (true) {
            try {
                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();
                } else {
                    PreparedStatement ps = con.prepareStatement(query);
                    //ps.executeQuery();
                    Log.d("qqqqq", query);
                    ps.executeUpdate();
                    //==========CALL PROCEDURE=(9/16/2021)===========
                    ps = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','COUNTERSALEITEM',1,'0',''");
                    ps.executeUpdate();

                    ps = con.prepareStatement("update countersaleitem set watr_code="+m_WATRCODE+" where tbno_code="+TBNO_CODE+"");
                    ps.executeUpdate();
                    m_exit_yn=1;

                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
                if(m_exit_yn==1){
                    break;
                }
        }

        if (m_EVERY_ENTRY_KOT_YN==1) {
            query = "INSERT INTO TBNOFROMTABFORKOT(TBNO_CODE,ACTION_TYPE,DOC_SRNO_UPTO) values('" + TBNO_CODE + "',1," + doc_no + ")";
            m_exit_yn = 0;
            while (true) {
                try {
                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                    if (con == null) {
                        Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
                    } else {
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.executeUpdate();
                        m_exit_yn = 1;
                    }
                } catch (Exception e) {
                }
                if (m_exit_yn == 1) {
                    break;
                }
            }
        }
    }
    public class load_spinner_group extends AsyncTask<String, String, String> {
        List<Map<String, String>> sp_data = new ArrayList<Map<String, String>>();
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                Connection con = connectionClass.CONN(con_ipaddress,portnumber);
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    //String query="select size_code,size_desc from sizemast";
                    String query = "SELECT 0 AS MENU_CODE,'***ALL***' AS MENU_DESC,1 AS SEQ UNION  SELECT MENU_CODE,MENU_DESC,2 AS SEQ FROM MENUMAST WHERE MENU_CODE IN(SELECT MENU_CODE FROM MENUCARDITEMMAST) UNION SELECT 0 AS MENU_CODE,case when (select count(*) from onlnstok where item_type=1) > 0 then '***LIQUOR***' else '***LIQUOR NOT***' end AS MENU_DESC,3 AS SEQ ORDER BY SEQ,MENU_DESC";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        if (!rs.getString("menu_desc").equals("***LIQUOR NOT***")) {
                            data.put("B", rs.getString(1));
                            data.put("A", rs.getString(2));
                            sp_data.add(data);
                        }

                    }

                }  //z = "Success";


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getApplicationContext(), sp_data, R.layout.spin, from, views);
            sp_group_filter.setAdapter(spnr_data);
            sp_group_filter.setSelection(0);

            sp_group_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    searchView.setFocusable(false);
                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    m_group_name = (String) obj.get("A");
                    m_group_code = (String) obj.get("B");
                    if (m_group_name.equals("***ALL***")){
                        m_liqrsearch="";
                        m_foodsearch="";
                    }
                    else if (m_group_name.equals("***LIQUOR***")){
                        m_liqrsearch="";
                        m_foodsearch=" menumast.menu_code = 9999999999 and ";
                    }
                    else {
                        m_liqrsearch=" mfg_code = 9999999999 and ";
                        m_foodsearch=" menumast.menu_code = " + m_group_code + " and ";
                    }
                    searchView.setQuery("", false);
                    menu_search("");


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
        }
    }

}