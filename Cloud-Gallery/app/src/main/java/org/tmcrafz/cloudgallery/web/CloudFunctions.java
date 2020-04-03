package org.tmcrafz.cloudgallery.web;

import java.util.ArrayList;
import java.util.Arrays;

public class CloudFunctions {
    private final static ArrayList<String> mSupportedPictureExtensions = new ArrayList<String>(Arrays.asList(".jpg", ".jpeg", ".png"));

    // Check if the given path on server is supported file
    // ToDo: (Optional) Check via meta Data
    public static boolean isFileSupportedPicture(String remotePath) {
        // Check if file is valid image
        if (remotePath.lastIndexOf(".") == -1) {
            return false;
        }
        String fileExtension = remotePath.substring(remotePath.lastIndexOf("."));
        if (!mSupportedPictureExtensions.contains(fileExtension)) {
            return false;
        }
        return true;
    }

}
