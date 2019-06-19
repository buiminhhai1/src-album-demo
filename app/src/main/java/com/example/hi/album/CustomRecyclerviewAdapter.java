
package com.example.hi.album;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//***Custom các RecyclerView có trong Project***//
public class CustomRecyclerviewAdapter extends RecyclerView.Adapter<CustomRecyclerviewAdapter.MyViewHolder> {
    private Context context;

    private ArrayList<Hinh> data;

    //***album=true hoac kho ảnh=false***//
    boolean loai;
    //***Layout muốn dán***//
    private LayoutInflater inflater;
    private int pos;
    public CustomRecyclerviewAdapter(Context context, ArrayList<Hinh> data, boolean loai, int pos) {

        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        this.loai = loai;
        this.pos = pos;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.custom_item_recyclerview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .fitCenter()
                .override(250,250)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH);

        Glide.with(context).load(data.get(position).getDuongdan())
                .apply(options).thumbnail(0.6f)
                .into(myViewHolder.imageView);

        //***lưu ý trang thái được click của các ảnh trong trường hợp chuyển qua imageactivity rồi quay lại***//
        for(int i=0;i<MainActivity.collectedimgs.size();i++)
        {
            if(MainActivity.collectedimgs.get(i).duongdan.equals(data.get(position).duongdan))
            {
                myViewHolder.checkBox.setChecked(true);
            }
        }


        if (MainActivity.status) {
            myViewHolder.linearLayout.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.linearLayout.setVisibility(View.INVISIBLE);
        }
        myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //chuyen man hinh image activity
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra("vitri", data.get(position).getDuongdan());
                intent.putExtra("loai", loai);
                context.startActivity(intent);

            }
        });
        myViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myViewHolder.checkBox.isChecked()) {
                    MainActivity.collectedimgs.add(new Hinh(data.get(position).getDuongdan()
                            , data.get(position).getTenhinh()
                            , data.get(position).getAddDate()));
                    if (!loai)
                        AnhFragment.mangHinhDate.get(pos).get(position).setCheck(true);
                    else {
                        MainActivity.mang.get(pos).get(position).setCheck(true);
                    }
                } else {
                    for (int i = 0; i < MainActivity.collectedimgs.size(); i++) {
                        if (MainActivity.collectedimgs.get(i).getDuongdan().equals(data.get(position).duongdan)) {
                            MainActivity.collectedimgs.remove(i);
                        }
                    }
                    if (!loai)
                        AnhFragment.mangHinhDate.get(pos).get(position).setCheck(false);
                    else {
                        MainActivity.mang.get(pos).get(position).setCheck(false);
                    }
                }
            }

        });
        myViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(loai==false) {
                    if (((MainActivity) context).status == false) {
                        ((MainActivity) context).status = true;
                        ImageActivity.position=pos;
                        MainActivity.viewPager.setAdapter(MainActivity.pagerAdapter);



                        //***Show những lựa chọn cần thiết sau khi Select***//
                        ((MainActivity) context).toolbar.getMenu().getItem(0).setVisible(false);
                        ((MainActivity) context).toolbar.getMenu().getItem(1).setVisible(true);
                        ((MainActivity) context).toolbar.getMenu().getItem(2).setVisible(true);
                        ((MainActivity) context).toolbar.getMenu().getItem(3).setVisible(true);
                        ((MainActivity) context).toolbar.getMenu().getItem(4).setVisible(true);
                        ((MainActivity) context).toolbar.getMenu().getItem(5).setVisible(true);
                    }
                }
                else
                {

                }
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ConstraintLayout linearLayout;
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgrv);
            linearLayout = itemView.findViewById(R.id.LNofcardview);
            checkBox = itemView.findViewById(R.id.Check);


        }
    }
}
