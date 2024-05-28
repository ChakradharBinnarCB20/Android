package com.example.apicalling;

public class SaleReport {
    private String DOCDT;
    private String AMOUNT_1;
    private String AMOUNT_2;
    private String AMOUNT_3;
    private String AMOUNT_4;
    private String AMOUNT_5;
    private String AMOUNT_6;
    private String DIS_AMOUNT;
    private String AMOUNT;
    private String DOC_DT;


    public String getDOCDT() {
        return DOCDT;
    }

    public void setDOCDT(String DOCDT) {
        this.DOCDT = DOCDT;
    }

    public String getAMOUNT_1() {
        return AMOUNT_1;
    }

    public void setAMOUNT_1(String AMOUNT_1) {
        this.AMOUNT_1 = AMOUNT_1;
    }

    public String getAMOUNT_2() {
        return AMOUNT_2;
    }

    public void setAMOUNT_2(String AMOUNT_2) {
        this.AMOUNT_2 = AMOUNT_2;
    }

    public String getAMOUNT_3() {
        return AMOUNT_3;
    }

    public void setAMOUNT_3(String AMOUNT_3) {
        this.AMOUNT_3 = AMOUNT_3;
    }

    public String getAMOUNT_4() {
        return AMOUNT_4;
    }

    public void setAMOUNT_4(String AMOUNT_4) {
        this.AMOUNT_4 = AMOUNT_4;
    }

    public String getAMOUNT_5() {
        return AMOUNT_5;
    }

    public void setAMOUNT_5(String AMOUNT_5) {
        this.AMOUNT_5 = AMOUNT_5;
    }

    public String getAMOUNT_6() {
        return AMOUNT_6;
    }

    public void setAMOUNT_6(String AMOUNT_6) {
        this.AMOUNT_6 = AMOUNT_6;
    }

    public String getDIS_AMOUNT() {
        return DIS_AMOUNT;
    }

    public void setDIS_AMOUNT(String DIS_AMOUNT) {
        this.DIS_AMOUNT = DIS_AMOUNT;
    }

    public String getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(String AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getDOC_DT() {
        return DOC_DT;
    }

    public void setDOC_DT(String DOC_DT) {
        this.DOC_DT = DOC_DT;
    }

    public SaleReport(String DOCDT, String AMOUNT_1, String AMOUNT_2, String AMOUNT_3, String AMOUNT_4, String AMOUNT_5, String AMOUNT_6, String DIS_AMOUNT, String AMOUNT, String DOC_DT) {
        this.DOCDT = DOCDT;
        this.AMOUNT_1 = AMOUNT_1;
        this.AMOUNT_2 = AMOUNT_2;
        this.AMOUNT_3 = AMOUNT_3;
        this.AMOUNT_4 = AMOUNT_4;
        this.AMOUNT_5 = AMOUNT_5;
        this.AMOUNT_6 = AMOUNT_6;
        this.DIS_AMOUNT = DIS_AMOUNT;
        this.AMOUNT = AMOUNT;
        this.DOC_DT = DOC_DT;
    }
    public SaleReport(){

    }

}
