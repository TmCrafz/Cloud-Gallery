package org.tmcrafz.cloudgallery.adapters;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

public abstract class GalleryItem {

    public static final int TYPE_FOLDER = 0;
    public static final int TYPE_IMAGE = 1;

    public int type;

    public GalleryItem(int type) {
        this.type = type;
    }

    // Get the identifier used for sorting
    public abstract String getSortIdentifier();

    // Sort by name and type
    public static void sortByName(ArrayList<GalleryItem> items) {
        Collections.sort(items,
                new Comparator<GalleryItem>() {
                    @Override
                    public int compare(GalleryItem o1, GalleryItem o2) {
                        // When both are folders we only sort by "identifier" (usually name)
                        if (o1.type == GalleryItem.TYPE_FOLDER && o2.type == GalleryItem.TYPE_FOLDER) {
                            return o1.getSortIdentifier().compareTo(o2.getSortIdentifier());
                        }
                        // Same here
                        else if (o1.type == GalleryItem.TYPE_IMAGE && o2.type == GalleryItem.TYPE_IMAGE) {
                            return o1.getSortIdentifier().compareTo(o2.getSortIdentifier());
                        }
                        // Folders are shown first, so they are first in the collections
                        else if (o1.type == GalleryItem.TYPE_FOLDER && o2.type == GalleryItem.TYPE_IMAGE) {
                            return -1;
                        }
                        // Then images are following
                        else if (o1.type == GalleryItem.TYPE_IMAGE && o2.type == GalleryItem.TYPE_FOLDER) {
                            return 1;
                        }
                        return 0;
                    }
                });
    }

    public static class FolderItem extends GalleryItem {
        public String name;
        public String path;

        public FolderItem(int type, String name, String path) {
            super(type);
            this.name = name;
            this.path = path;
        }

        @Override
        public String getSortIdentifier() {
            return name;
        }

//        private String formatName(String name) {
//            // Remove / at beginning and end of path name
//            if (name.length() > 1 && name.charAt(0) == '/') {
//                name = name.substring(1);
//            }
//            if (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
//                name = name.substring(0, name.length() - 1);
//            }
//            return name;
//        }

    }


    public static class ImageItem extends GalleryItem {
        // The directory where the downloaded item is stored
        public String localDirectory;
        public String localFilePath;
        public String remotePath;
        public boolean isLocalFileDownloaded;
        public String name;

        public ImageItem(int type, String localDirectory, String localFilePath, String remotePath, boolean isLocalFileDownloaded) {
            super(type);
            this.localDirectory = localDirectory;
            this.localFilePath = localFilePath;
            this.remotePath = remotePath;
            this.isLocalFileDownloaded = isLocalFileDownloaded;
            this.name = createName(remotePath);
        }

        private String createName(String remotePath) {
            File file = new File(remotePath);
            return file.getName();
        }

        @Override
        public String getSortIdentifier() {
            return name;
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
