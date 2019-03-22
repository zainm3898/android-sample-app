package ch.datatrans.android.sample.model;

import java.io.Serializable;

public class TransactionDetails implements Serializable {

    private String merchantId;
    private int amount;
    private String currency;
    private String refrenceNumber;
    private String sign;
    private String status;
    private String paymentMethod;
    private String aliasCC;

    // TODO - builder
    public TransactionDetails(String merchantId, int amount, String currency, String refrenceNumber, String paymentMethod, String sign) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
        this.refrenceNumber = refrenceNumber;
        this.paymentMethod = paymentMethod;
        this.sign = sign;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRefrenceNumber() {
        return refrenceNumber;
    }

    public void setRefrenceNumber(String refrenceNumber) {
        this.refrenceNumber = refrenceNumber;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAliasCC() {
        if(null == aliasCC || "".equals(aliasCC)) {
            return "n/a";
        }

        return aliasCC;
    }

    public void setAliasCC(String aliasCC) {
        this.aliasCC = aliasCC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionDetails that = (TransactionDetails) o;

        if (amount != that.amount) return false;
        if (!merchantId.equals(that.merchantId)) return false;
        if (!currency.equals(that.currency)) return false;
        if (!refrenceNumber.equals(that.refrenceNumber)) return false;
        if (sign != null ? !sign.equals(that.sign) : that.sign != null) return false;
        if (!status.equals(that.status)) return false;
        if (paymentMethod != null ? !paymentMethod.equals(that.paymentMethod) : that.paymentMethod != null)
            return false;
        return aliasCC != null ? aliasCC.equals(that.aliasCC) : that.aliasCC == null;
    }

    @Override
    public int hashCode() {
        int result = merchantId.hashCode();
        result = 31 * result + amount;
        result = 31 * result + currency.hashCode();
        result = 31 * result + refrenceNumber.hashCode();
        result = 31 * result + (sign != null ? sign.hashCode() : 0);
        result = 31 * result + status.hashCode();
        result = 31 * result + (paymentMethod != null ? paymentMethod.hashCode() : 0);
        result = 31 * result + (aliasCC != null ? aliasCC.hashCode() : 0);
        return result;
    }
}
