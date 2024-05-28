package com.example.admin_beerbar_new;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.example.admin_beerbar_new.Class.SessionManager;

public class Home extends AppCompatActivity {
    int backCount;
    Toolbar mToolBar;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    String db;
    String user;
    SessionManager sessionManager;
    SharedPreferences sp;
    String con_ipaddress ,portnumber,user_name,m_comp_desc,password,mob;
    int m_comp_code;
    String IMEINumber;
    Config connectionClass;
    Connection con;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        sp = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp.getString("ipaddress", "");
        db = sp.getString("db", "");
        portnumber = sp.getString("portnumber", "");

        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        user_name = sp.getString("user_name", "");
        mob = sp.getString("mac_id", "");
        password = sp.getString("password", "");
        try
        {
            connectionClass = new Config();
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if(con==null)
            {
              /*  Toast.makeText(getApplicationContext(), "Invalid Ip Address", Toast.LENGTH_LONG).show();
                String packageName = getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);*/
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Connection Error..!");
                builder.setIcon(R.drawable.warn);
                builder.setMessage("Please Check Your Ip Address Connection First Then Try Again..");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //sessionManager.logoutUser();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("session","0");
                        editor.putString("flag","1");
                        editor.putString("mob",mob);
                        editor.putString("pass",password);
                        editor.commit();
                        Intent i=new Intent(getApplicationContext(),Login.class);
                        startActivity(i);

                        finish();

                    }
                });
                // builder.setNegativeButton("Cancel", null);
                builder.show();

            }
        }catch (Exception e)
        {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
        try {
            connectionClass = new Config();
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Connection Error..!");
                builder.setIcon(R.drawable.warn);
                builder.setMessage("Please Check Your Ip Address Connection First Then Try Again..");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //sessionManager.logoutUser();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("session","0");
                        editor.putString("flag","1");
                        editor.putString("mob",mob);
                        editor.putString("pass",password);
                        editor.commit();
                        Intent i=new Intent(getApplicationContext(),Login.class);
                        startActivity(i);
                       /* Intent i=new Intent(getApplicationContext(),Login.class);
                        i.putExtra("flag","0");
                        i.putExtra("mob",mob);
                        i.putExtra("pass",password);
                        startActivity(i);*/
                         finish();

                    }
                });
               // builder.setNegativeButton("Cancel", null);
                builder.show();
               // builder.setCanceledOnTouchOutside(false);
                //Intent i=new Intent(getApplicationContext(),IPAdderss_Activity.class);
                //startActivity(i);
               // Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                String q="select comp_desc,comp_code from compmast";

                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                while(rs.next())
                {
                    m_comp_desc=rs.getString("comp_desc");
                    m_comp_code=rs.getInt("comp_code");
                    Log.d("m_comp_desc",m_comp_desc);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("COMP_DESC", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("COMP_DESC",m_comp_desc);
                    editor.putInt("COMP_CODE",m_comp_code);
                    editor.commit();
                }
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
        }

        sessionManager = new SessionManager(this);
        //------------------------User Session------------------------------------------
        Bundle b=getIntent().getExtras();
        try
        {
            user=b.getString("email");
        }
        catch (Exception e)
        {
        }
        if(user==null)
        {
            // Toast.makeText(getApplicationContext(),"User Id Null...",Toast.LENGTH_LONG).show();
        }
        else
        {
            sp=this.getSharedPreferences("PI", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("email",user);
            editor.commit();
        }
        //-------------------------------------------------------------------

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.containerView,new WelcomeFragment()).commit();
        }

        mToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView)findViewById(R.id.navigationDrawer);

        //===============Display name on Navigation Drawer===============

        View header=mNavigationView.getHeaderView(0);

       // user_name = (TextView)header.findViewById(R.id.user_name);
       // txt_imei_num = (TextView)header.findViewById(R.id.txt_imei_num);
        //  profileImage = (ImageView)header.findViewById(R.id.profileImage);
        //  String nm=user.name;
       // user_name.setText("Welcome: "+str_waiter);
        //txt_imei_num.setText("MAC Address: "+imei);
        //  profileImage.setImageBitmap(bitmap);

        //==================================================================
        
        mActionBarDrawerToggle = setupDrawerToggle();

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        setupDrawerContent(mNavigationView);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private ActionBarDrawerToggle setupDrawerToggle(){

        return new ActionBarDrawerToggle(this,mDrawerLayout,mToolBar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
    }
    private void setupDrawerContent(NavigationView mNavigationView){
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem){

        Fragment fragment = null;

        Class fragmentClass;

        int id = menuItem.getItemId();

        if (id == R.id.text_home) {
            fragmentClass = HomeFragment.class;

        } else if (id==R.id.stock) {
            fragmentClass = Closing_Stock_Liquor_Fragment.class;

        }else if (id==R.id.stock_other) {
            fragmentClass = Closing_Stock_Other_Fragment.class;

        }else if (id==R.id.cust_ledger) {
            fragmentClass = Customer_ledger_Fragment.class;

        }else if (id==R.id.daily_cash_flow) {
            fragmentClass = Daily_Cash_Flow_Fragment.class;

        }else if (id==R.id.item_wise_ledger) {
            fragmentClass = Item_Wise_Ledger_Fragment.class;

        }else if (id==R.id.item_wise_ledger_liquor) {
            fragmentClass = Item_Wise_Ledger_Liquor_Fragment.class;

        }else if (id==R.id.purchase_register) {
            fragmentClass = Purchase_Register_Value_Wise_Fragment.class;

        }else if (id==R.id.purchase_register_other) {
            fragmentClass = Purchase_Register_Value_Wise_Other_Fragment.class;

        }else if (id==R.id.chalan_value_wise) {
            fragmentClass = Chalan_Value_Wise_Fragment.class;

        }else if (id==R.id.pur_supl_smry) {
            fragmentClass = Purchase_Supplier_Summary_Fragment.class;

        }else if (id==R.id.pur_supl_smry_other) {
            fragmentClass = Purchase_Supplier_Summary_Other_Fragment.class;

        }else if (id==R.id.chalan_supl_smry) {
            fragmentClass = Chalan_Supplier_Summary_Fragment.class;

        }else if (id==R.id.date_wise_cash_withdrawal_report) {
            fragmentClass = Date_Wise_Cash_Withdrawal_Fragment.class;

        }else if (id==R.id.date_wise_receipts_and_expense_report) {
            fragmentClass = Date_Wise_Receipts_and_Expense_Fragment.class;

        }else if (id==R.id.food_group_total_report) {
            fragmentClass = Food_Group__Total_Fragment.class;

        }else if (id==R.id.liquor_group_total_report) {
            fragmentClass = Liquor_Group_Total_Fragment.class;

        }else if (id==R.id.date_wise_transfer_note_liquor) {
            fragmentClass = Datewise_Transfer_Note_Liquer_Fragment.class;

        }else if (id==R.id.date_wise_transfer_note_other) {
            fragmentClass = Datewise_Transfer_Note_Other_Fragment.class;

        }else if (id==R.id.section_wise_summary) {
            fragmentClass = Section_Wise_Summary_Fragment.class;

        }else if (id==R.id.waiter_wise_summary) {
            fragmentClass = Waiter_Wise_Summary_Fragment.class;

        }else if (id==R.id.customer_wise_summary) {
            fragmentClass = Customer_Wise_Summary_Fragment.class;

        }else if (id==R.id.item_stock_other) {
            fragmentClass = Item_Wise_Stock_Other_Fragment.class;

        }else if (id==R.id.item_stock_liquor) {
            fragmentClass = Item_Wise_Stock_liquor_Fragment.class;

        }else if (id==R.id.table_list) {
            fragmentClass = Table_Grid_Fragment.class;

        }else if (id==R.id.bill_wise_value_wise_daily_monthly_smry) {
            fragmentClass = Bill_Value_Wise_Daily_Monthly_Summary_Fragment.class;

        }else {
            fragmentClass = WelcomeFragment.class;
        }


//        switch (menuItem.getItemId()){
//
//            case android.R.id.home:
//                fragmentClass = HomeFragment.class;
//                break;
//           case R.id.stock:
//                fragmentClass = Closing_Stock_Liquor_Fragment.class;
//                break;
//            case R.id.stock_other:
//                fragmentClass = Closing_Stock_Other_Fragment.class;
//                break;
//
//          /*  case R.id.cust_wise_summary:
//                fragmentClass = Customer_Wise_Summary_Fragment.class;
//                break;*/
//
//            case R.id.cust_ledger:
//                fragmentClass = Customer_ledger_Fragment.class;
//                break;
//
//            case R.id.daily_cash_flow:
//                fragmentClass = Daily_Cash_Flow_Fragment.class;
//                break;
//
//            case R.id.item_wise_ledger:
//                fragmentClass = Item_Wise_Ledger_Fragment.class;
//                break;
//
//            case R.id.item_wise_ledger_liquor:
//                fragmentClass = Item_Wise_Ledger_Liquor_Fragment.class;
//                break;
//
//            case R.id.purchase_register:
//                fragmentClass = Purchase_Register_Value_Wise_Fragment.class;
//                break;
//            case R.id.purchase_register_other:
//                fragmentClass = Purchase_Register_Value_Wise_Other_Fragment.class;
//                break;
//            case R.id.chalan_value_wise:
//                fragmentClass = Chalan_Value_Wise_Fragment.class;
//                break;
//            case R.id.pur_supl_smry:
//                fragmentClass = Purchase_Supplier_Summary_Fragment.class;
//                break;
//            case R.id.pur_supl_smry_other:
//                fragmentClass = Purchase_Supplier_Summary_Other_Fragment.class;
//                break;
//            case R.id.chalan_supl_smry:
//                fragmentClass = Chalan_Supplier_Summary_Fragment.class;
//                break;
//          /*  case R.id.date_brand_wise_sale_report:
//                fragmentClass = Date_Brand_Wise_Sale_Fragment.class;
//                break;
//            case R.id.scan:
//                fragmentClass = Online_Scanning_Fragment.class;
//                break;*/
//            case R.id.date_wise_cash_withdrawal_report:
//                fragmentClass = Date_Wise_Cash_Withdrawal_Fragment.class;
//                break;
//            case R.id.date_wise_receipts_and_expense_report:
//                fragmentClass = Date_Wise_Receipts_and_Expense_Fragment.class;
//                break;
//            case R.id.food_group_total_report:
//                fragmentClass = Food_Group__Total_Fragment.class;
//                break;
//            case R.id.liquor_group_total_report:
//                fragmentClass = Liquor_Group_Total_Fragment.class;
//                break;
//            case R.id.date_wise_transfer_note_liquor:
//                fragmentClass = Datewise_Transfer_Note_Liquer_Fragment.class;
//                break;
//            case R.id.date_wise_transfer_note_other:
//                fragmentClass = Datewise_Transfer_Note_Other_Fragment.class;
//                break;
//            case R.id.section_wise_summary:
//                fragmentClass = Section_Wise_Summary_Fragment.class;
//                break;
//            case R.id.waiter_wise_summary:
//                fragmentClass = Waiter_Wise_Summary_Fragment.class;
//                break;
//            case R.id.customer_wise_summary:
//                fragmentClass = Customer_Wise_Summary_Fragment.class;
//                break;
//            case R.id.item_stock_other:
//                fragmentClass = Item_Wise_Stock_Other_Fragment.class;
//                break;
//            case R.id.item_stock_liquor:
//                fragmentClass = Item_Wise_Stock_liquor_Fragment.class;
//                break;
//            case R.id.table_list:
//                fragmentClass = Table_Grid_Fragment.class;
//                break;
//            case R.id.bill_wise_value_wise_daily_monthly_smry:
//                fragmentClass = Bill_Value_Wise_Daily_Monthly_Summary_Fragment.class;
//                break;
//            case R.id.chalan:
//                fragmentClass = Chalan_Fragment.class;
//                break;
//            default:
//                fragmentClass = WelcomeFragment.class;
//
//        }

        try{
            fragment = (Fragment) fragmentClass.newInstance();
        }catch (Exception ex){
            ex.printStackTrace();
        }


        FragmentManager fragmentManager = getSupportFragmentManager();

        //-------------BackStack------------------------
        fragmentManager.beginTransaction().replace(R.id.containerView,fragment).addToBackStack("Tag").commit();


        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);
        // Set action bar title
        getSupportActionBar().setTitle(menuItem.getTitle());

        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.

        int id = item.getItemId();

        if (id==R.id.text_home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (id==R.id.Logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Warning");
            // builder.setIcon(R.drawable.exit);
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // sessionManager.logoutUser();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("session","0");
                    editor.putString("flag","1");
                    editor.putString("mob",mob);
                    editor.putString("pass",password);
                    editor.commit();
                    Intent i=new Intent(getApplicationContext(),Login.class);
                    startActivity(i);
                    finish();
                       /* Intent i=new Intent(getApplicationContext(),Login.class);
                        i.putExtra("flag","0");
                        i.putExtra("mob",mob);
                        i.putExtra("pass",password);
                        startActivity(i);*/
                    // finish();

                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
            return true;
            
        }


//        switch (item.getItemId()) {
//           case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//            case R.id.Logout:
//                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this, R.style.AppCompatAlertDialogStyle);
//                builder.setTitle("Warning");
//               // builder.setIcon(R.drawable.exit);
//                builder.setMessage("Are you sure you want to logout?");
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                       // sessionManager.logoutUser();
//                        SharedPreferences pref = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.putString("session","0");
//                        editor.putString("flag","1");
//                        editor.putString("mob",mob);
//                        editor.putString("pass",password);
//                        editor.commit();
//                        Intent i=new Intent(getApplicationContext(),Login.class);
//                        startActivity(i);
//                        finish();
//                       /* Intent i=new Intent(getApplicationContext(),Login.class);
//                        i.putExtra("flag","0");
//                        i.putExtra("mob",mob);
//                        i.putExtra("pass",password);
//                        startActivity(i);*/
//                       // finish();
//
//                    }
//                });
//                builder.setNegativeButton("Cancel", null);
//                builder.show();
//                return true;
//
//          /*  case R.id.Refresh:
//                Intent i=new Intent(getApplicationContext(),Home.class);
//                startActivity(i);
//                finish();
//                return true;*/
//
//
//
//        }

        if (mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    //-------------Backstack---------------------------------------
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        FragmentManager fragmentManager = getSupportFragmentManager();
        backCount = fragmentManager.getBackStackEntryCount();
        System.out.println("back count= " + backCount);


        if (backCount == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Warning");
            builder.setMessage("Are you sure you want to exit?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        } else if (backCount == 1) {
            fragmentManager.popBackStack();
            getSupportActionBar().setTitle("Home");

        } else if (backCount > 1) {

            for (int i = fragmentManager.getBackStackEntryCount(); i >= 1; i--) {
                fragmentManager.popBackStack();
                System.out.println("back count loop=" + fragmentManager.getBackStackEntryCount());
            }
            getSupportActionBar().setTitle("Home");
//-------------------------------------------------------------------------------------------------------
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

    }
}

