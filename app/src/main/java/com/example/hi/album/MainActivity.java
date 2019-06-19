package com.example.hi.album;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MainCallbacks {

    public static Map<String, Long> funcExecuteTime = new HashMap<>();

    //***Sử dụng khi bắt sự kiện trong Context Menu***//
    AdapterView.AdapterContextMenuInfo info;

    Toolbar toolbar;
    TabLayout tabLayout;

    static ViewPager viewPager;
    static PagerAdapter pagerAdapter;

    AnhFragment anhFragment;
    AlbumFragment albumFragment;

    static boolean SeleteAlbum = false;

    //***Lưu tên các Album***//
    static public ArrayList<String> MangTen;
    //***Status thể hiện hai trang thái show ảnh:***//
    //*     +True: có checkbox                     *//
    //*     +False: không checkbox                 *//
    //**********************************************//
    static boolean status = true;
    static boolean statusalbum = false;
    static int pos=0;
    //***mang là mảng 2 chiều lưu các ảnh của từng album***//
    static public ArrayList<ArrayList<Hinh>> mang;

    //***Lưu các ảnh và album được check trong App***//
    static public ArrayList<Hinh> collectedimgs;
    static public ArrayList<ThongtinAlbum> collectedalbums;

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //***Ánh xạ***//
        long start = System.currentTimeMillis();
        anhxa();
        long end = System.currentTimeMillis();
        funcExecuteTime.put("Anh Xa", end - start);

        //***Bắt sự kiện Click các tab***//
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //***Bắt sự kiện trượt các View***//
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                tabLayout.getTabAt(position).select();
                if (position == 0) {
                    if (status == true) {
                        toolbar.getMenu().getItem(0).setVisible(false);
                        toolbar.getMenu().getItem(1).setVisible(true);
                        toolbar.getMenu().getItem(2).setVisible(true);
                        toolbar.getMenu().getItem(3).setVisible(true);
                        toolbar.getMenu().getItem(4).setVisible(true);
                        toolbar.getMenu().getItem(5).setVisible(true);
                    } else {
                        toolbar.getMenu().getItem(0).setVisible(true);
                        toolbar.getMenu().getItem(1).setVisible(false);
                        toolbar.getMenu().getItem(2).setVisible(false);
                        toolbar.getMenu().getItem(3).setVisible(false);
                        toolbar.getMenu().getItem(4).setVisible(false);
                        toolbar.getMenu().getItem(5).setVisible(false);
                    }
                } else {
                    if (statusalbum == true) {
                        toolbar.getMenu().getItem(0).setVisible(false);
                        toolbar.getMenu().getItem(1).setVisible(false);
                        toolbar.getMenu().getItem(2).setVisible(true);
                        toolbar.getMenu().getItem(3).setVisible(false);
                        toolbar.getMenu().getItem(4).setVisible(false);
                        toolbar.getMenu().getItem(5).setVisible(true);
                    } else {
                        {
                            toolbar.getMenu().getItem(0).setVisible(true);
                            toolbar.getMenu().getItem(1).setVisible(false);
                            toolbar.getMenu().getItem(2).setVisible(false);
                            toolbar.getMenu().getItem(3).setVisible(false);
                            toolbar.getMenu().getItem(4).setVisible(false);
                            toolbar.getMenu().getItem(5).setVisible(false);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //***Lấy id của item được chọn trong menu***//
                int i = item.getItemId();
                //*** Xử lý khi select***//
                if (i == R.id.Select) {
                    //***Xử lý select ảnh***//
                    if (viewPager.getCurrentItem() == 0) {


                        //***Cập nhật status để show ảnh có checkbox***//
                        status = true;

                        //***Clear collectedimgs***//
                        collectedimgs.clear();

                        //***Show lại viewPager***//
                        viewPager.setAdapter(pagerAdapter);

                        //***Show những lựa chọn cần thiết sau khi Select***//
                        toolbar.getMenu().getItem(0).setVisible(false);
                        toolbar.getMenu().getItem(1).setVisible(true);
                        toolbar.getMenu().getItem(2).setVisible(true);
                        toolbar.getMenu().getItem(3).setVisible(true);
                        toolbar.getMenu().getItem(4).setVisible(true);
                        toolbar.getMenu().getItem(5).setVisible(true);
                    }
                    //***Xử lý select album***//
                    else {
                        //***Cập nhật status để show album có checkbox***//
                        statusalbum = true;

                        //***Clear collectedalbums***//
                        collectedalbums.clear();

                        //***Show lại viewPager***//
                        viewPager.setAdapter(pagerAdapter);

                        //***Vỉ show lại view pager mặc định sẽ quay về Anhfragment***//
                        //***Nên phải xử lý để giữ nguyên màn hình tại AlbumFragment***//
                        viewPager.setCurrentItem(1);

                        //***Show những lựa chọn cần thiết sau khi Select***//
                        toolbar.getMenu().getItem(0).setVisible(false);
                        toolbar.getMenu().getItem(1).setVisible(false);
                        toolbar.getMenu().getItem(2).setVisible(true);
                        toolbar.getMenu().getItem(3).setVisible(false);
                        toolbar.getMenu().getItem(4).setVisible(false);
                        toolbar.getMenu().getItem(5).setVisible(true);

                    }

                } else if (i == R.id.addalbum) {
                    if (MainActivity.mang.size() == 0) {
                        //Toast.makeText(MainActivity.this, "Không có album để add!", //Toast.LENGTH_SHORT).show();
                    } else {
                        SeleteAlbum = true;
                        status = false;
                        viewPager.setAdapter(pagerAdapter);
                        viewPager.setCurrentItem(1);
                        tabLayout.getTabAt(1).select();
                    }
                } else if (i == R.id.createalbum) {
                    //***Show diaalog để đặt tên cho album***//
                    final Dialog dialog1;
                    dialog1 = new Dialog(MainActivity.this);
                    dialog1.setTitle("Nhập tên: ");
                    dialog1.setContentView(R.layout.dialog_rename_album);
                    dialog1.show();

                    //***Khai báo và ánh xạ các control trong dialog***//
                    final EditText edt;
                    final Button btnOK, btnCancel;
                    edt = dialog1.findViewById(R.id.nameedt);
                    btnOK = dialog1.findViewById(R.id.okbtn);
                    btnCancel = dialog1.findViewById(R.id.cancelbtn);

                    //***Bắt sự kiện click button OK***//


                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //***Kiểm tra tên có đúng định dạnh chưa***//
                            if (MangTen.contains(edt.getText().toString()) || (edt.getText().toString().contains("$")) || (edt.getText().toString().contains("#"))) {
                                //***Không đúng định dạng hoặc trùng tên***//
                                edt.setText("");
                                //Toast.makeText(MainActivity.this, "Nhập lại tên!!", //Toast.LENGTH_SHORT).show();
                            } else {
                                //***Đúng định dạng***//

                                //***Thêm tên album vào MangTen***//
                                MangTen.add(edt.getText().toString());
                                //***Cập nhật bộ nhớ***//
                                ghivaobonhotrongtenalbum();
                                //***Xét điều kiện khi Create album***//
                                if (collectedimgs.size() == 0) {
                                    //***Không có ảnh nào thì Toast thông báo***//
                                    //Toast.makeText(MainActivity.this, "Chua chon anh", //Toast.LENGTH_SHORT).show();
                                } else {
                                    //***Thêm album vào mang***//
                                    mang.add(new ArrayList<Hinh>(collectedimgs));

                                    //***Cập nhật bộ nhớ***//
                                    ghivaobonhotrong();

                                    //***Toast thông báo số lượng ảnh trong Album vừa tạo***//
                                    //Toast.makeText(MainActivity.this, "Kích Thước: " + collectedimgs.size(), //Toast.LENGTH_SHORT).show();

                                }

                                //***Show lại ViewPager***//
                                status = false;
                                viewPager.setAdapter(pagerAdapter);

                                //***Tắt dialog***//
                                dialog1.dismiss();

                                //***Show những lựa chọn cần thiết sau khi Canccel***//
                                toolbar.getMenu().getItem(0).setVisible(true);
                                toolbar.getMenu().getItem(1).setVisible(false);
                                toolbar.getMenu().getItem(2).setVisible(false);
                                toolbar.getMenu().getItem(3).setVisible(false);
                                toolbar.getMenu().getItem(4).setVisible(false);
                                toolbar.getMenu().getItem(5).setVisible(false);
                            }


                        }
                    });
                    //***Bắt sự kiện click button Cancel***//

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                            status = false;
                            viewPager.setAdapter(pagerAdapter);
                            //***Show những lựa chọn cần thiết sau khi Canccel***//
                            toolbar.getMenu().getItem(0).setVisible(true);
                            toolbar.getMenu().getItem(1).setVisible(false);
                            toolbar.getMenu().getItem(2).setVisible(false);
                            toolbar.getMenu().getItem(3).setVisible(false);
                            toolbar.getMenu().getItem(4).setVisible(false);
                            toolbar.getMenu().getItem(5).setVisible(false);
                        }
                    });
                } else if (i == R.id.createSlideshow) {
                    Intent intent = new Intent(getBaseContext(), Image_Slideshow.class);
                    toolbar.getMenu().getItem(0).setVisible(true);
                    toolbar.getMenu().getItem(1).setVisible(false);
                    toolbar.getMenu().getItem(2).setVisible(false);
                    toolbar.getMenu().getItem(3).setVisible(false);
                    toolbar.getMenu().getItem(4).setVisible(false);
                    toolbar.getMenu().getItem(5).setVisible(false);
                    ArrayList<String> data = new ArrayList<String>();
                    if (MainActivity.collectedimgs.size() != 0) {
                        for (Hinh c : MainActivity.collectedimgs) {
                            data.add(c.duongdan);
                        }
                        intent.putExtra("data", data);
                        startActivity(intent);
                    }

                    MainActivity.status = false;
                    viewPager.setAdapter(pagerAdapter);
                } else if (i == R.id.back) {
                    //***Xử lí Back trong Anhfragment***//
                    if (viewPager.getCurrentItem() == 0) {
                        //***Cập nhật Status để show ảnh không checkbox***//
                        status = false;

                        //***Show lại ViewPager***//
                        viewPager.setAdapter(pagerAdapter);

                        //***giai phong collectedimgs***//;
                        collectedimgs.clear();

                        //***Show những lựa chọn cần thiết sau khi back***//
                        toolbar.getMenu().getItem(0).setVisible(true);
                        toolbar.getMenu().getItem(1).setVisible(false);
                        toolbar.getMenu().getItem(2).setVisible(false);
                        toolbar.getMenu().getItem(3).setVisible(false);
                        toolbar.getMenu().getItem(4).setVisible(false);
                        toolbar.getMenu().getItem(5).setVisible(false);
                    }
                    //***Xử lí Back trong Albumfragment***//
                    else {
                        //***Cập nhật Status để show Album không checkbox***//
                        statusalbum = false;

                        //***Show lại ViewPager***//
                        viewPager.setAdapter(pagerAdapter);

                        //***Vỉ show lại view pager mặc định sẽ quay về Anhfragment***//
                        //***Nên phải xử lý để giữ nguyên màn hình tại AlbumFragment***//
                        viewPager.setCurrentItem(1);

                        //***giai phong collectedalbums***//;
                        collectedalbums.clear();

                        //***Show những lựa chọn cần thiết sau khi back***//
                        toolbar.getMenu().getItem(0).setVisible(true);
                        toolbar.getMenu().getItem(1).setVisible(false);
                        toolbar.getMenu().getItem(2).setVisible(false);
                        toolbar.getMenu().getItem(3).setVisible(false);
                        toolbar.getMenu().getItem(4).setVisible(false);
                        toolbar.getMenu().getItem(5).setVisible(false);
                    }
                } else if (i == R.id.delete) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn có muốn xóa không?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //***Xử lý khi click delete AnhFragment***//
                            if (viewPager.getCurrentItem() == 0) {
                                //***Cập nhật Status để show ảnh không checkbox***//
                                status = !status;

                                //***Xét điều kiện khi Delete***//
                                if (collectedimgs.size() == 0) {
                                    //***Không có ảnh nào thì Toast thông báo***//
                                    //Toast.makeText(MainActivity.this, "Chua chon anh", //Toast.LENGTH_SHORT).show();
                                } else {
                                    //***Xóa tận gốc các ảnh trong collectedimgs thông qua đường dẫn***//
                                    for (int j = 0; j < collectedimgs.size(); j++) {
                                        File file = new File(collectedimgs.get(j).getDuongdan());
//                                        boolean flag = file.delete();
                                        if(file.exists()){
                                            try {
                                                file.getCanonicalFile().delete();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            if(file.exists()){
                                                getApplicationContext().deleteFile(file.getName());
                                            }
                                        }
                                        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                    }
                                    //***Cập nhật album trong mang, MangTen và bộ nhớ***//
                                    refreshfAlbum(collectedimgs);
                                }

                                //***Show lại ViewPager***//
                                viewPager.setAdapter(pagerAdapter);

                                //***Show những lựa chọn cần thiết sau khi Delete***//
                                toolbar.getMenu().getItem(0).setVisible(true);
                                toolbar.getMenu().getItem(1).setVisible(false);
                                toolbar.getMenu().getItem(2).setVisible(false);
                                toolbar.getMenu().getItem(3).setVisible(false);
                                toolbar.getMenu().getItem(4).setVisible(false);
                                toolbar.getMenu().getItem(5).setVisible(false);
                            }

                            //***Xử lý khi click delete AbumFragment***//
                            else {
                                //***delete Album trong MangTen va mang***//
                                for (int j = 0; j < collectedalbums.size(); j++) {
                                    for (int i1 = 0; i1 < MangTen.size(); i1++) {
                                        if (MangTen.get(i1).toString().equals(collectedalbums.get(j).getTen())) {
                                            MangTen.remove(i1);
                                            mang.remove(i1);
                                        }
                                    }
                                }
//                                for(int i1=0;i1<collectedimgs.size();i1++)
//                                {
//                                    for(int j=0;j<AnhFragment.mangHinh.size();j++)
//                                    {
//                                        if(AnhFragment.mangHinh.get(j).getDuongdan().equals(collectedimgs.get(i1).duongdan))
//                                        {
//                                            AnhFragment.mangHinh.remove(j);
//                                        }
//                                    }
//                                }
                                //***Cập nhật vào bộ nhớ***//
                                ghivaobonhotrongtenalbum();
                                ghivaobonhotrong();

                                //***Show lại ViewPager***//
                                statusalbum = false;
                                viewPager.setAdapter(pagerAdapter);
                                viewPager.setCurrentItem(1);

                                //***Show những lựa chọn cần thiết sau khi Delete***//
                                toolbar.getMenu().getItem(0).setVisible(true);
                                toolbar.getMenu().getItem(1).setVisible(false);
                                toolbar.getMenu().getItem(2).setVisible(false);
                                toolbar.getMenu().getItem(3).setVisible(false);
                                toolbar.getMenu().getItem(4).setVisible(false);
                                toolbar.getMenu().getItem(5).setVisible(false);
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }

                return true;
            }
        });


    }

    @Override
    //***Xử lý khi nhận back***//
    public void onBackPressed() {
        //***Tắt chế độ select***//
        if (MainActivity.status == true) {

            MainActivity.collectedimgs.clear();
            MainActivity.status = false;
            //***Show lại ViewPager***//
            viewPager.setAdapter(pagerAdapter);

            //***Show những lựa chọn cần thiết sau khi Delete***//
            toolbar.getMenu().getItem(0).setVisible(true);
            toolbar.getMenu().getItem(1).setVisible(false);
            toolbar.getMenu().getItem(2).setVisible(false);
            toolbar.getMenu().getItem(3).setVisible(false);
            toolbar.getMenu().getItem(4).setVisible(false);
            toolbar.getMenu().getItem(5).setVisible(false);

        } else if (MainActivity.statusalbum == true) {

            MainActivity.collectedalbums.clear();
            MainActivity.statusalbum = false;
            //***Show lại ViewPager***//
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(1);

            //***Show những lựa chọn cần thiết sau khi Delete***//
            toolbar.getMenu().getItem(0).setVisible(true);
            toolbar.getMenu().getItem(1).setVisible(false);
            toolbar.getMenu().getItem(2).setVisible(false);
            toolbar.getMenu().getItem(3).setVisible(false);
            toolbar.getMenu().getItem(4).setVisible(false);
            toolbar.getMenu().getItem(5).setVisible(false);
        } else {
            finish();
        }
    }

    //***Hàm cập nhật album trong mang, MangTen và bộ nhớ***//
    public void refreshfAlbum(ArrayList<Hinh> collectedimgs) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < collectedimgs.size(); i++) {
            for (int j = 0; j < MainActivity.mang.size(); j++)
                for (int j1 = 0; j1 < MainActivity.mang.get(j).size(); j1++) {
                    if (MainActivity.mang.get(j).get(j1).getDuongdan().equals(collectedimgs.get(i).getDuongdan())) {
                        MainActivity.mang.get(j).remove(j1);
                    }
                }
        }
        for (int i = 0; i < collectedimgs.size(); i++) {
            for (int j = 0; j < AnhFragment.mangHinh.size(); j++) {
                if (AnhFragment.mangHinh.get(j).getDuongdan().equals(collectedimgs.get(i).duongdan)) {
                    AnhFragment.mangHinh.remove(j);
                }
            }
        }
        for (int i = 0; i < mang.size(); i++) {
            if (mang.get(i).size() == 0) {
                mang.remove(i);
                MangTen.remove(i);
            }
        }
        ghivaobonhotrong();
        ghivaobonhotrongtenalbum();

        funcExecuteTime.put("Refresh Album", System.currentTimeMillis() - start);
    }

    private void anhxa() {


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        status = !toolbar.getMenu().getItem(0).isVisible();

        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("Ảnh"));
        tabLayout.addTab(tabLayout.newTab().setText("Album"));


        //***Xin cấp quyền***//
//kiem tra xem version la truoc hay sau M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//bang M
            //kiem tra da xin quyen va duoc chap nhan chua
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
// if nay la cap roi
                //Toast.makeText(MainActivity.this, "da duoc cap roi", //Toast.LENGTH_SHORT).show();

            } else { //else la chua cap quyen
                //Toast.makeText(MainActivity.this, "can kiem tra, hien dialog xin quyen", //Toast.LENGTH_SHORT).show();
//hien dialog de xin quyen, goi onRequestPermissionResult
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        mang = new ArrayList<ArrayList<Hinh>>();
        MangTen = new ArrayList<String>();

        docvaobonhotrong();
        doctubonhotrongtenalbum();

        collectedimgs = new ArrayList<Hinh>();
        collectedalbums = new ArrayList<ThongtinAlbum>();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        anhFragment = (AnhFragment) pagerAdapter.getItem(0);
        albumFragment = (AlbumFragment) pagerAdapter.getItem(1);


    }

    public void ghivaobonhotrong() {

        try {
            //bo nho trong
            //FileOutputStream out=openFileOutput("abc.txt",MODE_PRIVATE);

            //bo nho trong, cache
            File duongdan = getCacheDir();
            File taptin = new File(duongdan, "imgofalbum.txt");
            Log.d("lienket", taptin + "");

            FileOutputStream out = new FileOutputStream(taptin);
            String buffer = new String();
            for (int i = 0; i < mang.size(); i++) {
                for (int j = 0; j < mang.get(i).size(); j++) {
                    if (j == mang.get(i).size() - 1) {
                        buffer = buffer + mang.get(i).get(j).getDuongdan().toString()
                                + "#"
                                + mang.get(i).get(j).getTenhinh().toString()
                                + "#"
                                + mang.get(i).get(j).getAddDate().toString()
                        ;
                    } else {
                        buffer = buffer + mang.get(i).get(j).getDuongdan().toString()
                                + "#"
                                + mang.get(i).get(j).getTenhinh().toString()
                                + "#"
                                + mang.get(i).get(j).getAddDate().toString()
                                + "#";
                    }

                }
                buffer += "%";
            }

            Log.e("mang", buffer);
            out.write(buffer.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }


    }

    public void ghivaobonhotrongtenalbum() {
        File duongdan = getCacheDir();
        File file = new File(duongdan, "nameofalbum.txt");
        if (file.exists())
            file.delete();

        file = new File(duongdan, "nameofalbum.txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            String buffer = new String();
            for (int i = 0; i < MangTen.size(); i++) {
                if (i != MainActivity.MangTen.size() - 1)
                    buffer += MainActivity.MangTen.get(i) + "#";
                else
                    buffer += MainActivity.MangTen.get(MainActivity.MangTen.size() - 1);
            }


            Log.d("ALBUM","ALBUMNAME= MainActivity" + buffer);
            fileOutputStream.write(buffer.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void docvaobonhotrong() {
        try {
            //bo nho trong
            //FileInputStream in=openFileInput("abc.txt");

            //bo nho trong, cache
            File duongdan = getCacheDir();
            File taptin = new File(duongdan, "imgofalbum.txt");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(taptin));
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            String tmp = new String(buffer);
            String splitalbum[] = tmp.split("%");

            for (int i = 0; i < splitalbum.length; i++) {
                if (splitalbum[i] != "") {
                    mang.add(new ArrayList<Hinh>());
                    String splitimg[] = splitalbum[i].split("#");
                    for (int j = 0; j < splitimg.length; j = j + 3) {
                        mang.get(mang.size() - 1).add(new Hinh(splitimg[j], splitimg[j + 1], Integer.parseInt(splitimg[j + 2])));
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }

    }

    public void doctubonhotrongtenalbum() {
        try {
            //bo nho trong
            //FileInputStream in=openFileInput("abc.txt");

            //bo nho trong, cache
            File duongdan = getCacheDir();
            File taptin = new File(duongdan, "nameofalbum.txt");
            FileInputStream in = new FileInputStream(taptin);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            String tmp = new String(buffer);
            String splitalbum[] = tmp.split("#");

            for (int i = 0; i < splitalbum.length; i++) {
                if (splitalbum[i] != "")
                    MangTen.add(splitalbum[i]);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_popup_item_listview, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.renamealbum: {
                //Toast.makeText(this, "rename"+info.position, //Toast.LENGTH_SHORT).show();
                //***show Dialog để nhập tên mún thay đổi***//
                final Dialog dialog;
                dialog = new Dialog(MainActivity.this);
                dialog.setTitle("Nhập tên: ");
                dialog.setContentView(R.layout.dialog_rename_album);
                dialog.show();
                final EditText edt;
                Button btnOK, btnCancel;
                edt = dialog.findViewById(R.id.nameedt);
                btnOK = dialog.findViewById(R.id.okbtn);
                btnCancel = dialog.findViewById(R.id.cancelbtn);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MangTen.set(MangTen.size() - 1 - info.position, edt.getText().toString());
                        ghivaobonhotrongtenalbum();
                        dialog.dismiss();

                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                break;
            }
            case R.id.deleteAllAlbum: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn có muốn xóa không?");
                builder.setCancelable(false);
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //***Xóa thông tin album***//
                        AlbumFragment.Mang.remove(info.position);
                        AlbumFragment.gvadapter.notifyDataSetChanged();

                        //***Cập nhật mang, MangTen và bộ nhớ***//
                        mang.remove(mang.size() - 1 - info.position);
                        MangTen.remove(MangTen.size() - 1 - info.position);
                        ghivaobonhotrongtenalbum();
                        ghivaobonhotrong();
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Khong duoc cap quyen roi", //Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        for (Map.Entry entry : funcExecuteTime.entrySet()){
            Log.d("Function Time",entry.getKey().toString() + " - " + entry.getValue().toString());
        }

        Log.d("end","Destroyed");
    }
}
