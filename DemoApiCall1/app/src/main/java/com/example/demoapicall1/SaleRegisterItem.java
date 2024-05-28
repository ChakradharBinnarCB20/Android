package com.example.demoapicall1;

public class SaleRegisterItem {
    private String docDate;
    private String amount1;
    private String amount2;
    private String amount3;
    private String amount4;
    private String amount5;
    private String amount6;
    private String discountAmount;
    private String totalAmount;

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
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

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public SaleRegisterItem(String docDate, String amount1, String amount2, String amount3, String amount4, String amount5, String amount6, String discountAmount, String totalAmount) {
        this.docDate = docDate;
        this.amount1 = amount1;
        this.amount2 = amount2;
        this.amount3 = amount3;
        this.amount4 = amount4;
        this.amount5 = amount5;
        this.amount6 = amount6;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
    }
}
