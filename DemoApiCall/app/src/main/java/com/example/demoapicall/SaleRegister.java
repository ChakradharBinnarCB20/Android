package com.example.demoapicall;


import com.google.gson.annotations.SerializedName;

public class SaleRegister {
    @SerializedName("DOCDT")
    private String docDt;
    @SerializedName("AMOUNT_1")
    private String amount1;
    @SerializedName("AMOUNT_2")
    private String amount2;
    @SerializedName("AMOUNT_3")
    private String amount3;
    @SerializedName("AMOUNT_4")
    private String amount4;
    @SerializedName("AMOUNT_5")
    private String amount5;
    @SerializedName("AMOUNT_6")
    private String amount6;
    @SerializedName("DIS_AMOUNT")
    private String disAmount;
    @SerializedName("AMOUNT")
    private String amount;
    @SerializedName("DOC_DT")
    private String docDtFull;

    public String getDocDt() {
        return docDt;
    }

    public void setDocDt(String docDt) {
        this.docDt = docDt;
    }

    public String getAmount1() {
        return amount1;
    }

    public void setAmount1(String amount1) {
        this.amount1 = amount1;
    }

    public String getAmount2() {
        return amount2;
    }

    public void setAmount2(String amount2) {
        this.amount2 = amount2;
    }

    public String getAmount3() {
        return amount3;
    }

    public void setAmount3(String amount3) {
        this.amount3 = amount3;
    }

    public String getAmount4() {
        return amount4;
    }

    public void setAmount4(String amount4) {
        this.amount4 = amount4;
    }

    public String getAmount5() {
        return amount5;
    }

    public void setAmount5(String amount5) {
        this.amount5 = amount5;
    }

    public String getAmount6() {
        return amount6;
    }

    public void setAmount6(String amount6) {
        this.amount6 = amount6;
    }

    public String getDisAmount() {
        return disAmount;
    }

    public void setDisAmount(String disAmount) {
        this.disAmount = disAmount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDocDtFull() {
        return docDtFull;
    }

    public void setDocDtFull(String docDtFull) {
        this.docDtFull = docDtFull;
    }
}

