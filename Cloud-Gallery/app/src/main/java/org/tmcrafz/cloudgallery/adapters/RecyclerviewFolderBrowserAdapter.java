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
import org.tmcrafz.cloudgallery.ui.showpictures.ShowPicturesFragment;

import java.util.ArrayList;

public class RecyclerviewFolderBrowserAdapter extends RecyclerView.Adapter<RecyclerviewFolderBrowserAdapter.PlaceViewHolder> {

    public interface OnLoadFolderData {
        void onLoadPathData(String path);
    }

    private static String TAG = RecyclerviewFolderBrowserAdapter.class.getCanonicalName();

    private OnLoadFolderData mContext;
    private ArrayList<AdapterItem> mPathData;


    public RecyclerviewFolderBrowserAdapter(OnLoadFolderData context, ArrayList<AdapterItem> pathData) {
        mContext = context;
        mPathData = pathData;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_folder_browser, parent, false);
        return new RecyclerviewFolderBrowserAdapter.PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        AdapterItem data = mPathData.get(position);
        if (data.type == AdapterItem.TYPE_FOLDER) {
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
        }



        //File file = new File(path);
        //holder.mImagePreview.setImageResource(R.drawable.ic_launcher_foreground);
    }

    @Override
    public int getItemCount() {
        return mPathData.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {

        //public ImageView mImagePreview;
        public TextView mTextFolderName;
        public Button mButtonShow;
        //public boolean mIsLoaded = false;

        public PlaceViewHolder(@NonNull View itemView) {
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
