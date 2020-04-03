package org.tmcrafz.cloudgallery.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tmcrafz.cloudgallery.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PlaceViewHolder> {
    private static String TAG = GalleryAdapter.class.getCanonicalName();

    private Context mContext;
    // Key: remote path, Value: local path
    // value is local path
    private AdapterItems mMediaPaths;

    public GalleryAdapter(Context context, AdapterItems mediaPaths) {
        mContext = context;
        mMediaPaths = mediaPaths;
    }


    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_gallery_custom_layout, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        String path = mMediaPaths.getItem(position).localPath;
        //String path = mMediaPaths.get(position).localPath;
        // Show when media is not already loaded in view and file is downloaded
        if (!holder.mIsLoaded && mMediaPaths.getItem(position).isLocalFileDownloaded) {
            holder.mImagePreview.setImageBitmap(BitmapFactory.decodeFile(path));
        }
        // Else show placeholder
        else {
            holder.mImagePreview.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return mMediaPaths.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImagePreview;
        public boolean mIsLoaded = false;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            mImagePreview = itemView.findViewById(R.id.imageView_preview);
        }
    }

    // ToDo: Find different solution and encapsulate
    public static class AdapterItems {
        public static class Item {
            public String localPath;
            public String remotePath;
            public boolean isLocalFileDownloaded;

            public Item(String localPath, String remotePath, boolean isLocalFileDownloaded) {
                this.localPath = localPath;
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

        public void add(String localPath, String remotePath, boolean isLocalFileDownloaded) {
            mItems.add(new Item(localPath, remotePath, isLocalFileDownloaded));
        }

        public boolean updateDownloadStatusByLocalPath(String localPath, boolean isLocalFileDownloaded) {
            for (Item item : mItems) {
                if (item.localPath.equals(localPath)) {
                    item.isLocalFileDownloaded = isLocalFileDownloaded;
                    return true;
                }
            }
            return false;
        }

        public int getPositionByLocalPath(String localPath) {
            for (int i = 0; i < mItems.size(); i++) {
                Item item = mItems.get(i);
                if (item == null) {
                    continue;
                }
                if (item.localPath.equals(localPath)) {
                    return i;
                }
            }
            return -1;
        }

        public Item getItem(int position) {
            return mItems.get(position);
        }

        public void removeByLocalPath(String localPath) {
            Iterator<Item> itemIterator = mItems.iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                if (item.localPath.equals(localPath)) {
                    itemIterator.remove();
                }
            }
        }

        public void removeLocalPaths(HashSet<String> localPaths) {
            Iterator<Item> itemIterator = mItems.iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                if (localPaths.contains(item.localPath)) {
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



