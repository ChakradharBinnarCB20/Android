package com.example.home;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class Table_Grid_Fragment extends Fragment {
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    // ConnectionClass connectionClass;
    GridView lstpro;
    AlertDialog dialog;
    ArrayList<HashMap<String, String>> contact_arryList;
    TransparentProgressDialog pd;
    TextView txt_total_table_bill,txt_date;
    String doc_dt, doc_dt_display;
    String search_word = "", value;
   // int m_TAB_CODE;
    String con_ipaddress, portnumber;
    SearchView place_searchView;
    String tab_user_code, tab_user_name;
    String str_waiter_name,str_waiter_code, TBNO_DESC, SERVICE_TAX_PER, CGST_PER, SGST_PER, TABL_VALUE,RATETYPE_DESC;
    int m_WATRCODE = 0;
    int m_BRANDCLUBYN = 0;
    float  TBNO_CODE;
    int m_tab_code;
    Double Total_Table_Bill = 0.0;
    Double Last_total = 0.0;
    int m_swap;
    DecimalFormat d;
    ScheduledExecutorService scheduler;
    int sp_WATRCODE=0 ;
    float WATRCODE;
    int i=0;
    String crr_date, val2;
    IMEI_Activity connectionClass;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    Spinner sp_waiter;
    int m_SWAP_TABLE_YN = 0;
    int m_CANCEL_KOT_YN = 0;
    Button btn_ok, btn_contact_operation_cancle, btn_update;
    public Table_Grid_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_table, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        d = new DecimalFormat("0.00");

        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");

        SharedPreferences sp = getActivity().getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        tab_user_name = sp.getString("tab_user_name", "");
        tab_user_code = sp.getString("tab_user_code", "");
        //tab_user_code = "1";
        //================================================
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) view.findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new GridLayoutManager(getActivity(), 3);
        // layoutManager_pe = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getActivity(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        //==========Date=========================
        //current date
     /*   Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        crr_date = df.format(c.getTime());*/
            connectionClass = new IMEI_Activity();
            try {

                Connection con = connectionClass.CONN(con_ipaddress, portnumber);

                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    //String query="select size_code,size_desc from sizemast";

                    String query = "select WATR_CODE,BRAND_CLUB_YN,SWAP_TABLE_YN,CANCEL_KOT_YN from tabusermast WHERE TABUSER_code='" + tab_user_code + "'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        m_WATRCODE = rs.getInt("WATR_CODE");
                        m_BRANDCLUBYN = rs.getInt("BRAND_CLUB_YN");
                        m_SWAP_TABLE_YN = rs.getInt("SWAP_TABLE_YN");
                        m_CANCEL_KOT_YN = rs.getInt("CANCEL_KOT_YN");
                    }
                }  //z = "Success";
            } catch (Exception e) {

            }
            //======================================
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {

                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                                if (con == null) {
                                    Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

                                } else {

                                    PreparedStatement ps = con.prepareStatement("select isnull(sum(item_value),0) as item_value from countersaleitem");
                                    ResultSet rs = ps.executeQuery();
                                    while (rs.next()) {
                                        Last_total = rs.getDouble("item_value");
                                    }
                                }

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
                            }
                            // Toast.makeText(getActivity(), "It works", Toast.LENGTH_SHORT).show();
                            if (Total_Table_Bill != Last_total) {
                                //  new load_table().execute();
                                search_word = "";
                                sales_data();
                            }

                        }
                    });
                }
            }, 10, 10, TimeUnit.SECONDS);
            //================================================

          //  txt_date = (TextView) view.findViewById(R.id.txt_date);
           // txt_date.setText(crr_date);
            txt_total_table_bill = (TextView) view.findViewById(R.id.txt_total_table_bill);
            //--------- search ------------
            place_searchView = (SearchView) view.findViewById(R.id.place_searchView);
            place_searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                }
            });
            place_searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {

                    // new Attendance_list().execute();
                    //  new load_all_table().execute();
                    sales_data();
                    return false;
                }
            });
            place_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // TODO Auto-generated method stub
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.length() > 0) {
                        search_word = "";
                        search_word = newText;
                        //new FetchSearchResult().execute();
                        //  new table_search().execute();
                        sales_data();
                    } else if (TextUtils.isEmpty(newText)) {
                        //new Attendance_list().execute();
                        //   new load_all_table().execute();
                        search_word = "";
                        sales_data();
                    } else {
                    }
                    return false;
                }
            });
            //********************
            pd = new TransparentProgressDialog(getActivity(), R.drawable.busy);

            con = CONN(con_ipaddress, portnumber);
            contact_arryList = new ArrayList<HashMap<String, String>>();

            // new load_table().execute();
            sales_data();
        }
    public void sales_data() {

        try {
            pd.show();
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {
                
                PreparedStatement ps = con.prepareStatement("SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE,(SELECT COUNT(*) FROM COUNTERSALEITEM WHERE PRINT_YN=0 AND COUNTERSALEITEM.TBNO_CODE = TBNOMAST.TBNO_CODE) AS BILL_NO,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM A),'') AS RUNNING_TOTAL FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"' AND TBNO_DESC LIKE '%"+search_word+"%' ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1)");
                //PreparedStatement ps = con.prepareStatement("SELECT TBNO_CODE,TBNO_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE COUNTERSALEITEM.TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE,(SELECT COUNT(*) FROM COUNTERSALEITEM WHERE PRINT_YN=0 AND COUNTERSALEITEM.TBNO_CODE = TBNOMAST.TBNO_CODE) AS BILL_NO FROM TBNOMAST,SECTIONMAST WHERE TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND TBNO_DESC LIKE '%"+search_word+"%' AND TBNO_CODE IN(SELECT TBNO_CODE FROM COUNTERSALEITEM) ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1)");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                Total_Table_Bill=0.0;
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("TBNO_CODE", rs.getString("TBNO_CODE"));

                    map.put("TBNO_DESC", rs.getString("TBNO_DESC"));
                    map.put("RATETYPE_DESC", rs.getString("RATETYPE_DESC"));
                    map.put("FOOD_SRVICE_TAX", rs.getString("FOOD_SRVICE_TAX"));
                    map.put("CGST_PER", rs.getString("CGST_PER"));
                    map.put("SGST_PER", rs.getString("SGST_PER"));
                    map.put("TABL_VALUE", rs.getString("TABL_VALUE"));
                    map.put("BILL_NO", rs.getString("BILL_NO"));
                    if(!rs.getString("RUNNING_TOTAL").equals("")) {
                        Total_Table_Bill = Double.parseDouble(rs.getString("RUNNING_TOTAL"));
                        Last_total = Double.parseDouble(rs.getString("RUNNING_TOTAL"));
                    }
                    menu_card_arryList.add(map);

                }
                txt_total_table_bill.setText(""+d.format(Total_Table_Bill));
                m_swap=menu_card_arryList.size();

            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_list_test, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            double t=0;
            // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.txt_d3.setText(attendance_list.get(position).get("TBNO_DESC"));
            holder.txt_d4.setText(attendance_list.get(position).get ("TABL_VALUE"));
            if(!attendance_list.get(position).get("TABL_VALUE").equals("")) {
                t = Double.parseDouble(attendance_list.get(position).get("TABL_VALUE"));
            }
            double v=Double.parseDouble(attendance_list.get(position).get("BILL_NO"));
            if(v==0 && t>0)
            {
                holder.lin.setBackgroundColor(Color.rgb(255, 198, 179));
            }
            else {
                holder.lin.setBackgroundColor(Color.rgb(255, 255, 204));

            }

            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run()
                        {
                            try {
                                con = CONN(con_ipaddress,portnumber);
                                if (con == null) {
                                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                                } else {
                                    //String query="select size_code,size_desc from sizemast";
                                    //------Last sletected table waiter code------------------------
                                    String q = "select top 1 watr_code from countersaleitem where tbno_code='"+attendance_list.get(position).get("TBNO_CODE")+"'";
                                    PreparedStatement pss = con.prepareStatement(q);
                                    ResultSet rss = pss.executeQuery();
                                    //ArrayList data1 = new ArrayList();
                                    sp_WATRCODE=0;

                                    while (rss.next()) {
                                        WATRCODE=Float.parseFloat(rss.getString("WATR_CODE"));
                                        sp_WATRCODE = Math.round(WATRCODE);
                                    }

                                    String query = "select watrmast.watr_code,watr_desc,0 as seqno from tabusermast,watrmast where tabusermast.watr_code=watrmast.watr_code and tabuser_code='"+tab_user_code+"' union select watr_code,watr_desc,1 from watrmast where watr_code not in(select watr_code from tabusermast where live_yn=1) order by seqno,watr_desc";
                                    PreparedStatement ps = con.prepareStatement(query);
                                    ResultSet rs = ps.executeQuery();
                                    //ArrayList data1 = new ArrayList();
                                    i=-1;
                                    int wcd=0;
                                    int seti=0;
                                    while (rs.next()) {
                                        i++;
                                        float  wcode=Float.parseFloat(rs.getString("WATR_CODE"));
                                      wcd = Math.round(wcode);
                                      if(wcd==sp_WATRCODE)
                                      {
                                          seti=1;
                                          break;
                                      }
                                    }
                                    if(seti==0)
                                    {
                                        i=0;
                                    }

                                }  //z = "Success";


                            } catch (Exception e) {

                            }
                            waiter_popup();
                    SharedPreferences pref = getActivity().getSharedPreferences("HOME_DATA", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("TBNO_CODE",attendance_list.get(position).get("TBNO_CODE"));
                    editor.putString("TBNO_DESC",attendance_list.get(position).get("TBNO_DESC"));
                    editor.putString("RATETYPE_DESC",attendance_list.get(position).get("RATETYPE_DESC"));
                    editor.putString("SERVICE_TAX_PER",attendance_list.get(position).get("FOOD_SRVICE_TAX"));
                    editor.putString("CGST_PER",attendance_list.get(position).get("CGST_PER"));
                    editor.putString("SGST_PER",attendance_list.get(position).get("SGST_PER"));
                    editor.putInt("m_WATRCODE",m_WATRCODE);
                    editor.putInt("m_BRANDCLUBYN",m_BRANDCLUBYN);
                    editor.putInt("m_swap",m_swap);
                    editor.putInt(" m_SWAP_TABLE_YN", m_SWAP_TABLE_YN);
                    editor.putInt(" m_CANCEL_KOT_YN", m_CANCEL_KOT_YN);
                    editor.commit();

                    pd.dismiss();
                        }
                    }, 3000);

                }
            });
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView txt_d3, txt_d4;
            LinearLayout lin;
            ImageView img_tbl;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.txt_d3 = (TextView) itemView.findViewById(R.id.txt_d3);
                this.txt_d4 = (TextView) itemView.findViewById(R.id.txt_d4);
                this.img_tbl = (ImageView) itemView.findViewById(R.id.img_tbl);

            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            con = CONN(con_ipaddress,portnumber);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
            } else {
                //String query="select size_code,size_desc from sizemast";
               /* //------Last sletected table waiter code------------------------
                String q = "select watr_code from countersaleitem where tbno_code='"+m_TAB_CODE+"'";
                PreparedStatement pss = con.prepareStatement(q);
                ResultSet rss = pss.executeQuery();
                //ArrayList data1 = new ArrayList();
                while (rss.next()) {
                    WATRCODE=Float.parseFloat(rss.getString("WATR_CODE"));
                    sp_WATRCODE = Math.round(WATRCODE);
                }*/
                String query = "select WATR_CODE,BRAND_CLUB_YN from tabusermast WHERE TABUSER_code='"+tab_user_code+"'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    m_WATRCODE=rs.getInt("WATR_CODE");
                    m_BRANDCLUBYN=rs.getInt("BRAND_CLUB_YN");
                }

            }  //z = "Success";


        } catch (Exception e) {

        }
     //   new load_all_table().execute();
        //Table_Fragment rSum = new Table_Fragment();
       // getActivity().getSupportFragmentManager().beginTransaction().remove(rSum).commit();
    }
    public void waiter_popup() {

        LayoutInflater inflater = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inflater = getLayoutInflater();
        }
        View alertLayout = inflater.inflate(R.layout.waiter_popup_form, null);

        sp_waiter = (Spinner) alertLayout.findViewById(R.id.sp_waiter);
        // sp_size=(Spinner) alertLayout.findViewById(R.id.sp_size);
        new load_waiter().execute();
        // qty=edt_order.getText().toString();
        btn_contact_operation_cancle = (Button) alertLayout.findViewById(R.id.btn_contact_operation_cancle);
        btn_contact_operation_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_ok = (Button) alertLayout.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("wwww",""+str_waiter_code);
                Log.d("wwww",""+str_waiter_name);

                SharedPreferences pref = getActivity().getSharedPreferences("SPIN_DATA", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("m_waiter_name", str_waiter_name);
                editor.putString("m_waiter_code", str_waiter_code);
                editor.commit();

                Intent i=new Intent(getContext(),MenuCard.class);
                startActivity(i);
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }
      //===================waiter list============================
      public class load_waiter extends AsyncTask<String, String, String> {
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
                      Toast.makeText(getActivity(), "SQL Server Connection Error", Toast.LENGTH_SHORT).show();

                  } else {
                      //String query="select size_code,size_desc from sizemast";
                      //String query = "select watr_code,watr_desc from watrmast where live_yn=1";
                      String query = "select watrmast.watr_code,watr_desc,0 as seqno from tabusermast,watrmast where tabusermast.watr_code=watrmast.watr_code and tabuser_code="+tab_user_code +" union select watr_code,watr_desc,1 from watrmast where watr_code not in(select watr_code from tabusermast where live_yn=1) order by seqno,watr_desc";
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
                  Toast.makeText(getActivity(), "Exception"+e, Toast.LENGTH_SHORT).show();
              }
              return null;
          }

          @Override
          protected void onPostExecute(String s) {

              String[] from = {"A", "B"};
              int[] views = {R.id.list_d1};

              final SimpleAdapter spnr_data = new SimpleAdapter(getActivity(), sp_data, R.layout.spin, from, views);
              sp_waiter.setAdapter(spnr_data);
            //  sp_waiter.setSelection(0);
              sp_waiter.setSelection(i);
              sp_waiter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                      HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                      str_waiter_name = (String) obj.get("A");
                      str_waiter_code = (String) obj.get("B");

                      //   String text = sp_food_test.getSelectedItem().toString();
                    //  Toast.makeText(getActivity(), ""+str_waiter_code+"\n"+str_waiter_name, Toast.LENGTH_SHORT).show();

                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> adapterView) {

                  }
              });
              super.onPostExecute(s);
          }
      }
    //=========================================================================

    public Connection CONN(String ip, String port) {

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
