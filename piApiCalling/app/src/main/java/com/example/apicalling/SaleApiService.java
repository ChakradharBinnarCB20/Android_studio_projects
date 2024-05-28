package com.example.apicalling;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SaleApiService {
    @GET("http://online.pranaliinfotech.com/pigateway/api/SaleRegister/InsertSaleRegister?tab_code=899&from_date=02/01/2023&to_date=02/25/2023&comp_code=1&i=WINES_KAKA_NAVAPUR&str_chk_all=1&str_chk_monthly_summary=0&str_radio_mrp=1&str_chk_adj_sdk=0&str_radio_cash_memo=0") // Replace with your actual API endpoint
    Call<List<SaleReport>> getSaleData();
}
