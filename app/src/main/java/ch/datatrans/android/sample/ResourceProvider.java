package ch.datatrans.android.sample;

import android.content.Context;

import ch.datatrans.payment.android.IResourceProvider;

/**
 * Created by domi on 1/12/15.
 */
public class ResourceProvider implements IResourceProvider {

    @Override
    public String getNWErrorButtonCancelText(Context context) {
        return null;
    }

    @Override
    public String getNWErrorButtonRetryText(Context context) {
        return null;
    }

    @Override
    public String getNWErrorDialogMessageText(Context context) {
        return null;
    }

    @Override
    public String getNWErrorDialogTitleText(Context context) {
        return null;
    }

    @Override
    public String getNWIndicatorMessageText(Context context) {
        return null;
    }

}
