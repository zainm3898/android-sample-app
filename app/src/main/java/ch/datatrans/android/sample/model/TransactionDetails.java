package ch.datatrans.android.sample.model;

import java.io.Serializable;

public class TransactionDetails implements Serializable {

    private String merchantId;
    private int amount;
    private String currency;
    private String refrenceNumber;
    private String sign;
    private String status;

    // TODO - builder
    public TransactionDetails(String merchantId, int amount, String currency, String refrenceNumber, String sign) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
        this.refrenceNumber = refrenceNumber;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionDetails that = (TransactionDetails) o;

        if (amount != that.amount) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null)
            return false;
        if (merchantId != null ? !merchantId.equals(that.merchantId) : that.merchantId != null)
            return false;
        if (refrenceNumber != null ? !refrenceNumber.equals(that.refrenceNumber) : that.refrenceNumber != null)
            return false;
        if (sign != null ? !sign.equals(that.sign) : that.sign != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = merchantId != null ? merchantId.hashCode() : 0;
        result = 31 * result + amount;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (refrenceNumber != null ? refrenceNumber.hashCode() : 0);
        result = 31 * result + (sign != null ? sign.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
