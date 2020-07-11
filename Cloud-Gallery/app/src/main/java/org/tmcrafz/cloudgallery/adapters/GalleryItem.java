package org.tmcrafz.cloudgallery.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class GalleryItem {

    public static final int TYPE_FOLDER = 0;
    public static final int TYPE_IMAGE = 1;

    public int type;

    public GalleryItem(int type) {
        this.type = type;
    }


    public static class FolderItem extends GalleryItem {
        public String name;
        public String path;

        public FolderItem(int type, String name, String path) {
            super(type);
            this.name = name;
            this.path = path;
        }
    }


    public static class ImageItem extends GalleryItem {
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

        public static boolean updateDownloadStatusByLocalFilePath(ArrayList<GalleryItem> items, String localFilePath, boolean isLocalFileDownloaded) {
            for (GalleryItem item : items) {
                if (item.type != GalleryItem.TYPE_IMAGE) {
                    continue;
                }
                ImageItem imageItem = (ImageItem) item;
                if (imageItem.localFilePath.equals(localFilePath)) {
                    imageItem.isLocalFileDownloaded = isLocalFileDownloaded;
                    return true;
                }
            }
            return false;
        }

        public static int getPositionByLocalFilePath(ArrayList<GalleryItem> items, String localFilePath) {
            for (int i = 0; i < items.size(); i++) {
                GalleryItem item = items.get(i);
                if (item == null) {
                    continue;
                }
                if (item.type != GalleryItem.TYPE_IMAGE) {
                    continue;
                }
                ImageItem imageItem = (ImageItem) item;
                if (imageItem.localFilePath.equals(localFilePath)) {
                    return i;
                }
            }
            return -1;
        }


        public static void removeByLocalFilePath(ArrayList<GalleryItem> items, String localFilePath) {
            Iterator<GalleryItem> itemIterator = items.iterator();
            while (itemIterator.hasNext()) {
                GalleryItem item = itemIterator.next();
                if (item.type != GalleryItem.TYPE_IMAGE) {
                    continue;
                }
                ImageItem imageItem = (ImageItem) item;
                if (imageItem.localFilePath.equals(localFilePath)) {
                    itemIterator.remove();
                }
            }
        }

        public static void removeLocalFilePaths(ArrayList<GalleryItem> items, HashSet<String> localFilePaths) {
            Iterator<GalleryItem> itemIterator = items.iterator();
            while (itemIterator.hasNext()) {
                GalleryItem item = itemIterator.next();
                if (item.type != GalleryItem.TYPE_IMAGE) {
                    continue;
                }
                ImageItem imageItem = (ImageItem) item;
                if (localFilePaths.contains(imageItem.localFilePath)) {
                    itemIterator.remove();
                }
            }
        }
    }

}
