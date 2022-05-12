package com.example.bookapp.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.example.bookapp.URLs;
import com.example.bookapp.buyer.BookDetailsActivity;
import com.example.bookapp.buyer.openDisputeActivity;
import com.example.bookapp.buyer.orderDetailsActivity;
import com.example.bookapp.models.Book;
import com.example.bookapp.models.Order;
import com.example.bookapp.models.OrderDetails;
import com.example.bookapp.seller.sellerOrderDetailsActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class orderAdapter extends RecyclerView.Adapter<orderAdapter.MyViewHolder> {

    List<Order> list;
    Activity context;
    SessionManager session;
    public orderAdapter(List<Order> list, Activity context) {
        this.list = list;
        this.context = context;
        session = new SessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_order, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order item = list.get(position);

        holder.viewOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                if(context instanceof  com.example.bookapp.seller.ordersList){

                    intent = new Intent(context, sellerOrderDetailsActivity.class);
                }else {
                    intent = new Intent(context, orderDetailsActivity.class);
                }
                intent.putExtra("order_id",  item.getId());
                context.startActivity(intent);
            }
        });


        holder.order_date.setText(context.getString(R.string.order_date) + " " + item.getOrder_date());
        holder.order_status.setText(context.getString(R.string.order_status) + " " + item.getStatusText(context));
 //       holder.book_name.setText(item.getBook().getBookName() );

        if(session.getUserType().equals("Seller")){
            holder.seller_name.setText(context.getString(R.string.buyer_name) + " " + item.getSeller().getName());
            holder.seller_email.setText(context.getString(R.string.buyer_email) + " " + item.getSeller().getEmail());
            holder.seller_address.setText(context.getString(R.string.buyer_address) + " " + item.getSeller().getAddress());
            //holder.chatLbl.setText(R.string.chat_with_buyer);
            holder.seller_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    URLs.sendEmail(context , item.getSeller().getEmail());
                }
            });
            holder.chatLbl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, com.example.bookapp.seller.chat_seller.class);
                    i.putExtra("buyer_id",item.getBuyer_id());
                    context.startActivity(i);
                }
            });
            holder.disputeBtn.setVisibility(View.GONE);
            holder.changeStatus.setVisibility(View.VISIBLE);
            int sts = Integer.parseInt(item.getOrderStatus());
            if( sts == 0){
                holder.changeStatus.setText(context.getString(R.string.confirm_order));
                holder.cancelOrderBtn.setVisibility(View.VISIBLE);
            }else if( sts == 1 ){
                holder.changeStatus.setText(context.getString(R.string.out_for_delivery));
            }else if(sts == 2){
                holder.changeStatus.setText(context.getString(R.string.delivered));
            }else if(sts >= 3){
                holder.changeStatus.setVisibility(View.GONE);
            }
            holder.changeStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    com.example.bookapp.seller.ordersList temp =
                            (com.example.bookapp.seller.ordersList ) context;
                    temp.changeOrderStatus(item);
                }
            });
        }else {
            holder.seller_name.setText(context.getString(R.string.seller_name) + " " + item.getSeller().getName());
            holder.seller_email.setText(context.getString(R.string.seller_email) + " " + item.getSeller().getEmail());
            holder.seller_address.setText(context.getString(R.string.seller_address) + " " + item.getSeller().getAddress());
            //holder.chatLbl.setText(R.string.chat_with_seller);
            int sts = Integer.parseInt(item.getOrderStatus());
            if( sts == 0){
                holder.changeStatus.setText(context.getString(R.string.confirm_order));
                holder.cancelOrderBtn.setVisibility(View.VISIBLE);
            }
            holder.chatLbl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, com.example.bookapp.buyer.chat_buyer.class);
                    i.putExtra("seller_id",item.getSeller().getId());
                    context.startActivity(i);
                }
            });
            if(item.getDisputeOpend().equals("0")) {
                holder.disputeBtn.setVisibility(View.VISIBLE);
                holder.disputeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDispute(item);
                    }
                });
            }
        }

        holder.cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrder(item , session.getUserType());
            }
        });

        holder.delivery_option.setText(item.getDeliveryOption());
        holder.address.setText(item.getDeliveryAddress());
        holder.deliver_days.setText(item.getDeliver_days(context));
        if(item.getDeliveryOption().equals("Pick up")) {
            holder.addressLayout.setVisibility(View.GONE);
        }


    }
    String status = "4";
    private void cancelOrder(Order item, String userType) {
        String user_id = session.getUserID();
        String request = URLs.CANCEL_ORDER;

        if(userType.equals("Seller"))
        {
            status = "5";
        }
        final ProgressDialog loading = ProgressDialog.show(context, "Loading", context.getString(R.string.waiting), false, false);
        Toast.makeText(context, request, Toast.LENGTH_LONG).show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(context, "Order Canceled", Toast.LENGTH_LONG).show();
                        item.setOrderStatus(status);
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(register.this,error.toString(),Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        Toast.makeText(context, "Couldn't cancel order", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("order_id" , item.getId());
                params.put("status" , status);
                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public  void openDispute(Order order)
    {
        Intent intent = new Intent(context, openDisputeActivity.class);
        intent.putExtra("Order", order);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView book_name, seller_name, seller_address, seller_email,order_status, order_date,
                delivery_option , address, deliver_days;
        ImageButton chatLbl;
        Button viewOrderDetails, cancelOrderBtn, disputeBtn,changeStatus;
        LinearLayout addressLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            book_name = itemView.findViewById(R.id.book_name);
            seller_name = itemView.findViewById(R.id.bookOwner);
            seller_address = itemView.findViewById(R.id.sellerPhone);
            seller_email = itemView.findViewById(R.id.sellerEmail);
            order_date = itemView.findViewById(R.id.order_date);
            chatLbl = itemView.findViewById(R.id.chatLbl);
            order_status = itemView.findViewById(R.id.order_status);
            viewOrderDetails = itemView.findViewById(R.id.visitBtn);
            disputeBtn = itemView.findViewById(R.id.dipBtn);
            cancelOrderBtn = itemView.findViewById(R.id.cancelOrderBtn);
            changeStatus = itemView.findViewById(R.id.changeStatus);
            addressLayout = itemView.findViewById(R.id.addressLayout);
            delivery_option = itemView.findViewById(R.id.delivery_option);
            address = itemView.findViewById(R.id.address);
            deliver_days = itemView.findViewById(R.id.deliver_with_in);

        }
    }
}
