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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class RecyclerviewGalleryAdapter extends RecyclerView.Adapter<RecyclerviewGalleryAdapter.PlaceViewHolder> {
    private static String TAG = RecyclerviewGalleryAdapter.class.getCanonicalName();

    private Context mContext;
    // Key: remote path, Value: local path
    // value is local path
    private AdapterItems mMediaPaths;

    public RecyclerviewGalleryAdapter(Context context, AdapterItems mediaPaths) {
        mContext = context;
        mMediaPaths = mediaPaths;
    }


    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_gallery_custom_layout_image_entry, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        String path = mMediaPaths.getItem(position).localFilePath;
        File file = new File(path);

        // Show when media is not already loaded in view and file is downloaded
        //if (!holder.mIsLoaded && mMediaPaths.getItem(position).isLocalFileDownloaded) {
        if (file.exists() && file.isFile()) {
            holder.mImagePreview.setImageBitmap(BitmapFactory.decodeFile(path));
//            Glide.with(mContext)
//                    .load(new File(path)) // Uri of the picture
//                    .into(holder.mImagePreview);
        }
        // Else show placeholder
        else {
            holder.mImagePreview.setImageResource(R.drawable.ic_launcher_foreground);
//            Glide.with(mContext)
//                    .load(R.drawable.ic_launcher_foreground) // Uri of the picture
//                    .into(holder.mImagePreview);
        }
    }

    @Override
    public int getItemCount() {
        return mMediaPaths.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImagePreview;
        //public boolean mIsLoaded = false;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            mImagePreview = itemView.findViewById(R.id.imageView_preview);
        }
    }

    // ToDo: Find different solution and encapsulate
    public static class AdapterItems {
        public static class Item {
            // The directory where the downloaded item is stored
            public String localDirectory;
            public String localFilePath;
            public String remotePath;
            public boolean isLocalFileDownloaded;

            public Item(String localDirectory, String localFilePath, String remotePath, boolean isLocalFileDownloaded) {
                this.localDirectory = localDirectory;
                this.localFilePath = localFilePath;
                this.remotePath = remotePath;
                this.isLocalFileDownloaded = isLocalFileDownloaded;
            }
        }

        private ArrayList<Item> mItems;

        public AdapterItems() {
            mItems = new ArrayList<Item>();
        }

        public ArrayList<Item> getArrayList() {
            return mItems;
        }

        public void add(String localDirectory, String localFilePath, String remotePath, boolean isLocalFileDownloaded) {
            mItems.add(new Item(localDirectory, localFilePath, remotePath, isLocalFileDownloaded));
        }

        public boolean updateDownloadStatusByLocalFilePath(String localFilePath, boolean isLocalFileDownloaded) {
            for (Item item : mItems) {
                if (item.localFilePath.equals(localFilePath)) {
                    item.isLocalFileDownloaded = isLocalFileDownloaded;
                    return true;
                }
            }
            return false;
        }

        public int getPositionByLocalFilePath(String localFilePath) {
            for (int i = 0; i < mItems.size(); i++) {
                Item item = mItems.get(i);
                if (item == null) {
                    continue;
                }
                if (item.localFilePath.equals(localFilePath)) {
                    return i;
                }
            }
            return -1;
        }

        public Item getItem(int position) {
            return mItems.get(position);
        }

        public void removeByLocalFilePath(String localFilePath) {
            Iterator<Item> itemIterator = mItems.iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                if (item.localFilePath.equals(localFilePath)) {
                    itemIterator.remove();
                }
            }
        }

        public void removeLocalFilePaths(HashSet<String> localFilePaths) {
            Iterator<Item> itemIterator = mItems.iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                if (localFilePaths.contains(item.localFilePath)) {
                    itemIterator.remove();
                }
            }
        }

        public int size() {
            return mItems.size();
        }
    }

//    public static class AdapterItems {
//        private static class Item {
//            public String key;
//            public String value;
//
//            public Item(String key, String value) {
//                this.key = key;
//                this.value = value;
//            }
//        }
//
//        private ArrayList<Item> mItems;
//
//        public AdapterItems() {
//            mItems = new ArrayList<Item>();
//        }
//
//        public void add(String key, String value) {
//            mItems.add(new Item(key, value));
//        }
//
//        public boolean updateKey(String key, String value) {
//            for (Item item : mItems) {
//                if (item.key.equals(key)) {
//                    item.value = value;
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        public int getPositionOfKey(String key) {
//            for (int i = 0; i < mItems.size(); i++) {
//                Item item = mItems.get(i);
//                if (item == null) {
//                    continue;
//                }
//                if (item.key.equals(key)) {
//                    return i;
//                }
//            }
//            return -1;
//        }
//
//        public String getValue(int position) {
//            return mItems.get(position).value;
//        }
//
//        public String getValue(String key) {
//            for (Item item : mItems) {
//                if (item.key.equals(key)) {
//                    return item.value;
//                }
//            }
//            return null;
//        }
//
//        public void removeByKey(String key) {
//            Iterator<Item> itemIterator = mItems.iterator();
//            while (itemIterator.hasNext()) {
//                Item item = itemIterator.next();
//                if (item.key.equals(key)) {
//                    itemIterator.remove();
//                }
//            }
//        }
//
//        public void removeByKeys(HashSet<String> keys) {
//            Iterator<Item> itemIterator = mItems.iterator();
//            while (itemIterator.hasNext()) {
//                Item item = itemIterator.next();
//                if (keys.contains(item.key)) {
//                    itemIterator.remove();
//                }
//            }
//        }
//
//        public int size() {
//            return mItems.size();
//        }
//    }
}



