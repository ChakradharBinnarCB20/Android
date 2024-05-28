package com.example.admin_beerbar_new;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Closing_Stock_Other_Fragment extends Fragment {
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
    String con_ipaddress,portnumber,IMEINumber,str_month="",str_day="",db,m_loct_desc,m_loct_code;
    DatePickerDialog  datePickerDialog;
    PreparedStatement ps1;
    double m_clbal,m_grntot,m_openingcash,o_cashopbal,m_cashopbal,m_retailcash,m_stockadjamt,m_computernetcash,m_shortextracash,m_withdrawalcash,m_nextdayopcash,m_cardamount;
    int m_closingcashyn;
    int m_entryseqno;
    Spinner sp_stock_loc;
    ProgressDialog progressDoalog;
    String TEMPSTOCK;
    String WHOLESALE;
    public Closing_Stock_Other_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.closing_stock_other, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp_stock_loc=(Spinner)view.findViewById(R.id.sp_stock_loc);

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

                        insert_data(Temp_date);
                        Intent i=new Intent(getActivity(),Closing_Stock_Other_Report.class);
                        i.putExtra("date",edt_as_on_date.getText().toString());
                        i.putExtra("loc",m_loct_desc);
                        i.putExtra("Query_date",Temp_date);
                        startActivity(i);
                        pd.dismiss();
                    }
                }, 1000);

            }
        });

        new load_spinner_data().execute();
    }

    public void insert_data(String date)
    {
        //===========ALL==============================
        try {
            con = CONN(con_ipaddress, portnumber,db);


            ps1 = con.prepareStatement("delete from tabreportparameters where tab_code ="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,item_code,gl_opbal,tab_code) select (select menu_code from menucarditemmast where menuitem_code=opitstok.item_code),item_code,op_qty,"+m_TAB_CODE+" from opitstok where item_type=2 and loct_code ="+m_loct_code+" and comp_code="+m_compcode+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,item_code,gl_opbal,tab_code) select (select menu_code from menucarditemmast where menuitem_code=puritem.item_code),item_code,sum(bottle_qty+free_qty), "+m_TAB_CODE+" from puritem where pur_type = 2 and doc_dt <='"+Temp_date+"' and comp_code="+m_compcode+" and pur_type=2 and loct_code ="+m_loct_code+" group by item_code ");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,item_code,gl_opbal,tab_code) select (select menu_code from menucarditemmast where menuitem_code=transfernote.item_code),item_code,sum(bottle_qty),"+m_TAB_CODE+" from transfernote where doc_dt <='"+Temp_date+"' and comp_code="+m_compcode+" and to_loct_code ="+m_loct_code+" and item_type=2 group by item_code");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,item_code,gl_opbal,tab_code) select (select menu_code from menucarditemmast where menuitem_code=transfernote.item_code),item_code,-1*isnull(sum(bottle_qty),0),"+m_TAB_CODE+" from transfernote where doc_dt<='"+Temp_date+"' and comp_code="+m_compcode+" and from_loct_code ="+m_loct_code+"  and item_type=2 group by item_code ");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,item_code,gl_opbal,tab_code) select (select menu_code from menucarditemmast where menuitem_code=countersaleitem.item_code),item_code,-1*isnull(sum(qty),0),"+m_TAB_CODE+" from countersaleitem where maintain_stock=1 and doc_dt <='"+Temp_date+"' and comp_code="+m_compcode+" and item_type=3 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code ="+m_loct_code+" group by item_code");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,item_code,gl_opbal,tab_code) select (select menu_code from menucarditemmast where menuitem_code=saleitem.item_code),item_code,-1*isnull(sum(qty),0),"+m_TAB_CODE+" from saleitem where maintain_stock=1 and doc_dt <='"+Temp_date+"' and comp_code="+m_compcode+" and item_type=3 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code ="+m_loct_code+" group by item_code");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set AMOUNT_1 = (select purchase_price from menucarditemmast where menuitem_code=tabreportparameters.item_code) WHERE TAB_CODE="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set AMOUNT_1 = isnull((select top 1 max(basic_rate) from puritem where item_code=tabreportparameters.item_code and pur_type = 2 and doc_dt in(select max(doc_dt) from puritem a where a.pur_type = 2 and a.item_code = tabreportparameters.item_code and doc_dt <='"+Temp_date+"')),AMOUNT_1) WHERE TAB_CODE="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set amount = round(gl_opbal*AMOUNT_1,2) where tab_code="+m_TAB_CODE+"");
            ps1.executeUpdate();
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
    public class load_spinner_data extends AsyncTask<String, String, String> {
        List<Map<String, String>> sp_data = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            progressDoalog = new ProgressDialog(getActivity());
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                con = CONN(con_ipaddress,portnumber,db);
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    String query = "select LOCT_CODE,LOCT_DESC from LOCTmast where LOCT_CODE >0 and loct_code in(select loct_code from onlnstok where item_type =2) order by LOCT_DESC";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));
                        sp_data.add(data);
                    }
                }

            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDoalog.dismiss();
            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getActivity(), sp_data, R.layout.spin, from, views);
            sp_stock_loc.setAdapter(spnr_data);
            sp_stock_loc.setSelection(0);
            sp_stock_loc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    m_loct_desc = (String) obj.get("A");
                    m_loct_code = (String) obj.get("B");
                    //  Toast.makeText(getActivity(), "loct_code: "+m_loct_code, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
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
