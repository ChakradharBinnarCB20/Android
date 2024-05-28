package com.example.demoapicall1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
   // @GET("SaleRegister/InsertSaleRegister?")

    @GET("SaleRegister/InsertSaleRegister?tab_code=899&from_date=02/01/2023&to_date=02/25/2023&comp_code=1&i=WINES_KAKA_NAVAPUR&str_chk_all=1&str_chk_monthly_summary=0&str_radio_mrp=1&str_chk_adj_sdk=0&str_radio_cash_memo=0")
    Call<List<SaleRegisterItem>> getSaleRegisterData(
//            @Query("tab_code") String tabCode,
//            @Query("from_date") String fromDate,
//            @Query("to_date") String toDate,
//            @Query("comp_code") String compCode,
//            @Query("i") String i,
//            @Query("str_chk_all") String strChkAll,
//            @Query("str_chk_monthly_summary") String strChkMonthlySummary,
//            @Query("str_radio_mrp") String strRadioMrp,
//            @Query("str_chk_adj_sdk") String strChkAdjSdk,
//            @Query("str_radio_cash_memo") String strradicashmemo
    );
}