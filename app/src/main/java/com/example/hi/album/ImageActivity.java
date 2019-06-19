package com.example.hi.album;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hi.album.imageeditor.EditImageActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import id.zelory.compressor.Compressor;
//Activity dùng đề show 1 ảnh được chọn
// Activity được gọi Intent từ AlbumActivity hoặc AnhFragment

public class ImageActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPagerFixer viewPager;
    Button btn_Delete;
    Button btn_Share;
    Button btn_Crop;
    Button btn_Resize;
    Button btn_Edit;


    static public int position = 0;
    String diachi;
    boolean loai;
    static public boolean co = false;

    public static Hinh currentImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //nhan thong tin tu activity truoc
        final Intent intent = getIntent();
        diachi = intent.getStringExtra("vitri");
        loai = intent.getBooleanExtra("loai", false);
        //

        Anhxa();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewPager.setCurrentItem(position);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.compress) {
                    compressImage();
                } else if (i == R.id.metadata) {
                    showMetadata();
                }

                return false;
            }
        });
        //Sự kiện crop ảnh
        btn_Crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        //Xử lý sự kiện Share ảnh
        btn_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tmp = viewPager.getCurrentItem();
                Hinh choosenImage = AnhFragment.mangHinh.get(tmp);
                try {
                    File photoFile = new File(choosenImage.duongdan);
                    Uri imageUri = FileProvider.getUriForFile(ImageActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);
                    final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    startActivity(Intent.createChooser(intent, "Share via"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //Sự kiện resize ảnh
        btn_Resize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resizeImage();
            }
        });

        //*************************************************
        //* Xử lí sự kiện xóa ảnh trong hai trường hợp    *
        //*     I:  ảnh của Anhfragment                   *
        //*     II: ảnh của Album                         *
        //*************************************************
        btn_Delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Dialog confirmDialog = new Dialog(ImageActivity.this);
                confirmDialog.setTitle("Xác nhận xóa ảnh");
                confirmDialog.setContentView(R.layout.delete_dialog);
                confirmDialog.show();
                Button btnOK = confirmDialog.findViewById(R.id.btnDeleteOk);
                Button btnCancel = confirmDialog.findViewById(R.id.btnDeleteCancel);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                    }
                });

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteImage();
                        confirmDialog.dismiss();
                    }
                });
            }
        });

        final Intent editIntent = new Intent(getApplicationContext(), EditImageActivity.class);

        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImage = AnhFragment.mangHinh.get(viewPager.getCurrentItem());
                startActivity(editIntent);
            }
        });
    }

    private void showMetadata() {
        try {
            currentImage = AnhFragment.mangHinh.get(viewPager.getCurrentItem());

            ListView listView = new ListView(this);

            ExifUtility exif = new ExifUtility();
            String[] items = exif.getExif(currentImage.exif).toArray(new String[0]);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listview_metadata, R.id.txtitem, items);
            listView.setAdapter(adapter);

            AlertDialog.Builder builder = new
                    AlertDialog.Builder(ImageActivity.this);
            builder.setCancelable(true);
            builder.setPositiveButton("OK", null);
            builder.setView(listView);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        catch (Exception e){

        }

    }


    private void editBitmap(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            file.setReadable(true, false);
            file.setWritable(true, false);
            /*FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();*/
            CropImage.activity(Uri.fromFile(file))
                    .start(this);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap bm = null;
                InputStream is = null;
                String url = resultUri.toString();
                BufferedInputStream bis = null;
                try {
                    URLConnection conn = new URL(url).openConnection();
                    conn.connect();
                    is = conn.getInputStream();
                    bis = new BufferedInputStream(is, 8192);
                    bm = BitmapFactory.decodeStream(bis);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                File file = new File(resultUri.getPath());


                try {
                    FileOutputStream out = new FileOutputStream(file);
                    file.setReadable(true, false);
                    file.setWritable(true, false);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onBackPressed() {
        for(int i=0;i<AnhFragment.mangHinhDate.size();i++)
        {
            for(int j=0;j<AnhFragment.mangHinhDate.get(i).size();j++)
            {
                if(AnhFragment.mangHinhDate.get(i).get(j).duongdan==AnhFragment.mangHinh.get(viewPager.getCurrentItem()).getDuongdan())
                {
                    position=i;
                    break;
                }
            }
        }
        super.onBackPressed();
    }

    private void Anhxa() {

        toolbar = findViewById(R.id.toolbarofimgactivity);

        btn_Delete = findViewById(R.id.delete_btn);
        btn_Share = (Button) findViewById(R.id.share_btn);
        btn_Crop = (Button) findViewById(R.id.crop_btn);
        btn_Resize = (Button) findViewById(R.id.Resize_btn);
        btn_Edit = (Button) findViewById(R.id.edit_btn);
//        //Tạo nút back trên toolbar
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.inflateMenu(R.menu.menu_image);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        viewPager = (ViewPagerFixer) findViewById(R.id.viewpagerofimgactivity);

        if (loai == false) {
            //********************************************
            //*Xử lí trường hợp: Xuất ảnh của Anhfragment*
            //********************************************
            for (int i = 0; i < AnhFragment.mangHinh.size(); i++) {
                if (diachi.equals(AnhFragment.mangHinh.get(i).getDuongdan())) {
                    position = i;
                    break;
                }
            }
            ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(AnhFragment.mangHinh, this);
            viewPager.setAdapter(imagePagerAdapter);
        } else {
            //****************************************
            //*Xử lí trường hợp: Xuất ảnh của 1 album*
            //****************************************
            for (int i = 0; i < MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).size(); i++) {
                if (diachi.equals(MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).get(i).getDuongdan())) {
                    position = i;
                    break;
                }
            }
            ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum), this);
            viewPager.setAdapter(imagePagerAdapter);

        }


    }

    //*** Hàm dùng để cập nhật mảng chứa các album sau khi xóa và ghi lại vào bộ nhớ***//
    public void refreshfAlbum(ArrayList<Hinh> collectedimgs) {


        //***Các ảnh cần xóa nằm trong collectedimgs***//                                         *


        for (int i = 0; i < collectedimgs.size(); i++) {
            for (int j = 0; j < MainActivity.mang.size(); j++)
                for (int j1 = 0; j1 < MainActivity.mang.get(j).size(); j1++) {
                    if (MainActivity.mang.get(j).get(j1).getDuongdan().equals(collectedimgs.get(i).getDuongdan())) {
                        MainActivity.mang.get(j).remove(j1);
                    }
                }
        }


    }

    //*** Hàm dùng để ghi lại danh sách album vào bộ nhớ***//
    public void ghivaobonhotrong() {

        try {
            //***Lấy đường dẫn bộ nhớ Cache***//
            File duongdan = getCacheDir();
            //***Đặt tên file chứa thông tin album***//
            File taptin = new File(duongdan, "imgofalbum.txt");
            //***Tạo luồng ghi***//
            FileOutputStream out = new FileOutputStream(taptin);
            //*** Tạo buffer để chứa dự liệu cần ghi vào File "imgofalbum.txt" ***//
            String buffer = new String();
            //*** Tiến hành ghi dữ liệu vào buffer***//

            //**********************************************************************************************************************
            //* Một album được ghi dưới dạng:                                                                                      *
            //*                                                                                                                    *
            //* "duongdan[1]"#"tenhinh[1]"#"date[1]"#"duongdan[2]"#"tenhinh[2]"#"date[2]"#"duongdan[3]"#"tenhinh[4]"#"date[3]"...% *
            //*                                                                                                                    *
            //* Kết thúc 1 album là dấu (  %  )                                                                                    *
            //**********************************************************************************************************************
            for (int i = 0; i < MainActivity.mang.size(); i++) {
                for (int j = 0; j < MainActivity.mang.get(i).size(); j++) {
                    if (j == MainActivity.mang.get(i).size() - 1) {
                        //Trường hợp ảnh cuối cùng của Album
                        //Không có dấu (  #  ) ở cuối
                        buffer = buffer + MainActivity.mang.get(i).get(j).getDuongdan().toString()
                                + "#"
                                + MainActivity.mang.get(i).get(j).getTenhinh().toString()
                                + "#"
                                + MainActivity.mang.get(i).get(j).getAddDate().toString()
                        ;
                    } else {
                        //Trường hợp bình thường
                        //Có dấu (  #  ) ở cuối
                        buffer = buffer + MainActivity.mang.get(i).get(j).getDuongdan().toString()
                                + "#"
                                + MainActivity.mang.get(i).get(j).getTenhinh().toString()
                                + "#"
                                + MainActivity.mang.get(i).get(j).getAddDate().toString()
                                + "#";
                    }

                }
                buffer += "%";
            }
            //*** ghi vào bộ nhớ ***//
            out.write(buffer.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
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
            for (int i = 0; i < MainActivity.MangTen.size(); i++) {
                if (i != MainActivity.MangTen.size() - 1)
                    buffer += MainActivity.MangTen.get(i) + "#";
                else
                    buffer += MainActivity.MangTen.get(MainActivity.MangTen.size() - 1);
            }
            Log.d("ALBUM","ALBUMNAME= ImageActivity" + buffer);
            fileOutputStream.write(buffer.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void resizeImage() {
        int tmp = viewPager.getCurrentItem();
        final Hinh choosenImage = AnhFragment.mangHinh.get(tmp);
        final Dialog dialog1;
        dialog1 = new Dialog(ImageActivity.this);
        dialog1.setTitle("Nhập kích thước: ");
        dialog1.setContentView(R.layout.resize_dialog);
        dialog1.show();
        final EditText Width, Height;
        Button btnOK, btnCancel;
        Width = dialog1.findViewById(R.id.editText_Width);
        Height = dialog1.findViewById(R.id.editText_Height);
        btnOK = dialog1.findViewById(R.id.btnOk);
        btnCancel = dialog1.findViewById(R.id.btnCancel);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int width = Integer.parseInt(Width.getText().toString());
                    int height = Integer.parseInt(Height.getText().toString());

                    File imgFile = new File(choosenImage.duongdan);
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    Bitmap resBitmap = Bitmap.createScaledBitmap(myBitmap, width, height, false);
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "resize_" + UUID.randomUUID().toString() + ".jpeg");

                    f.createNewFile();
                    FileOutputStream out = new FileOutputStream(f);
                    f.setReadable(true, false);
                    f.setWritable(true, false);
                    resBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent mediaScanIntent = new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri = Uri.fromFile(f);
                        mediaScanIntent.setData(contentUri);
                        getBaseContext().sendBroadcast(mediaScanIntent);
                    } else {
                        sendBroadcast(new Intent(
                                Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://"
                                        + Environment.getExternalStorageDirectory())));
                    }
                } catch (NumberFormatException | IOException e) {
                    e.printStackTrace();
                }
                dialog1.dismiss();


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
    }

    private void deleteImage() {
        // luu lai vi tri cua hinh
        int tmp = viewPager.getCurrentItem();

        if (loai == false) {
            //xử lý I

            //Xóa collectedimgs và add đối tượng cần xóa
            MainActivity.collectedimgs.clear();
            MainActivity.collectedimgs.add(AnhFragment.mangHinh.get(tmp));

            // Tìm và xóa ảnh


            for (int j = 0; j < MainActivity.collectedimgs.size(); j++) {
                File file = new File(MainActivity.collectedimgs.get(j).getDuongdan());
                file.delete();
                if (file.exists()) {
                    try {
                        file.getCanonicalFile().delete();
                        if (file.exists()) {
                            getApplicationContext().deleteFile(file.getName());
                        }
                    } catch (IOException e) {

                    }

                }
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            }

            //***Cập nhập mảng chứa album***//
            refreshfAlbum(MainActivity.collectedimgs);
            //*** Xóa album rộng => Xóa Mảng rỗng trong MainActivity.mang ***//
            for (int i = 0; i < MainActivity.mang.size(); i++) {
                if (MainActivity.mang.get(i).size() == 0) {
                    MainActivity.mang.remove(i);
                    MainActivity.MangTen.remove(i);
                }
            }
            for (int i1 = 0; i1 < MainActivity.collectedimgs.size(); i1++) {
                for (int j = 0; j < AnhFragment.mangHinh.size(); j++) {
                    if (AnhFragment.mangHinh.get(j).getDuongdan().equals(MainActivity.collectedimgs.get(i1).duongdan)) {
                        AnhFragment.mangHinh.remove(j);
                    }
                }
            }
            ghivaobonhotrongtenalbum();
            ghivaobonhotrong();
            //su ly sau khi xoa

            ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(AnhFragment.mangHinh, ImageActivity.this);
            viewPager.setAdapter(imagePagerAdapter);

            //*** Tìm vị trị thích hợp để setCurrent cho ViewPager***//
            if (tmp == AnhFragment.mangHinh.size())
                viewPager.setCurrentItem(tmp - 1);
            else
                viewPager.setCurrentItem(tmp);

        } else {
            //xử lý II

            MainActivity.collectedimgs.clear();
            MainActivity.collectedimgs.add(MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).get(viewPager.getCurrentItem()));

            //***Cập nhật mảng chứa các album***//
            if (MainActivity.collectedimgs.size() == 0) {
                //Toast.makeText(getApplicationContext(), "Chua chon anh", //Toast.LENGTH_SHORT).show();
            } else {
                //***Các ảnh cần xóa nằm trong collectedimgs***//                                         *
                for (int i = 0; i < MainActivity.collectedimgs.size(); i++) {
                    for (int j1 = 0; j1 < MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).size(); j1++) {
                        if (MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).get(j1).getDuongdan().equals(MainActivity.collectedimgs.get(i).getDuongdan())) {
                            MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).remove(j1);
                        }
                    }


                }
            }


            //su ly sau khi xoa
            if (MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).size() == 0) {
                //*** Xóa album rộng => Xóa Mảng rỗng trong MainActivity.mang ***//
                for (int i = 0; i < MainActivity.mang.size(); i++) {
                    if (MainActivity.mang.get(i).size() == 0) {
                        MainActivity.mang.remove(i);
                        MainActivity.MangTen.remove(i);
                    }
                }
                ghivaobonhotrongtenalbum();
                ghivaobonhotrong();
                co = true;
                finish();
            } else {
                //*** Xóa album rộng => Xóa Mảng rỗng trong MainActivity.mang ***//
                for (int i = 0; i < MainActivity.mang.size(); i++) {
                    if (MainActivity.mang.get(i).size() == 0) {
                        MainActivity.mang.remove(i);
                        MainActivity.MangTen.remove(i);
                    }
                }
                ghivaobonhotrongtenalbum();
                ghivaobonhotrong();
                ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum), ImageActivity.this);
                viewPager.setAdapter(imagePagerAdapter);
                //*** Tìm vị trị thích hợp để setCurrent cho ViewPager***//
                if (tmp == MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).size() - 1)
                    viewPager.setCurrentItem(tmp - 1);
                else
                    viewPager.setCurrentItem(tmp);
            }
        }

    }

    private void cropImage() {
        int tmp = viewPager.getCurrentItem();
        Hinh choosenImage = (loai) ? MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).get(tmp) : AnhFragment.mangHinh.get(tmp);
        File imgFile = new File(choosenImage.duongdan);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        editBitmap(myBitmap, choosenImage.duongdan);
    }

    private void compressImage() {
        final Dialog dialog1 = new Dialog(ImageActivity.this);
        dialog1.setTitle("Nhập phần trăm muốn nén: ");
        dialog1.setContentView(R.layout.compress_dialog);
        dialog1.show();
        final SeekBar seekBar = (SeekBar) dialog1.findViewById(R.id.seekBar);
        final TextView textView = (TextView) dialog1.findViewById(R.id.textView_per);
        final EditText editText = (EditText) dialog1.findViewById(R.id.editText_per);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    //Update Seekbar value after entering a number
                    if (s.toString().equals("")) {
                        seekBar.setProgress(0);
                    } else seekBar.setProgress(Integer.parseInt(s.toString()));
                } catch (Exception ex) {

                }

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText("" + progress + "%");
                editText.setText("" + seekBar.getProgress());
                editText.setSelection(editText.getText().length());


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value = seekBar.getProgress();


            }
        });
        Button btn_OK, btn_Cancel;
        btn_OK = (Button) dialog1.findViewById(R.id.btn_OK);
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loai == false) {
                    int tmp = viewPager.getCurrentItem();
                    Hinh choosenImage = AnhFragment.mangHinh.get(tmp);
                    File imgFile = new File(choosenImage.duongdan);

                    try {

                        int Quality = 0;
                        if (editText.getText().toString().equals("")) {
                            Quality = 5;
                        } else {
                            Quality = Integer.parseInt(editText.getText().toString()) / 10;
                        }
                                /*BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = Quality;
                                Bitmap myBm= BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);*/
                        Bitmap mySample = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        Bitmap myBm = new Compressor(getBaseContext()).setMaxWidth(mySample.getWidth() / Quality).setMaxHeight(mySample.getHeight() / Quality).compressToBitmap(imgFile);
                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "compress_" + UUID.randomUUID().toString() + ".jpeg");
                        f.createNewFile();
                        FileOutputStream out = new FileOutputStream(f);
                        f.setReadable(true, false);
                        f.setWritable(true, false);
                        myBm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent mediaScanIntent = new Intent(
                                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(f);
                            mediaScanIntent.setData(contentUri);
                            getBaseContext().sendBroadcast(mediaScanIntent);
                        } else {
                            sendBroadcast(new Intent(
                                    Intent.ACTION_MEDIA_MOUNTED,
                                    Uri.parse("file://"
                                            + Environment.getExternalStorageDirectory())));
                        }
                        dialog1.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    int tmp = viewPager.getCurrentItem();
                    Hinh choosenImage = MainActivity.mang.get(AlbumFragment.postionofFocusingAlbum).get(tmp);
                    File imgFile = new File(choosenImage.duongdan);

                    try {

                        int Quality = 0;
                        if (editText.getText().toString().equals("")) {
                            Quality = 5;
                        } else {
                            Quality = Integer.parseInt(editText.getText().toString()) / 10;
                        }
                                /*BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = Quality;
                                Bitmap myBm= BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);*/
                        Bitmap mySample = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        Bitmap myBm = new Compressor(getBaseContext()).setMaxWidth(mySample.getWidth() / Quality).setMaxHeight(mySample.getHeight() / Quality).compressToBitmap(imgFile);
                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "compress_" + UUID.randomUUID().toString() + ".jpeg");
                        f.createNewFile();
                        FileOutputStream out = new FileOutputStream(f);
                        f.setReadable(true, false);
                        f.setWritable(true, false);
                        myBm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent mediaScanIntent = new Intent(
                                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(f);
                            mediaScanIntent.setData(contentUri);
                            getBaseContext().sendBroadcast(mediaScanIntent);
                        } else {
                            sendBroadcast(new Intent(
                                    Intent.ACTION_MEDIA_MOUNTED,
                                    Uri.parse("file://"
                                            + Environment.getExternalStorageDirectory())));
                        }
                        dialog1.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btn_Cancel = (Button) dialog1.findViewById(R.id.btn_Cancel);
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
    }
}

