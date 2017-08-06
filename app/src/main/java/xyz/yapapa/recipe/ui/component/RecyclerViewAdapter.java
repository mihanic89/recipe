package xyz.yapapa.recipe.ui.component;

import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import xyz.yapapa.recipe.R;
import xyz.yapapa.recipe.ui.activity.MainActivity;
import xyz.yapapa.recipe.ui.activity.MainActivityRecycler;

/**
 * Created by Misha on 24.07.2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private int[] itemList;
    private Context context;



       public class ViewHolder extends RecyclerView.ViewHolder
       {

        private final ImageView imageView;
        private Context context;
        private SoundPool sp;

        public ViewHolder(View v) {
            super(v);


            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);


                    intent.putExtra("image", getAdapterPosition());

                    context.startActivity(intent);

                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {

                public boolean onLongClick(View v) {

                return false;}
            });


            imageView = (ImageView) v.findViewById(R.id.rebus_image);
        }



        public ImageView getImageView() {
            return imageView;
        }

        public void setContext(Context context) {
            this.context = context;
        }

    }


    public RecyclerViewAdapter(MainActivityRecycler mainActivity, int[] dataSet) {
        itemList = dataSet;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycle_cardview, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        viewHolder.setContext(context);

        viewHolder.getImageView().setImageResource(itemList[position]);
    }

    @Override
    public int getItemCount() {
        return itemList.length;
    }
}