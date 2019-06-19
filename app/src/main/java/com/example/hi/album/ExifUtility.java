package com.example.hi.album;

import android.support.media.ExifInterface;
import org.json.JSONObject;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.example.hi.album.MainActivity.viewPager;

class ExifUtility {

    List<String> getExif(ExifInterface exif) {

        List<String> exifList = new ArrayList<>();

        File f = new File(AnhFragment.mangHinh.get(viewPager.getCurrentItem()).duongdan);

        if (!getExifTag(exif, ExifInterface.TAG_DATETIME).isEmpty()) {
            exifList.add("Date: " + getExifTag(exif, ExifInterface.TAG_DATETIME));
        } else {
            DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
            exifList.add("Date: " + dateTimeInstance.format(new Date(f.lastModified())));
        }
        if (!getExifTag(exif, ExifInterface.TAG_FILE_SOURCE).isEmpty()) {
            exifList.add("File Source: " + getExifTag(exif, ExifInterface.TAG_FILE_SOURCE));
        }
        exifList.add("File Size: " + f.length() / 1024 + "KB");

        if (!getExifTag(exif, ExifInterface.TAG_IMAGE_WIDTH).isEmpty() && !getExifTag(exif, ExifInterface.TAG_IMAGE_LENGTH).isEmpty()) {
            exifList.add("Image Size: " + getExifTag(exif, ExifInterface.TAG_IMAGE_WIDTH) + "x" + getExifTag(exif, ExifInterface.TAG_IMAGE_LENGTH));
        }
        if (!getExifTag(exif, ExifInterface.TAG_MAKE).isEmpty()) {
            exifList.add("Make: " + getExifTag(exif, ExifInterface.TAG_MAKE));
        }
        if (!getExifTag(exif, ExifInterface.TAG_MODEL).isEmpty()) {
            exifList.add("Model: " + getExifTag(exif, ExifInterface.TAG_MODEL));
        }
        if (!getExifTag(exif, ExifInterface.TAG_WHITE_BALANCE).isEmpty()) {
            exifList.add("White Balance: " + getExifTag(exif, ExifInterface.TAG_WHITE_BALANCE));
        }
        if (!getExifTag(exif, ExifInterface.TAG_BRIGHTNESS_VALUE).isEmpty()) {
            exifList.add("Brightness: " + getExifTag(exif, ExifInterface.TAG_BRIGHTNESS_VALUE));
        }
        if (!getExifTag(exif, ExifInterface.TAG_CONTRAST).isEmpty()) {
            exifList.add("Contrast: " + getExifTag(exif, ExifInterface.TAG_CONTRAST));
        }
        if (!getExifTag(exif, ExifInterface.TAG_GAMMA).isEmpty()) {
            exifList.add("Gamma: " + getExifTag(exif, ExifInterface.TAG_GAMMA));
        }
        if (!getExifTag(exif, ExifInterface.TAG_SATURATION).isEmpty()) {
            exifList.add("Saturation: " + getExifTag(exif, ExifInterface.TAG_SATURATION));
        }
        if (!getExifTag(exif, ExifInterface.TAG_SHARPNESS).isEmpty()) {
            exifList.add("Sharpness: " + getExifTag(exif, ExifInterface.TAG_SHARPNESS));
        }
        if (!getExifTag(exif, ExifInterface.TAG_FOCAL_LENGTH).isEmpty()) {
            exifList.add("Focal Length: " + getExifTag(exif, ExifInterface.TAG_FOCAL_LENGTH));
        }
        if (!getExifTag(exif, ExifInterface.TAG_ISO_SPEED).isEmpty()) {
            exifList.add("ISO Speed: " + getExifTag(exif, ExifInterface.TAG_ISO_SPEED));
        }
        if (!getExifTag(exif, ExifInterface.TAG_SHUTTER_SPEED_VALUE).isEmpty()) {
            exifList.add("Shutter Speed: " + getExifTag(exif, ExifInterface.TAG_SHUTTER_SPEED_VALUE));
        }

        String location = GetLocationExif(exif);
        if (!location.isEmpty()){
            exifList.add("Location: " + location);
        }

        return exifList;
    }

    private String GetLocationExif(ExifInterface exif) {

        try {
            String latitudeRef, longitudeRef;
            Float latitude = 0.f;
            Float longitude = 0.f;

            if (exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) != null) {
                latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                if (latitudeRef.equals("N")) {
                    latitude = convertFromDegreeMinuteSeconds(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                } else {
                    latitude = 0 - convertFromDegreeMinuteSeconds(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                }
            }

            if (exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF) != null) {
                longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                if (longitudeRef.equals("E")) {
                    longitude = convertFromDegreeMinuteSeconds(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                } else {
                    longitude = 0 - convertFromDegreeMinuteSeconds(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                }
            }

            String location = new LocationFetch(latitude, longitude).execute().get();

            JSONObject jsonObj = new JSONObject(location);
            return jsonObj.getString("display_name");

        } catch (Exception e) {
            return "";
        }


    }

    private static Float convertFromDegreeMinuteSeconds(String stringDMS) {
        Float result;
        String[] DMS = stringDMS.split(",", 3);
        String[] stringD = DMS[0].split("/", 2);
        Double D0 = Double.valueOf(stringD[0]);
        Double D1 = Double.valueOf(stringD[1]);
        Double FloatD = D0 / D1;
        String[] stringM = DMS[1].split("/", 2);
        Double M0 = Double.valueOf(stringM[0]);
        Double M1 = Double.valueOf(stringM[1]);
        Double FloatM = M0 / M1;
        String[] stringS = DMS[2].split("/", 2);
        Double S0 = Double.valueOf(stringS[0]);
        Double S1 = Double.valueOf(stringS[1]);
        Double FloatS = S0 / S1;
        result = (float) (FloatD + (FloatM / 60) + (FloatS / 3600));
        return result;
    }

    private String getExifTag(ExifInterface exif, String tag) {
        String attribute = exif.getAttribute(tag);

        return (null != attribute && !attribute.equals("0") ? attribute : "");
    }
}
