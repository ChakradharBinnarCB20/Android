package com.example.home;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Other_Stock_Fragment extends Fragment {

    SearchView searchView;
    String SubCodeStr;
    IMEI_Activity connectionClass;
    ProgressBar pbbar;
    HashMap<String, String> map;
    String con_ipaddress ,portnumber;
    ArrayList<HashMap<String, String>> data_list;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    //=====PDF=========================
    PdfPTable table = new PdfPTable(7);
    PdfPCell cell1, cell2,cell3,cell4, cell5,cell6,cell7, cell8,cell9,cell10,cell11,cell12,cell13,cell14;
    File cacheDir;
    final Context context = getActivity();
    private static final int PERMISSION_REQUEST_CODE = 1;
    Button btn_pdf_export;
    public Other_Stock_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_stock_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        data_list = new ArrayList<HashMap<String, String>>();
        searchView = (SearchView) view.findViewById(R.id.report_searchView);
        pbbar = (ProgressBar) view.findViewById(R.id.pgb);
        //btn_pdf_export=(Button)view.findViewById(R.id.btn_pdf_export);
//        btn_pdf_export.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  check_pdf_permission();
//            }
//        });
        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) view.findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getActivity(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        stock_search("");
        //------------------------------------------------------------------------------------------
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                //menu_card_arryList.clear();
                // new Attendance_list().execute();
                // All_menu_search();
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

                    SubCodeStr = newText;
                    SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();
                    //subcodestr = subcodestr.replaceAll("\\s+", "% ").toLowerCase();
                    Log.d("ssss", SubCodeStr);

                    //new FetchSearchResult().execute();
                    stock_search(SubCodeStr);
                } else if (TextUtils.isEmpty(newText)) {
                    // lin_grid_visible.setVisibility(View.INVISIBLE);
                    // menu_card_arryList.clear();
                    // menu_search("");
                } else {
                    stock_search("");
                }
                return false;
            }
        });
    }


    public void stock_search(String SubCodeStr) {
        //SubCodeStr,m_ratetype
        // m_compcode,m_loctcode,m_maintainstockyn,m_gstreverseyn,
        connectionClass = new IMEI_Activity();
        connectionClass = new IMEI_Activity();
        try {
            pbbar.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            Connection con = connectionClass.CONN(con_ipaddress, portnumber);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                PreparedStatement ps = con.prepareStatement("SELECT MENUITEM_DESC,(SELECT LOCT_DESC FROM LOCTMAST WHERE LOCT_CODE=ONLNSTOK.LOCT_CODE) AS LOCT_DESC,CASE WHEN CL_BALANCE = 0 THEN '' ELSE LTRIM(STR(CL_BALANCE)) END AS CL_BALANCE FROM ONLNSTOK,MENUCARDITEMMAST WHERE ONLNSTOK.ITEM_CODE=MENUCARDITEMMAST.MENUITEM_CODE AND ITEM_TYPE=2 AND CL_BALANCE > 0 and MENUITEM_DESC like '" + SubCodeStr + "%' ORDER BY MENUITEM_DESC,LOCT_DESC");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                data_list.clear();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map = new HashMap<String, String>();
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("1", rs.getString(1));
                    map.put("2", rs.getString(2));
                    map.put("3", rs.getString(3));

                    menu_card_arryList.add(map);
                    data_list.add(map);
                }
            }
            pbbar.setVisibility(View.GONE);
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
        public atnds_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_stock_list_, parent, false);
            atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new atnds_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {


            holder.list_item.setText(attendance_list.get(position).get("1"));
            holder.list_size.setText(attendance_list.get(position).get("2"));
            holder.list_menu_type.setText(attendance_list.get(position).get("3"));
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_item, list_size, list_menu_type;


            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_item = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_size = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_menu_type = (TextView) itemView.findViewById(R.id.list_d3);



            }
        }
    }



    //------pdf--------------------
    public void check_pdf_permission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                save_pdf();
            } else {
                requestPermission(); // Code for permission
            }
        } else {

            save_pdf();
            // Toast.makeText(Scan_Master_Reports.this, "Below 23 API Oriented Device....", Toast.LENGTH_SHORT).show();
        }
    }
    //-----------------------------
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void save_pdf()
    {
        //-------------PDF-------------------

        //  String FILE = Environment.getExternalStorageDirectory().toString() +  "report.pdf";
        String FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/stock_rpt.pdf";
        // Create New Blank Document
        Document document = new Document(PageSize.A4);

        // Create Pdf Writer for Writting into New Created Document
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            // Open Document for Writting into document
            document.open();
            // User Define Method
            addTitlePage(document);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Close Document after writting all content
        document.close();

        Toast.makeText(getActivity(), "PDF File is Created."+FILE, Toast.LENGTH_LONG).show();
        //-----------------------------------
    }

    //=============================PDF====================================
// Set PDF document Properties
    public void addTitlePage(Document document) throws DocumentException
    {
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD| Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // Start New Paragraph
        Paragraph prHead = new Paragraph();
        // Set Font in this Paragraph
        prHead.setFont(titleFont);
        // Add item into Paragraph
        prHead.add("OTHER STOCK REPORT\n");
        prHead.add("\n");
        prHead.setAlignment(Element.ALIGN_CENTER);

       /* Paragraph cat = new Paragraph();
        cat.setFont(catFont);
        cat.add("\n");
        cat.add("CUSTOMER REPORT \n");
        cat.add("\n");
        cat.add("Customer Name: "+name+"   "+"Mobile: "+number);
        cat.add("\n");
        cat.add("\n");
        cat.setAlignment(Element.ALIGN_CENTER);*/

        // Add all above details into Document
        document.add(prHead);
        // document.add(cat);
        document.add(table);

        /* Header values*/
        table = new PdfPTable(3);
        cell1 = new PdfPCell(new Phrase("ITEM NAME"));
        cell2 = new PdfPCell(new Phrase("LICT DESC"));
        cell3 = new PdfPCell(new Phrase("CL BALANCE."));
       // cell4 = new PdfPCell(new Phrase("AMOUNT"));
       // cell5 = new PdfPCell(new Phrase("NOTES"));



        cell1.setVerticalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_LEFT);
        cell3.setVerticalAlignment(Element.ALIGN_LEFT);
       // cell4.setVerticalAlignment(Element.ALIGN_LEFT);
        //cell5.setVerticalAlignment(Element.ALIGN_LEFT);


        cell1.setBorder(Rectangle.BOX);
        cell1.setPadding(5);

        cell2.setBorder(Rectangle.BOX);
        cell2.setPadding(5);

        cell3.setBorder(Rectangle.BOX);
        cell3.setPadding(5);
/*
        cell4.setBorder(Rectangle.BOX);
        cell4.setPadding(5);

        cell5.setBorder(Rectangle.BOX);
        cell5.setPadding(5);*/


        cell1.setBackgroundColor(BaseColor.ORANGE);
        cell2.setBackgroundColor(BaseColor.ORANGE);
        cell3.setBackgroundColor(BaseColor.ORANGE);
      //  cell4.setBackgroundColor(BaseColor.ORANGE);
      //  cell5.setBackgroundColor(BaseColor.ORANGE);

        /*//Table values*//**//*
    cell5 = new PdfPCell(new Phrase(b));
    cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
    cell5.setBorder(Rectangle.NO_BORDER);
    cell5.setPadding(5);*/
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
       // table.addCell(cell4);
       // table.addCell(cell5);
        //=================================================================

        for (int k = 0; k < data_list.size(); k++) {
            map = (HashMap) data_list.get(k);

                String id  = map.get("1");;
                String date  = map.get("2");;
                String cat  = map.get("3");;
              //  String amt  =c.getString(3);
              //  String note  =c.getString(4);

              /*  Double pttlamt=Double.parseDouble(amt);
                ptotal=ptotal+pttlamt;
                tot_exp= Double.toString(ptotal);*/

                cell6 = new PdfPCell(new Phrase(id));
                cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell6.setBorder(Rectangle.BOX);
                cell6.setPadding(5);

                cell7 = new PdfPCell(new Phrase(date));
                cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell7.setBorder(Rectangle.BOX);
                cell7.setPadding(5);

                cell8 = new PdfPCell(new Phrase(cat));
                cell8.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell8.setBorder(Rectangle.BOX);
                cell8.setPadding(5);

/*
                cell9 = new PdfPCell(new Phrase(amt));
                cell9.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell9.setBorder(Rectangle.BOX);
                cell9.setPadding(5);

                cell10 = new PdfPCell(new Phrase(note));
                cell10.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell10.setBorder(Rectangle.BOX);
                cell10.setPadding(5);*/



                table.addCell(cell6);
                table.addCell(cell7);
                table.addCell(cell8);
               // table.addCell(cell9);
                //table.addCell(cell10);

            /*table.addCell(cell11);
			table.addCell(cell12);*/

                // add table into document
            }

        document.add(table);

        Paragraph p = new Paragraph();
        p.setFont(catFont);
      //  p.add("                                     Total : "+tot_exp);
       p.add("\n");

        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);

        //===================================================================
        // Create new Page in PDF
        document.newPage();
        //Toast.makeText(this, "PDF File is Created.", Toast.LENGTH_LONG).show();
    }

    //===================================================================
}
