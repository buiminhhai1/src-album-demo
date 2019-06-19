package com.example.hi.album;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
//***Fragment dùng để show danh sách album***//
public class AlbumFragment extends android.support.v4.app.Fragment implements FragmentCallbacks{


    GridView gridView;
    static public CustomListviewAdapter gvadapter;

   //***Mang chứa class thông tin của album***//
    static public ArrayList<ThongtinAlbum> Mang;
    //***Vị trí của mảng đang được focus***//
    static public int postionofFocusingAlbum=0;

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG 1","Onresume");
        //***Khởi tạo Mang***//
        Mang=new ArrayList<ThongtinAlbum>();

        //***Cập nhật Mang thông qua MainActivity.Mang và MainActivity.MangTen***//
        for(int i=0;i<MainActivity.mang.size();i++)
        {
            //***Nếu ten album là "$" tức là chưa có tên thì lấy tên hình đầu tiên làm tên***//
            if(MainActivity.MangTen.get(i).equals("$")) {
                Mang.add(new ThongtinAlbum(MainActivity.mang.get(i).get(0).getTenhinh()
                        , MainActivity.mang.get(i).get(0).getDuongdan()
                        , MainActivity.mang.get(i).size()));
            }
            else
            {

                Mang.add(new ThongtinAlbum(MainActivity.MangTen.get(i)
                        , MainActivity.mang.get(i).get(0).getDuongdan()
                        , MainActivity.mang.get(i).size()));
            }
        }
        //***Sort Mang để show album theo dạng STACK***//
        Collections.reverse(Mang);

        //***xuat listview album***//
        gvadapter=new CustomListviewAdapter(getActivity(),Mang,R.layout.custom_item_listview_album);
            gridView.setAdapter(gvadapter);

        //***Bắt sự kiện click item của list view để xuất Album đó trong AlbumActivity***//
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(MainActivity.SeleteAlbum==false) {
                    //***Lấy vị trí album được focus***//
                    postionofFocusingAlbum = MainActivity.mang.size() - 1 - position;
                    //Toast.makeText(getActivity(), "" + postionofFocusingAlbum, //Toast.LENGTH_SHORT).show();

                    //***Gọi Intent sang AlbumActivity***//
                    Intent intent = new Intent(getContext(), AlbumActivity.class);
                    intent.putExtra("thutu", postionofFocusingAlbum);
                    intent.putExtra("ten", Mang.get(position).getTen());
                    getContext().startActivity(intent);
                }
                else
                {
                        for (int i = 0; i < MainActivity.collectedimgs.size(); i++) {
                            boolean flag=false;
                            for(int j=0;j<MainActivity.mang.get(MainActivity.mang.size() - 1 - position).size();j++) {
                                if(MainActivity.mang.get(MainActivity.mang.size() - 1 - position).get(j).getDuongdan().equals(MainActivity.collectedimgs.get(i).getDuongdan()))
                                {
                                    flag=true;
                                    break;
                                }
                            }
                            if(flag==false)
                            {
                                MainActivity.mang.get(MainActivity.mang.size() - 1 - position).add(MainActivity.collectedimgs.get(i));

                            }
                        }
                        MainActivity.collectedimgs.clear();
                        //Toast.makeText(getContext(), "Thành Công!", //Toast.LENGTH_SHORT).show();
                        MainActivity.SeleteAlbum = false;
                        ghivaobonhotrong();
                        MainActivity.viewPager.setAdapter(MainActivity.pagerAdapter);
                        MainActivity.viewPager.setCurrentItem(1);
                }
            }
        });

        //***Đăng kí sử dụng ContextMenu cho đối tượng listview***//
        registerForContextMenu(gridView);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("TAG 1","AlbumFragment");
        //***Dán R.layout.album_layout vào View để dán vào Fragment***//
        View view=inflater.inflate(R.layout.album_layout,container,false);

        //***Ánh xạ***//
        gridView=(GridView) view.findViewById(R.id.gvalbum);
        return view;
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {


    }
    //***Hàm cập nhật album trong mang, MangTen và bộ nhớ***//
    public void refreshfAlbum(ArrayList<Hinh> collectedimgs) {
        for(int i=0;i<collectedimgs.size();i++)
        {
            for( int j=0;j<MainActivity.mang.size();j++)
                for(int j1=0;j1<MainActivity.mang.get(j).size();j1++)
                {
                    if(MainActivity.mang.get(j).get(j1).getDuongdan().equals(collectedimgs.get(i).getDuongdan()))
                    {
                        MainActivity.mang.get(j).remove(j1);
                    }
                }
        }
        for(int i=0;i<MainActivity.mang.size();i++)
        {
            if(MainActivity.mang.get(i).size()==0)
            {
                MainActivity.mang.remove(i);
                MainActivity.MangTen.remove(i);
            }
        }
        ghivaobonhotrong();

    }
    public void ghivaobonhotrong()
    {

        try {
            //bo nho trong
            //FileOutputStream out=openFileOutput("abc.txt",MODE_PRIVATE);

            //bo nho trong, cache
            File duongdan=getActivity().getCacheDir();
            File taptin=new File(duongdan,"imgofalbum.txt");
            Log.d("lienket",taptin+"");

            FileOutputStream out=new FileOutputStream(taptin);
            String buffer=new String();
            for(int i=0;i<MainActivity.mang.size();i++) {
                for (int j = 0; j < MainActivity.mang.get(i).size(); j++) {
                    if(j==MainActivity.mang.get(i).size()-1)
                    {
                        buffer = buffer + MainActivity.mang.get(i).get(j).getDuongdan().toString()
                                + "#"
                                + MainActivity.mang.get(i).get(j).getTenhinh().toString()
                                + "#"
                                + MainActivity.mang.get(i).get(j).getAddDate().toString()
                        ;
                    }
                    else {
                        buffer = buffer + MainActivity.mang.get(i).get(j).getDuongdan().toString()
                                + "#"
                                + MainActivity.mang.get(i).get(j).getTenhinh().toString()
                                + "#"
                                + MainActivity.mang.get(i).get(j).getAddDate().toString()
                                + "#";
                    }

                }
                buffer+="%";
            }

            Log.e("mang",buffer);
            out.write(buffer.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
