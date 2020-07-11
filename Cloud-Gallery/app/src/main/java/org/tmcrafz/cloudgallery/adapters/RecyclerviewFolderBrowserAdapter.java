package org.tmcrafz.cloudgallery.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tmcrafz.cloudgallery.R;
import org.tmcrafz.cloudgallery.ui.ShowPicturesActivity;

import java.util.ArrayList;

public class RecyclerviewFolderBrowserAdapter extends RecyclerView.Adapter<RecyclerviewFolderBrowserAdapter.FolderTypeViewHolder> {

    public interface OnLoadFolderData {
        void onLoadPathData(String path);
    }

    private static String TAG = RecyclerviewFolderBrowserAdapter.class.getCanonicalName();

    private OnLoadFolderData mContext;
    private ArrayList<AdapterItem> mData;


    public RecyclerviewFolderBrowserAdapter(OnLoadFolderData context, ArrayList<AdapterItem> pathData) {
        mContext = context;
        mData = pathData;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    @NonNull
    @Override
    public FolderTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case AdapterItem.TYPE_FOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_folder_browser, parent, false);
                return new FolderTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FolderTypeViewHolder holder, int position) {
        AdapterItem data = mData.get(position);
        switch (data.type) {
            case AdapterItem.TYPE_FOLDER:
                AdapterItem.FolderItem folderData = (AdapterItem.FolderItem) data;
                holder.mTextFolderName.setText(folderData.name);
                final String path = folderData.path;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       ((OnLoadFolderData) mContext).onLoadPathData(path); }
                                               }
                );
                holder.mButtonShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ShowPicturesActivity.class);
                    intent.putExtra(ShowPicturesActivity.EXTRA_PATH_TO_SHOW, path);
                    context.startActivity(intent);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class FolderTypeViewHolder extends RecyclerView.ViewHolder {
        //public ImageView mImagePreview;
        public TextView mTextFolderName;
        public Button mButtonShow;
        //public boolean mIsLoaded = false;

        public FolderTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextFolderName = itemView.findViewById(R.id.textView_folder_name);
            mButtonShow = itemView.findViewById(R.id.button_show);
        }
    }

    public static class AdapterItem {
        public static class ImageItem extends AdapterItem{
            // The directory where the downloaded item is stored
            public String localDirectory;
            public String localFilePath;
            public String remotePath;
            public boolean isLocalFileDownloaded;

            public ImageItem(int type, String localDirectory, String localFilePath, String remotePath, boolean isLocalFileDownloaded) {
                super(type);
                this.localDirectory = localDirectory;
                this.localFilePath = localFilePath;
                this.remotePath = remotePath;
                this.isLocalFileDownloaded = isLocalFileDownloaded;
            }
        }

        public static class FolderItem extends AdapterItem {
            public String name;
            public String path;

            public FolderItem(int type, String name, String path) {
                super(type);
                this.name = name;
                this.path = path;
            }
        }
        public static final int TYPE_FOLDER = 0;
        public static final int TYPE_IMAGE = 1;

        public int type;

        public AdapterItem(int type) {
            this.type = type;
        }
    }

}
