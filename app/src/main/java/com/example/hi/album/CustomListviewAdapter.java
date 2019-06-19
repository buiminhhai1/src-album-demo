package com.example.hi.album;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;
//***Custom List view dùng đề show danh sách album***//
public class CustomListviewAdapter extends ArrayAdapter<ThongtinAlbum> {
    Context context;
    ArrayList<ThongtinAlbum> mang;
    int layoutResource;

    public CustomListviewAdapter( Context context, ArrayList<ThongtinAlbum> mang, int layoutResource) {
        super(context, layoutResource, mang);
        this.context = context;
        this.mang = mang;
        this.layoutResource = layoutResource;
    }

    @Override
    public int getCount() {
        return mang.size();
    }

    //***ViewHolder***//
    public class ViewHolder
    {
        TextView ten,soluong;
        ImageView imageView;
        ConstraintLayout constraintLayout;
        CheckBox checkBox;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=((MainActivity)context).getLayoutInflater();

        final ViewHolder viewHolder;
        //***Xét điều kiện***//
        if(convertView==null) {
            viewHolder=new ViewHolder();
            convertView=layoutInflater.inflate(layoutResource,null);

            viewHolder.ten = (TextView) convertView.findViewById(R.id.tenalbum);
            viewHolder.soluong = (TextView) convertView.findViewById(R.id.soluong);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imgalbum);
            viewHolder.constraintLayout=(ConstraintLayout) convertView.findViewById(R.id.LNofitemalbum);
            viewHolder.checkBox=(CheckBox)convertView.findViewById(R.id.Checkalbum);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.checkBox.setChecked(false);
        viewHolder.ten.setText(mang.get(position).getTen());
        viewHolder.soluong.setText(mang.get(position).getSoluong()+"");
        //File file=new File(mang.get(position).getDuongdan());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH);

        Glide.with(context).load(mang.get(position).getDuongdan())
                .apply(options).thumbnail(0.6f).into(viewHolder.imageView);

        if(MainActivity.statusalbum==true)
        {
            viewHolder.constraintLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.constraintLayout.setVisibility(View.INVISIBLE);
        }

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.checkBox.isChecked()==true)
                {
                    MainActivity.collectedalbums.add(new ThongtinAlbum(mang.get(position).getTen()
                            ,mang.get(position).getDuongdan()
                            ,mang.get(position).getSoluong()));
                }
                else
                {
                    for(int i=0;i<MainActivity.collectedalbums.size();i++)
                    {
                        if(MainActivity.collectedalbums.get(i).getTen().equals(mang.get(position).getTen()))
                        {
                            MainActivity.collectedalbums.remove(i);
                        }
                    }
                }
            }

        });



        return convertView;
    }
}
