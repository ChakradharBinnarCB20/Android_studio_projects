package com.example.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.HashMap;

public class ReportActivity extends AppCompatActivity {
    //================Recyclerview 2======================
    ArrayList<HashMap<String, String>> bill_arryList;
    private RecyclerView.LayoutManager layoutManager_bill;
    tbill_recyclerAdapter bill_recyclerAdapter;
    private RecyclerView recycler_bill_list;
    Toolbar toolbar;
    ProgressDialog progressDoalog;
    TextView txt_bar_amt,txt_bar_lbl,txt_food_lbl,txt_food_amt,txt_item_wise_amt,txt_gst_amt,txt_service_tax_amt,txt_net_bill_amt;
    IMEI_Activity m_com;
    IMEI_Activity connectionClass;
    String con_ipaddress ,portnumber,doc_no,TBNO_DESC,str_compcode,str_compdesc;
    ProgressBar pbbar;
    float m_compcode;
    HashMap<String, String> map;
    float TBNO_CODE;
    String doc_dt,m_lastdocsrno,flag,query,doc_dt_display,menu_option;
    String m_print_kot_yn;
    String m_print_bill_yn;
    String m_itemcode;
    LinearLayout lin_rpt;
    Double m_foodamt=0.00;
    Double m_baramt =0.00;
    Double m_cgstamt =0.00;
    Double m_sgstamt =0.00;
    Double m_gstamt =0.00;
    Double m_srvctaxamt =0.00;
    Double m_nettamt =0.00;
    Double SERVICE_TAX_PER=0.00;
    Double CGST_PER=0.00;
    Double SGST_PER=0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        SharedPreferences sp11 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp11.getString("ipaddress", "");
        portnumber = sp11.getString("portnumber", "");
        SharedPreferences ss = getSharedPreferences("COMP_CODE", MODE_PRIVATE);
        str_compcode = ss.getString("COMP_CODE", "");
        str_compdesc = ss.getString("COMP_DESC", "");

        m_compcode = Float.parseFloat(str_compcode);



        SharedPreferences s = getSharedPreferences("MENU_DATA", MODE_PRIVATE);
        doc_dt=s.getString("doc_dt","");
        m_lastdocsrno=s.getString("m_lastdocsrno","");
        doc_dt_display=s.getString("doc_dt_display","");
        menu_option=s.getString("menu_option","");
        flag=s.getString("flag","");

        SharedPreferences sp = getSharedPreferences("HOME_DATA", MODE_PRIVATE);
        TBNO_CODE = Float.parseFloat(sp.getString("TBNO_CODE", ""));
        TBNO_DESC = sp.getString("TBNO_DESC", "");
        SERVICE_TAX_PER = Double.parseDouble(sp.getString("SERVICE_TAX_PER", ""));
        CGST_PER = Double.parseDouble(sp.getString("CGST_PER", ""));
        SGST_PER = Double.parseDouble(sp.getString("SGST_PER", ""));

        Bundle b=getIntent().getExtras();
        try
        {
            doc_dt=b.getString("doc_dt");
            m_lastdocsrno=b.getString("m_lastdocsrno");
            doc_dt_display=b.getString("doc_dt_display");
            menu_option=b.getString("menu_option");
            flag=b.getString("flag");
        }catch(Exception e){}
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TextView tbl_bill = (TextView) toolbar.findViewById(R.id.txt_total);//title
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);//date
        TextView txt_option = (TextView) toolbar.findViewById(R.id.txt_option);//menu

        TextView txt_hotel_name = (TextView) toolbar.findViewById(R.id.txt_hotel_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Table : "+TBNO_DESC);
        toolbar_date.setText("Date : "+doc_dt_display);
        txt_option.setText(menu_option);
        txt_hotel_name.setText(str_compdesc);
        txt_hotel_name.setTextColor(0xFFFFFFFF);
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_date.setTextColor(0xFFFFFFFF);
        txt_option.setTextColor(0xFFFFFFFF);
        pbbar = (ProgressBar) findViewById(R.id.pgb);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

            //---------------------Recyclerview 2-----------------------------------------
            bill_arryList = new ArrayList<HashMap<String, String>>();
            recycler_bill_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
            layoutManager_bill = new LinearLayoutManager(ReportActivity.this, RecyclerView.VERTICAL, false);
            recycler_bill_list.setLayoutManager(layoutManager_bill);
            bill_recyclerAdapter = new tbill_recyclerAdapter(ReportActivity.this, bill_arryList);
            recycler_bill_list.setAdapter(bill_recyclerAdapter);

            lin_rpt = (LinearLayout) findViewById(R.id.lin_rpt);
            //txt_tbl_bill = (TextView) findViewById(R.id.txt_tbl_bill);

            txt_food_amt = (TextView) findViewById(R.id.txt_food_amt);
            txt_food_lbl = (TextView) findViewById(R.id.txt_food_lbl);
            txt_bar_amt = (TextView) findViewById(R.id.txt_bar_amt);
            txt_bar_lbl = (TextView) findViewById(R.id.txt_bar_lbl);
            txt_item_wise_amt = (TextView) findViewById(R.id.txt_item_wise_amt);
            txt_gst_amt = (TextView) findViewById(R.id.txt_gst_amt);
            txt_service_tax_amt = (TextView) findViewById(R.id.txt_service_tax_amt);
            txt_net_bill_amt = (TextView) findViewById(R.id.txt_net_bill_amt);
            //------------------------------------------------------------------------------------------
        }

        //flags 1-Latest order  2-Total order  3-Total order(summary)
        //parameter to be pass doc_dt,tbno_code,doc_srno
        if(flag.equals("1"))
        {
            lin_rpt.setVisibility(View.INVISIBLE);
            query="select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,ltrim(str(rate,9,2)) as rate,ltrim(str(item_value,12,2)) as item_value,doc_srno,print_kot_yn,case when bill_no = 0 then 0.0 else 1.0 end as print_yn,LEFT(CONVERT(VARCHAR(10),tran_date,8),5) as entrytime,isnull((select ltrim(str(sum(item_value),12,2)) from countersaleitem a where a.doc_dt = '"+doc_dt+"' and a.comp_code = "+m_compcode+" and a.tbno_code <> 0 and a.tbno_code="+TBNO_CODE+" and a.doc_srno > "+m_lastdocsrno+"),'0.00') as totalvalue,item_code,0.00 as food_amt from countersaleitem where doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+TBNO_CODE+" and doc_srno > "+m_lastdocsrno+" order by doc_srno";
        }
        else if(flag.equals("2")) {
            query="select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,ltrim(str(rate,9,2)) as rate,ltrim(str(item_value,12,2)) as item_value,doc_srno,print_kot_yn,case when bill_no = 0 then 0.0 else 1.0 end as print_yn,LEFT(CONVERT(VARCHAR(10),tran_date,8),5) as entrytime,isnull((select ltrim(str(sum(item_value),12,2)) from countersaleitem a where a.doc_dt = '"+doc_dt+"' and a.comp_code = "+m_compcode+" and a.tbno_code <> 0 and a.tbno_code="+TBNO_CODE+"),'0.00') as totalvalue,item_code,isnull((select ltrim(str(sum(item_value),12,2)) FROM COUNTERSALEITEM A,MENUCARDITEMMAST,MENUMAST WHERE A.doc_dt = '"+doc_dt+"' and A.comp_code = "+m_compcode+" and A.tbno_code <> 0 and A.tbno_code="+TBNO_CODE+" AND A.ITEM_TYPE=3 AND A.ITEM_CODE=MENUCARDITEMMAST.MENUITEM_CODE AND MENUCARDITEMMAST.MENU_CODE=MENUMAST.MENU_CODE AND GST_YN=1),'0.00') AS FOOD_AMT from countersaleitem where doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+TBNO_CODE+" order by doc_srno";
        }
        else
        {
            query="select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(sum(qty),7)) as qty,ltrim(str(sum(item_value)/sum(qty),9,2)) as rate,ltrim(str(sum(item_value),12,2)) as item_value,isnull((select ltrim(str(sum(item_value),12,2)) from countersaleitem a where a.doc_dt = '"+doc_dt+"' and a.comp_code = "+m_compcode+" and a.tbno_code <> 0 and a.tbno_code="+TBNO_CODE+"),'0.00') as totalvalue,'0.0' as print_kot_yn, '0.0' as print_yn,isnull((select ltrim(str(sum(item_value),12,2)) FROM COUNTERSALEITEM A,MENUCARDITEMMAST,MENUMAST WHERE A.doc_dt = '"+doc_dt+"' and A.comp_code = "+m_compcode+" and A.tbno_code <> 0 and A.tbno_code="+TBNO_CODE+" AND A.ITEM_TYPE=3 AND A.ITEM_CODE=MENUCARDITEMMAST.MENUITEM_CODE AND MENUCARDITEMMAST.MENU_CODE=MENUMAST.MENU_CODE AND GST_YN=1),'0.00') AS FOOD_AMT from countersaleitem where doc_dt = '"+doc_dt+"' and comp_code = "+m_compcode+" and tbno_code <> 0 and tbno_code="+TBNO_CODE+" group by item_code,item_type,brnd_code_as order by item_type desc,item_name";
        }
            Log.d("report_query",""+query);
        load_bill_data();
    }

    public void load_bill_data() {
        // ttl=0;
        // bill_arryList.clear();
        progressDoalog = new ProgressDialog(ReportActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        connectionClass = new IMEI_Activity();
        try {
            progressDoalog.dismiss();
            pbbar.setVisibility(View.VISIBLE);
            //bill_arryList.clear();
            Connection con = connectionClass.CONN(con_ipaddress,portnumber);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                //String query = "select case when item_type < 3 and brnd_code_as = 0 then (select brnd_desc+' ['+(select liqr_desc from liqrmast where liqr_code=itemmast.liqr_code)+']' from brndmast,Itemmast where brndmast.brnd_code=itemmast.brnd_code and itemmast.item_code=countersaleitem.item_code and brndmast.brnd_code > 0) when item_type < 3 and brnd_code_as <> 0 then (select brnd_desc from brndmast where countersaleitem.brnd_code_as=brndmast.brnd_code and brndmast.brnd_code > 0 ) else (select MENUITEM_DESC from menucarditemmast where menuitem_code = countersaleitem.item_code) end as item_name,case when item_type < 3 then (select size_desc from sizemast,itemmast where sizemast.size_code=itemmast.size_code and itemmast.item_code=countersaleitem.item_code) else '' end as size_desc,ltrim(str(qty,7)) as qty,ltrim(str(rate,9,2)) as rate,ltrim(str(item_value,12,2)) as item_value,doc_srno,print_kot_yn,print_yn from countersaleitem where doc_dt = '12/21/2019' and comp_code = 1 and tbno_code <> 0 and tbno_code=20 order by doc_srno";
                //String query = "\n" +
                //       "select * from SURESH_TESTING where TBL_NO='"+TBNO_DESC+"'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("ITEM", rs.getString("item_name"));
                    map.put("LSIZE", rs.getString("size_desc"));
                    map.put("QTY", rs.getString("qty"));
                    map.put("RATE", rs.getString("rate"));
                    map.put("VALUE", rs.getString("item_value"));
                    map.put("PRINT_KOT_YN", rs.getString("print_kot_yn"));
                    map.put("PRINT_YN", rs.getString("print_yn"));
                    txt_item_wise_amt.setText(rs.getString("totalvalue"));
                    txt_food_amt.setText(rs.getString("food_amt"));

                    //-------------------------------------------
                    if(!flag.equals("3"))
                    {
                        map.put("DOC_SRNO", rs.getString("doc_srno"));
                        map.put("entrytime", rs.getString("entrytime"));
                        map.put("ITEM_CODE", rs.getString("ITEM_CODE"));
                    }
                    if(!flag.equals("1")) {
                        DecimalFormat df = new DecimalFormat("0.00");

                        m_foodamt = 0.00;
                        m_baramt = 0.00;
                        m_cgstamt = 0.00;
                        m_sgstamt = 0.00;
                        m_gstamt = 0.00;
                        m_srvctaxamt = 0.00;
                        m_nettamt = 0.00;

                        m_baramt = Double.parseDouble(txt_item_wise_amt.getText().toString()) - Double.parseDouble(txt_food_amt.getText().toString());
                        // df.setRoundingMode(RoundingMode.DOWN);
                        //df.setRoundingMode(RoundingMode.UP);
                        txt_bar_amt.setText("" + df.format(m_baramt));
                        m_foodamt = Double.parseDouble(txt_food_amt.getText().toString());
                        m_cgstamt = m_foodamt * CGST_PER / 100;
                        // m_cgstamt = Math.round(m_foodamt * CGST_PER) / 100.0;

                        m_sgstamt = m_foodamt * SGST_PER / 100;
                        //m_sgstamt = Math.round(m_foodamt * SGST_PER) / 100.0;
                        m_gstamt = m_cgstamt + m_sgstamt;
                        txt_gst_amt.setText("" + df.format(m_gstamt));
                        m_nettamt = Double.parseDouble(txt_item_wise_amt.getText().toString()) + m_gstamt;
                        m_srvctaxamt = m_nettamt * SERVICE_TAX_PER / 100;
                        // m_srvctaxamt = Math.round(m_nettamt * SERVICE_TAX_PER) / 100.0;
                        txt_service_tax_amt.setText("" + df.format(m_srvctaxamt));
                        m_nettamt = m_nettamt + m_srvctaxamt;
                        // df.setRoundingMode(RoundingMode.DOWN);
                        txt_net_bill_amt.setText("" + Math.round(m_nettamt));
                    }
                        if(m_baramt==0)
                        {
                            txt_bar_amt.setVisibility(View.INVISIBLE);
                            txt_food_amt.setVisibility(View.INVISIBLE);
                            txt_bar_lbl.setVisibility(View.INVISIBLE);
                            txt_food_lbl.setVisibility(View.INVISIBLE);
                        }
                    //-------------------------------------------
                    bill_arryList.add(map);
                    if(flag.equals("1"))
                    {
                        lin_rpt.setVisibility(View.INVISIBLE);
                    }
                }
            }
            pbbar.setVisibility(View.GONE);
            Log.d("bill_arryList_Data", "" + bill_arryList.toString());

            if (bill_recyclerAdapter != null) {
                bill_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + bill_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }

    public class tbill_recyclerAdapter extends RecyclerView.Adapter<tbill_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public tbill_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            // holder.contact_list_id.setText(attendance_list.get(position).get("A"));
            m_print_kot_yn=attendance_list.get(position).get("PRINT_KOT_YN");
            m_print_bill_yn=attendance_list.get(position).get("PRINT_YN");

            holder.name.setTextColor(Color.BLACK);
            holder.lsize.setTextColor(Color.BLACK);
            holder.qty.setTextColor(Color.BLACK);
            holder.rate.setTextColor(Color.BLACK);
            holder.value.setTextColor(Color.BLACK);
            holder.time.setTextColor(Color.BLACK);
            if(!flag.equals("3")) {
                holder.img_delete.setVisibility(View.VISIBLE);
            }

            if(m_print_bill_yn.equals("1.0"))
            {
                holder.name.setTextColor(Color.RED);
                holder.lsize.setTextColor(Color.RED);
                holder.qty.setTextColor(Color.RED);
                holder.rate.setTextColor(Color.RED);
                holder.value.setTextColor(Color.RED);
                holder.time.setTextColor(Color.RED);
                holder.img_delete.setVisibility(View.INVISIBLE);
            }
            else if(m_print_kot_yn.equals("1.0"))
            {
                holder.name.setTextColor(Color.BLUE);
                holder.lsize.setTextColor(Color.BLUE);
                holder.qty.setTextColor(Color.BLUE);
                holder.rate.setTextColor(Color.BLUE);
                holder.value.setTextColor(Color.BLUE);
                holder.time.setTextColor(Color.BLUE);
                holder.img_delete.setVisibility(View.INVISIBLE);
            }

            holder.name.setText(attendance_list.get(position).get("ITEM"));
            holder.lsize.setText(attendance_list.get(position).get("LSIZE"));
            holder.qty.setText(attendance_list.get(position).get("QTY"));
            holder.rate.setText(attendance_list.get(position).get("RATE"));
            holder.value.setText(attendance_list.get(position).get("VALUE"));
            holder.time.setText(attendance_list.get(position).get("entrytime"));

            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doc_no = attendance_list.get(position).get("DOC_SRNO");
                    m_itemcode = attendance_list.get(position).get("ITEM_CODE");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.delete);
                    builder.setMessage("Are you sure you want to delete?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int m_exit_yn=0;
                            while (true) {
                                try {
                                    Connection con = connectionClass.CONN(con_ipaddress, portnumber);
                                    if (con == null) {
                                        Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                                    } else {
                                        PreparedStatement ps = con.prepareStatement("insert into countersaleitemcancel select * from countersaleitem where doc_srno = "+doc_no+" and doc_dt = '"+doc_dt+"' ");
                                        ps.executeUpdate();
                                        ps = con.prepareStatement("delete from countersaleitem where doc_srno = "+doc_no+" and doc_dt = '"+doc_dt+"' ");
                                        ps.executeUpdate();
                                        ps = con.prepareStatement("delete from liqrrecipetran where doc_no = "+doc_no+" and doc_dt = '"+doc_dt+"' ");
                                        ps.executeUpdate();
                                        //========Procedure Call(9/17/2021)============
                                        ps = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','COUNTERSALEITEM',1,'0',''");
                                        ps.executeUpdate();
                                        ps = con.prepareStatement("EXEC SP_PIGATEWAYUPLOADRECORDS "+doc_no+",'"+doc_dt+"','LIQRRECIPETRAN',1,'0',''");
                                        ps.executeUpdate();

                                        m_exit_yn=1;
                                    }
                                } catch (Exception e) {
                                }
                                if (m_exit_yn==1){
                                    break;
                                }
                            }
                            bill_arryList.remove(position);
                            bill_recyclerAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            Intent i=new Intent(getApplicationContext(),ReportActivity.class);
                            startActivity(i);
                            finish();

                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name, qty, rate, value, lsize,time;
            ImageView img_delete;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.lsize = (TextView) itemView.findViewById(R.id.list_d2);
                this.qty = (TextView) itemView.findViewById(R.id.list_d3);
                this.rate = (TextView) itemView.findViewById(R.id.list_d4);
                this.value = (TextView) itemView.findViewById(R.id.list_d5);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.img_delete = (ImageView) itemView.findViewById(R.id.img_delete);
                this.time = (TextView) itemView.findViewById(R.id.list_d6);

                if(flag.equals("3")) {
                    img_delete.setVisibility(View.INVISIBLE);
                }

            }
        }
    }

}
