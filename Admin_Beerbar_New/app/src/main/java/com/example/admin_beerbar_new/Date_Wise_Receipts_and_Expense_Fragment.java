package com.example.admin_beerbar_new;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.admin_beerbar_new.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class Date_Wise_Receipts_and_Expense_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date;
    int m_TAB_CODE;
    String con_ipaddress ,portnumber, str_month="",str_day="",db;
    ProgressBar pbbar;
    ProgressDialog progressDoalog;
    TransparentProgressDialog pd;
    Button btn_proceed,btn_report,btn_check_all;
    String IMEINumber;
    int m_compcode;

    //--------------------------
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    //String db = "WINESHOP";
   // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    ProgressBar progressBar;
    PreparedStatement ps1;
    String Temp_frm_date,Temp_to_date,formattedDate;

    RadioButton radio_reciepts,radio_expense,radio_detail,radio_summary;
    LinearLayout lin_radio_hide;
    public Date_Wise_Receipts_and_Expense_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.date_wise_receipts_and_expense, container, false);
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
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        pbbar = (ProgressBar) view.findViewById(R.id.pgb);
        lin_radio_hide = (LinearLayout) view.findViewById(R.id.lin_radio_hide);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar_cyclic);
        radio_reciepts=(RadioButton) view.findViewById(R.id.radio_reciepts);
        radio_reciepts.setChecked(true);
        radio_expense=(RadioButton)view.findViewById(R.id.radio_expense);
        radio_detail=(RadioButton)view.findViewById(R.id.radio_detail);
        radio_detail.setChecked(true);
        radio_summary=(RadioButton)view.findViewById(R.id.radio_summary);

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
                }catch (Exception e)  {     }

                //datePickerDialog.getDatePicker().setMinDate(d.getTime());
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

       /* btn_proceed=(Button)view.findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_check_all.setVisibility(View.VISIBLE);
                lin_radio_hide.setVisibility(View.VISIBLE);
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                       // insert_data(Temp_frm_date, Temp_to_date);

                        pd.dismiss();
                    }
                }, 3000);

            }
        });*/

        btn_report=(Button)view.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // progressBar.setVisibility(View.VISIBLE);
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        try {

                            if (radio_reciepts.isChecked() == true && radio_detail.isChecked() == true) {
                                Intent i=new Intent(getActivity(),Date_Wise_Reciept_Report.class);
                                i.putExtra("dfrm_date",edt_frm_date.getText().toString());
                                i.putExtra("dto_date",edt_to_date.getText().toString());
                                i.putExtra("frm_date",Temp_frm_date);
                                i.putExtra("to_date",Temp_to_date);
                                startActivity(i);
                            }
                            if (radio_reciepts.isChecked() == true && radio_summary.isChecked() == true) {
                                Intent i=new Intent(getActivity(),Date_Wise_Receipts_Summary_Report.class);
                                i.putExtra("dfrm_date",edt_frm_date.getText().toString());
                                i.putExtra("dto_date",edt_to_date.getText().toString());
                                i.putExtra("frm_date",Temp_frm_date);
                                i.putExtra("to_date",Temp_to_date);
                                startActivity(i);

                            }

                            if (radio_expense.isChecked() == true && radio_detail.isChecked() == true) {
                                Intent i=new Intent(getActivity(),Date_Wise_Expense_Report.class);

                                i.putExtra("dfrm_date",edt_frm_date.getText().toString());
                                i.putExtra("dto_date",edt_to_date.getText().toString());
                                i.putExtra("frm_date",Temp_frm_date);
                                i.putExtra("to_date",Temp_to_date);

                                startActivity(i);
                            }
                            if (radio_expense.isChecked() == true && radio_summary.isChecked() == true) {
                                Intent i=new Intent(getActivity(),Date_Wise_Expense_Summary_Report.class);
                                i.putExtra("dfrm_date",edt_frm_date.getText().toString());
                                i.putExtra("dto_date",edt_to_date.getText().toString());
                                i.putExtra("frm_date",Temp_frm_date);
                                i.putExtra("to_date",Temp_to_date);
                                startActivity(i);

                            }

                        } catch (Exception e) {  }

                        pd.dismiss();
                        //  progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 2000);
            }
        });
    }


    public Connection CONN(String ip,String port,String db) {

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
