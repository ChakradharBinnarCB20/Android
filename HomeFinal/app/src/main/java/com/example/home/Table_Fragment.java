package com.example.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Table_Fragment extends Fragment {
    IMEI_Activity m_com;
    IMEI_Activity connectionClass;
   // ConnectionClass connectionClass;
    GridView lstpro;
    AlertDialog dialog;
    ArrayList<HashMap<String, String>> contact_arryList;
    com.example.homefinalfinal.Class.TransparentProgressDialog pd;
    TextView edit_place_code;
    String place_code,DATA;
    String search_word;
    int cnt;
    String con_ipaddress ,portnumber;
    SearchView place_searchView;
    String tab_user_code,tab_user_name;
    String TBNO_CODE,TBNO_DESC,RATETYPE_DESC,SERVICE_TAX_PER,CGST_PER,SGST_PER,TABL_VALUE;
    int m_WATRCODE =0;
    int m_BRANDCLUBYN =0;
    int m_SWAP_TABLE_YN =0;
    int m_CANCEL_KOT_YN =0;
    int m_swap;
    public Table_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sp = getActivity().getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        tab_user_name = sp.getString("tab_user_name", "");
        tab_user_code = sp.getString("tab_user_code", "");
        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        connectionClass = new IMEI_Activity();
            try {

            Connection con = connectionClass.CONN(con_ipaddress,portnumber);

            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                //String query="select size_code,size_desc from sizemast";

                String query = "select WATR_CODE,BRAND_CLUB_YN,SWAP_TABLE_YN,CANCEL_KOT_YN from tabusermast WHERE TABUSER_code='"+tab_user_code+"'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    m_WATRCODE=rs.getInt("WATR_CODE");
                    m_BRANDCLUBYN=rs.getInt("BRAND_CLUB_YN");
                    m_SWAP_TABLE_YN=rs.getInt("SWAP_TABLE_YN");
                    m_CANCEL_KOT_YN=rs.getInt("CANCEL_KOT_YN");
                }

            }  //z = "Success";


        } catch (Exception e) {

        }
        //--------- search ------------
        place_searchView=(SearchView)view.findViewById(R.id.place_searchView);
        place_searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        place_searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                // new Attendance_list().execute();
                new load_all_table().execute();
                return false;
            }
        });

        place_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >0)
                {   search_word=newText;
                    //new FetchSearchResult().execute();
                   new table_search().execute();
                }
                else  if (TextUtils.isEmpty(newText)){
                    //new Attendance_list().execute();
                    new load_all_table().execute();
                }
                else
                { }
                return false;
            }
        });
        //********************
        pd = new com.example.homefinalfinal.Class.TransparentProgressDialog(getActivity(), R.drawable.busy);

        connectionClass = new IMEI_Activity();
        contact_arryList = new ArrayList<HashMap<String, String>>();
        lstpro = (GridView) view.findViewById(R.id.lv);

        new load_table().execute();
    }
    public class load_table extends AsyncTask<String,String,String>
    {
        List<Map<String, String>> prolist  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String query = "SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"' ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1)";
           // String query = "SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"'";
            int m_exit_yn=0;
            while (true) {
                try {
                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                    if (con == null) {
                        Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                    } else {
                        // String query = "SELECT TBNO_CODE,TBNO_DESC FROM TBNOMAST";
                        // String query = "select TBNO_DESC from tabrights,TBNOMAST where tabrights.TBNO_CODE = TBNOMAST.TBNO_CODE AND TABUSER_CODE='"+tab_user_code+"'";
                        PreparedStatement ps = con.prepareStatement(query);
                        ResultSet rs = ps.executeQuery();
                        m_exit_yn=1;
                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {

                            Map<String, String> datanum = new HashMap<String, String>();
                            TBNO_CODE = rs.getString(1);
                            TBNO_DESC = rs.getString(2);
                            RATETYPE_DESC = rs.getString(3);
                            SERVICE_TAX_PER = rs.getString(4);
                            CGST_PER = rs.getString(5);
                            SGST_PER = rs.getString(6);
                            TABL_VALUE = rs.getString("TABL_VALUE");
                            datanum.put("TBNO_CODE", TBNO_CODE);
                            datanum.put("TBNO_DESC", TBNO_DESC);
                            datanum.put("RATETYPE_DESC", RATETYPE_DESC);
                            datanum.put("SERVICE_TAX_PER", SERVICE_TAX_PER);
                            datanum.put("CGST_PER", CGST_PER);
                            datanum.put("SGST_PER", SGST_PER);
                            datanum.put("TABL_VALUE", TABL_VALUE);

                            prolist.add(datanum);

                        }
                        m_swap=prolist.size();
                    }

                } catch (Exception e) {


                }
                if (m_exit_yn==1){
                    break;
                }
            }
                    return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            String[] from = { "TBNO_DESC","TABL_VALUE"};
            int[] views = {R.id.txt_d3,R.id.txt_d4};
            final SimpleAdapter ADA = new SimpleAdapter(getActivity(), prolist, R.layout.place_list_test, from, views);
            lstpro.setAdapter(ADA);

            lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    pd.show();
                   // String tbno_code=ADA.getItem(position);
                    HashMap<String,Object> obj=(HashMap<String,Object>)ADA.getItem(position);
                    TBNO_CODE=(String)obj.get("TBNO_CODE");
                    TBNO_DESC=(String)obj.get("TBNO_DESC");
                    RATETYPE_DESC=(String)obj.get("RATETYPE_DESC");
                    SERVICE_TAX_PER=(String)obj.get("SERVICE_TAX_PER");
                    CGST_PER=(String)obj.get("CGST_PER");
                    SGST_PER=(String)obj.get("SGST_PER");
                    TABL_VALUE=(String)obj.get("TABL_VALUE");

                    SharedPreferences pref = getActivity().getSharedPreferences("HOME_DATA", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("TBNO_CODE",TBNO_CODE);
                    editor.putString("TBNO_DESC",TBNO_DESC);
                    editor.putString("RATETYPE_DESC",RATETYPE_DESC);
                    editor.putString("SERVICE_TAX_PER",SERVICE_TAX_PER);
                    editor.putString("CGST_PER",CGST_PER);
                    editor.putString("SGST_PER",SGST_PER);
                    editor.putInt("m_WATRCODE",m_WATRCODE);
                    editor.putInt("m_BRANDCLUBYN",m_BRANDCLUBYN);
                    editor.putInt("m_swap",m_swap);
                    editor.putInt(" m_SWAP_TABLE_YN", m_SWAP_TABLE_YN);
                    editor.putInt(" m_CANCEL_KOT_YN", m_CANCEL_KOT_YN);

                    editor.commit();

                    Intent i=new Intent(getContext(),MenuCard.class);
                    startActivity(i);
                    pd.dismiss();
                }
            });

            super.onPostExecute(s);
        }
    }

    public class load_all_table extends AsyncTask<String,String,String>
    {
        List<Map<String, String>> prolist  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                Connection con = connectionClass.CONN(con_ipaddress,portnumber);
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                   // String query = "select TBNO_DESC from tabrights,TBNOMAST where tabrights.TBNO_CODE = TBNOMAST.TBNO_CODE AND TABUSER_CODE='"+tab_user_code+"'";
                    String query = "SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"' ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1)";
                    // String query = "SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    prolist.clear();
                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {

                        Map<String, String> datanum = new HashMap<String, String>();
                        TBNO_CODE= rs.getString(1);
                        TBNO_DESC= rs.getString(2);
                        RATETYPE_DESC= rs.getString(3);
                        SERVICE_TAX_PER= rs.getString(4);
                        CGST_PER= rs.getString(5);
                        SGST_PER= rs.getString(6);
                        TABL_VALUE= rs.getString("TABL_VALUE");

                        datanum.put("TBNO_CODE", TBNO_CODE);
                        datanum.put("TBNO_DESC", TBNO_DESC);
                        datanum.put("RATETYPE_DESC", RATETYPE_DESC);
                        datanum.put("SERVICE_TAX_PER", SERVICE_TAX_PER);
                        datanum.put("CGST_PER", CGST_PER);
                        datanum.put("SGST_PER", SGST_PER);
                        datanum.put("TABL_VALUE", TABL_VALUE);

                        prolist.add(datanum);

                    }
                    m_swap=prolist.size();
                }

            }catch (Exception e)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            String[] from = {"TBNO_DESC","TABL_VALUE"};
            int[] views = {R.id.txt_d3,R.id.txt_d4};
            final SimpleAdapter ADA = new SimpleAdapter(getActivity(), prolist, R.layout.place_list_test, from, views);
            lstpro.setAdapter(ADA);

            lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pd.show();
                    HashMap<String,Object> obj=(HashMap<String,Object>)ADA.getItem(position);
                    TBNO_CODE=(String)obj.get("TBNO_CODE");
                    TBNO_DESC=(String)obj.get("TBNO_DESC");
                    RATETYPE_DESC=(String)obj.get("RATETYPE_DESC");
                    SERVICE_TAX_PER=(String)obj.get("SERVICE_TAX_PER");
                    CGST_PER=(String)obj.get("CGST_PER");
                    SGST_PER=(String)obj.get("SGST_PER");
                    TABL_VALUE=(String)obj.get("TABL_VALUE");

                    SharedPreferences pref = getActivity().getSharedPreferences("HOME_DATA", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("TBNO_CODE",TBNO_CODE);
                    editor.putString("TBNO_DESC",TBNO_DESC);
                    editor.putString("RATETYPE_DESC",RATETYPE_DESC);
                    editor.putString("SERVICE_TAX_PER",SERVICE_TAX_PER);
                    editor.putString("CGST_PER",CGST_PER);
                    editor.putString("SGST_PER",SGST_PER);
                    editor.putInt("m_WATRCODE",m_WATRCODE);
                    editor.putInt("m_BRANDCLUBYN",m_BRANDCLUBYN);
                    editor.putInt("m_swap",m_swap);
                    editor.putInt(" m_SWAP_TABLE_YN", m_SWAP_TABLE_YN);
                    editor.putInt(" m_CANCEL_KOT_YN", m_CANCEL_KOT_YN);
                    editor.commit();

                    Intent i=new Intent(getContext(),MenuCard.class);
                    startActivity(i);
                    pd.show();
                }
            });

            super.onPostExecute(s);
        }
    }

    public class table_search extends AsyncTask<String,String,String>
    {
        List<Map<String, String>> prolist  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                Connection con = connectionClass.CONN(con_ipaddress,portnumber);
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    String query = "SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"' AND TBNO_DESC LIKE '"+search_word+"%' ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1)";
                    //String query="SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"' AND TBNO_DESC LIKE '%"+search_word+"%'";
                   /* String query = "SELECT     *\n" +
                            "FROM         tables\n" +
                            "WHERE     (Table_number LIKE '%"+search_word+"%')";*/
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    prolist.clear();
                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {

                        Map<String, String> datanum = new HashMap<String, String>();
                        TBNO_CODE= rs.getString(1);
                        TBNO_DESC= rs.getString(2);
                        RATETYPE_DESC= rs.getString(3);
                        SERVICE_TAX_PER= rs.getString(4);
                        CGST_PER= rs.getString(5);
                        SGST_PER= rs.getString(6);
                        TABL_VALUE= rs.getString("TABL_VALUE");
                        datanum.put("TBNO_CODE", TBNO_CODE);
                        datanum.put("TBNO_DESC", TBNO_DESC);
                        datanum.put("RATETYPE_DESC", RATETYPE_DESC);
                        datanum.put("SERVICE_TAX_PER", SERVICE_TAX_PER);
                        datanum.put("CGST_PER", CGST_PER);
                        datanum.put("SGST_PER", SGST_PER);
                        datanum.put("TABL_VALUE", TABL_VALUE);

                        prolist.add(datanum);



                    }
                    m_swap=prolist.size();
                }

            }catch (Exception e)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            String[] from = {"TBNO_DESC","TABL_VALUE"};
            int[] views = {R.id.txt_d3,R.id.txt_d4};
            final SimpleAdapter ADA = new SimpleAdapter(getActivity(), prolist, R.layout.place_list_test, from, views);
            lstpro.setAdapter(ADA);

            lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pd.show();
                    HashMap<String,Object> obj=(HashMap<String,Object>)ADA.getItem(position);
                    TBNO_CODE=(String)obj.get("TBNO_CODE");
                    TBNO_DESC=(String)obj.get("TBNO_DESC");
                    RATETYPE_DESC=(String)obj.get("RATETYPE_DESC");
                    SERVICE_TAX_PER=(String)obj.get("SERVICE_TAX_PER");
                    CGST_PER=(String)obj.get("CGST_PER");
                    SGST_PER=(String)obj.get("SGST_PER");

                    SharedPreferences pref = getActivity().getSharedPreferences("HOME_DATA", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("TBNO_CODE",TBNO_CODE);
                    editor.putString("TBNO_DESC",TBNO_DESC);
                    editor.putString("RATETYPE_DESC",RATETYPE_DESC);
                    editor.putString("SERVICE_TAX_PER",SERVICE_TAX_PER);
                    editor.putString("CGST_PER",CGST_PER);
                    editor.putString("SGST_PER",SGST_PER);
                    editor.putInt("m_WATRCODE",m_WATRCODE);
                    editor.putInt("m_BRANDCLUBYN",m_BRANDCLUBYN);
                    editor.putInt("m_swap",m_swap);
                    editor.putInt(" m_SWAP_TABLE_YN", m_SWAP_TABLE_YN);
                    editor.putInt(" m_CANCEL_KOT_YN", m_CANCEL_KOT_YN);
                    editor.commit();
                    Intent i=new Intent(getContext(),MenuCard.class);
                    startActivity(i);
                    pd.show();

                }
            });

            super.onPostExecute(s);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {

            Connection con = connectionClass.CONN(con_ipaddress,portnumber);

            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                //String query="select size_code,size_desc from sizemast";

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
        new load_all_table().execute();
        //Table_Fragment rSum = new Table_Fragment();
       // getActivity().getSupportFragmentManager().beginTransaction().remove(rSum).commit();
    }
}
