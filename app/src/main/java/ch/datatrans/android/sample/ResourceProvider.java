package ch.datatrans.android.sample;

import android.content.Context;

import ch.datatrans.payment.IResourceProvider;

public class ResourceProvider implements IResourceProvider {

    @Override
    public String getNWErrorButtonCancelText(Context context) {
        return context.getString(R.string.nw_error_button_cancel);
    }

    @Override
    public String getNWErrorButtonRetryText(Context context) {
        return context.getString(R.string.nw_error_button_retry);
    }

    @Override
    public String getNWErrorDialogMessageText(Context context) {
        return context.getString(R.string.nw_error_message);
    }

    @Override
    public String getNWErrorDialogTitleText(Context context) {
        return context.getString(R.string.nw_error_title);
    }

    @Override
    public String getNWIndicatorMessageText(Context context) {
        return context.getString(R.string.nw_indicator_message);
    }

}
