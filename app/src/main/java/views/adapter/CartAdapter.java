package views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dooyd.R;
import com.bumptech.glide.Glide;
import datamodel.Constants;
import datamodel.MainItem;
import views.listener.CommonRecyclerListener;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MainViewHolder> {

    private Context context;
    private List<MainItem> mainItemList;
    private CommonRecyclerListener productRecyclerListener;

    public CartAdapter(Context context, List<MainItem> itemList, CommonRecyclerListener listener) {
        this.context = context;
        this.mainItemList = itemList;
        this.productRecyclerListener = listener;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {

        MainItem mainItem = mainItemList.get(holder.getAdapterPosition());

        holder.productCard.setTag(position);
        holder.itemName.setText(mainItem.getItemName());
        holder.itemPrice.setText(String.valueOf(mainItem.getItemPrice()));
        holder.itemCutPrice.setText(String.valueOf(mainItem.getItemCutPrice()));
        holder.tv_count.setText(String.valueOf(mainItem.getQuantity()));

        Glide.with(context).load(mainItem.getItemImageUrl()).into(holder.itemImageView);

        if(mainItem.getProductquantity() > 0)
        {
            holder.item_quantity.setText(Constants.IN_STOCK);
            holder.item_quantity.setTextColor(Color.parseColor("#12CE19"));
        } else {
            holder.item_quantity.setText(Constants.OUT_OF_STOCK);
            holder.item_quantity.setTextColor(Color.RED);
        }

        holder.tv_minus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null));
        holder.tv_add.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null));

        holder.tv_minus.setBackgroundResource(R.drawable.circle_corner);
        holder.tv_add.setBackgroundResource(R.drawable.circle_corner);

        if (mainItem.getQuantity() <= 1) {
            holder.tv_minus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.colorJoinLine, null));
            holder.tv_minus.setBackgroundResource(R.drawable.circle_corner_grey);
        }

        if (mainItem.getQuantity() >= mainItem.getProductquantity()) {
            holder.tv_add.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.colorJoinLine, null));
            holder.tv_add.setBackgroundResource(R.drawable.circle_corner_grey);
        }
    }

    @Override
    public int getItemCount() {
        return mainItemList.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_remove, tv_minus, tv_add, tv_count;
        private CardView productCard;
        private AppCompatTextView itemName;
        private AppCompatTextView itemPrice;
        private AppCompatTextView itemCutPrice;
        private AppCompatTextView item_quantity;
        private AppCompatImageView itemImageView;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.item_name_view);
            itemPrice = itemView.findViewById(R.id.item_price_view);
            itemCutPrice = itemView.findViewById(R.id.item_cut_price_view);
            itemImageView = itemView.findViewById(R.id.item_image_view);
            productCard = itemView.findViewById(R.id.productCard);
            tv_remove = itemView.findViewById(R.id.tv_remove);
            item_quantity = itemView.findViewById(R.id.item_quantity);
            tv_minus = itemView.findViewById(R.id.tv_minus);
            tv_add = itemView.findViewById(R.id.tv_add);
            tv_count = itemView.findViewById(R.id.tv_count);

            itemCutPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            productCard.setOnClickListener(this);
            tv_remove.setOnClickListener(this);
            tv_minus.setOnClickListener(this);
            tv_add.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.productCard: {
                    productRecyclerListener.onItemClick(v.getId(), getAdapterPosition());
                    break;
                }

                case R.id.tv_remove: {
                    productRecyclerListener.onItemClick(v.getId(), getAdapterPosition());
                    break;
                }

                case R.id.tv_add: {
                    if (mainItemList.get(getAdapterPosition()).getQuantity() < mainItemList.get(getAdapterPosition()).getProductquantity()) {
                        productRecyclerListener.onItemClick(v.getId(), getAdapterPosition());
                    }
                    break;
                }

                case R.id.tv_minus: {
                    if (mainItemList.get(getAdapterPosition()).getQuantity() > 1) {
                        productRecyclerListener.onItemClick(v.getId(), getAdapterPosition());
                    }
                    break;
                }
            }
        }
    }
}
