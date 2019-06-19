package com.example.hi.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

//Activity dùng để show tất cả ảnh trong điện thoại theo ngày chụp
public class AnhFragment extends android.support.v4.app.Fragment implements FragmentCallbacks {

    Context context = null;
    static final Uri Image_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    static ArrayList<Hinh> mangHinh = new ArrayList<>();

    static ArrayList<ArrayList<Hinh>> mangHinhDate = new ArrayList<>();
    static LinearLayoutManager linearLayoutManager = null;

    static public RecyclerView listView;
    CustomListviewImageAdapter customListviewImageAdapter;

    View view;

    @Nullable

    @Override
    public void onResume() {
        super.onResume();

        //***Khởi tạo các mảng***//
        Map<Integer, ArrayList<Hinh>> mapImage = new TreeMap<>(Collections.<Integer>reverseOrder());
        mangHinh = new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor cursor = contentResolver.query(Image_URI, null, null, null, null);
        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
            final String duongdanhinhanh = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String tenhinh = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

            File TempFiles = new File(duongdanhinhanh);
            Date lastModDate = new Date(TempFiles.lastModified());
            SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
            Integer dateText = Integer.valueOf(df2.format(lastModDate));
            if (TempFiles.exists()) {
                Hinh currentImage = new Hinh(duongdanhinhanh, tenhinh, dateText);
                mangHinh.add(currentImage);
                if (mapImage.containsKey(dateText)) {
                    {
                        mapImage.get(dateText).add(currentImage);
                    }
                } else {
                    ArrayList<Hinh> temp = new ArrayList<>();
                    temp.add(currentImage);
                    mapImage.put(dateText, temp);
                }

            }
            cursor.moveToPrevious();
        }

        cursor.close();

        mangHinhDate.clear();
        mangHinhDate.addAll(mapImage.values());

        customListviewImageAdapter = new CustomListviewImageAdapter(context, mangHinhDate, R.layout.custom_item_listview_img);
        listView.setAdapter(customListviewImageAdapter);
        listView.setLayoutManager(linearLayoutManager);
        //listView.scrollToPosition(ImageActivity.position);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        view = inflater.inflate(R.layout.anh_layout, container, false);
        listView = (RecyclerView) view.findViewById(R.id.lvimg);

        return view;
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {
    }
}
