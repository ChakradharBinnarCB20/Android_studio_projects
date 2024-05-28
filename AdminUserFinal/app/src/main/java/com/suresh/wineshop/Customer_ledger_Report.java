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
import java.util.ArrayList;
import java.util.HashMap;

public class Customer_ledger_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,from_date,to_date;
    TextView txt_total;
    Double total=0.0;
    Double ttl=0.0;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String IMEINumber,cdate;
    int m_TAB_CODE;
    double m_clbal;
    double debit_total=0.00;
    double credit_total=0.00;
    TextView txt_debit_total,txt_credit_total;
    String mclbal,forname,db;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_ledger_report);
        txt_debit_total=(TextView)findViewById(R.id.txt_debit_total);
        txt_credit_total=(TextView)findViewById(R.id.txt_credit_total);
        Bundle bd = getIntent().getExtras();
        try {
            from_date = bd.getString("from_date");
            to_date = bd.getString("to_date");
            mclbal = (bd.getString("m_clbal"));
            forname = (bd.getString("forname"));
            Log.d("m_clbal",mclbal);


        } catch (Exception e) {
        }
        m_clbal=Double.parseDouble(mclbal);
        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");

        SharedPreferences sp =getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        TextView toolbar_for_name = (TextView) toolbar.findViewById(R.id.toolbar_for_name);//title
        TextView toolbar_op_bal = (TextView) toolbar.findViewById(R.id.toolbar_op_bal);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Customer Wise Ledger");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(to_date);
        toolbar_to_date.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(from_date);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);

        toolbar_for_name.setText(forname);
        toolbar_for_name.setTextColor(0xFFFFFFFF);
        NumberFormat nf = new DecimalFormat(".00");
        toolbar_op_bal.setText(""+nf.format(m_clbal));
        toolbar_op_bal.setTextColor(0xFFFFFFFF);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
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
        txt_total = (TextView) findViewById(R.id.txt_total);

        connectionClass = new Config();
        try {
            debit_total=0.00;
            credit_total=0.00;
            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else
            {

                //PreparedStatement ps = con.prepareStatement("select (convert(varchar(10),doc_dt,103))docdt,amount,crdr_cd,doc_type from tabreportparameters where TAB_CODE = "+m_TAB_CODE+" order by doc_dt,doc_no,crdr_cd");
                PreparedStatement ps = con.prepareStatement("SELECT CONVERT(VARCHAR(10),DOC_DT,103) AS DOCDT,doc_type,CASE WHEN CRDR_CD = 'D' THEN LTRIM(STR(AMOUNT,12,2)) ELSE '' END AS DEBIT, CASE WHEN CRDR_CD = 'C' THEN LTRIM(STR(AMOUNT,12,2)) ELSE '' END AS CREDIT,crdr_cd FROM TABREPORTPARAMETERS WHERE TAB_CODE="+m_TAB_CODE+"  order by doc_dt,doc_no,crdr_cd");
                ResultSet rs = ps.executeQuery();

                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("date", rs.getString("DOCDT"));
                    map.put("type", rs.getString("DOC_TYPE"));
                    map.put("debit", rs.getString("DEBIT"));
                    map.put("credit", rs.getString("CREDIT"));
                    if (rs.getString("crdr_cd").equals("D")){
                        m_clbal=m_clbal+rs.getDouble("DEBIT");
                        debit_total=debit_total+rs.getDouble("DEBIT");
                    }
                    else {
                        m_clbal=m_clbal-rs.getDouble("CREDIT");
                        credit_total=credit_total+rs.getDouble("CREDIT");
                    }
                    NumberFormat nff = new DecimalFormat(".00");
                    map.put("CLBLAL", ""+nff.format(m_clbal));

                    menu_card_arryList.add(map);
                }
                NumberFormat nff = new DecimalFormat(".00");
                txt_credit_total.setText(""+nff.format(credit_total));
                txt_debit_total.setText(""+nff.format(debit_total));
            }
            pbbar.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }
            con.close();
        } catch (Exception e) {
           // Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_ledger_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("type"));
            holder.list_d2.setText(attendance_list.get(position).get("date"));
            holder.list_d3.setText(attendance_list.get(position).get("debit"));
            holder.list_d4.setText(attendance_list.get(position).get("credit"));
            holder.list_d5.setText(attendance_list.get(position).get("CLBLAL"));

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3,list_d4,list_d5;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);

            }
        }
    }

    
}
