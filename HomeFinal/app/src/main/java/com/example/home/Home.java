package com.example.home;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.home.Class.SessionManager;
import com.example.homefinal.R;
import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class Home extends AppCompatActivity {
    int backCount;
    Toolbar mToolBar;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    String imei,id;
    private TextView user_name,txt_imei_num;
    String user,m_compcode,m_compdesc,m_loctcode,m_maintainstockyn,m_gstreverseyn,size_selection_sequence,m_orderbyseq,m_LOOSEFROMSIZEALLYN,m_ipaddress;
    SessionManager sessionManager;
    SharedPreferences sp;
    int m_EVERY_ENTRY_KOT_YN;
    CountDownTimer countDownTimer;
    private ImageView profileImage;
    String con_ipaddress ,portnumber,str_waiter;
    Bitmap bitmap;
    //  String add_phon="9922551001";
    IMEI_Activity  m_com=new IMEI_Activity();
    //ConnectionClass connectionClass;
    IMEI_Activity connectionClass;
    int print_kot_yn=0;
    //FCM
    private static final String TAG = Home.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        sp = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp.getString("ipaddress", "");
        portnumber = sp.getString("portnumber", "");

        SharedPreferences s = getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        str_waiter = s.getString("tab_user_name", "");
        load_data();
      //-------------------------------------------------------------------------------------------
        SharedPreferences sp1 = getSharedPreferences("IMEI", MODE_PRIVATE);
        imei = sp1.getString("imei", "");
        // startTimer();
        //countDownTimer.start();
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

//        if(savedInstanceState == null){
//            getSupportFragmentManager().beginTransaction().replace(R.id.containerView, new Table_Grid_Fragment()).commit();
//        }

        mToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView)findViewById(R.id.navigationDrawer);

        //===============Display name on Navigation Drawer===============

        View header=mNavigationView.getHeaderView(0);

         user_name = (TextView)header.findViewById(R.id.user_name);
         txt_imei_num = (TextView)header.findViewById(R.id.txt_imei_num);
        //  profileImage = (ImageView)header.findViewById(R.id.profileImage);
        //  String nm=user.name;
           user_name.setText("Welcome: "+str_waiter);
           txt_imei_num.setText("Device Id: "+imei);
        //  profileImage.setImageBitmap(bitmap);

        //==================================================================


        mActionBarDrawerToggle = setupDrawerToggle();

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        setupDrawerContent(mNavigationView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

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

        if (id== R.id.home){
            fragmentClass = Table_Grid_Fragment.class;
        } else if (id==R.id.stock) {
            fragmentClass = Brand_Stock_Fragment.class;
        } else if (id==R.id.otherstock) {
            fragmentClass = Other_Stock_Fragment.class;
        }else {
            fragmentClass = Table_Grid_Fragment.class;
        }

//        switch (menuItem.getItemId()){
//
//            case R.id.home:
//                fragmentClass = Table_Grid_Fragment.class;
//                break;
//
//            case R.id.stock:
//                fragmentClass = Brand_Stock_Fragment.class;
//                break;
//            case R.id.otherstock:
//                fragmentClass = Other_Stock_Fragment.class;
//                break;
//
//            default:
//                fragmentClass = Table_Grid_Fragment.class;
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

        if (id== R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (id==R.id.Logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Warning");
            builder.setIcon(R.drawable.exit);
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sessionManager.logoutUser();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("IMEI", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("session","0");
                    editor.commit();
                    finish();

                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();

        } else if (id==R.id.Refresh) {
            Intent i=new Intent(getApplicationContext(),Home.class);
            startActivity(i);
            finish();
        }

//        switch (item.getItemId()) {
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//            case R.id.Logout:
//                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this, R.style.AppCompatAlertDialogStyle);
//                builder.setTitle("Warning");
//                builder.setIcon(R.drawable.exit);
//                builder.setMessage("Are you sure you want to logout?");
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        sessionManager.logoutUser();
//                        SharedPreferences pref = getApplicationContext().getSharedPreferences("IMEI", MODE_PRIVATE); // 0 - for private mode
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.putString("session","0");
//                        editor.commit();
//                        finish();
//
//                    }
//                });
//                builder.setNegativeButton("Cancel", null);
//                builder.show();
//                return true;
//
//            case R.id.Refresh:
//                 Intent i=new Intent(getApplicationContext(),Home.class);
//                 startActivity(i);
//                 finish();
//                return true;
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        load_data();

    }

    public void load_data()
    {



        //-----------------------------------------
        try
        {
                m_ipaddress = m_com.M_get_string("SELECT IP_ADDRESS FROM IPADMAST WHERE IPAD_DESC = 'PRANALISERVER'",con_ipaddress,portnumber);
            if(m_ipaddress.length()>0)
            {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("IPADDRESS", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("IPADDRESS",m_ipaddress);
                editor.commit();

            }
            else
            {
                Toast.makeText(this, "IPADDRESS not found..", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e)
        {
        }
        //-----------------------------------------
        try
        {
            m_compdesc = m_com.M_get_string("select COMP_DESC + ', ' + CITY AS COMP_DESC from COMPMAST",con_ipaddress,portnumber);
            m_compcode = m_com.M_get_string("select COMP_CODE from COMPMAST",con_ipaddress,portnumber);
            if(m_compcode.length()>0)
            {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("COMP_CODE", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("COMP_CODE",m_compcode);
                editor.putString("COMP_DESC",m_compdesc);
                editor.commit();

            }
            else
            {
                Toast.makeText(this, "COMP_CODE not found..", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e)
        {
        }

        //===========ALL==============================
        try
        {
            connectionClass = new IMEI_Activity();
            Connection con = connectionClass.CONN(con_ipaddress,portnumber);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {

                String query = "select SALE_LOCT_CODE,MAINTAIN_STOCK,GST_REVERSE_YN ,size_selection_sequence,LOOSE_FROM_SIZE_ALL_YN,print_kot_yn,EVERY_ENTRY_KOT_YN from PROFILE";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    m_EVERY_ENTRY_KOT_YN= rs.getInt("EVERY_ENTRY_KOT_YN");
                    print_kot_yn= rs.getInt("print_kot_yn");
                    m_loctcode= rs.getString("SALE_LOCT_CODE");
                    m_maintainstockyn= rs.getString("MAINTAIN_STOCK");
                    m_gstreverseyn= rs.getString("GST_REVERSE_YN");
                    m_LOOSEFROMSIZEALLYN= rs.getString("LOOSE_FROM_SIZE_ALL_YN");
                    size_selection_sequence= rs.getString("size_selection_sequence");
                    if(size_selection_sequence.equals("1.0"))
                    {
                        m_orderbyseq="Brnd_desc,seq_no desc";
                    }
                    else
                    {
                        m_orderbyseq="Brnd_desc";
                    }

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("Profile_data", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("print_kot_yn",print_kot_yn);
                    editor.putString("m_loctcode",m_loctcode);
                    editor.putString("m_maintainstockyn",m_maintainstockyn);
                    editor.putString("m_gstreverseyn",m_gstreverseyn);
                    editor.putString("m_orderbyseq",m_orderbyseq);
                    editor.putString("m_LOOSEFROMSIZEALLYN",m_LOOSEFROMSIZEALLYN);
                    editor.putInt("m_EVERY_ENTRY_KOT_YN",m_EVERY_ENTRY_KOT_YN);
                    editor.commit();

                }
            }

        }catch(Exception e)
        {
            Toast.makeText(this, "Error.."+e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

    }
}

