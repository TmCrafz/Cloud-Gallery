package org.tmcrafz.cloudgallery.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tmcrafz.cloudgallery.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PlaceViewHolder> {

    private Context mContext;
    private ArrayList<String> mImagePaths;

    public GalleryAdapter(Context context, ArrayList<String> placeList) {
        mContext = context;
        mImagePaths = placeList;
    }


    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_gallery_custom_layout, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        String path = mImagePaths.get(position);
        holder.mImagePreview.setImageBitmap(BitmapFactory.decodeFile(path));
        //holder.mImagePreview.setImageResource(R.drawable.ic_launcher_foreground);
    }

    @Override
    public int getItemCount() {
        return mImagePaths.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImagePreview;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            mImagePreview = itemView.findViewById(R.id.imageView_preview);
        }
    }
}


