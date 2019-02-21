package views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dooyd.R;
import views.listener.HomeRecyclerListener;

import java.util.List;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder> {

    private Context context;
    private List<String> categoryList;
    private HomeRecyclerListener homeRecyclerListener;

    public HomeRecyclerAdapter(Context context, List<String> categoryList, HomeRecyclerListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.homeRecyclerListener = listener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_home_item, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {

        holder.categoryCard.setTag(holder.getAdapterPosition());
        holder.categoryName.setText(categoryList.get(holder.getAdapterPosition()));

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatTextView categoryName;

        private CardView categoryCard;

        private HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryCard = itemView.findViewById(R.id.category_card);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.category_card: {
                    homeRecyclerListener.onItemClick((int) v.getTag(), categoryList.get((int) v.getTag()));
                    break;
                }
            }
        }
    }
}
