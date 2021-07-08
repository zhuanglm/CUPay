package com.braintreepayments.api.dropin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.R;
import com.braintreepayments.api.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;

public class VaultedPaymentMethodsAdapter extends RecyclerView.Adapter<VaultedPaymentMethodsAdapter.ViewHolder> {

    private final PaymentMethodNonceCreatedListener mSelectedListener;

    //private final List<PaymentMethodNonce> mPaymentMethodNonces;
    private AvailablePaymentMethodNonceList mAvailablePaymentMethodNonces;

    private PaymentMethodType mPaymentMethod;

    public VaultedPaymentMethodsAdapter(PaymentMethodNonceCreatedListener listener,
                                        AvailablePaymentMethodNonceList paymentMethodNonces,
                                        DropInRequest dropInRequest) {
        mSelectedListener = listener;
        mAvailablePaymentMethodNonces = paymentMethodNonces;
        mPaymentMethod = dropInRequest.getPaymentMethodType();
    }

    /*public void setup(Context context, Configuration configuration, DropInRequest dropInRequest,
                      boolean googlePayEnabled, boolean unionpaySupported) {
        mPaymentMethod = dropInRequest.getPaymentMethodType();
        *//*mAvailablePaymentMethodNonces = new AvailablePaymentMethodNonceList(
                context, configuration, mPaymentMethodNonces, dropInRequest, googlePayEnabled);*//*
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_vaulted_payment_method_card,
                parent, false));
        /*return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ct_vaulted_payment_method_item,
                parent, false));*/
        //return new ViewHolder(new PaymentMethodItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PaymentMethodNonce paymentMethodNonce = mAvailablePaymentMethodNonces.get(position);
        PaymentMethodType paymentMethodType = PaymentMethodType.forType(paymentMethodNonce);

        holder.icon.setImageResource(paymentMethodType.getVaultedDrawable());
        holder.title.setText(paymentMethodType.getLocalizedName());

        if (paymentMethodNonce instanceof CardNonce) {
            holder.description.setText("••• ••" + ((CardNonce) paymentMethodNonce).getLastTwo());
        } else {
            holder.description.setText(paymentMethodNonce.getDescription());
        }

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // payment mode
                if(mPaymentMethod != PaymentMethodType.NONE)
                    mSelectedListener.onPaymentMethodNonceCreated(paymentMethodNonce);
                else {
                    //Todo: Management mode
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAvailablePaymentMethodNonces.size();
    }

    public boolean hasCardNonce() {
        return mAvailablePaymentMethodNonces.hasCardNonce();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView title;
        public TextView description;

        ViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.bt_payment_method_icon);
            title = view.findViewById(R.id.bt_payment_method_title);
            description = view.findViewById(R.id.bt_payment_method_description);
        }
    }
}
