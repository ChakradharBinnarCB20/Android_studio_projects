package com.example.admin_beerbar_new;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.example.admin_beerbar_new.Class.TransparentProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class Customer_ledger_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    ImageView img_frm_date,img_to_date;
    TextView edt_frm_date,edt_to_date;
    DatePickerDialog datePickerDialog;
    SearchView searchView;
    Button btn_report;
    String SubCodeStr;
    ProgressBar pgb;
    PreparedStatement ps1;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "WINESHOP";
    // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="",db;
    TransparentProgressDialog pd;
    DecimalFormat df2;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    int m_compcode,m_TAB_CODE;
    double m_clbal,cbal;
    String IMEINumber,con_ipaddress,portnumber,str_ac_head_id;
    public Customer_ledger_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_ledger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences ss = getActivity().getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

        pgb=(ProgressBar)view.findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) view.findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getActivity(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
       // report_search("");
        //------------------------------------------------------------------------------------------
        searchView=(SearchView)view.findViewById(R.id.report_searchView);
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

                    SubCodeStr = newText;
                   // SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();
                    //subcodestr = subcodestr.replaceAll("\\s+", "% ").toLowerCase();
                    Log.d("ssss", SubCodeStr);

                    //new FetchSearchResult().execute();
                    report_search(SubCodeStr);
                } else if (TextUtils.isEmpty(newText)) {
                    // lin_grid_visible.setVisibility(View.INVISIBLE);
                    // menu_card_arryList.clear();
                    // menu_search("");
                } else {
                    report_search("");
                }
                return false;
            }
        });


        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        edt_frm_date=(TextView) view.findViewById(R.id.edt_frm_date);
        edt_to_date=(TextView) view.findViewById(R.id.edt_to_date);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        formattedDate = df.format(c);
        edt_to_date.setText(formattedDate);
        edt_frm_date.setText(formattedDate);
        Date  d = Calendar.getInstance().getTime();

        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Temp_frm_date=out.format(d);

        Date dd = Calendar.getInstance().getTime();
        SimpleDateFormat ot = new SimpleDateFormat("MM/dd/yyyy");
        Temp_to_date=ot.format(dd);

        edt_frm_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edt_frm_date.setText(""+str_day + "/" + str_month + "/" + year);
                                Temp_frm_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                //edt_frm_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        edt_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edt_to_date.setText(""+str_day + "/" + str_month + "/" + year);
                                Temp_to_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                //edt_to_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);

                try {
                    //String sDate1="12/02/2020";
                    String sDate1=edt_frm_date.getText().toString();
                    Date date=new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
                    datePickerDialog.getDatePicker().setMinDate(date.getTime());
                }catch (Exception e)
                {

                }

                //datePickerDialog.getDatePicker().setMinDate(d.getTime());
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        report_search("");
    }

    public void report_search(String SubCodeStr) {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                PreparedStatement ps = con.prepareStatement("select glmast.ac_head_id,glmast.gl_desc,rtrim(party_add1)+','+rtrim(party_add2)+','+rtrim(party_add3) as address,plac_desc From glmast, placmast Where ac_head_id in(select ac_head_id from sales where doc_dt <= '"+Temp_to_date+"' and ac_head_id<>0 and comp_code= "+m_compcode+" union select ac_head_id from CUSTWISEOPENINGBAL where comp_code= "+m_compcode+" union select ac_head_id from dailyrcp where doc_dt <= '"+Temp_to_date+"' and comp_code= "+m_compcode+")and glmast.gl_desc like '"+SubCodeStr+"%"+"' and glmast.plac_code=placmast.plac_code order by gl_desc");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("ac_head_id", rs.getString("ac_head_id"));
                    map.put("gl_desc", rs.getString("gl_desc"));
                    map.put("address", rs.getString("address"));
                    map.put("plac_desc", rs.getString("plac_desc"));

                    menu_card_arryList.add(map);
                }
            }
            pgb.setVisibility(View.GONE);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {


           // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.list_d1.setText(attendance_list.get(position).get("gl_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("address"));
            holder.list_d3.setText(attendance_list.get(position).get("plac_desc"));
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            df2 = new DecimalFormat("#.##");
                            str_ac_head_id=attendance_list.get(position).get("ac_head_id");
                            insert_data(Temp_frm_date,Temp_to_date);
                            Intent i=new Intent(getActivity(),Customer_ledger_Report.class);
                            i.putExtra("from_date", edt_frm_date.getText().toString());
                            i.putExtra("to_date", edt_to_date.getText().toString());
                            i.putExtra("m_clbal", df2.format(m_clbal));
                            i.putExtra("forname", attendance_list.get(position).get("gl_desc"));

                            startActivity(i);
                            pd.dismiss();
                        }
                    }, 5000);


                }
            });

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3, list_d4, list_item_type;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                //this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);

            }
        }
    }

    public void insert_data(String frm_date,String to_date)
    {
        //===========ALL==============================
        try
        {
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {

                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code= "+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("select ltrim(str(opening_bal,12,2)) as amount from custwiseopeningbal where ac_head_id = "+str_ac_head_id+" and comp_code="+m_compcode+"");
                ResultSet rs = ps1.executeQuery();
                m_clbal=0;
                while (rs.next()) {
                    m_clbal=  rs.getDouble("amount");
                   // m_clbal= m_clbal + rs.getDouble("amount");
                }

                ps1 = con.prepareStatement("select isnull(sum(net_amount),0) as amount from sales where doc_dt < '"+Temp_frm_date+"' and ac_head_id = "+str_ac_head_id+" and comp_code="+m_compcode+"");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_clbal= m_clbal + rs.getDouble("amount");
                }

                ps1 = con.prepareStatement("select isnull(sum(amount),0) as amount from dailyexp where doc_dt < '"+Temp_frm_date+"' and ac_head_id = "+str_ac_head_id+" and comp_code="+m_compcode+"");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_clbal= m_clbal + rs.getDouble("amount");
                }

                ps1 = con.prepareStatement("select isnull(sum(paid_amount),0) as amount from sales where doc_dt < '"+Temp_frm_date+"' and ac_head_id = "+str_ac_head_id+" and comp_code="+m_compcode+"");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_clbal= m_clbal - rs.getDouble("amount");
                }

                ps1 = con.prepareStatement("select isnull(sum(amount),0)as amount from dailyrcp where doc_dt < '"+Temp_frm_date+"' and ac_head_id = "+str_ac_head_id+" and comp_code="+m_compcode+"");
                rs = ps1.executeQuery();
                while (rs.next()) {
                    m_clbal= m_clbal - rs.getDouble("amount");
                }


                ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("insert into tabreportparameters (doc_no,doc_dt,amount,amount_1,amount_2,amount_3,tab_code,crdr_cd)select doc_no,doc_dt,amount,dis_amount,net_amount,paid_amount,"+m_TAB_CODE+",'D' from sales where doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and ac_head_id = " +str_ac_head_id+" and comp_code="+m_compcode+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("insert into tabreportparameters (doc_no,doc_dt,amount,tab_code,crdr_cd,doc_type,gl_desc)select doc_no,doc_dt,amount,"+m_TAB_CODE+",'D','CP','CASH PAID' from dailyexp where doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and ac_head_id = " +str_ac_head_id+" and comp_code="+m_compcode+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,amount_3,tab_code,crdr_cd)select doc_dt,amount,"+m_TAB_CODE+",'C' from dailyrcp where doc_dt between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and ac_head_id = " +str_ac_head_id+" and comp_code="+m_compcode+"");
                ps1.executeUpdate();
                //---------------------------------

                //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
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
